package com.habbybolan.textadventure.viewmodel.encounters;

import android.content.Context;
import android.widget.Toast;

import com.habbybolan.textadventure.model.dialogue.Dialogue;
import com.habbybolan.textadventure.model.dialogue.DialogueType;
import com.habbybolan.textadventure.model.effects.Dot;
import com.habbybolan.textadventure.model.effects.SpecialEffect;
import com.habbybolan.textadventure.model.encounter.TrapModel;
import com.habbybolan.textadventure.repository.SaveDataLocally;
import com.habbybolan.textadventure.viewmodel.characterEntityViewModels.CharacterViewModel;
import com.habbybolan.textadventure.viewmodel.MainGameViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TrapEncounterViewModel extends EncounterViewModel {
    public static final int firstState = 1;
    public static final int secondState = 2;
    public static final int thirdState = 3;

    private CharacterViewModel characterVM;
    private MainGameViewModel mainGameVM;
    private TrapModel trapModel;

    private JSONObject encounter;
    private Context context;

    public TrapEncounterViewModel(JSONObject encounter, Context context) throws JSONException {
        setDialogueRemainingInDialogueState(encounter);
        mainGameVM = MainGameViewModel.getInstance();
        characterVM = CharacterViewModel.getInstance();
        this.encounter = encounter;
        this.context = context;
        trapModel = new TrapModel(characterVM);
    }

    public CharacterViewModel getCharacterVM() {
        return characterVM;
    }


    @Override
    public void saveEncounter(ArrayList<DialogueType> dialogueList) {
        SaveDataLocally save = new SaveDataLocally(context);
        JSONObject encounterData = new JSONObject();
        try {
            encounterData.put(ENCOUNTER_TYPE, TYPE_TRAP);
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
            if (Dot.isDot(debuffType)) {
                // debuff is a Dot
                characterVM.addInputDot(new Dot(debuffType, false));
            } else if (SpecialEffect.isSpecial(debuffType)) {
                // debuff is a special effect
                characterVM.addInputSpecial(new SpecialEffect(debuffType, debuff.getInt("duration")));
            } else {
                // otherwise, debuff is direct damage
                characterVM.damageCharacterEntity(debuff.getInt("damage"));
            }
        }
    }

    public void secondStateUseItem() {
        if (characterVM.checkInventoryForTrapItem()) {
            String dialogue = "You throw the escape orb to the ground, the black smoke releasing and engulfing you in an instant. Once the smoke dissipates, Everything around you has changed. A feeling of relief washes over you as you wonder what would have happened if you did not possess that item.";
            setNewDialogue(new Dialogue(dialogue));
            incrementStateIndex();
        } else {
            Toast.makeText(context, "You don't possess any item to escape.", Toast.LENGTH_SHORT).show();
        }
    }
}
