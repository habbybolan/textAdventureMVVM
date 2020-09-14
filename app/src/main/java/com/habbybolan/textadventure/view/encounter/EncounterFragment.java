package com.habbybolan.textadventure.view.encounter;

public interface EncounterFragment {

    void checkState(int state);
    // state for going through the beginning dialogue of an encounter
    void endState();
}
