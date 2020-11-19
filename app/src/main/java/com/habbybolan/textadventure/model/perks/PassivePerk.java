package com.habbybolan.textadventure.model.perks;

public class PassivePerk extends Perk {

    final public static String EXTRA_WEAPONS = "Extra Weapons";
    final public static String EXTRA_ABILITIES = "Extra Abilities";
    final public static String EXTRA_ITEMS = "Extra Items";
    final public static String INCREASE_EVASION = "Increase Evasion";
    final public static String INCREASE_BLOCK = "Increase Block";

    private String name;

    PassivePerk(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isBetweenEncounter() {
        return false;
    }

    @Override
    public boolean isInCombat() {
        return false;
    }

    @Override
    public boolean isPassive() {
        return true;
    }

    @Override
    public boolean isMisc() {
        return false;
    }

}
