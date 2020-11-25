package com.habbybolan.textadventure.model.inventory;

import com.habbybolan.textadventure.model.inventory.weapon.Weapon;

import org.json.JSONException;
import org.json.JSONObject;

public class InventoryFactory {

    /**
     * Factory method to create an Inventory object from the inventoryString JSON String.
     * Shouldn't call Attack and Special Attack from here. Need to attack the weapon to both WeaponActions
     * @param inventoryString   The serialized Inventory JSON string
     * @return                  The Inventory object created from the inventoryString
     */
    public static Inventory createInventory(String inventoryString) {
        String type = "";
        try {
            JSONObject inventoryJSON = new JSONObject(inventoryString);
            type = inventoryJSON.getString(Inventory.TYPE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        switch (type) {
            case Inventory.TYPE_ABILITY:
                return new Ability(inventoryString);
            case Inventory.TYPE_ITEM:
                return new Item(inventoryString);
            case Inventory.TYPE_WEAPON:
                return new Weapon(inventoryString);
                /*
            case Inventory.TYPE_ATTACK:
                return new Attack(inventoryString);
            case Inventory.TYPE_S_ATTACK:
                return new SpecialAttack(inventoryString);*/
            default:
                throw new IllegalArgumentException(type + " is not a valid type");
        }
    }
}
