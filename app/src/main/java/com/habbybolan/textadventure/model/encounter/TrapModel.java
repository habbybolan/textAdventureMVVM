package com.habbybolan.textadventure.model.encounter;

import com.habbybolan.textadventure.model.inventory.Item;
import com.habbybolan.textadventure.viewmodel.CharacterViewModel;

import java.util.ArrayList;
import java.util.Random;
/*
holds the business logic for the Trap encounter
 */
public class TrapModel {
    private final static int SPD_SCALE = 5;

    CharacterViewModel characterVM;
    public TrapModel(CharacterViewModel characterVM) {
        this.characterVM = characterVM;
    }

    // do speed check for the trap
    private boolean isSuccessfulEscape() {
        Random rand = new Random();
        int valToBeat = rand.nextInt(100);
        // each point of speed adds 5%
        return (characterVM.getCharacter().getSpeed() * SPD_SCALE) > valToBeat;
    }

    // returns true if a disarm trap exists, and removes the trap from inventory
    private boolean checkInventoryForTrapItem() {
        ArrayList<Item> items = characterVM.getCharacter().getItems();
        for (Item trapItem : items) {
            if (trapItem.getEscapeTrap() == 1) {
                items.remove(trapItem);
                return true;
            }
        }
        return false;
    }


}
