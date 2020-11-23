package com.habbybolan.textadventure.model.locations;

import com.habbybolan.textadventure.viewmodel.MainGameViewModel;

public class Outdoor {

    final public static int COMBAT_DUNGEON = 0;
    final public static String COMBAT_DUNGEON_TYPE = MainGameViewModel.COMBAT_DUNGEON_TYPE;
    final public static int MULTI_DUNGEON = 1;
    final public static String MULTI_DUNGEON_TYPE =  MainGameViewModel.MULTI_DUNGEON_TYPE;
    final public static int CHOICE = 2;
    final public static String CHOICE_TYPE =  MainGameViewModel.CHOICE_TYPE;
    final public static int COMBAT = 3;
    final public static String COMBAT_TYPE =  MainGameViewModel.COMBAT_TYPE;
    final public static int TRAP = 4;
    final public static String TRAP_TYPE =  MainGameViewModel.TRAP_TYPE;
    final public static int SHOP = 5;
    final public static String SHOP_TYPE =  MainGameViewModel.SHOP_TYPE;
    final public static int CHOICE_BENEFIT = 6;
    final public static String CHOICE_BENEFIT_TYPE =  MainGameViewModel.CHOICE_BENEFIT_TYPE;
    final public static int RANDOM_BENEFIT = 7;
    final public static String RANDOM_BENEFIT_TYPE =  MainGameViewModel.RANDOM_BENEFIT_TYPE;

    final public static String BREAK_TYPE = MainGameViewModel.BREAK_TYPE;

    // The percentage that each encounter occurs and the lower value out of 100 of that encounter to be chosen.
    private static final int COMBAT_DUNGEON_PERCENT = 3;
    private static final int COMBAT_DUNGEON_LOWER = 0;
    private static final int MULTI_DUNGEON_PERCENT = 4;
    private static final int MULTI_DUNGEON_LOWER = COMBAT_DUNGEON_LOWER + COMBAT_DUNGEON_PERCENT;
    private static final int CHOICE_PERCENT = 20;
    private static final int CHOICE_LOWER = MULTI_DUNGEON_LOWER + MULTI_DUNGEON_PERCENT;
    private static final int COMBAT_PERCENT = 25;
    private static final int COMBAT_LOWER = CHOICE_LOWER + CHOICE_PERCENT;
    private static final int TRAP_PERCENT = 10;
    private static final int TRAP_LOWER = COMBAT_LOWER + COMBAT_PERCENT;
    private static final int SHOP_PERCENT = 10;
    private static final int SHOP_LOWER = TRAP_LOWER + TRAP_PERCENT;
    private static final int CHOICE_BENEFIT_PERCENT = 14;
    private static final int CHOICE_BENEFIT_LOWER = SHOP_LOWER + SHOP_PERCENT;
    private static final int RANDOM_BENEFIT_PERCENT = 14;
    private static final int RANDOM_BENEFIT_LOWER = CHOICE_BENEFIT_LOWER + CHOICE_BENEFIT_PERCENT;

    /**
     * Returns a random outdoor encounter int value, given a specific weighting to each encounter
     * @return  An outdoor encounter int value corresponding to its index placement in a JSONArray
     */
    public static int getRandomEncounter() {
        return RANDOM_BENEFIT;
        /*
        Random rand = new Random();
        int val = rand.nextInt(100);
        if (isBetween(COMBAT_DUNGEON_LOWER, COMBAT_DUNGEON_LOWER + COMBAT_DUNGEON_PERCENT, val))
            return COMBAT_DUNGEON;
        else if (isBetween(MULTI_DUNGEON_LOWER, MULTI_DUNGEON_LOWER + MULTI_DUNGEON_PERCENT, val))
            return MULTI_DUNGEON;
        else if (isBetween(CHOICE_LOWER, CHOICE_LOWER + CHOICE_PERCENT, val))
            return CHOICE;
        else if (isBetween(COMBAT_LOWER, COMBAT_LOWER + COMBAT_PERCENT, val))
            return COMBAT;
        else if (isBetween(TRAP_LOWER, TRAP_LOWER + TRAP_PERCENT, val))
            return TRAP;
        else if (isBetween(SHOP_LOWER, SHOP_LOWER + SHOP_PERCENT, val))
            return SHOP;
        else if (isBetween(CHOICE_BENEFIT_LOWER, CHOICE_BENEFIT_LOWER + CHOICE_BENEFIT_PERCENT, val))
            return CHOICE_BENEFIT;
        else
            return RANDOM_BENEFIT;*/
    }

    /**
     * Returns true if value if between value inclusive lower, exclusive higher.
     * @param lower     The smallest value of the interval.
     * @param higher    The largest value of the interval
     * @param val       The value to check is between lower and higher
     * @return          True if val is between inclusive lower, exclusive higher.
     */
    private static boolean isBetween(int lower, int higher, int val) {
        return val >= lower && val < higher;
    }
}
