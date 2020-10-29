package com.habbybolan.textadventure.repository;

import android.content.Context;
import android.os.AsyncTask;

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


public class SaveDataLocally {
    Context context;

    public SaveDataLocally(Context context) {
        this.context = context;
    }

    // todo: memory leaks and static - can't be static
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
                    jsonClassObject.put("encounterState", 0);
                    jsonClassObject.put("distance", 0);
                    jsonClassObject.put("dungeonCounter", 0);

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
                    abilitiesArray.put(context.getString(R.string.Wizard_Start_Ability)); // starts with one ability
                    jsonClassObject.put("abilities", abilitiesArray);
                    JSONArray weaponsArray = new JSONArray();
                    weaponsArray.put(context.getString(R.string.Wizard_Start_Weapon)); // starts with one weapon
                    jsonClassObject.put("weapons", weaponsArray);
                    // bars
                    jsonClassObject.put("health", constitution * Character.HEALTH_CON_MULTIPLIER + Character.BASE_HEALTH);
                    jsonClassObject.put("maxHealth", constitution * Character.HEALTH_CON_MULTIPLIER + Character.BASE_HEALTH);
                    jsonClassObject.put("mana", intelligence * Character.MANA_INT_MULTIPLIER + Character.BASE_MANA);
                    jsonClassObject.put("maxMana", intelligence * Character.MANA_INT_MULTIPLIER + Character.BASE_MANA);
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
                    abilitiesArray.put(context.getString(R.string.Paladin_Start_Ability)); // starts with one ability
                    jsonClassObject.put("abilities", abilitiesArray);
                    JSONArray weaponsArray = new JSONArray();
                    weaponsArray.put(context.getString(R.string.Paladin_Start_Weapon)); // starts with one weapon
                    jsonClassObject.put("weapons", weaponsArray);
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
                    abilitiesArray.put(context.getString(R.string.Archer_Start_Ability)); // starts with one ability
                    jsonClassObject.put("abilities", abilitiesArray);
                    JSONArray weaponsArray = new JSONArray();
                    weaponsArray.put(context.getString(R.string.Archer_Start_Weapon)); // starts with one weapon
                    jsonClassObject.put("weapons", weaponsArray);
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
                    abilitiesArray.put(context.getString(R.string.Warrior_Start_Ability)); // starts with one ability
                    jsonClassObject.put("abilities", abilitiesArray);
                    JSONArray weaponsArray = new JSONArray();
                    weaponsArray.put(context.getString(R.string.Warrior_Start_Weapon)); // starts with one weapon
                    jsonClassObject.put("weapons", weaponsArray);
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
                JSONArray emptyArray = new JSONArray();
                jsonClassObject.put("level", 0);
                jsonClassObject.put("expIncrease", 0);
                jsonClassObject.put("gold", context.getString(R.string.Starting_Gold));
                jsonClassObject.put("goldIncrease", 0);
                JSONArray itemsArray = new JSONArray();
                itemsArray.put(context.getString(R.string.Starting_Item));
                jsonClassObject.put("items", itemsArray);
                // specials - set all to 0
                jsonClassObject.put("isStun", 0);
                jsonClassObject.put("isConfuse", 0);
                jsonClassObject.put("isInvincibility", 0);
                jsonClassObject.put("isSilence", 0);
                jsonClassObject.put("invisibility", 0);
                jsonClassObject.put("specialMap", emptyArray);
                // stats
                jsonClassObject.put("strIncrease", 0);
                jsonClassObject.put("strDecrease", 0);
                jsonClassObject.put("intIncrease", 0);
                jsonClassObject.put("intDecrease", 0);
                jsonClassObject.put("conIncrease", 0);
                jsonClassObject.put("conDecrease", 0);
                jsonClassObject.put("spdIncrease", 0);
                jsonClassObject.put("spdDecrease", 0);
                jsonClassObject.put("evasionIncrease", 0);
                jsonClassObject.put("evasionDecrease", 0);
                jsonClassObject.put("blockIncrease", 0);
                jsonClassObject.put("blockDecrease", 0);
                jsonClassObject.put("tempExtraHealth", 0);
                // stats array
                jsonClassObject.put("statIncreaseList", emptyArray);
                jsonClassObject.put("statDecreaseList", emptyArray);
                jsonClassObject.put("tempHealthList", emptyArray);
                jsonClassObject.put("tempManaList", emptyArray);
                // DOT
                jsonClassObject.put("dotMap", emptyArray);



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
            } catch (FileNotFoundException e) {
                e.printStackTrace();
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
            fos.write(jsonString.getBytes());
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
    private void deleteCharacter() {
        File file = new File(context.getResources().getString(R.string.fileCharacter));
        if (file.delete())
            System.out.println("Character deleted");
    }

    // deletes the previous encounters from local storage
    private void deletePreviousEncounters() {
        File file = new File(context.getResources().getString(R.string.fileEncounter));
        if (file.delete())
            System.out.println("Previous encounters deleted");
    }




    // todo: saves incorrectly - fix this
    public void saveEncounter(String encounter) throws JSONException {
        /*String filename = context.getResources().getString(R.string.fileEncounter);
        FileOutputStream fOut;
        JSONObject jsonObject;
        JSONArray jsonArray;
        if (!readSavedEncounter(context).equals("")) {
            jsonObject = new JSONObject(readAllEncounters(context));
            jsonArray = jsonObject.getJSONArray("encounters");
            jsonArray.put(encounter);
            jsonObject.remove("encounters");
        } else {
            jsonObject = new JSONObject();
            jsonArray = new JSONArray();
            jsonArray.put(encounter);
            jsonObject.remove("encounters");
        }
        jsonObject.put("encounters", jsonArray);
        jsonArray.put(encounter);

        try {
            fOut = context.openFileOutput(filename, Context.MODE_PRIVATE);
            fOut.write(jsonObject.toString().getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}


