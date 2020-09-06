package com.habbybolan.textadventure.viewmodel.encounters;

import androidx.databinding.ObservableField;

import org.json.JSONException;

public interface EncounterViewModel {

    ObservableField<Integer> getStateIndex();
    int getStateIndexValue();
    // increment the state index to next state
    void incrementStateIndex();

    ObservableField<String> getNewDialogue();
    void setNewDialogue(String newDialogue);
    String getNewDialogueValue();

    void firstDialogueState() throws JSONException;
}
