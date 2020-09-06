package com.habbybolan.textadventure.model.inventory;

public interface Inventory {
    static String TYPE_WEAPON = "weapons";
    static String TYPE_ABILITY = "abilities";
    static String TYPE_ITEM = "items";

    String getType();
    String getName();

    void setPictureResource();
    int getPictureResource();
}
