package com.habbybolan.textadventure.model;

import com.habbybolan.textadventure.viewmodel.characterEntityViewModels.CharacterViewModel;

import java.util.Random;

/*
creates new encounters, retrieved from JSON asset file
   holds logic for continuing and multi-levelled encounter
 */
public class MainGameModel {


    public MainGameModel() {
    }

    // creates an completely new JSONObject encounter
    public String setNewEncounter() {
        return "";
    }

    // continues into the encounter sub-encounters, retrieving the next sub-encounter
    public String continueSubEncounter() {
        return "";
    }

    /**
     * Calculated if the dungeon turn counter has reached 0 to signal the dungeon is finished.
     * @return  True if the dungeon is over. False otherwise.
     */
    public boolean isDungeonOver(CharacterViewModel characterVM) {
        characterVM.decrementDungeonCounter();
        return characterVM.getDungeonCounter() == 0;
    }

    /**
     * Break encounter check that will signal to enter a break encounter or not.
     * @return  true if break encounter check passes.
     */
    public boolean breakEncounterCheck() {
        Random rand = new Random();
        int val = rand.nextInt(100);
        // chance to enter break encounter
        // TODO: what chance for break encounter?
        return val < 0;
    }
}
