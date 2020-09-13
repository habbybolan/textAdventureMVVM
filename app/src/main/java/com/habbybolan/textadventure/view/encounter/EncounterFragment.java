package com.habbybolan.textadventure.view.encounter;

import org.json.JSONException;

public interface EncounterFragment {

    void stateListener();
    void checkState();
    void setUpDialogueRV() throws JSONException;
    // state for going through the beginning dialogue of an encounter
    void dialogueState() throws JSONException;
    void endState();
}
