package com.habbybolan.textadventure.view.encounter;

import org.json.JSONException;

public interface EncounterFragment {

    void stateListener();
    void checkState();
    void setUpDialogueRV();
    void dialogueState() throws JSONException;
    void endState();
}
