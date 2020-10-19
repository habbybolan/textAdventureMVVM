package com.habbybolan.textadventure.repository;
/*
Loads and Reads JSON files in the assets folder in the background thread
 - parses and retrieves a specific/random encounter for user character to enter
 */

import android.content.Context;
import android.os.AsyncTask;

import com.habbybolan.textadventure.viewmodel.MainGameViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class JsonAssetFileReader {
    private String JSONFileName;// = "outdoor_encounters";
    private Context context;
    private String json;

    private int location;
    private String location_type;

    public JsonAssetFileReader(Context context, String JSONFileName) {
        this.context = context;
        this.JSONFileName = JSONFileName;
    }

    // load JSON data, given path, from assets
    static public String loadJSONFromData(String path) throws IOException, JSONException {

        String encounter = "";
        try {
            File fileReader = new File(path);
            BufferedReader br = new BufferedReader(new FileReader(fileReader));
            String tempEncounter;
            while ((tempEncounter = br.readLine()) != null)
                encounter += tempEncounter;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // todo: encounter might be null - what to do>?
        return encounter;
    }

    public class loadJSONFromAssets extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                InputStream is = context.getAssets().open(JSONFileName);

                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();

                json = new String(buffer, StandardCharsets.UTF_8);


            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }

    public String getJSON() {
        return json;
    }

    // reads the json file in assets and saves to json field
    public void loadJSONFromAssets() {
        new loadJSONFromAssets().execute();
    }

    public class parseRandomJSONData extends AsyncTask<Void, Void, JSONObject> {
        JSONObject outputEncounter = new JSONObject();

        @Override
        protected JSONObject doInBackground(Void ... voids) {
            try {
                // get the json object and array
                JSONObject obj = new JSONObject(json);
                JSONArray jsonArray = obj.getJSONArray(ALL_ENCOUNTERS);
                // encounterTemp: encounter data to retrieve, store and return
                JSONObject encounterTemp = jsonArray.getJSONObject(location);
                jsonArray = encounterTemp.getJSONArray(location_type);
                // get a random type of encounter // todo: control the weighting of the randomness for the type of encounter
                //encounterTemp = jsonArray.getJSONObject(getRandomJsonArrayIndex(jsonArray));

                // ***
                // todo: used for testing specific encounters
                //Random rand = new Random();
                //int num = rand.nextInt(2);
                encounterTemp = jsonArray.getJSONObject(MainGameViewModel.COMBAT);
                // ***

                // put type into encounter
                String type = encounterTemp.getString(TYPE);
                outputEncounter.put(TYPE, encounterTemp.get(TYPE));

                // get a random encounter specific of "type"
                jsonArray = encounterTemp.getJSONArray(ENCOUNTERS);
                encounterTemp = jsonArray.getJSONObject(getRandomJsonArrayIndex(jsonArray));
                // find the proper encounter to initiate
                switch (type) {
                    case MainGameViewModel.DUNGEON_TYPE:
                        dungeonEncounter(encounterTemp, outputEncounter);
                        break;
                    case MainGameViewModel.MULTI_LEVEL_TYPE:
                        multiLevelEncounter(encounterTemp, outputEncounter);
                        break;
                    case MainGameViewModel.CHOICE_TYPE:
                        choiceEncounter(encounterTemp, outputEncounter);
                        break;
                    case MainGameViewModel.COMBAT_TYPE:
                        combatEncounter(encounterTemp, outputEncounter);
                        break;
                    case MainGameViewModel.TRAP_TYPE:
                        trapEncounter(encounterTemp, outputEncounter);
                        break;
                    case MainGameViewModel.SHOP_TYPE: case MainGameViewModel.CHOICE_BENEFIT_TYPE: case MainGameViewModel.RANDOM_BENEFIT_TYPE:
                        shopRandomChoiceEncounter(encounterTemp, outputEncounter);
                        break;
                    case MainGameViewModel.QUEST_TYPE:
                        questEncounter(encounterTemp, outputEncounter);
                        break;
                    default: //  shouldn't reach here
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return outputEncounter;
        }
    }

    /**
     * helper method for retrieving and storing the combat encounter JSON data
     *
     * @params encounter   location to put the JSON data retrieved from encounterTemp
     * @Params encounterTemp    JSON object to retrieve the encounter data from
     */
    private void combatEncounter(JSONObject encounterTemp, JSONObject encounter) throws JSONException {
        // get a random dialogue from that encounter specific
        encounter.put(DIALOGUE, getRandomDialogue(encounterTemp));
        // if there is a conversation dialogue, then add to "conversation" object
        if (encounterTemp.has(CONVERSATION)) encounter.put(CONVERSATION, encounterTemp.getJSONObject(CONVERSATION));
        encounter.put(FIGHT, encounterTemp.getJSONObject(FIGHT));
    }

    /**
     * helper method for retrieving and storing the trap encounter JSON data
     */
    private void trapEncounter(JSONObject encounterTemp, JSONObject encounter) throws JSONException {
        // get a random dialogue from that encounter specific
        encounter.put(DIALOGUE, getRandomDialogue(encounterTemp));
        // put in the fail object
        encounter.put(FAIL, encounterTemp.getJSONObject(FAIL));
        // put in the success object
        encounter.put(SUCCESS, encounterTemp.getString(SUCCESS));
    }

    /**
     * helper method for retrieving and storing the Dungeon encounter JSON data
     */
    private void dungeonEncounter(JSONObject encounterTemp, JSONObject encounter) throws JSONException {
        // get a random dialogue from that encounter specific
        encounter.put(DIALOGUE, getRandomDialogue(encounterTemp));
    }

    /**
     * helper method for retrieving and storing the shop, random benefit, and choice benefit encounter JSON data
     */
    private void shopRandomChoiceEncounter(JSONObject encounterTemp, JSONObject encounter) throws JSONException {
        // get a random dialogue from that encounter specific
        encounter.put(DIALOGUE, getRandomDialogue(encounterTemp));
        // if there is a conversation dialogue, then add to "conversation" object
        if (encounterTemp.has(CONVERSATION)) encounter.put(CONVERSATION, encounterTemp.getJSONObject(CONVERSATION));
    }

    /**
     * helper method for retrieving and storing the Quest encounter JSON data
     */
    private void questEncounter(JSONObject encounterTemp, JSONObject encounter) throws JSONException {
        String type = encounterTemp.getString(TYPE);
        JSONArray jsonArray = encounterTemp.getJSONArray(type);
        encounterTemp = jsonArray.getJSONObject(getRandomJsonArrayIndex(jsonArray));
        encounterTemp.put(SUB_TYPE, type);
        encounter.put(ENCOUNTER, encounterTemp);
    }

    /**
     * helper method for retrieving and storing the Choice encounter JSON data
     */
    private void choiceEncounter(JSONObject encounterTemp, JSONObject encounter) throws JSONException {
        // get a random dialogue from that encounter specific
        encounter.put(DIALOGUE, getRandomDialogue(encounterTemp));
        JSONArray options = new JSONArray(); // holds each option and the randomized encounter of each option
        JSONObject singleOption; // holds each single option to put into options JSONArray
        JSONArray jsonArray = encounterTemp.getJSONArray(OPTIONS);
        JSONArray allOptions = new JSONArray(); // array to add to encounter object
        JSONObject optionTemp;
        for (int i = 0; i < jsonArray.length(); i++) {
            singleOption = new JSONObject();
            optionTemp = jsonArray.getJSONObject(i);
            // set up option name
            String name = optionTemp.getString(NAME);
            singleOption.put(NAME, name);
            // get a random encounter within this specific option
            JSONArray randEncounter = optionTemp.getJSONArray(name);
            optionTemp = randEncounter.getJSONObject(getRandomJsonArrayIndex(randEncounter));
            // get the type of the random encounter chosen
            String type = optionTemp.getString(TYPE);
            singleOption.put(TYPE, type);
            // set up the new object for the random encounter given the specific type of encounter, constrained by the possible encounters in the JSON file.
            randEncounter = optionTemp.getJSONArray(type);
            optionTemp = randEncounter.getJSONObject(getRandomJsonArrayIndex(randEncounter));
            singleOption.put(type, optionTemp);

            allOptions.put(singleOption);
        }
        // put the array of possible options, each with one randomized encounter for each index
        encounter.put(OPTIONS, allOptions);
    }

    /**
     * helper method for retrieving and storing the multi level encounter JSON data
     */
    private void multiLevelEncounter(JSONObject encounterTemp, JSONObject encounter) throws JSONException {
        // get a random dialogue from that encounter specific
        encounter.put(DIALOGUE, getRandomDialogue(encounterTemp));
        JSONArray jsonArray = encounterTemp.getJSONArray(OPTIONS);
        encounter.put(OPTIONS, jsonArray);
    }



    // get a random encounter using the json String
    public JSONObject getRandomEncounter(int location, String location_type) throws ExecutionException, InterruptedException {
        this.location = location;
        this.location_type = location_type;
        return new parseRandomJSONData().execute().get();
    }

    private JSONObject getRandomDialogue(JSONObject dialogue) throws JSONException {
        JSONArray dialogueArray = dialogue.getJSONArray(DIALOGUE);
        int index = getRandomJsonArrayIndex(dialogueArray);
        return dialogueArray.getJSONObject(index);
    }

    // return a random index in the JSONArray
    private int getRandomJsonArrayIndex(JSONArray array) {
        Random rand = new Random();
        return rand.nextInt(array.length());
    }

    // getters
    public String getJson() {
        return json;
    }


    public static final String FIGHT = "fight";
    public static final String ENCOUNTER = "encounter";
    public static final String DIALOGUE = "dialogue";
    public static final String SUB_TYPE = "sub_type";
    public static final String TYPE = "type";
    public static final String CONVERSATION = "conversation";
    public static final String FAIL = "fail";
    public static final String SUCCESS = "success";
    public static final String OPTIONS = "options";
    public static final String NAME = "name";
    public static final String ENCOUNTERS = "encounters";
    public static final String ALL_ENCOUNTERS = "allEncounters";
    public static final String DIFFICULTY = "difficulty";
}
