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
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class JsonAssetFileReader {
    private static final String TAG = "";
    private String JSONFileName;// = "outdoor_encounters";
    private Context context;
    private String json;

    private int location;
    private String location_type;

    private JSONObject encounter;

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
                encounter = encounter + tempEncounter;
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

                json = new String(buffer, "UTF-8");


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
                JSONArray jsonArray = obj.getJSONArray("allEncounters");
                JSONObject encounterTemp = jsonArray.getJSONObject(location);
                jsonArray = encounterTemp.getJSONArray(location_type);
                // get a random type of encounter // todo: control the weighting of the randomness for the type of encounter
                //encounterTemp = jsonArray.getJSONObject(getRandomJsonArrayIndex(jsonArray));

                // ***
                // todo: used for testing specific encounters
                //Random rand = new Random();
                //int num = rand.nextInt(2);
                encounterTemp = jsonArray.getJSONObject(MainGameViewModel.TRAP);
                // ***

                // put type into encounter
                String type = encounterTemp.getString("type");
                outputEncounter.put("type", encounterTemp.get("type"));

                // get a random encounter specific of "type"
                jsonArray = encounterTemp.getJSONArray("encounters");
                encounterTemp = jsonArray.getJSONObject(getRandomJsonArrayIndex(jsonArray));
                // find the proper encounter to initiate
                switch (type) {
                    case MainGameViewModel.DUNGEON_TYPE:
                        // get a random dialogue from that encounter specific
                        outputEncounter.put("dialogue", getRandomDialogue(encounterTemp));
                        break;
                    case MainGameViewModel.MULTI_LEVEL_TYPE:
                        // get a random dialogue from that encounter specific
                        outputEncounter.put("dialogue", getRandomDialogue(encounterTemp));
                        jsonArray = encounterTemp.getJSONArray("options");
                        outputEncounter.put("options", jsonArray);
                        break;
                    case MainGameViewModel.CHOICE_TYPE:
                        // get a random dialogue from that encounter specific
                        outputEncounter.put("dialogue", getRandomDialogue(encounterTemp));
                        JSONArray options = new JSONArray(); // holds each option and the randomized encounter of each option
                        JSONObject singleOption; // holds each single option to put into options JSONArray
                        jsonArray = encounterTemp.getJSONArray("options");
                        JSONArray allOptions = new JSONArray(); // array to add to encounter object
                        JSONObject optionTemp;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            singleOption = new JSONObject();
                            optionTemp = jsonArray.getJSONObject(i);
                            // set up option name
                            String name = optionTemp.getString("name");
                            singleOption.put("name", name);
                            // get a random encounter within this specific option
                            JSONArray randEncounter = optionTemp.getJSONArray(name);
                            optionTemp = randEncounter.getJSONObject(getRandomJsonArrayIndex(randEncounter));
                            // get the type of the random encounter chosen
                            type = optionTemp.getString("type");
                            singleOption.put("type", type);
                            // set up the new object for the random encounter given the specific type of encounter, constrained by the possible encounters in the JSON file.
                            randEncounter = optionTemp.getJSONArray(type);
                            optionTemp = randEncounter.getJSONObject(getRandomJsonArrayIndex(randEncounter));
                            singleOption.put(type, optionTemp);

                            allOptions.put(singleOption);
                        }
                        // put the array of possible options, each with one randomized encounter for each index
                        outputEncounter.put("options", allOptions);
                        break;
                    case MainGameViewModel.COMBAT_TYPE:
                        // get a random dialogue from that encounter specific
                        outputEncounter.put("dialogue", getRandomDialogue(encounterTemp));
                        // if there is a conversation dialogue, then add to "conversation" object
                        if (encounterTemp.has("conversation")) outputEncounter.put("conversation", encounterTemp.getJSONObject("conversation"));
                        outputEncounter.put("fight", encounterTemp.getJSONObject("fight"));
                        break;
                    case MainGameViewModel.TRAP_TYPE:
                        // get a random dialogue from that encounter specific
                        outputEncounter.put("dialogue", getRandomDialogue(encounterTemp));
                        // put in the fail object
                        outputEncounter.put("fail", encounterTemp.getJSONObject("fail"));
                        // put in the success object
                        outputEncounter.put("success", encounterTemp.getString("success"));
                        break;
                    case MainGameViewModel.SHOP_TYPE: case MainGameViewModel.CHOICE_BENEFIT_TYPE: case MainGameViewModel.RANDOM_BENEFIT_TYPE:
                        // get a random dialogue from that encounter specific
                        outputEncounter.put("dialogue", getRandomDialogue(encounterTemp));
                        // if there is a conversation dialogue, then add to "conversation" object
                        if (encounterTemp.has("conversation")) outputEncounter.put("conversation", encounterTemp.getJSONObject("conversation"));
                        break;
                    case MainGameViewModel.QUEST_TYPE:
                        type = encounterTemp.getString("type");
                        jsonArray = encounterTemp.getJSONArray(type);
                        encounterTemp = jsonArray.getJSONObject(getRandomJsonArrayIndex(jsonArray));
                        encounterTemp.put("sub_type", type);
                        outputEncounter.put("encounter", encounterTemp);
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


    // get a random encounter using the json String
    public JSONObject getRandomEncounter(int location, String location_type) throws ExecutionException, InterruptedException {
        this.location = location;
        this.location_type = location_type;
        return new parseRandomJSONData().execute().get();
    }

    private JSONObject getRandomDialogue(JSONObject dialogue) throws JSONException {
        JSONArray dialogueArray = dialogue.getJSONArray("dialogue");
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

}
