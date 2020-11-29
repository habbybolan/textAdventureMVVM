package com.habbybolan.textadventure.viewmodel.encounters;

import android.app.Application;

import com.habbybolan.textadventure.model.dialogue.Dialogue;
import com.habbybolan.textadventure.model.dialogue.DialogueType;
import com.habbybolan.textadventure.model.effects.Dot;
import com.habbybolan.textadventure.model.effects.SpecialEffect;
import com.habbybolan.textadventure.model.encounter.TrapModel;
import com.habbybolan.textadventure.repository.LocallySavedFiles;
import com.habbybolan.textadventure.viewmodel.characterEntityViewModels.CharacterViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TrapViewModel extends EncounterViewModel {
    public static final int firstState = 1;
    public static final int secondState = 2;
    public static final int thirdState = 3;

    private TrapModel trapModel;

    public TrapViewModel(Application application) {
        super(application);
        trapModel = new TrapModel(characterVM);
    }

    public CharacterViewModel getCharacterVM() {
        return characterVM;
    }

    @Override
    public void saveEncounter(ArrayList<DialogueType> dialogueList) {
        LocallySavedFiles save = new LocallySavedFiles(application);
        JSONObject encounterData = new JSONObject();
        try {
            encounterData.put(ENCOUNTER_TYPE, TYPE_TRAP);
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

    // button clicked to escape the trap
    public void secondStateEscapeTrap() throws JSONException {
        if (trapModel.isSuccessfulEscape()) {
            String success = encounter.getString("success");
            setNewDialogue(new Dialogue(success));
        } else {
            String fail = encounter.getJSONObject("fail").getString("dialogue");
            setNewDialogue(new Dialogue(fail));
            failDebuffs();
        }
        incrementStateIndex();
    }

    // helper for second state dealing with failing the trap speed check
    private void failDebuffs() throws JSONException {
        JSONObject fail = encounter.getJSONObject("fail");
        // all debuffs (Dot, special, and direct damage applied to character)
        JSONArray debuffs = fail.getJSONArray("debuff");
        for (int i = 0; i < debuffs.length(); i++) {
            JSONObject debuff = debuffs.getJSONObject(i);
            String debuffType = debuff.getString("debuff");
            if (SpecialEffect.isSpecial(debuffType)) throw new IllegalArgumentException("Can't have special effects applied from traps");
            if (Dot.isDot(debuffType)) {
                // debuff is a Dot
                characterVM.addInputDot(new Dot(debuffType, false));
            } else {
                // otherwise, debuff is direct damage
                characterVM.damageCharacterEntity(debuff.getInt("damage"));
            }
        }
    }

    /**
     * If possible, use an item to escape the trap and return true, otherwise return false.
     * @return  True if escape trap item is in inventory, otherwise return false;
     */
    public boolean secondStateUseItem() {
        if (characterVM.checkInventoryForTrapItem()) {
            String dialogue = "You throw the escape orb to the ground, the black smoke releasing and engulfing you in an instant. Once the smoke dissipates, " +
                    "Everything around you has changed. A feeling of relief washes over you as you wonder what would have happened if you did not possess that item.";
            setNewDialogue(new Dialogue(dialogue));
            incrementStateIndex();
            return true;
        }
        return false;
    }
}
