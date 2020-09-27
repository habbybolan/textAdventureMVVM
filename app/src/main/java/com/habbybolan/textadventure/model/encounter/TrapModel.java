package com.habbybolan.textadventure.model.encounter;

import com.habbybolan.textadventure.viewmodel.characterEntityViewModels.CharacterViewModel;

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
    public boolean isSuccessfulEscape() {
        Random rand = new Random();
        int valToBeat = rand.nextInt(100);
        // each point of speed adds 5%
        return (characterVM.getCharacter().getSpeed() * SPD_SCALE) > valToBeat;
    }
}
