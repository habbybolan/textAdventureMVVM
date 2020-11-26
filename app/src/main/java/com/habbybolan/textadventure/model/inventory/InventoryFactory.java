package com.habbybolan.textadventure.model.inventory;

import com.habbybolan.textadventure.model.inventory.weapon.Weapon;

import org.json.JSONException;
import org.json.JSONObject;

public class InventoryFactory {

    /**
     * Factory method to create an Inventory object from the inventoryString JSON String.
     * @param inventoryString   The serialized Inventory JSON string
     * @return                  The Inventory object created from the inventoryString
     */
    public static Inventory createInventory(String inventoryString) {
        String type = "";
        try {
            JSONObject inventoryJSON = new JSONObject(inventoryString);
            type = inventoryJSON.getString(InventoryEntity.TYPE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        switch (type) {
            case InventoryEntity.TYPE_ABILITY:
                return new Ability(inventoryString);
            case InventoryEntity.TYPE_ITEM:
                return new Item(inventoryString);
            case InventoryEntity.TYPE_WEAPON:
                return new Weapon(inventoryString);
            default:
                throw new IllegalArgumentException(type + " is not a valid Inventory object type.");
        }
    }
}
