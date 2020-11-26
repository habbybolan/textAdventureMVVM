package com.habbybolan.textadventure.viewmodel.encounters;

import android.app.Application;

import com.habbybolan.textadventure.model.dialogue.DialogueType;
import com.habbybolan.textadventure.model.encounter.RandomBenefitModel;
import com.habbybolan.textadventure.model.inventory.InventoryEntity;
import com.habbybolan.textadventure.repository.SaveDataLocally;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Stack;


public class RandomBenefitViewModel extends EncounterViewModel {

    public static final int dialogueState = 1;
    public static final int ExpGoldRewardState = 2;
    public static final int inventoryRewardState = 3;
    public static final int endState = 4;

    private Stack<InventoryEntity> inventoryEntityToRetrieve = null;

    public RandomBenefitViewModel(Application application) {
        super(application);
    }

    @Override
    public void saveEncounter(ArrayList<DialogueType> dialogueList) {
        SaveDataLocally save = new SaveDataLocally(application);
        JSONObject encounterData = new JSONObject();
        try {
            encounterData.put(ENCOUNTER_TYPE, TYPE_RANDOM_BENEFIT);
            encounterData.put(ENCOUNTER, encounter);
            encounterData.put(STATE, getStateIndexValue());
            if (getFirstStateJSON() != null) encounterData.put(DIALOGUE_REMAINING, getFirstStateJSON());
            // convert the multiple inventory rewards to JSON and store if array is not null
            if (inventoryEntityToRetrieve != null) {
                JSONArray inventoryRewardArray = new JSONArray();
                for (InventoryEntity inventoryEntity : inventoryEntityToRetrieve) {
                    inventoryRewardArray.put(inventoryEntity.serializeToJSON());
                }
                encounterData.put(INVENTORY, inventoryRewardArray);
            }
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

    @Override
    public void setSavedData() {
        try {
            if (getIsSaved()) {
                setDialogueList(mainGameVM);
                inventoryEntityToRetrieve = setMultiSavedInventory();
            }
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    // if space in inventory, return true and add inventory object to inventory, otherwise return false
    public boolean addNewInventory() {
        if (characterVM.addNewInventory(inventoryEntityToRetrieve.peek())) {
            removeTopRewardAndContinue();
            return true;
        }
        return false;
    }

    // return message for being full on inventory space
    public String getFullMessageString() {
        if (inventoryEntityToRetrieve.peek().getType().equals(InventoryEntity.TYPE_ITEM)) {
            return "You are full on Items";
        } else if (inventoryEntityToRetrieve.peek().getType().equals(InventoryEntity.TYPE_ABILITY)) {
            return  "You are full on Ability Scrolls";
        } else {
            return "You are full on Weapons";
        }
    }

    /**
     * Removes inventoryToRetrieve's top of the stack. If it is empty, goto next state.
     */
    public void removeTopRewardAndContinue() {
        inventoryEntityToRetrieve.pop();
        if (inventoryEntityToRetrieve.isEmpty()) incrementStateIndex();
    }

    /**
     * @return  True if there are more rewards to gather.
     */
    public boolean isMoreRewards() {
        return !inventoryEntityToRetrieve.isEmpty();
    }

    /**
     * @return  The name of the next Inventory reward.
     */
    public String getNextInventoryName() {
        return inventoryEntityToRetrieve.peek().getName();
    }

    /**
     * @return  The picture resource of the next Inventory reward.
     */
    public int getNextInventoryPicResource() {
        return inventoryEntityToRetrieve.peek().getPictureResource();
    }

    /**
     * @return  The next Inventory reward.
     */
    public InventoryEntity getNextInventory() {
        return inventoryEntityToRetrieve.peek();
    }

    /**
     * Creates and sets exp reward if it's enabled.
     */
    public void createExpReward() {
        try {
            int exp = RandomBenefitModel.getExpReward(encounter, characterVM.getDistance());
            if (exp > 0) characterVM.addExp(exp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates and sets gold reward if it's enabled.
     */
    public void createGoldReward() {
        try {
            int gold = RandomBenefitModel.getGoldReward(encounter, characterVM.getDistance());
            if (gold > 0) characterVM.goldChange(gold);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Populates the inventoryToRetrieve stack with inventory rewards, amount specified inside encounter JSONObject.
     * If the inventoryToRetrieve is already populated, then this is a saved encounter, so use already populated stack.
     */
    public void createInventoryReward() {
        if (inventoryEntityToRetrieve == null) {
            try {
                inventoryEntityToRetrieve = RandomBenefitModel.getInventoryRewards(application, encounter, characterVM.getDistance());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
