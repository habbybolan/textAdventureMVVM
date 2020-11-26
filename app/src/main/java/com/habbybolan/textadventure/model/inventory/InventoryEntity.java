package com.habbybolan.textadventure.model.inventory;

import org.json.JSONException;
import org.json.JSONObject;

public interface InventoryEntity {
    String TYPE_WEAPON = "weapons";
    String TYPE_ATTACK = "attacks";
    String TYPE_S_ATTACK = "sAttacks";

    String TYPE_ABILITY = "abilities";
    String TYPE_ITEM = "items";

    String INVENTORY_TYPE = "inventory_type";
    String ID = "id";

    public static final String TYPE = "type";

    String getType();
    String getName();

    int getID();

    // serializes an entire Inventory object into a JSON string
    JSONObject serializeToJSON() throws JSONException;


    void setPictureResource();
    int getPictureResource();

    boolean isAbility();
    boolean isItem();
    boolean isWeapon();
    boolean isAttack();
    boolean isSpecialAttack();
}
