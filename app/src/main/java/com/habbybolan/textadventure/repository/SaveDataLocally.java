package com.habbybolan.textadventure.repository;

import android.content.Context;
import android.os.AsyncTask;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.model.characterentity.Character;
import com.habbybolan.textadventure.model.effects.Dot;
import com.habbybolan.textadventure.model.effects.SpecialEffect;
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
                    jsonClassObject.put("class", "Wizard");
                    jsonClassObject.put("str", context.getString(R.string.Wizard_Start_Str)); // str
                    jsonClassObject.put("strBase", context.getString(R.string.Wizard_Start_Str)); // base str
                    jsonClassObject.put("int", context.getString(R.string.Wizard_Start_Int)); // int
                    jsonClassObject.put("intBase", context.getString(R.string.Wizard_Start_Int)); // base int
                    jsonClassObject.put("con", context.getString(R.string.Wizard_Start_Con)); // con
                    jsonClassObject.put("conBase", context.getString(R.string.Wizard_Start_Con)); // base con
                    jsonClassObject.put("spd", context.getString(R.string.Wizard_Start_Spd)); // spd
                    jsonClassObject.put("spdBase", context.getString(R.string.Wizard_Start_Spd)); // base spd
                    JSONArray abilitiesArray = new JSONArray();
                    abilitiesArray.put(context.getString(R.string.Wizard_Start_Ability)); // starts with one ability
                    for (int i = 0; i < Character.MAX_ABILITIES - 1; i++) {
                        abilitiesArray.put(0); // empty ability slots
                    }
                    jsonClassObject.put("abilities", abilitiesArray);
                    JSONArray weaponsArray = new JSONArray();
                    weaponsArray.put(context.getString(R.string.Wizard_Start_Weapon)); // starts with one weapon
                    for (int i = 0; i < Character.MAX_WEAPONS - 1; i++) {
                        weaponsArray.put(0); // empty weapon slots
                    }
                    jsonClassObject.put("weapons", weaponsArray);
                    // equipped weapon
                    jsonClassObject.put("equipped", context.getString(R.string.Wizard_Start_Weapon));
                    // bars
                    jsonClassObject.put("health", Integer.parseInt(context.getString(R.string.Wizard_Start_Con)) + 10); // health = 10 + Con
                    jsonClassObject.put("maxHealth", Integer.parseInt(context.getString(R.string.Wizard_Start_Con)) + 10);
                    jsonClassObject.put("mana", Integer.parseInt(context.getString(R.string.Wizard_Start_Int)) + 10); // mana = 10 + Int
                    jsonClassObject.put("maxMana", Integer.parseInt(context.getString(R.string.Wizard_Start_Int)) + 10);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if ((CharacterChoiceViewModel.paladin).equals(character[0])) {
                try {
                    jsonClassObject.put("class", "Paladin");
                    jsonClassObject.put("str", context.getString(R.string.Paladin_Start_Str)); // str
                    jsonClassObject.put("strBase", context.getString(R.string.Paladin_Start_Str)); // base str
                    jsonClassObject.put("int", context.getString(R.string.Paladin_Start_Int)); // int
                    jsonClassObject.put("intBase", context.getString(R.string.Paladin_Start_Int)); // base int
                    jsonClassObject.put("con", context.getString(R.string.Paladin_Start_Con)); // con
                    jsonClassObject.put("conBase", context.getString(R.string.Paladin_Start_Con)); // base con
                    jsonClassObject.put("spd", context.getString(R.string.Paladin_Start_Spd)); // spd
                    jsonClassObject.put("spdBase", context.getString(R.string.Paladin_Start_Spd)); // base spd
                    JSONArray abilitiesArray = new JSONArray();
                    abilitiesArray.put(context.getString(R.string.Paladin_Start_Ability)); // starts with one ability
                    for (int i = 0; i < Character.MAX_ABILITIES - 1; i++) {
                        abilitiesArray.put(0); // empty ability slots
                    }
                    jsonClassObject.put("abilities", abilitiesArray);
                    JSONArray weaponsArray = new JSONArray();
                    weaponsArray.put(context.getString(R.string.Paladin_Start_Weapon)); // starts with one weapon
                    for (int i = 0; i < Character.MAX_WEAPONS - 1; i++) {
                        weaponsArray.put(0); // empty weapon slots
                    }
                    jsonClassObject.put("weapons", weaponsArray);
                    // equipped weapon
                    jsonClassObject.put("equipped", context.getString(R.string.Paladin_Start_Weapon));
                    // bars
                    jsonClassObject.put("health", Integer.parseInt(context.getString(R.string.Paladin_Start_Con)) + 10); // health = 10 + Con
                    jsonClassObject.put("maxHealth", Integer.parseInt(context.getString(R.string.Paladin_Start_Con)) + 10);
                    jsonClassObject.put("mana", Integer.parseInt(context.getString(R.string.Paladin_Start_Int)) + 10); // mana = 10 + Int
                    jsonClassObject.put("maxMana", Integer.parseInt(context.getString(R.string.Paladin_Start_Int)) + 10);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if ((CharacterChoiceViewModel.archer).equals(character[0])) {
                try {
                    jsonClassObject.put("class", "Archer");
                    jsonClassObject.put("str", context.getString(R.string.Archer_Start_Str)); // str
                    jsonClassObject.put("strBase", context.getString(R.string.Archer_Start_Str)); // base str
                    jsonClassObject.put("int", context.getString(R.string.Archer_Start_Int)); // int
                    jsonClassObject.put("intBase", context.getString(R.string.Archer_Start_Int)); // base int
                    jsonClassObject.put("con", context.getString(R.string.Archer_Start_Con)); // con
                    jsonClassObject.put("conBase", context.getString(R.string.Archer_Start_Con)); // base con
                    jsonClassObject.put("spd", context.getString(R.string.Archer_Start_Spd)); // spd
                    jsonClassObject.put("spdBase", context.getString(R.string.Archer_Start_Spd)); // base spd
                    JSONArray abilitiesArray = new JSONArray();
                    abilitiesArray.put(context.getString(R.string.Archer_Start_Ability)); // starts with one ability
                    for (int i = 0; i < Character.MAX_ABILITIES - 1; i++) {
                        abilitiesArray.put(0); // empty ability slots
                    }
                    jsonClassObject.put("abilities", abilitiesArray);
                    JSONArray weaponsArray = new JSONArray();
                    weaponsArray.put(context.getString(R.string.Archer_Start_Weapon)); // starts with one weapon
                    for (int i = 0; i < Character.MAX_WEAPONS - 1; i++) {
                        weaponsArray.put(0); // empty weapon slots
                    }
                    jsonClassObject.put("weapons", weaponsArray);
                    // equipped weapon
                    jsonClassObject.put("equipped", context.getString(R.string.Archer_Start_Weapon));
                    // bars
                    jsonClassObject.put("health", Integer.parseInt(context.getString(R.string.Archer_Start_Con)) + 10); // health = 10 + Con
                    jsonClassObject.put("maxHealth", Integer.parseInt(context.getString(R.string.Archer_Start_Con)) + 10);
                    jsonClassObject.put("mana", Integer.parseInt(context.getString(R.string.Archer_Start_Int)) + 10); // mana = 10 + Int
                    jsonClassObject.put("maxMana", Integer.parseInt(context.getString(R.string.Archer_Start_Int)) + 10);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if ((CharacterChoiceViewModel.warrior).equals(character[0])) {
                try {
                    jsonClassObject.put("class", "Warrior");
                    jsonClassObject.put("str", context.getString(R.string.Warrior_Start_Str)); // str
                    jsonClassObject.put("strBase", context.getString(R.string.Warrior_Start_Str)); // base str
                    jsonClassObject.put("int", context.getString(R.string.Warrior_Start_Int)); // int
                    jsonClassObject.put("intBase", context.getString(R.string.Warrior_Start_Int)); // base int
                    jsonClassObject.put("con", context.getString(R.string.Warrior_Start_Con)); // con
                    jsonClassObject.put("conBase", context.getString(R.string.Warrior_Start_Con)); // base con
                    jsonClassObject.put("spd", context.getString(R.string.Warrior_Start_Spd)); // spd
                    jsonClassObject.put("spdBase", context.getString(R.string.Warrior_Start_Spd)); // base spd
                    JSONArray abilitiesArray = new JSONArray();
                    abilitiesArray.put(context.getString(R.string.Warrior_Start_Ability)); // starts with one ability
                    for (int i = 0; i < Character.MAX_ABILITIES - 1; i++) {
                        abilitiesArray.put(0); // empty ability slots
                    }
                    jsonClassObject.put("abilities", abilitiesArray);
                    JSONArray weaponsArray = new JSONArray();
                    weaponsArray.put(context.getString(R.string.Warrior_Start_Weapon)); // starts with one weapon
                    for (int i = 0; i < Character.MAX_WEAPONS - 1; i++) {
                        weaponsArray.put(0); // empty weapon slots
                    }
                    jsonClassObject.put("weapons", weaponsArray);
                    // equipped weapon
                    jsonClassObject.put("equipped", context.getString(R.string.Warrior_Start_Weapon));
                    // bars
                    jsonClassObject.put("health", Integer.parseInt(context.getString(R.string.Warrior_Start_Con)) + 10); // health = 10 + Con
                    jsonClassObject.put("maxHealth", Integer.parseInt(context.getString(R.string.Warrior_Start_Con)) + 10);
                    jsonClassObject.put("mana", Integer.parseInt(context.getString(R.string.Warrior_Start_Int)) + 10); // mana = 10 + Int
                    jsonClassObject.put("maxMana", Integer.parseInt(context.getString(R.string.Warrior_Start_Int)) + 10);
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
                for (int i = 0; i < Character.MAX_ITEMS-1; i++) {
                    itemsArray.put(0);
                }
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
            JSONObject jsonClassObject = new JSONObject();
            Character character = characterArr[0];

            try {
                jsonClassObject.put("class", character.getClassType());
                jsonClassObject.put("str", character.getStrength()); // str
                jsonClassObject.put("strBase", character.getStrBase()); // base str
                jsonClassObject.put("strIncrease", character.getStrIncrease());
                jsonClassObject.put("strDecrease", character.getStrDecrease());
                jsonClassObject.put("int", character.getIntelligence()); // int
                jsonClassObject.put("intBase", character.getIntBase()); // base int
                jsonClassObject.put("intIncrease", character.getIntIncrease());
                jsonClassObject.put("intDecrease", character.getIntDecrease());
                jsonClassObject.put("con", character.getConstitution()); // con
                jsonClassObject.put("conBase", character.getConBase()); // base con
                jsonClassObject.put("conIncrease", character.getConIncrease());
                jsonClassObject.put("conDecrease", character.getConDecrease());
                jsonClassObject.put("spd", character.getSpeed()); // spd
                jsonClassObject.put("spdBase", character.getSpdBase()); // base spd
                jsonClassObject.put("spdIncrease", character.getSpdIncrease());
                jsonClassObject.put("spdDecrease", character.getSpdDecrease());
                // abilities
                JSONArray abilitiesArray = new JSONArray();
                for (int i = 0; i < Character.MAX_ABILITIES; i++) {
                    if (character.getAbilities().size() > i) {
                        abilitiesArray.put(character.getAbilities().get(i).getAbilityID());
                    }
                    abilitiesArray.put(0);
                }
                jsonClassObject.put("abilities", abilitiesArray);
                // weapons
                JSONArray weaponsArray = new JSONArray();
                for (int i = 0; i < Character.MAX_WEAPONS; i++) {
                    if (character.getWeapons().size() > i) {
                        weaponsArray.put(character.getWeapons().get(i).getWeaponID());
                    } else {
                        weaponsArray.put(0);
                    }
                }
                jsonClassObject.put("weapons", weaponsArray);
                // equipped weapon
                jsonClassObject.put("equipped", character.getEquippedWeapon());
                // bars
                jsonClassObject.put("health", character.getHealth());
                jsonClassObject.put("maxHealth", character.getMaxHealth());
                jsonClassObject.put("mana", character.getMana());
                jsonClassObject.put("maxMana", character.getMaxMana());
                // misc
                jsonClassObject.put("level", character.getLevel());
                jsonClassObject.put("gold", character.getGold());
                // Items
                JSONArray itemsArray = new JSONArray();
                for (int i = 0; i < Character.MAX_ITEMS; i++) {
                    if (character.getItems().size() > i) {
                        if (character.getItems().get(i).getIsGeneric()) {
                            // save the generic Item name instead of its ID
                            itemsArray.put(character.getItems().get(i).getName());
                        } else {
                            itemsArray.put(character.getItems().get(i).getItemID());
                        }
                    } else {
                        itemsArray.put(0);
                    }
                }
                jsonClassObject.put("items", itemsArray);
                // specials
                jsonClassObject.put("isStun", character.getIsStun());
                jsonClassObject.put("isConfuse", character.getIsConfuse());
                jsonClassObject.put("isInvincibility", character.getIsInvincible());
                jsonClassObject.put("isSilence", character.getIsSilence());
                jsonClassObject.put("invisibility", character.getIsInvisible());
                JSONArray specialArray = new JSONArray();
                for (SpecialEffect appliedSpecial: character.getSpecialList()) {
                    JSONArray special = new JSONArray();
                    special.put(appliedSpecial.getType());
                    special.put(appliedSpecial.getDuration());
                    specialArray.put(special);
                }
                jsonClassObject.put("specialMap", specialArray);
                // temp health
                jsonClassObject.put("tempExtraHealth", character.getTempExtraHealth());
                JSONArray tempHealthArray = new JSONArray(); // <key, value>
                for (int i = 0; i < character.getTempHealthList().size(); i++) {
                    JSONArray tempHealth = new JSONArray(); // <duration, amount>
                    tempHealth.put(character.getTempHealthList().get(i).getDuration());
                    tempHealth.put(character.getTempHealthList().get(i).getAmount());
                    tempHealthArray.put(tempHealth);
                }
                jsonClassObject.put("tempHealthList", tempHealthArray);
                // temp mana
                jsonClassObject.put("tempExtraMana", character.getTempExtraMana());
                JSONArray tempManaArray = new JSONArray(); // <key, value>
                for (int i = 0; i < character.getTempManaList().size(); i++) {
                    JSONArray tempMana = new JSONArray(); // <duration, amount>
                    tempMana.put(character.getTempManaList().get(i).getDuration());
                    tempMana.put(character.getTempManaList().get(i).getAmount());
                    tempManaArray.put(tempMana);
                }
                jsonClassObject.put("tempManaList", tempManaArray);
                // stat increase
                JSONArray statIncreaseArray = new JSONArray(); // <stat, duration, amount>
                for (int i = 0; i < character.getStatIncreaseList().size(); i++) {
                    JSONArray statIncrease = new JSONArray();
                    statIncrease.put(character.getStatIncreaseList().get(i).getType());
                    statIncrease.put(character.getStatIncreaseList().get(i).getDuration());
                    statIncrease.put(character.getStatIncreaseList().get(i).getAmount());
                    statIncreaseArray.put(statIncrease);
                }
                jsonClassObject.put("statIncreaseList", statIncreaseArray);
                // stat decrease
                JSONArray statDecreaseArray = new JSONArray(); // <stat, duration, amount>
                for (int i = 0; i < character.getStatDecreaseList().size(); i++) {
                    JSONArray statDecrease = new JSONArray();
                    statDecrease.put(character.getStatDecreaseList().get(i).getType());
                    statDecrease.put(character.getStatDecreaseList().get(i).getDuration());
                    statDecrease.put(character.getStatDecreaseList().get(i).getAmount());
                    statDecreaseArray.put(statDecrease);
                }
                jsonClassObject.put("statDecreaseList", statDecreaseArray);
                // DOT
                JSONArray dotArray = new JSONArray(); // <key, value>
                for (Dot appliedDot: character.getDotList()) {
                    JSONArray dot = new JSONArray();
                    dot.put(appliedDot.getType());
                    dot.put(appliedDot.getDuration());
                    dotArray.put(dot);
                }
                jsonClassObject.put("dotList", dotArray);

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


    // return the string of the character data saved locally
    public String readCharacterData(Context ctx) {
        String fileName = ctx.getResources().getString(R.string.fileCharacter);
        return readJSON(ctx, fileName);
    }

    public String readPrevEncounter(Context ctx) throws JSONException {
        String fileName = ctx.getResources().getString(R.string.filePrevEncounters);
        JSONObject jsonObject = new JSONObject(readJSON(ctx, fileName));
        JSONArray jsonArray = jsonObject.getJSONArray("encounters");
        int length = jsonArray.length();
        if (length == 0) // no previous encounters saved before
            return "";
        return jsonArray.getString(length-1);
    }

    private String readAllEncounters(Context ctx) {
        String fileName = ctx.getResources().getString(R.string.filePrevEncounters);
        return readJSON(ctx, fileName);
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
        File file = new File(context.getResources().getString(R.string.filePrevEncounters));
        if (file.delete())
            System.out.println("Previous encounters deleted");
    }




    // todo: saves incorrectly - fix this
    public void saveEncounter(Context ctx, String encounter) throws JSONException {
        String filename = ctx.getResources().getString(R.string.filePrevEncounters);
        FileOutputStream fOut;
        JSONObject jsonObject;
        JSONArray jsonArray;
        if (!readPrevEncounter(ctx).equals("")) {
            jsonObject = new JSONObject(readAllEncounters(ctx));
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
            fOut = ctx.openFileOutput(filename, Context.MODE_PRIVATE);
            fOut.write(jsonObject.toString().getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


