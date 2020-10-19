package com.habbybolan.textadventure.viewmodel.encounters;

import android.content.Context;

import com.habbybolan.textadventure.model.dialogue.DialogueType;
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
import com.habbybolan.textadventure.viewmodel.characterEntityViewModels.CharacterViewModel;
import com.habbybolan.textadventure.viewmodel.MainGameViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ChoiceBenefitViewModel extends EncounterViewModel {


    private MainGameViewModel mainGameVM;
    private CharacterViewModel characterVM;
    private Context context;
    private JSONObject encounter;
    private ChoiceBenefitModel choiceBenefitModel;

    public static final int firstState = 1;
    public static final int secondState = 2;
    public static final int thirdState = 3;

    private Inventory inventoryToRetrieve = null;
    
    public ChoiceBenefitViewModel(MainGameViewModel mainGameVM, CharacterViewModel characterVM, JSONObject encounter, Context context) throws JSONException {
        setDialogueRemainingInDialogueState(encounter);
        this.mainGameVM = mainGameVM;
        this.characterVM = characterVM;
        this.encounter = encounter;
        this.context = context;
        choiceBenefitModel = new ChoiceBenefitModel(context);
    }

    // sets the saved dialogue list and inventory item to retrieve if there is one
    @Override
    public void setSavedData() throws JSONException, ExecutionException, InterruptedException {
        if (getIsSaved()) {
            setDialogueList(mainGameVM);
            setSavedInventory();
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
            if (getFirstStateJSON() != null) encounterData.put(DIALOGUE_REMAINING, getFirstStateJSON());
            // convert the inventory to JSON and store if one exists
            if (inventoryToRetrieve != null) encounterData.put(INVENTORY, inventoryToRetrieve.serializeToJSON());
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

    // get the saved Inventory Object from the json
    private void setSavedInventory() throws JSONException, ExecutionException, InterruptedException {
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
                inventoryToRetrieve = new Item(id, mDbHelper);

            } else {
                // otherwise, saved Inventory object is a Weapon
                inventoryToRetrieve = new Weapon(id, mDbHelper);
            }
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

    // removes inventoryToRetrieve once it is taken
    public void removeInventoryToRetrieve() {
        inventoryToRetrieve = null;
    }
}
