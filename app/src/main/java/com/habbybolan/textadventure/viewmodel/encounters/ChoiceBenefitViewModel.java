package com.habbybolan.textadventure.viewmodel.encounters;

import android.app.Application;

import com.habbybolan.textadventure.model.dialogue.DialogueType;
import com.habbybolan.textadventure.model.effects.Effect;
import com.habbybolan.textadventure.model.effects.TempBar;
import com.habbybolan.textadventure.model.effects.TempStat;
import com.habbybolan.textadventure.model.encounter.ChoiceBenefitModel;
import com.habbybolan.textadventure.model.inventory.Ability;
import com.habbybolan.textadventure.model.inventory.InventoryEntity;
import com.habbybolan.textadventure.model.inventory.Item;
import com.habbybolan.textadventure.model.inventory.weapon.Weapon;
import com.habbybolan.textadventure.repository.LocallySavedFiles;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChoiceBenefitViewModel extends EncounterViewModel {

    private ChoiceBenefitModel choiceBenefitModel;

    public static final int firstState = 1;
    public static final int secondState = 2;
    public static final int thirdState = 3;

    private InventoryEntity inventoryEntityToRetrieve = null;
    
    public ChoiceBenefitViewModel(Application application) {
        super(application);
        choiceBenefitModel = new ChoiceBenefitModel(application);
    }

    // sets the saved dialogue list and inventory item to retrieve if there is one
    @Override
    public void setSavedData() {
        try {
            if (getIsSaved()) {
                setDialogueList(mainGameVM);
                inventoryEntityToRetrieve = setSingleSavedInventory();
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveEncounter(ArrayList<DialogueType> dialogueList) {
        LocallySavedFiles save = new LocallySavedFiles(application);
        JSONObject encounterData = new JSONObject();
        try {
            encounterData.put(ENCOUNTER_TYPE, TYPE_CHOICE_BENEFIT);
            encounterData.put(ENCOUNTER, encounter);
            encounterData.put(STATE, getStateIndexValue());
            if (getFirstStateJSON() != null) encounterData.put(DIALOGUE_REMAINING, getFirstStateJSON());
            // convert the inventory to JSON and store if one exists
            if (getInventoryEntityToRetrieve()) encounterData.put(INVENTORY, inventoryEntityToRetrieve.serializeToJSON());
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

    // gain a random tangible item
    public void setTangible() {
        inventoryEntityToRetrieve = choiceBenefitModel.getNewInventory();
    }

    // returns the inventory Object that the character can pick up
    public InventoryEntity getInventoryToRetrieve() {
        return inventoryEntityToRetrieve;
    }

    /**
     * Find if there is an Inventory object saved to be retrieved by character.
     * @return  True if there is a saved Inventory object to be retrieved.
     */
    public boolean getInventoryEntityToRetrieve() {
        return inventoryEntityToRetrieve != null;
    }

    public void setPermIncrease() {
        Effect effect = choiceBenefitModel.getNewPermanentStat();
        if (effect.isTempStat()) {
            // add permanent stat
            characterVM.permIncreaseStat((TempStat) effect);
        } else {
            // otherwise add the permanent heath/mana increase
            characterVM.setIndefiniteBarIncr((TempBar) effect);
        }
    }

    public void setTempIncrease() {
        Effect effect = choiceBenefitModel.getNewTempStat();
        if (effect.isTempStat()) {
            // add permanent stat
            characterVM.addInputStat((TempStat) effect);
        } else {
            // otherwise add the permanent heath/mana increase
            characterVM.addNewTempBar((TempBar) effect);
        }
    }

    // if space in inventory, return true and add inventory object to inventory, otherwise return false
    public boolean addNewInventory(InventoryEntity inventoryEntityToRetrieve) {
        if (inventoryEntityToRetrieve.getType().equals(InventoryEntity.TYPE_ABILITY)) {
            // if inventory is full, don't pick-up Ability
            return (characterVM.addAbility((Ability) inventoryEntityToRetrieve));

        } else if (inventoryEntityToRetrieve.getType().equals(InventoryEntity.TYPE_ITEM)) {
            // if inventory is full, don't pick-up item
            return (characterVM.addItem((Item) inventoryEntityToRetrieve)) ;

        } else {
            // if inventory is full, cant pick up weapon
            return (characterVM.addWeapon((Weapon) inventoryEntityToRetrieve));
        }
    }

    // return message for being full on inventory space
    public String getFullMessageString(InventoryEntity inventoryEntityToRetrieve) {
        if (inventoryEntityToRetrieve.getType().equals(InventoryEntity.TYPE_ITEM)) {
            return "You are full on Items";
        } else if (inventoryEntityToRetrieve.getType().equals(InventoryEntity.TYPE_ABILITY)) {
            return  "You are full on Ability Scrolls";
        } else {
            return "You are full on Weapons";
        }
    }

    // removes inventoryToRetrieve once it is taken
    public void removeInventoryToRetrieve() {
        inventoryEntityToRetrieve = null;
    }
}
