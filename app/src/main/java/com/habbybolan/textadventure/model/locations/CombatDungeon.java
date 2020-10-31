package com.habbybolan.textadventure.model.locations;

import com.habbybolan.textadventure.viewmodel.MainGameViewModel;

import java.util.Random;

public class CombatDungeon {

    final public static int COMBAT = 0;
    final public static String COMBAT_TYPE =  MainGameViewModel.COMBAT_TYPE;

    // min number of encounters inside multi dungeon
    private static final int MIN_AMOUNT = 5;
    // max number of encounters inside multi dungeon
    private static final int MAX_AMOUNT = 10;

    /**
     * Creates a random integer between MIN_AMOUNT and MAX_AMOUNT that sets up the number
     * of encounters that are in the multi dungeon.
     * @return  The number of encounters inside the multi dungeon.
     */
    public static int getCombatDungeonLength() {
        Random rand = new Random();
        return rand.nextInt((MAX_AMOUNT - MIN_AMOUNT)/2 + 1) + MIN_AMOUNT;
    }
}
