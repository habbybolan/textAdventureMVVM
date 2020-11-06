package com.habbybolan.textadventure.viewmodel.encounters;

import android.content.Context;

import com.habbybolan.textadventure.model.dialogue.DialogueType;
import com.habbybolan.textadventure.repository.SaveDataLocally;
import com.habbybolan.textadventure.viewmodel.MainGameViewModel;
import com.habbybolan.textadventure.viewmodel.characterEntityViewModels.CharacterViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CombatDungeonViewModel extends EncounterViewModel {

    private MainGameViewModel mainGameVM = MainGameViewModel.getInstance();
    private CharacterViewModel characterVM = CharacterViewModel.getInstance();
    private JSONObject encounter;
    Context context;

    public static final int firstState = 1;
    public static final int secondState = 2;

    public CombatDungeonViewModel(Context context) throws JSONException {
        encounter = mainGameVM.getEncounter().getEncounterJSON();
        setDialogueRemainingInDialogueState(encounter);
        this.context = context;
    }


    @Override
    void saveEncounter(ArrayList<DialogueType> dialogueList) {
        SaveDataLocally save = new SaveDataLocally(context);
        JSONObject encounterData = new JSONObject();
        try {
            encounterData.put(ENCOUNTER_TYPE, TYPE_CHOICE);
            encounterData.put(ENCOUNTER, encounter);
            encounterData.put(STATE, stateIndex.get());
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
     * Button functionality for entering the combat dungeon.
     */
    public void clickEnter() {
        try {
            mainGameVM.startCombatDungeon();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Button functionality for not entering the combat dungeon. Continue with outdoor encounters.
     */
    public void clickLeave() {
        mainGameVM.gotoNextRandomEncounter();
    }
}
