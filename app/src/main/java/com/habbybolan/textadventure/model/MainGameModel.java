package com.habbybolan.textadventure.model;

import com.habbybolan.textadventure.viewmodel.characterEntityViewModels.CharacterViewModel;

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
}
