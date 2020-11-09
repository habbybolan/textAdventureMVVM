package com.habbybolan.textadventure.view.encounter;

public interface EncounterFragment {

    // Checks the UI state to enter given state index value
    void checkState(int state);
    // state for going through the beginning dialogue of an encounter
    void endState();
}
