package com.habbybolan.textadventure.viewmodel.encounters;

import androidx.databinding.ObservableField;

import com.habbybolan.textadventure.model.dialogue.DialogueType;
import com.habbybolan.textadventure.viewmodel.MainGameViewModel;

import org.json.JSONException;

import java.util.ArrayList;

public interface EncounterViewModel {

    // key values for JSON to save encounter
    String ENCOUNTER_TYPE = "encounter_type";
    String ENCOUNTER = "encounter";
    String STATE = "state";
    String DIALOGUE_REMAINING = "dialogue_remaining";
    String INVENTORY = "inventory";
    String DIALOGUE_ADDED = "dialogue_added";

    // types of encounters that are values for the key ENCOUNTER_TYPE
    String TYPE_CHOICE_BENEFIT = MainGameViewModel.CHOICE_BENEFIT_TYPE;
    String TYPE_RANDOM_BENEFIT = MainGameViewModel.RANDOM_BENEFIT_TYPE;
    String TYPE_TRAP = MainGameViewModel.TRAP_TYPE;

    ObservableField<Integer> getStateIndex();
    int getStateIndexValue();
    // increment the state index to next state
    void incrementStateIndex();

    // saved the necessary data of fragment to retrieve it
    void saveEncounter(ArrayList<DialogueType> dialogueList);

    // observed for changes to add to RecyclerViewer
    ObservableField<String> getNewDialogue();
    // sets the new dialogue value inside dialogue RV
    void setNewDialogue(String newDialogue);
    String getNewDialogueValue(); // todo: remove?

    void firstDialogueState() throws JSONException;
}
