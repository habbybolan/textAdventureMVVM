package com.habbybolan.textadventure.repository;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.model.characterentity.Character;
import com.habbybolan.textadventure.viewmodel.CharacterChoiceViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;


public class LocallySavedFiles {
    Context context;
    private final static String TAG = "MainActivity";

    public LocallySavedFiles(Context context) {
        this.context = context;
    }

    // todo: memory leaks and static - can't be static

    /**
     * Saves a new character into local storage by creating its JSONString and storing it.
     */
    public class saveNewCharacterLocally extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String ... character) {
            String filename = context.getResources().getString(R.string.fileCharacter);
            FileOutputStream fOut;
            // JSON object holding all character information
            JSONObject jsonClassObject = new JSONObject();

            if ((CharacterChoiceViewModel.wizard).equals(character[0])) {
                try {
                    jsonClassObject.put("class", Character.WIZARD_CLASS_TYPE);

                    jsonClassObject.put("str", context.getString(R.string.Wizard_Start_Str)); // str
                    jsonClassObject.put("strBase", context.getString(R.string.Wizard_Start_Str)); // base str
                    int intelligence = Integer.parseInt(context.getString(R.string.Wizard_Start_Int));
                    jsonClassObject.put("int", intelligence); // int
                    jsonClassObject.put("intBase", intelligence); // base int
                    int constitution = Integer.parseInt(context.getString(R.string.Wizard_Start_Con));
                    jsonClassObject.put("con", constitution); // con
                    jsonClassObject.put("conBase", constitution); // base con
                    jsonClassObject.put("spd", context.getString(R.string.Wizard_Start_Spd)); // spd
                    jsonClassObject.put("spdBase", context.getString(R.string.Wizard_Start_Spd)); // base spd
                    JSONArray abilitiesArray = new JSONArray();
                    abilitiesArray.put(context.getResources().getInteger(R.integer.Wizard_Start_Ability)); // starts with one ability
                    jsonClassObject.put("abilities", abilitiesArray);
                    JSONArray weaponsArray = new JSONArray();
                    weaponsArray.put(context.getResources().getInteger(R.integer.Wizard_Start_Weapon)); // starts with one weapon
                    jsonClassObject.put("weapons", weaponsArray);
                    JSONArray itemsArray = new JSONArray();
                    itemsArray.put(context.getResources().getInteger(R.integer.Wizard_Start_Item));
                    jsonClassObject.put("items", itemsArray);
                    // bars
                    int health = constitution * Character.HEALTH_CON_MULTIPLIER + Character.BASE_HEALTH;
                    jsonClassObject.put("health", health);
                    jsonClassObject.put("maxHealth", health);
                    int mana = intelligence * Character.MANA_INT_MULTIPLIER + Character.BASE_MANA;
                    jsonClassObject.put("mana", mana);
                    jsonClassObject.put("maxMana", mana);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if ((CharacterChoiceViewModel.paladin).equals(character[0])) {
                try {
                    jsonClassObject.put("class", Character.PALADIN_CLASS_TYPE);
                    jsonClassObject.put("str", context.getString(R.string.Paladin_Start_Str)); // str
                    jsonClassObject.put("strBase", context.getString(R.string.Paladin_Start_Str)); // base str
                    int intelligence = Integer.parseInt(context.getString(R.string.Paladin_Start_Int));
                    jsonClassObject.put("int", intelligence); // int
                    jsonClassObject.put("intBase", intelligence); // base int
                    int constitution = Integer.parseInt(context.getString(R.string.Paladin_Start_Con));
                    jsonClassObject.put("con", constitution); // con
                    jsonClassObject.put("conBase", constitution); // base con
                    jsonClassObject.put("spd", context.getString(R.string.Paladin_Start_Spd)); // spd
                    jsonClassObject.put("spdBase", context.getString(R.string.Paladin_Start_Spd)); // base spd
                    JSONArray abilitiesArray = new JSONArray();
                    abilitiesArray.put(context.getResources().getInteger(R.integer.Paladin_Start_Ability)); // starts with one ability
                    jsonClassObject.put("abilities", abilitiesArray);
                    JSONArray weaponsArray = new JSONArray();
                    weaponsArray.put(context.getResources().getInteger(R.integer.Paladin_Start_Weapon)); // starts with one weapon
                    jsonClassObject.put("weapons", weaponsArray);
                    JSONArray itemsArray = new JSONArray();
                    itemsArray.put(context.getResources().getInteger(R.integer.Paladin_Start_Item));
                    jsonClassObject.put("items", itemsArray);
                    // bars
                    jsonClassObject.put("health", constitution * Character.HEALTH_CON_MULTIPLIER + Character.BASE_HEALTH);
                    jsonClassObject.put("maxHealth", constitution * Character.HEALTH_CON_MULTIPLIER + Character.BASE_HEALTH);
                    jsonClassObject.put("mana", intelligence * Character.MANA_INT_MULTIPLIER + Character.BASE_MANA);
                    jsonClassObject.put("maxMana", intelligence * Character.MANA_INT_MULTIPLIER + Character.BASE_MANA);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if ((CharacterChoiceViewModel.archer).equals(character[0])) {
                try {
                    jsonClassObject.put("class", Character.ARCHER_CLASS_TYPE);
                    jsonClassObject.put("str", context.getString(R.string.Archer_Start_Str)); // str
                    jsonClassObject.put("strBase", context.getString(R.string.Archer_Start_Str)); // base str
                    int intelligence = Integer.parseInt(context.getString(R.string.Archer_Start_Int));
                    jsonClassObject.put("int", intelligence); // int
                    jsonClassObject.put("intBase", intelligence); // base int
                    int constitution = Integer.parseInt(context.getString(R.string.Archer_Start_Con));
                    jsonClassObject.put("con", constitution); // con
                    jsonClassObject.put("conBase", constitution); // base con
                    jsonClassObject.put("spd", context.getString(R.string.Archer_Start_Spd)); // spd
                    jsonClassObject.put("spdBase", context.getString(R.string.Archer_Start_Spd)); // base spd
                    JSONArray abilitiesArray = new JSONArray();
                    abilitiesArray.put(context.getResources().getInteger(R.integer.Archer_Start_Ability)); // starts with one ability
                    jsonClassObject.put("abilities", abilitiesArray);
                    JSONArray weaponsArray = new JSONArray();
                    weaponsArray.put(context.getResources().getInteger(R.integer.Archer_Start_Weapon)); // starts with one weapon
                    jsonClassObject.put("weapons", weaponsArray);
                    JSONArray itemsArray = new JSONArray();
                    itemsArray.put(context.getResources().getInteger(R.integer.Archer_Start_Item));
                    jsonClassObject.put("items", itemsArray);
                    // bars
                    jsonClassObject.put("health", constitution * Character.HEALTH_CON_MULTIPLIER + Character.BASE_HEALTH);
                    jsonClassObject.put("maxHealth", constitution * Character.HEALTH_CON_MULTIPLIER + Character.BASE_HEALTH);
                    jsonClassObject.put("mana", intelligence * Character.MANA_INT_MULTIPLIER + Character.BASE_MANA);
                    jsonClassObject.put("maxMana", intelligence * Character.MANA_INT_MULTIPLIER + Character.BASE_MANA);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if ((CharacterChoiceViewModel.warrior).equals(character[0])) {
                try {
                    jsonClassObject.put("class", Character.WARRIOR_CLASS_TYPE);
                    jsonClassObject.put("str", context.getString(R.string.Warrior_Start_Str)); // str
                    jsonClassObject.put("strBase", context.getString(R.string.Warrior_Start_Str)); // base str
                    int intelligence = Integer.parseInt(context.getString(R.string.Warrior_Start_Int));
                    jsonClassObject.put("int", intelligence); // int
                    jsonClassObject.put("intBase", intelligence); // base int
                    int constitution = Integer.parseInt(context.getString(R.string.Warrior_Start_Con));
                    jsonClassObject.put("con", constitution); // con
                    jsonClassObject.put("conBase", constitution); // base con
                    jsonClassObject.put("spd", context.getString(R.string.Warrior_Start_Spd)); // spd
                    jsonClassObject.put("spdBase", context.getString(R.string.Warrior_Start_Spd)); // base spd
                    JSONArray abilitiesArray = new JSONArray();
                    abilitiesArray.put(context.getResources().getInteger(R.integer.Warrior_Start_Ability)); // starts with one ability
                    jsonClassObject.put("abilities", abilitiesArray);
                    JSONArray weaponsArray = new JSONArray();
                    weaponsArray.put(context.getResources().getInteger(R.integer.Warrior_Start_Weapon)); // starts with one weapon
                    jsonClassObject.put("weapons", weaponsArray);
                    JSONArray itemsArray = new JSONArray();
                    itemsArray.put(context.getResources().getInteger(R.integer.Warrior_Start_Item));
                    jsonClassObject.put("items", itemsArray);
                    // bars
                    jsonClassObject.put("health", constitution * Character.HEALTH_CON_MULTIPLIER + Character.BASE_HEALTH);
                    jsonClassObject.put("maxHealth", constitution * Character.HEALTH_CON_MULTIPLIER + Character.BASE_HEALTH);
                    jsonClassObject.put("mana", intelligence * Character.MANA_INT_MULTIPLIER + Character.BASE_MANA);
                    jsonClassObject.put("maxMana", intelligence * Character.MANA_INT_MULTIPLIER + Character.BASE_MANA);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            // this info is consistent for all starting characters
            try {
                jsonClassObject.put("encounterState", 0);
                jsonClassObject.put("distance", 0);
                jsonClassObject.put("gold", context.getString(R.string.Starting_Gold));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                fOut = context.openFileOutput(filename, Context.MODE_PRIVATE);
                fOut.write(jsonClassObject.toString().getBytes());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    // saves the data of a new character
    public void saveNewCharacterLocally(String character) {
        new saveNewCharacterLocally().execute(character);
    }

    public class saveCharacterLocally extends AsyncTask<Character, Void, Void> {

        @Override
        protected Void doInBackground(Character... characterArr) {
            String filename = context.getResources().getString(R.string.fileCharacter);
            FileOutputStream fOut;
            // JSON object holding all character information
            Character character = characterArr[0];
            JSONObject jsonClassObject = new JSONObject();
            try {
                jsonClassObject = character.serializeToJSON();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            try {
                fOut = context.openFileOutput(filename, Context.MODE_PRIVATE);
                fOut.write(jsonClassObject.toString().getBytes());
                Log.i(TAG, "Character saved");
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    // todo: check if works and put on background thread
    // save an already made character locally to json file
    public void saveCharacterLocally(Character character) {
        new saveCharacterLocally().execute(character);
    }


    // return the string of the character data saved locally, null if no character exists
    public String readCharacterData() {
        return readStringFomFile(context.getResources().getString(R.string.fileCharacter));
    }

    // returns the JSON of encounter object, or null if none exists
    public JSONObject readSavedEncounter() {
        String encounterString = readStringFomFile(context.getResources().getString(R.string.fileEncounter));
        try {
            if (encounterString != null) return new JSONObject(encounterString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveEncounter(JSONObject objectToSave) {
        String fileName = context.getResources().getString(R.string.fileEncounter);
        String jsonString = objectToSave.toString();
        try (FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE)) {
            fos.write("".getBytes());
            fos.write(jsonString.getBytes());
            Log.i(TAG, "Encounter saved");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // helper to read a String from local storage
    private String readStringFomFile(String fileName) {
        try {
            File file = new File(context.getFilesDir(), fileName);
            if (file.exists()) {
                FileInputStream fis = context.openFileInput(fileName);
                InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);

                StringBuilder stringBuilder = new StringBuilder();
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    stringBuilder.append(line);
                    line = reader.readLine();
                }
                return stringBuilder.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String readAllEncounters() {
        String fileName = context.getResources().getString(R.string.fileEncounter);
        return readJSON(context, fileName);
    }

    // read the local JSON data, given a fileName
    private String readJSON(Context ctx, String fileName) {
        String lineData= "";

        FileInputStream fileInputStream = null;
        try {
            fileInputStream = ctx.openFileInput(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        try {
            lineData = bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lineData;
    }

    public void resetGameFiles() {
        deleteCharacter();
        deletePreviousEncounters();
    }

    // deleted the character from local storage
    public void deleteCharacter() {
        File fileCharacter = new File(context.getFilesDir(), context.getResources().getString(R.string.fileCharacter));
        if (fileExists(fileCharacter)) {
            if (fileCharacter.delete()) {
                Log.i(TAG, "Saved character deleted successfully");
            }
        }
    }

    // deletes the previous encounters from local storage
    private void deletePreviousEncounters() {
        File fileEncounter = new File(context.getFilesDir(), context.getResources().getString(R.string.fileEncounter));
        if (fileExists(fileEncounter)) {
            if (fileEncounter.delete()) {
                Log.i(TAG, "Saved encounter deleted successfully");
            }
        }
    }

    // returns true if a character exists
    public boolean hasCharacter() {
        File file = new File(context.getFilesDir(), context.getResources().getString(R.string.fileCharacter));
        // if character data exists, bring up the continue fragment
        return fileExists(file);
    }

    // return true if the file exists
    private boolean fileExists(File file) {
        return (file.exists());
    }
}


