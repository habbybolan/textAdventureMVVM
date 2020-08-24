package com.habbybolan.textadventure.model.inventory;

public interface Inventory {
    public static String TYPE_WEAPON = "weapons";
    public static String TYPE_ABILITY = "abilities";
    public static String TYPE_ITEM = "items";

    public String getType();
    public String getName();
}
