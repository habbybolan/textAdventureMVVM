package com.habbybolan.textadventure.model.locations;

import com.habbybolan.textadventure.viewmodel.MainGameViewModel;

import java.util.Random;

public class MultiDungeon {


    final public static int CHOICE = 0;
    final public static String CHOICE_TYPE =  MainGameViewModel.CHOICE_TYPE;
    final public static int COMBAT = 1;
    final public static String COMBAT_TYPE =  MainGameViewModel.COMBAT_TYPE;
    final public static int TRAP = 2;
    final public static String TRAP_TYPE =  MainGameViewModel.TRAP_TYPE;
    final public static int SHOP = 3;
    final public static String SHOP_TYPE =  MainGameViewModel.SHOP_TYPE;
    final public static int CHOICE_BENEFIT = 4;
    final public static String CHOICE_BENEFIT_TYPE =  MainGameViewModel.CHOICE_BENEFIT_TYPE;
    final public static int RANDOM_BENEFIT = 5;
    final public static String RANDOM_BENEFIT_TYPE =  MainGameViewModel.RANDOM_BENEFIT_TYPE;
    final public static String CHECK_TYPE = MainGameViewModel.CHECK_TYPE;

    // min number of encounters inside multi dungeon
    private static final int MIN_AMOUNT = 4;
    // max number of encounters inside multi dungeon
    private static final int MAX_AMOUNT = 8;

    /**
     * Creates a random integer between MIN_AMOUNT and MAX_AMOUNT that sets up the number
     * of encounters that are in the multi dungeon.
     * @return  The number of encounters inside the multi dungeon.
     */
    public static int getMultiDungeonLength() {
        Random rand = new Random();
        return rand.nextInt((MAX_AMOUNT - MIN_AMOUNT)/2 + 1) + MIN_AMOUNT;
    }
}
