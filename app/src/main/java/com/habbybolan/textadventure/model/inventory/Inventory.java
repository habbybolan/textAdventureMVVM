package com.habbybolan.textadventure.model.inventory;

import org.json.JSONException;
import org.json.JSONObject;

public interface Inventory {
    String TYPE_WEAPON = "weapons";
    String TYPE_ATTACK = "attacks";
    String TYPE_S_ATTACK = "sAttacks";

    String TYPE_ABILITY = "abilities";
    String TYPE_ITEM = "items";

    String INVENTORY_TYPE = "inventory_type";
    String ID = "id";

    String getType();
    String getName();

    int getID();

    // converts the Inventory object to a JSON
    JSONObject toJSON();

    // serializes an entire Inventory object into a JSON string
    JSONObject serializeToJSON() throws JSONException;



    void setPictureResource();
    int getPictureResource();
}
