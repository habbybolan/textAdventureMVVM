package com.habbybolan.textadventure.model.perks;

public class BetweenEncounterPerk extends Perk {

    final public static String RESTORE_MANA = "Restore Mana";
    final public static String RESTORE_HEALTH = "Restore Health";
    final public static String FIND_GOLD = "Find Gold";
    final public static String FIND_INVENTORY = "Find Inventory";

    private String name;
    private int chance;

    BetweenEncounterPerk(String name, int chance) {
        this.name = name;
        this.chance = chance;
    }

    @Override
    public String getName() {
        return name;
    }

    public int getChance() {
        return chance;
    }

    @Override
    public boolean isBetweenEncounter() {
        return true;
    }

    @Override
    public boolean isInCombat() {
        return false;
    }

    @Override
    public boolean isPassive() {
        return false;
    }

    @Override
    public boolean isMisc() {
        return false;
    }
}
