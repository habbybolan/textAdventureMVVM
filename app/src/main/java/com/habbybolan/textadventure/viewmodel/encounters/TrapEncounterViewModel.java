package com.habbybolan.textadventure.viewmodel.encounters;

import android.view.View;

import androidx.databinding.BaseObservable;
import androidx.databinding.ObservableField;

import com.habbybolan.textadventure.model.characterentity.Damage;
import com.habbybolan.textadventure.model.encounter.TrapModel;
import com.habbybolan.textadventure.viewmodel.CharacterViewModel;
import com.habbybolan.textadventure.viewmodel.MainGameViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TrapEncounterViewModel extends BaseObservable {
    public static final int firstState = 1;
    public static final int secondState = 2;
    public static final int thirdState = 3;
    // holds all the UI states of the Trap encounter
    int[] states = new int[]{firstState, secondState, thirdState};

    private CharacterViewModel characterVM;
    private MainGameViewModel mainGameVM;
    private TrapModel trapModel;

    private JSONObject encounter;
    private JSONObject firstStateJSON;

    public TrapEncounterViewModel(MainGameViewModel mainGameVM, CharacterViewModel characterVM, JSONObject encounter) throws JSONException {
        this.characterVM = characterVM;
        this.mainGameVM = mainGameVM;
        trapModel = new TrapModel(characterVM);
        this.encounter = encounter;
        firstStateJSON = encounter.getJSONObject("dialogue");
    }

    public CharacterViewModel getCharacterVM() {
        return characterVM;
    }

    private ObservableField<Integer> stateIndex = new ObservableField<>(1);
    public ObservableField<Integer> getStateIndex() {
        return stateIndex;
    }
    // increment the state index to next state
    public void incrementStateIndex() {
        Integer state = stateIndex.get();
        if (state != null) {
            int newState = ++state;
            stateIndex.set(newState);
        }
    }

    // updates when a new bit of dialogue needs to be shown
    private ObservableField<String> newDialogue = new ObservableField<>();
    public ObservableField<String> getNewDialogue() {
        return newDialogue;
    }
    public void setNewDialogue(String newDialogue) {
        this.newDialogue.set(newDialogue);
    }

    public void gotoNextEncounter(View v) {
        mainGameVM.gotoNextEncounter();
    }

    // show the next dialogue snippet in the first state, called whenever dynamic button clicked
    public void firstState() throws JSONException {
        setNewDialogue(firstStateJSON.getString("dialogue"));
        if (firstStateJSON.has("next")) {
            firstStateJSON = firstStateJSON.getJSONObject("next");
        } else {
            incrementStateIndex();
        }
    }

    // button clicked to escape the trap
    public void secondStateEscapeTrap() throws JSONException {
        if (trapModel.isSuccessfulEscape()) {
            String success = encounter.getString("success");
            setNewDialogue(success);
        } else {
            String fail = encounter.getJSONObject("fail").getString("dialogue");
            setNewDialogue(fail);
            failDebuffs();
        }
        incrementStateIndex();
    }

    // helper for second state dealing with failing the trap speed check
    private void failDebuffs() throws JSONException {
        Damage damage = new Damage();
        JSONObject fail = encounter.getJSONObject("fail");
        // all debuffs (Dot, special, and direct damage applied to character)
        JSONArray debuffs = fail.getJSONArray("debuff");
        for (int i = 0; i < debuffs.length(); i++) {
            JSONObject debuff = debuffs.getJSONObject(i);
            String debuffType = debuff.getString("debuff");
            if (damage.isDot(debuffType)) {
                // debuff is a Dot
                characterVM.addDot(debuffType, false);
            } else if (damage.isSpecial(debuffType)) {
                // debuff is a special effect
                characterVM.addSpecial(debuffType, debuff.getInt("duration"));
            } else {
                // otherwise, debuff is direct damage
                characterVM.damageCharacter(debuff.getInt("damage"));
            }
        }
    }

    public void secondStateUseItem() {
       /* if (trapModel.checkInventoryForTrapItem()) throws JSONException {
            // todo: show success dialogue in rv
        } else {
            // todo: show toast message for no item to escape
        }*/
    }


}
