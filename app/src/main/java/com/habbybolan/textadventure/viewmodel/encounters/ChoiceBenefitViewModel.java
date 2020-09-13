package com.habbybolan.textadventure.viewmodel.encounters;

import android.content.Context;

import androidx.databinding.ObservableField;

import com.habbybolan.textadventure.model.dialogue.Dialogue;
import com.habbybolan.textadventure.model.dialogue.DialogueType;
import com.habbybolan.textadventure.model.dialogue.EffectDialogue;
import com.habbybolan.textadventure.model.dialogue.HealthDialogue;
import com.habbybolan.textadventure.model.dialogue.InventoryDialogue;
import com.habbybolan.textadventure.model.dialogue.ManaDialogue;
import com.habbybolan.textadventure.model.dialogue.StatDialogue;
import com.habbybolan.textadventure.model.dialogue.TempStatDialogue;
import com.habbybolan.textadventure.model.effects.Effect;
import com.habbybolan.textadventure.model.effects.TempBar;
import com.habbybolan.textadventure.model.effects.TempStat;
import com.habbybolan.textadventure.model.encounter.ChoiceBenefitModel;
import com.habbybolan.textadventure.model.inventory.Ability;
import com.habbybolan.textadventure.model.inventory.Inventory;
import com.habbybolan.textadventure.model.inventory.Item;
import com.habbybolan.textadventure.model.inventory.weapon.Weapon;
import com.habbybolan.textadventure.repository.SaveDataLocally;
import com.habbybolan.textadventure.repository.database.DatabaseAdapter;
import com.habbybolan.textadventure.viewmodel.CharacterViewModel;
import com.habbybolan.textadventure.viewmodel.MainGameViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ChoiceBenefitViewModel implements EncounterViewModel {


    private MainGameViewModel mainGameVM;
    private CharacterViewModel characterVM;
    private JSONObject encounter;
    private Context context;
    private JSONObject firstStateJSON;
    ChoiceBenefitModel choiceBenefitModel;

    private boolean isSaved = false;

    public static final int firstState = 1;
    public static final int secondState = 2;
    public static final int thirdState = 3;

    private Inventory inventoryToRetrieve = null;
    private ObservableField<Integer> stateIndex;
    private ObservableField<String> newDialogue;
    
    public ChoiceBenefitViewModel(MainGameViewModel mainGameVM, CharacterViewModel characterVM, JSONObject encounter, Context context) throws JSONException {
        if (mainGameVM.getSavedEncounter() != null) {
            isSaved = true;
            // first state dialogue to be iterated over
            if (mainGameVM.getSavedEncounter().has(DIALOGUE_REMAINING))
                firstStateJSON = mainGameVM.getSavedEncounter().getJSONObject(DIALOGUE_REMAINING);
        } else {
            firstStateJSON = encounter.getJSONObject("dialogue");
        }

        this.mainGameVM = mainGameVM;
        this.characterVM = characterVM;
        this.encounter = encounter;
        this.context = context;

        stateIndex = new ObservableField<>();
        newDialogue = new ObservableField<>();
        choiceBenefitModel = new ChoiceBenefitModel(context);
        // if there is a saved encounter, set isSaved to true
        if (mainGameVM.getSavedEncounter() != null) isSaved = true;
    }


    @Override
    public ObservableField<Integer> getStateIndex() {
        return stateIndex;
    }

    @Override
    public int getStateIndexValue() {
        Integer stateIndex = getStateIndex().get();
        if (stateIndex != null) return stateIndex;
        throw new NullPointerException();
    }

    @Override
    public void incrementStateIndex() {
        Integer state = stateIndex.get();
        if (state != null) {
            int newState = ++state;
            stateIndex.set(newState);
        }
    }

    @Override
    public void saveEncounter(ArrayList<DialogueType> dialogueList) {
        SaveDataLocally save = new SaveDataLocally(context);
        JSONObject encounterData = new JSONObject();
        try {
            encounterData.put(ENCOUNTER_TYPE, TYPE_CHOICE_BENEFIT);
            encounterData.put(ENCOUNTER, encounter);
            encounterData.put(STATE, stateIndex.get());
            if (firstStateJSON != null) encounterData.put(DIALOGUE_REMAINING, firstStateJSON);
            // convert the inventory to JSON and store if one exists
            if (inventoryToRetrieve != null) encounterData.put(INVENTORY, inventoryToRetrieve.toJSON());
            // store all DialogueTypes converted to JSON
            JSONArray JSONDialogue = new JSONArray();
            for (DialogueType dialogueType : dialogueList) {
                JSONObject dialogueObject = dialogueType.toJSON();
                JSONDialogue.put(dialogueObject);
            }
            encounterData.put(DIALOGUE_ADDED, JSONDialogue);
            save.saveEncounter(encounterData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // goto the the saved state if one is saved, otherwise start at beginning
    public void gotoState() throws JSONException {
        if (!isSaved) // if no save, then go to dialogue state normally
            stateIndex.set(1);
        else { // there is a save, goto saved state
            stateIndex.set(mainGameVM.getSavedEncounter().getInt("state"));
        }
    }

    // get the saved dialogue list of previously added dialogue to RV
    public ArrayList<DialogueType> getDialogueList() throws JSONException {
        JSONArray dialogue = mainGameVM.getSavedEncounter().getJSONArray(EncounterViewModel.DIALOGUE_ADDED);
        return JSONArrayIntoDialogueTypesList(dialogue);
    }

    // helper to convert a JSONArray of DialogueTypes strings to a list of DialogueTypes
    private ArrayList<DialogueType> JSONArrayIntoDialogueTypesList(JSONArray jsonArray) throws JSONException {
        ArrayList<DialogueType> dialogueList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            DialogueType dialogueType = JSONObjectToDialogueType(jsonArray.getJSONObject(i));
            dialogueList.add(dialogueType);
        }
        return dialogueList;
    }

    // helper for turning a JSONObject of DialogueType into DialogueType
    private DialogueType JSONObjectToDialogueType(JSONObject jsonObject) throws JSONException {
        switch (jsonObject.getString(DialogueType.DIALOGUE_TYPE)) {
            case DialogueType.TYPE_DIALOGUE:
                return new Dialogue(jsonObject.getString(DialogueType.DIALOGUE));
            case DialogueType.TYPE_EFFECT:
                return new EffectDialogue(jsonObject.getString(DialogueType.TYPE),
                        jsonObject.getInt(DialogueType.DURATION),
                        jsonObject.getInt(DialogueType.IMAGE_RESOURCE),
                        jsonObject.getBoolean(DialogueType.IS_INDEFINITE));
            case DialogueType.TYPE_HEALTH:
                return new HealthDialogue(jsonObject.getInt(DialogueType.AMOUNT));
            case DialogueType.TYPE_MANA:
                return new ManaDialogue(jsonObject.getInt(DialogueType.AMOUNT));
            case DialogueType.TYPE_INVENTORY:
                return new InventoryDialogue(jsonObject.getString(DialogueType.NAME),
                        jsonObject.getInt(DialogueType.IMAGE_RESOURCE),
                        jsonObject.getString(DialogueType.TYPE),
                        jsonObject.getBoolean(DialogueType.IS_ADDED));
            case DialogueType.TYPE_STAT:
                return new StatDialogue(jsonObject.getString(DialogueType.TYPE), jsonObject.getInt(DialogueType.AMOUNT));
            default:
                // otherWise, TYPE_TEMP_STAT
                return new TempStatDialogue(jsonObject.getString(DialogueType.TYPE), jsonObject.getInt(DialogueType.AMOUNT), jsonObject.getInt(DialogueType.DURATION));
        }
    }

    // get the saved Inventory Object from the json
    public void setSavedInventory() throws JSONException, ExecutionException, InterruptedException {
        // check if an inventory value has been stored
        if (mainGameVM.getSavedEncounter().has(INVENTORY)) {
            JSONObject inventory = mainGameVM.getSavedEncounter().getJSONObject(INVENTORY);
            // retrieve id of the Inventory Object
            int id = inventory.getInt("id");
            DatabaseAdapter mDbHelper = new DatabaseAdapter(context);

            if (inventory.getString("type").equals("ability")) {
                // saved Inventory object is an Ability
                inventoryToRetrieve = new Ability(id, mDbHelper);

            } else if (inventory.getString("type").equals("item")) {
                // saved Inventory object is an Item
                inventoryToRetrieve = new Item(String.valueOf(id), mDbHelper);

            } else {
                // otherwise, saved Inventory object is a Weapon
                inventoryToRetrieve = new Weapon(id, mDbHelper);
            }
        }
    }


    @Override
    public ObservableField<String> getNewDialogue() {
        return newDialogue;
    }

    @Override
    public void setNewDialogue(String newDialogue) {
        this.newDialogue.set(newDialogue);
    }

    @Override
    public String getNewDialogueValue() {
        String newDialogue = getNewDialogue().get();
        if (newDialogue != null) return newDialogue;
        throw new NullPointerException();
    }

    @Override
    public void firstDialogueState() throws JSONException {
        if (firstStateJSON.has("dialogue")) setNewDialogue(firstStateJSON.getString("dialogue"));
        if (firstStateJSON.has("next")) {
            firstStateJSON = firstStateJSON.getJSONObject("next");
        } else {
            firstStateJSON = null;
            incrementStateIndex();
        }
    }

    // gain a random tangible item
    public void setTangible() {
        inventoryToRetrieve = choiceBenefitModel.getNewInventory();
    }

    // returns the inventory Object that the character can pick up
    public Inventory getInventoryToRetrieve() {
        return inventoryToRetrieve;
    }

    public void setPermIncrease() {
        Effect effect = choiceBenefitModel.getNewPermanentStat();
        if (effect.isTempStat()) {
            // add permanent stat
            characterVM.permIncreaseStat((TempStat) effect);
        } else {
            // otherwise add the permanent heath/mana increase
            characterVM.setPermBarIncr((TempBar) effect);
        }
    }

    public void setTempIncrease() {
        Effect effect = choiceBenefitModel.getNewTempStat();
        if (effect.isTempStat()) {
            // add permanent stat
            characterVM.addInputStat((TempStat) effect);
        } else {
            // otherwise add the permanent heath/mana increase
            characterVM.addTempHealthMana((TempBar) effect);
        }
    }

    // if space in inventory, return true and add inventory object to inventory, otherwise return false
    public boolean addNewInventory(Inventory inventoryToRetrieve) {
        if (inventoryToRetrieve.getType().equals(Inventory.TYPE_ABILITY)) {
            // if inventory is full, don't pick-up Ability
            return (characterVM.addAbility((Ability) inventoryToRetrieve));

        } else if (inventoryToRetrieve.getType().equals(Inventory.TYPE_ITEM)) {
            // if inventory is full, don't pick-up item
            return (characterVM.addItem((Item) inventoryToRetrieve)) ;

        } else {
            // if inventory is full, cant pick up weapon
            return (characterVM.addWeapon((Weapon) inventoryToRetrieve));
        }
    }

    // return toast message for being full on inventory space
    public String getToastString(Inventory inventoryToRetrieve) {
        if (inventoryToRetrieve.getType().equals(Inventory.TYPE_ITEM)) {
            return "You are full on Items";
        } else if (inventoryToRetrieve.getType().equals(Inventory.TYPE_ABILITY)) {
            return  "You are full on Ability Scrolls";
        } else {
            return "You are full on Weapons";
        }
    }

    public boolean getIsSaved() {
        return isSaved;
    }

    public JSONObject
    getFirstStateJSON() {
        return firstStateJSON;
    }

    public void removeInventoryToRetrieve() {
        inventoryToRetrieve = null;
    }


    // once the save is recovered, set isSaved to false
    public void setSaveRecovered() {
        isSaved = false;
    }
}
