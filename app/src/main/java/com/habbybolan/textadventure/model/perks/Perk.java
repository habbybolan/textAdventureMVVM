package com.habbybolan.textadventure.model.perks;

public abstract class Perk {

    String TYPE = "type";
    String TYPE_BETWEEN_ENCOUNTER = "between_encounter_perk";
    String TYPE_IN_COMBAT = "in_combat_perk";
    String TYPE_PASSIVE = "passive_perk";
    String TYPE_MISC = "misc_perk";


    abstract String getName();
    abstract boolean isBetweenEncounter();
    abstract boolean isInCombat();
    abstract boolean isPassive();
    abstract boolean isMisc();
}
