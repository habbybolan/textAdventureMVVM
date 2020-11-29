package com.habbybolan.textadventure.viewmodel.encounters;

import android.app.Application;

import com.habbybolan.textadventure.model.dialogue.DialogueType;
import com.habbybolan.textadventure.repository.JsonAssetFileReader;
import com.habbybolan.textadventure.repository.LocallySavedFiles;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChoiceViewModel extends EncounterViewModel {

    public static final int firstState = 1;
    public static final int secondState = 2;

    public ChoiceViewModel(Application application) {
        super(application);
    }
    @Override
    public void saveEncounter(ArrayList<DialogueType> dialogueList) {
        LocallySavedFiles save = new LocallySavedFiles(application);
        JSONObject encounterData = new JSONObject();
        try {
            encounterData.put(ENCOUNTER_TYPE, TYPE_CHOICE);
            encounterData.put(ENCOUNTER, encounter);
            encounterData.put(STATE, getStateIndexValue());
            if (getFirstStateJSON() != null) encounterData.put(DIALOGUE_REMAINING, getFirstStateJSON());
            // store all DialogueTypes converted to JSON
            JSONArray JSONDialogue = new JSONArray();
            for (DialogueType dialogueType : dialogueList) {
                JSONObject dialogueObject = dialogueType.toJSON();
                JSONDialogue.put(dialogueObject);
            }
            encounterData.put(DIALOGUE_ADDED, JSONDialogue);

            save.saveEncounter(encounterData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setSavedData() {
        try {
            if (getIsSaved()) {
                setDialogueList(mainGameVM);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the number of possible options, including all options in encounter JSONArray and leaving the encounter
     * @return  Number of options user can choose.
     */
    public int getNumOptions() {
        try {
            return encounter.getJSONArray(JsonAssetFileReader.OPTIONS).length()+1;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("Failure reading option in encounter JSONObject");
    }

    /**
     * Get the option text that correlated with index i of the option in JSONArray inside encounter JSONObject
     * @param i     Index of the encounter object text
     * @return      The String to display on the option button
     */
    public String getOptionText(int i) {
        if (isLeaveOption(i)) {
            return "Leave";
        } else {
            try {
                JSONObject option = encounter.getJSONArray(JsonAssetFileReader.OPTIONS).getJSONObject(i);
                return option.getString(JsonAssetFileReader.NAME);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            throw new IllegalArgumentException("Failure reading option in encounter JSONObject");
        }
    }

    /**
     * Apply the action of the option associated with index in options JSONArray in encounter JSONObject
     * @param index     Index of option to perform.
     */
    public void applyAction(int index) {
        if (isLeaveOption(index)) {
            // action to leave the encounter and goto a new random one
            mainGameVM.gotoNextRandomEncounter();
        } else {
            // action to go into specified option encounter
            try {
                JSONObject option = encounter.getJSONArray(JsonAssetFileReader.OPTIONS).getJSONObject(index);
                mainGameVM.gotoSpecifiedEncounter(option);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns true if the option index is the option to leave the encounter.
     * @param index     Index of option to check
     * @return          True if index of option is the leave encounter option
     */
    private boolean isLeaveOption(int index) {
        return index == getNumOptions() - 1;
    }
}
