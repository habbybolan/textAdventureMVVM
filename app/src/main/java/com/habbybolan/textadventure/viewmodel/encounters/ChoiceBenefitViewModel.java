package com.habbybolan.textadventure.viewmodel.encounters;

import android.app.Application;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChoiceBenefitViewModel extends EncounterViewModel {

    private ChoiceBenefitModel choiceBenefitModel;

    public static final int firstState = 1;
    public static final int secondState = 2;
    public static final int thirdState = 3;

    private Inventory inventoryToRetrieve = null;
    
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
                inventoryToRetrieve = setSingleSavedInventory();
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveEncounter(ArrayList<DialogueType> dialogueList) {
        SaveDataLocally save = new SaveDataLocally(application);
        JSONObject encounterData = new JSONObject();
        try {
            encounterData.put(ENCOUNTER_TYPE, TYPE_CHOICE_BENEFIT);
            encounterData.put(ENCOUNTER, encounter);
            encounterData.put(STATE, getStateIndexValue());
            if (getFirstStateJSON() != null) encounterData.put(DIALOGUE_REMAINING, getFirstStateJSON());
            // convert the inventory to JSON and store if one exists
            if (isInventoryToRetrieve()) encounterData.put(INVENTORY, inventoryToRetrieve.serializeToJSON());
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
        inventoryToRetrieve = choiceBenefitModel.getNewInventory();
    }

    // returns the inventory Object that the character can pick up
    public Inventory getInventoryToRetrieve() {
        return inventoryToRetrieve;
    }

    /**
     * Find if there is an Inventory object saved to be retrieved by character.
     * @return  True if there is a saved Inventory object to be retrieved.
     */
    public boolean isInventoryToRetrieve() {
        return inventoryToRetrieve != null;
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

    // return message for being full on inventory space
    public String getFullMessageString(Inventory inventoryToRetrieve) {
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
