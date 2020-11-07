package com.habbybolan.textadventure.repository;
/*
Loads and Reads JSON files in the assets folder in the background thread
 - parses and retrieves a specific/random encounter for user character to enter
 */

import android.content.Context;

import com.habbybolan.textadventure.model.locations.CombatDungeon;
import com.habbybolan.textadventure.model.locations.MultiDungeon;
import com.habbybolan.textadventure.model.locations.Outdoor;
import com.habbybolan.textadventure.viewmodel.MainGameViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class JsonAssetFileReader {
    private String outdoorJSONFilename = MainGameViewModel.outdoorJSONFilename;
    private String multiDungeonJSONFilename = MainGameViewModel.multiDungeonJSONFilename;
    private String combatDungeonJSONFilename = MainGameViewModel.combatDungeonJSONFilename;
    private String breakJSONFilename = MainGameViewModel.breakJSONFilename;
    private Context context;
    private String json;

    private int location;
    private String location_type;

    public JsonAssetFileReader(Context context) {
        this.context = context;
    }

    /**
     * Calls callable to load the JSON String from assets on a separate thread, returning the result through call.
     */
    private static class LoadJSONFromFile implements Callable<String> {
        private final String fileName;
        private final Context context;

        LoadJSONFromFile(String fileName, Context context) {
            this.fileName = fileName;
            this.context = context;
        }

        @Override
        public String call() {
            try {
                InputStream is = context.getAssets().open(fileName);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                return new String(buffer, StandardCharsets.UTF_8);

            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return null;
        }
    }

    public String getJSON() {
        return json;
    }

    // reads the json file in assets and saves to json field
    private void loadJSONFromAssets(String fileName) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        Callable callable = new LoadJSONFromFile(fileName, context);
        Future<String> future = executor.submit(callable);
        json = future.get();
    }

    /**
     * Parse a random outdoor encounter from the stored JSONObject json from outdoor_encounters asset file
     */
    class parseRandomOutdoorJSON implements Callable<JSONObject> {
        JSONObject outputEncounter = new JSONObject();

        @Override
        public JSONObject call() {
            try {
                // get the json object and array
                JSONObject obj = new JSONObject(json);
                JSONArray jsonArray = obj.getJSONArray(ALL_ENCOUNTERS);
                // encounterTemp: encounter data to retrieve, store and return
                JSONObject encounterTemp = jsonArray.getJSONObject(location);
                jsonArray = encounterTemp.getJSONArray(location_type);
                // get a random type of encounter // todo: control the weighting of the randomness for the type of encounter
                //encounterTemp = jsonArray.getJSONObject(getRandomJsonArrayIndex(jsonArray));

                // get random outdoor encounter with weighting
                //encounterTemp = jsonArray.getJSONObject(Outdoor.getRandomEncounter());
                encounterTemp = jsonArray.getJSONObject(Outdoor.TRAP);

                // put type into encounter
                String type = encounterTemp.getString(TYPE);
                outputEncounter.put(TYPE, encounterTemp.get(TYPE));

                // get a random encounter specific of "type"
                jsonArray = encounterTemp.getJSONArray(ENCOUNTERS);
                encounterTemp = jsonArray.getJSONObject(getRandomJsonArrayIndex(jsonArray));
                finalizeOutdoorEncounter(encounterTemp, outputEncounter, type);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return outputEncounter;
        }
    }

    /**
     * Parse a random multi dungeon encounter from the stored JSONObject json from multi_dungeon_encounters asset file
     */
    class parseRandomMultiDungeonJSON implements Callable<JSONObject> {
        JSONObject outputEncounter = new JSONObject();

        @Override
        public JSONObject call() {
            try {
                // get the json object and array
                JSONObject obj = new JSONObject(json);
                JSONArray jsonArray = obj.getJSONArray(ALL_ENCOUNTERS);
                // encounterTemp: encounter data to retrieve, store and return
                JSONObject encounterTemp = jsonArray.getJSONObject(MultiDungeon.TRAP);

                // put type into encounter
                String type = encounterTemp.getString(TYPE);
                outputEncounter.put(TYPE, encounterTemp.get(TYPE));

                // get a random encounter specific of "type"
                jsonArray = encounterTemp.getJSONArray(ENCOUNTERS);
                encounterTemp = jsonArray.getJSONObject(getRandomJsonArrayIndex(jsonArray));
                finalizeMultiDungeonEncounter(encounterTemp, outputEncounter, type);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return outputEncounter;
        }
    }

    /**
     * Parse a random combat dungeon encounter from the stored JSONObject json in combat_dungeon_encounters asset file
     */
    class parseRandomCombatDungeonJSON implements Callable<JSONObject> {
        JSONObject outputEncounter = new JSONObject();

        @Override
        public JSONObject call() {
            try {
                // get the json object for the combat encounter(s).
                JSONObject encounterTemp = new JSONObject(json);
                // put type into encounter
                String type = encounterTemp.getString(TYPE);
                outputEncounter.put(TYPE, encounterTemp.get(TYPE));
                // get a random encounter specific of "type"
                JSONArray jsonArray = encounterTemp.getJSONArray(ENCOUNTERS);
                encounterTemp = jsonArray.getJSONObject(getRandomJsonArrayIndex(jsonArray));
                finalizeCombatDungeonEncounter(encounterTemp, outputEncounter, type);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return outputEncounter;
        }
    }

    /**
     * Parse a random break encounter from the stored JSONObject json in break_encounters asset file
     */
    class parseRandomBreakJSON implements Callable<JSONObject> {
        JSONObject outputEncounter = new JSONObject();

        @Override
        public JSONObject call() {
            try {
                // get the json object and parse a random dialogue from the array
                JSONObject obj = new JSONObject(json);
                JSONArray jsonArray = obj.getJSONArray(DIALOGUE);
                JSONObject encounterTemp = jsonArray.getJSONObject(getRandomJsonArrayIndex(jsonArray));

                finalizeBreakEncounter(encounterTemp, outputEncounter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return outputEncounter;
        }
    }

    /**
     * Takes a JSONObject encounterTemp and finds the method to call to get the appropriate JSON outdoor encounter to store for use
     * inside encounter
     * @param encounterTemp     The JSONObject to trim and store in encounter.
     * @param encounter         The JSONObject that holds the necessary encounter data.
     */
    private void finalizeBreakEncounter(JSONObject encounterTemp, JSONObject encounter) throws JSONException {
        // store the encounter type
        encounter.put(TYPE, MainGameViewModel.BREAK_TYPE);
        // get a random dialogue from that specific encounter
        encounter.put(DIALOGUE, encounterTemp);
    }


    /**
     * Takes a JSONObject encounterTemp and finds the method to call to get the appropriate JSON outdoor encounter to store for use
     * inside encounter
     * @param encounterTemp     The JSONObject to trim and store in encounter.
     * @param encounter         The JSONObject that holds the necessary encounter data.
     */
    private void finalizeOutdoorEncounter(JSONObject encounterTemp, JSONObject encounter, String type) throws JSONException {
        switch (type) {
            case Outdoor.COMBAT_DUNGEON_TYPE:
                combatDungeonEncounter(encounterTemp, encounter);
                break;
            case Outdoor.MULTI_DUNGEON_TYPE:
                multiLevelEncounter(encounterTemp, encounter);
                break;
            case Outdoor.CHOICE_TYPE:
                choiceEncounter(encounterTemp, encounter);
                break;
            case Outdoor.COMBAT_TYPE:
                combatEncounter(encounterTemp, encounter);
                break;
            case Outdoor.TRAP_TYPE:
                trapEncounter(encounterTemp, encounter);
                break;
            case Outdoor.SHOP_TYPE: case Outdoor.CHOICE_BENEFIT_TYPE: case Outdoor.RANDOM_BENEFIT_TYPE:
                shopRandomChoiceEncounter(encounterTemp, encounter);
                break;
            default: //  shouldn't reach here
                throw new IllegalArgumentException(type + " not a valid encounter type");
        }
    }

    /**
     * Takes a JSONObject encounterTemp and finds the method to call to get the appropriate JSON outdoor encounter to store for use
     * inside encounter
     * @param encounterTemp     The JSONObject to trim and store in encounter.
     * @param encounter         The JSONObject that holds the necessary encounter data.
     */
    private void finalizeMultiDungeonEncounter(JSONObject encounterTemp, JSONObject encounter, String type) throws JSONException {
        switch (type) {
            case MultiDungeon.CHOICE_TYPE:
                choiceEncounter(encounterTemp, encounter);
                break;
            case MultiDungeon.COMBAT_TYPE:
                combatEncounter(encounterTemp, encounter);
                break;
            case MultiDungeon.TRAP_TYPE:
                trapEncounter(encounterTemp, encounter);
                break;
            case MultiDungeon.SHOP_TYPE: case MultiDungeon.CHOICE_BENEFIT_TYPE: case MultiDungeon.RANDOM_BENEFIT_TYPE:
                shopRandomChoiceEncounter(encounterTemp, encounter);
                break;
            default: //  shouldn't reach here
                throw new IllegalArgumentException(type + " not a valid encounter type");
        }
    }

    /**
     * Takes a JSONObject encounterTemp and finds the method to call to get the appropriate JSON outdoor encounter to store for use
     * inside encounter
     * @param encounterTemp     The JSONObject to trim and store in encounter.
     * @param encounter         The JSONObject that holds the necessary encounter data.
     */
    private void finalizeCombatDungeonEncounter(JSONObject encounterTemp, JSONObject encounter, String type) throws JSONException {
        switch (type) {
            case CombatDungeon.COMBAT_TYPE:
                combatEncounter(encounterTemp, encounter);
                break;
            default: //  shouldn't reach here
                throw new IllegalArgumentException(type + " not a valid encounter type");
        }
    }

    /**
     * Helper method for retrieving and storing the combat encounter JSON data.
     * {"type":"combat",
     *  "dialogue":{...},
     *  "fight":{"type":["...", ...],
     *          "difficulty":["...", ...]
     *          }
     * }
     *
     * @param encounterTemp     object to parse
     * @param encounter         object to put parsed object in
     * @throws JSONException    JSON formatting error
     */
    private void combatEncounter(JSONObject encounterTemp, JSONObject encounter) throws JSONException {
        // get a random dialogue from that encounter specific
        encounter.put(DIALOGUE, getRandomDialogue(encounterTemp));
        encounter.put(FIGHT, encounterTemp.getJSONObject(FIGHT));
    }

    /**
     * Helper method for retrieving and storing the trap encounter JSON data.
     * {"type":"trap",
     *  "dialogue":{...},
     *   "fail":{"dialogue":{...},
     *           "debuff":[{...}, {...}, ...]
     *          },
     *    "success":"..."
     *   }
     *
     * @param encounterTemp     object to parse
     * @param encounter         object to put parsed object in
     * @throws JSONException    JSON formatting error
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
    private void combatDungeonEncounter(JSONObject encounterTemp, JSONObject encounter) throws JSONException {
        // get a random dialogue from that encounter specific
        encounter.put(DIALOGUE, getRandomDialogue(encounterTemp));
    }

    /**
     *  Helper method for retrieving and storing the shop, random benefit, and choice benefit encounter JSON data.
     *  {"type":"random_benefit",
     *  "dialogue":{...}
     *  }
     *
     * @param encounterTemp     object to parse
     * @param encounter         object to put parsed object in
     * @throws JSONException    JSON formatting error
     */
    private void shopRandomChoiceEncounter(JSONObject encounterTemp, JSONObject encounter) throws JSONException {
        // get a random dialogue from that encounter specific
        encounter.put(DIALOGUE, getRandomDialogue(encounterTemp));
    }

    /**
     * helper method for retrieving and storing the Quest encounter JSON data
     */
    private void questEncounter(JSONObject encounterTemp, JSONObject encounter) throws JSONException {
        // todo: quest JSON encounter parser
        String type = encounterTemp.getString(TYPE);
        JSONArray jsonArray = encounterTemp.getJSONArray(type);
        encounterTemp = jsonArray.getJSONObject(getRandomJsonArrayIndex(jsonArray));
        encounterTemp.put(SUB_TYPE, type);
        encounter.put(ENCOUNTER, encounterTemp);
    }

    /**
     * Helper method for retrieving and storing the Choice encounter JSON data.
     *  {"type":"choice",
     *   "dialogue":{...},
     *    "options":[
     *          {"name":"...",
     *          "type":"...",
     *          "dialogue":{...},
     *          ...},
     *
     *          {...},
     *
     *          {...}
     *          ]
     *  }
     * @param encounterTemp     object to parse
     * @param encounter         object to put parsed object in
     * @throws JSONException    JSON formatting error
     */
    private void choiceEncounter(JSONObject encounterTemp, JSONObject encounter) throws JSONException {
        // get a random dialogue from that encounter specific
        encounter.put(DIALOGUE, getRandomDialogue(encounterTemp));
        JSONArray options = encounterTemp.getJSONArray(OPTIONS);
        JSONArray optionsToStore = new JSONArray();
        for (int i = 0; i < options.length(); i++) {
            // create the objectToStore JSONObject and place it in optionsToStore JSONArray
            JSONObject optionToStore = new JSONObject();
            JSONObject option = options.getJSONObject(i);
            // name of the option
            optionToStore.put(NAME, option.getString("name"));
            JSONObject encounterInOption = getRandomOption(option.getJSONArray(ENCOUNTERS_IN_OPTION));
            optionToStore.put(TYPE, encounterInOption.getString(TYPE));
            finalizeOutdoorEncounter(encounterInOption.getJSONObject(ENCOUNTER), optionToStore, encounterInOption.getString(TYPE));
            optionsToStore.put(optionToStore);
        }
        encounter.put(OPTIONS, optionsToStore);
    }

    /**
     * Helper to choose random specific encounter for one of the JSONArray options that hold multiple possible encounters.
     * @param option    JSONArray that holds multiple possible encounters for an option.
     * @return          The random JSONObject encounter.
     */
    private JSONObject getRandomOption(JSONArray option) throws JSONException {
        Random random = new Random();
        int jsonIndex = random.nextInt(option.length());
        return option.getJSONObject(jsonIndex);
    }


    /**
     * Helper method for retrieving and storing the multi level encounter JSON data.
     */
    private void multiLevelEncounter(JSONObject encounterTemp, JSONObject encounter) throws JSONException {
        // get a random dialogue from that encounter specific
        encounter.put(DIALOGUE, getRandomDialogue(encounterTemp));
    }


    /**
     * Get a random outdoor JSON encounter given the location and location_type values
     * @param location                  Location int of outdoor encounter that decides the possible encounters from outdoor_encounters
     * @param location_type             Location String of outdoor encounter that decides the possible encounters from outdoor_encounters
     * @return                          The JSONObject of the new random outdoor encounter to enter
     */
    public JSONObject getRandomOutdoorEncounter(int location, String location_type) throws Exception {
        loadJSONFromAssets(outdoorJSONFilename);
        this.location = location;
        this.location_type = location_type;

        ExecutorService executor = Executors.newFixedThreadPool(1);
        Callable callable = new parseRandomOutdoorJSON();
        Future<JSONObject> future = executor.submit(callable);
        return future.get();
    }

    /**
     * Get a random break JSON encounter.
     * @return              The JSONObject of the new random break encounter to enter.
     */
    public JSONObject getRandomBreakEncounter() throws Exception {
        loadJSONFromAssets(breakJSONFilename);

        ExecutorService executor = Executors.newFixedThreadPool(1);
        Callable callable = new parseRandomBreakJSON();
        Future<JSONObject> future = executor.submit(callable);
        return future.get();
    }

    /**
     * create and return a random encounter inside the multi dungeon encounter set
     * @return  The JSON data of the multi dungeon encounter to enter.
     */
    public JSONObject getRandomMultiDungeonEncounter() throws Exception {
        loadJSONFromAssets(multiDungeonJSONFilename);

        ExecutorService executor = Executors.newFixedThreadPool(1);
        Callable callable = new parseRandomMultiDungeonJSON();
        Future<JSONObject> future = executor.submit(callable);
        return future.get();
    }

    /**
     * create and return a random encounter inside the multi dungeon encounter set
     * @return  The new random JSON data of a combat dungeon encounter.
     */
    public JSONObject getRandomCombatDungeonEncounter() throws Exception {
        loadJSONFromAssets(combatDungeonJSONFilename);

        ExecutorService executor = Executors.newFixedThreadPool(1);
        Callable callable = new parseRandomCombatDungeonJSON();
        Future<JSONObject> future = executor.submit(callable);
        return future.get();
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
    public static final String FAIL = "fail";
    public static final String SUCCESS = "success";
    public static final String OPTIONS = "options";
    public static final String NAME = "name";
    public static final String ENCOUNTERS = "encounters";
    public static final String ALL_ENCOUNTERS = "allEncounters";
    public static final String DIFFICULTY = "difficulty";
    public static final String ENCOUNTERS_IN_OPTION = "encounters_in_option";


}
