package com.habbybolan.textadventure.model.perks;

public class MiscPerk extends Perk {

    public final static String SKIP_TRAP = "Skip Trap";

    private String name;

    MiscPerk(String name) {
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
        return false;
    }

    @Override
    public boolean isMisc() {
        return true;
    }
}
