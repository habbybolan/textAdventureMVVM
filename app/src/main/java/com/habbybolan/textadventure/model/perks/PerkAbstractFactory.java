package com.habbybolan.textadventure.model.perks;

/**
 * Holds all factory methods for creating a specific perk.
 */
public class PerkAbstractFactory {

    // BetweenEncounter factories

    public static Perk createRestoreMana() {
        return new BetweenEncounterPerk(BetweenEncounterPerk.RESTORE_MANA, 20);
    }
    public static Perk createRestoreHealth() {
        return new BetweenEncounterPerk(BetweenEncounterPerk.RESTORE_HEALTH, 20);
    }
    public static Perk createFindGold() {
        return new BetweenEncounterPerk(BetweenEncounterPerk.FIND_GOLD, 10);
    }
    public static Perk createFindInventory() {
        return new BetweenEncounterPerk(BetweenEncounterPerk.FIND_INVENTORY, 5);
    }

    // InCombat factories

    public static Perk createStealHealth() {
        return new InCombatPerk(InCombatPerk.STEAL_HEALTH);
    }

    public static Perk createStealMana() {
        return new InCombatPerk(InCombatPerk.STEAL_MANA);
    }

    // Passive factories

    public static Perk createExtraAbilities() {
        return new PassivePerk(PassivePerk.EXTRA_ABILITIES);
    }
    public static Perk createExtraItems() {
        return new PassivePerk(PassivePerk.EXTRA_ITEMS);
    }
    public static Perk createExtraWeapons() {
        return new PassivePerk(PassivePerk.EXTRA_WEAPONS);
    }
    public static Perk createIncreaseEvasion() {
        return new PassivePerk(PassivePerk.INCREASE_EVASION);
    }
    public static Perk createIncreaseBlock() {
        return new PassivePerk(PassivePerk.INCREASE_BLOCK);
    }

    // misc factories

    public static Perk createSkipTrap() {
        return new MiscPerk(MiscPerk.SKIP_TRAP);
    }


}
