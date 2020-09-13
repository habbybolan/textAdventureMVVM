package com.habbybolan.textadventure.model.inventory;

import org.json.JSONObject;

public interface Inventory {
    static String TYPE_WEAPON = "weapons";
    static String TYPE_ABILITY = "abilities";
    static String TYPE_ITEM = "items";

    String getType();
    String getName();

    int getID();

    // converts the Inventory object to a JSON
    JSONObject toJSON();

    void setPictureResource();
    int getPictureResource();
}
