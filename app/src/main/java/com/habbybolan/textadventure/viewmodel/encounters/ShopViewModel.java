package com.habbybolan.textadventure.viewmodel.encounters;

import android.app.Application;

import com.habbybolan.textadventure.model.GridModel;
import com.habbybolan.textadventure.model.dialogue.DialogueType;
import com.habbybolan.textadventure.model.inventory.Ability;
import com.habbybolan.textadventure.model.inventory.Inventory;
import com.habbybolan.textadventure.model.inventory.InventoryFactory;
import com.habbybolan.textadventure.model.inventory.Item;
import com.habbybolan.textadventure.model.inventory.weapon.Weapon;
import com.habbybolan.textadventure.repository.SaveDataLocally;
import com.habbybolan.textadventure.repository.database.DatabaseAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;

/**
 * View Model that deals with the shop encounter data.
 */
public class ShopViewModel extends EncounterViewModel  {

    public static final int firstState = 1;
    public static final int secondState = 2;
    public static final int thirdState = 3;

    private final double chanceForItems = 0.4;
    private final double chanceForAll = 0.3;
    private final double chanceForWeapons = 0.2;
    private final double chanceForAbilities = 0.1;

    private ArrayList<GridModel> listGridModelBuy = new ArrayList<>();

    private ArrayList<Item> itemsToBuy = new ArrayList<>();
    private ArrayList<Ability> abilitiesToBuy = new ArrayList<>();
    private ArrayList<Weapon> weaponsToBuy = new ArrayList<>();

    private ArrayList<GridModel> listGridModelSell = new ArrayList<>();

    public ShopViewModel(Application application) {
        super(application);
    }

    @Override
    public void saveEncounter(ArrayList<DialogueType> dialogueList) {
        SaveDataLocally save = new SaveDataLocally(application);
        JSONObject encounterData = new JSONObject();
        try {
            encounterData.put(ENCOUNTER_TYPE, TYPE_SHOP);
            encounterData.put(ENCOUNTER, encounter);
            encounterData.put(STATE, getStateIndexValue());
            if (getFirstStateJSON() != null) encounterData.put(DIALOGUE_REMAINING, getFirstStateJSON());
            // store all DialogueTypes converted to JSON
            JSONArray JSONDialogue = new JSONArray();
            for (DialogueType dialogueType : dialogueList) {
                JSONObject dialogueObject = dialogueType.toJSON();
                JSONDialogue.put(dialogueObject);
            }
            encounterData.put(DIALOGUE_ADDED, JSONDialogue);
            encounterData.put(BUY_LIST, getInventoryBuyList());

            save.saveEncounter(encounterData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * create a JSONArray of the listGridModelSell by serializing the inventory objects into a JSON
     * @return  The JSONArray of serialized Inventory objects that can be bought
     */
    private JSONArray getInventoryBuyList() throws JSONException {
        JSONArray buyListArray = new JSONArray();
        for (GridModel gridModel : listGridModelBuy) {
            JSONObject sellObject = new JSONObject();
            sellObject.put(BUY_INVENTORY, gridModel.getInventory().serializeToJSON());
            sellObject.put(BUY_COST, gridModel.getCost());
            buyListArray.put(sellObject);
        }
        return buyListArray;
    }

    @Override
    public void setSavedData() {
        try {
            if (getIsSaved()) {
                setDialogueList(mainGameVM);
                setBuyListFromSave();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set up the buy list for the listGridModelBuy from saved data. Called if previous saved encounter exists.
     */
    private void setBuyListFromSave() throws JSONException {
        JSONArray buyArray = mainGameVM.getSavedEncounter().getJSONArray(BUY_LIST);
        for (int i = 0; i < buyArray.length(); i++) {
            JSONObject gridObject = buyArray.getJSONObject(i);
            GridModel gridModel = new GridModel(InventoryFactory.createInventory(gridObject.getString(BUY_INVENTORY)), gridObject.getInt(BUY_COST));
            listGridModelBuy.add(gridModel);
        }
    }

    /**
     * Calls the helper on the 3 Inventory ArrayLists that hold the Inventory object that
     * can be bought, converting them to a gridModel and storing into listGridModel
     */
    private void setBuyGridModels() {
        addListToGridModelBuyList(itemsToBuy);
        addListToGridModelBuyList(weaponsToBuy);
        addListToGridModelBuyList(abilitiesToBuy);
    }

    /**
     * Helper to add a single ArrayList of Inventory objects and converts them to GridModel objects and stores into
     * ListGridModel.
     * @param inventoryToBuy    The array to convert its elements into GridModel objects
     */
    private void addListToGridModelBuyList(ArrayList<? extends Inventory> inventoryToBuy) {
        for (Inventory inventory : inventoryToBuy) {
            GridModel gridModel = new GridModel(inventory, 5);
            listGridModelBuy.add(gridModel);
        }
    }


    /**
     * sets up the list of items to buy
     * chanceForItems chance for random set of items (3-5 items)
     * chanceForAll chance for weapons/items/abilities (3-4 items, 1-2 weapons, 0-1 abilities)
     * chanceForWeapons chance for random weapons (3-5)
     * chanceForAbilities chance for random abilities (2-4)
     */
    public void setUpItemToBuy() throws ExecutionException, InterruptedException {
        if (listGridModelBuy.isEmpty()) {
            int fullPercent = 100;
            Random rand = new Random();
            double shopType = rand.nextInt(fullPercent);
            // set up and open database
            DatabaseAdapter dbh = new DatabaseAdapter(application);
            // Get the type of shop
            if (shopType < fullPercent * chanceForItems) {
                // create shop of random items (5 items)
                itemsToBuy = dbh.getRandomItems(5);
            } else if (shopType < (fullPercent * chanceForItems) + (fullPercent * chanceForAll)) {
                // create a shop full of all random (3 items, 2 weapons, 1 abilities)
                itemsToBuy = dbh.getRandomItems(3);
                weaponsToBuy = dbh.getRandomWeapons(2);
                abilitiesToBuy = dbh.getRandomAbilities(1);
            } else if (shopType < (fullPercent * chanceForItems) + (fullPercent * chanceForAll) + (fullPercent * chanceForWeapons)) {
                // create a shop full of random weapons (5)
                weaponsToBuy = dbh.getRandomWeapons(5);
            } else {
                // create a shop full of random abilities (4)
                abilitiesToBuy = dbh.getRandomAbilities(4);
            }
            dbh.close();
            setBuyGridModels();
        }
    }

    public void setSellList() {
        ArrayList<Inventory> listOfAllToSell = new ArrayList<>();
        if (characterVM.getCharacter().getNumWeapons() > 0)
            listOfAllToSell.addAll(characterVM.getCharacter().getWeapons());
        if (characterVM.getCharacter().getNumAbilities() > 0)
            listOfAllToSell.addAll(characterVM.getCharacter().getAbilities());
        if (characterVM.getCharacter().getNumItems() > 0)
            listOfAllToSell.addAll(characterVM.getCharacter().getItems());
        addListToGridModelSellList(listOfAllToSell);
    }

    /**
     * Helper to add all Inventory objects to sell to listGridModelSell
     * @param inventoryToSell    The array to convert its elements into GridModel objects
     */
    private void addListToGridModelSellList(ArrayList<Inventory> inventoryToSell) {
        for (Inventory inventory : inventoryToSell) {
            GridModel gridModel = new GridModel(inventory, 5);
            listGridModelSell.add(gridModel);
        }
    }

    /**
     * Uses index position to remove corresponding GridModel from listGridModelSell and remove from player character's inventory.
     * @param position  index of the GridModel object in listGridModelSell
     */
    public void sellInventory(int position) {
        GridModel gridModel = listGridModelSell.get(position);
        // remove from sell list
        listGridModelSell.remove(position);
        // add cost to character for selling
        characterVM.goldChange(gridModel.getCost());
        // add to buy list
        listGridModelBuy.add(gridModel.buySoldInventory());
        // remove from inventory
        characterVM.removeInventory(gridModel.getInventory());
    }

    /**
     * Uses the index position to remove the GridModel from ListGridModelBuy and add to listGridModel sell, and add to player character inventory. Also spend
     * the gold cost associated with the Inventory object, successfully buying if the player character has enough gold. Return true if able to buy item.
     * @param position  Index of the GridModel object in listGridModelBuy.
     * @return          True if the item was successfully bought.
     */
    public boolean buyInventory(int position) {
        GridModel gridModel = listGridModelBuy.get(position);
        if (characterVM.getCharacter().getGold() >= gridModel.getCost()) {
            // remove from buy list
            listGridModelBuy.remove(position);
            // add to sell list
            listGridModelSell.add(gridModel);
            characterVM.addNewInventory(gridModel.getInventory());
            // remove cost from character for buying
            characterVM.goldChange(-gridModel.getCost());
            return true;
        }
        return false;
    }


    public ArrayList<GridModel> getListGridModelBuy() {
        return listGridModelBuy;
    }
    public ArrayList<GridModel> getListGridModelSell() {
        return listGridModelSell;
    }
}
