package com.habbybolan.textadventure.model.perks;

public class InCombatPerk extends Perk {

    final public static String STEAL_HEALTH = "Steal Health";
    final public static String STEAL_MANA = "Steal Mana";

    private String name;

    InCombatPerk(String name) {
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
        return true;
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
