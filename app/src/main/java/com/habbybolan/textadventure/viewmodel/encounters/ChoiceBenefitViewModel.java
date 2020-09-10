package com.habbybolan.textadventure.viewmodel.encounters;

import android.content.Context;

import androidx.databinding.ObservableField;

import com.habbybolan.textadventure.model.effects.Effect;
import com.habbybolan.textadventure.model.effects.TempBar;
import com.habbybolan.textadventure.model.effects.TempStat;
import com.habbybolan.textadventure.model.encounter.ChoiceBenefitModel;
import com.habbybolan.textadventure.model.inventory.Ability;
import com.habbybolan.textadventure.model.inventory.Inventory;
import com.habbybolan.textadventure.model.inventory.Item;
import com.habbybolan.textadventure.model.inventory.weapon.Weapon;
import com.habbybolan.textadventure.viewmodel.CharacterViewModel;
import com.habbybolan.textadventure.viewmodel.MainGameViewModel;

import org.json.JSONException;
import org.json.JSONObject;

public class ChoiceBenefitViewModel implements EncounterViewModel {


    private MainGameViewModel mainGameVM;
    private CharacterViewModel characterVM;
    private JSONObject encounter;
    private Context context;
    private JSONObject firstStateJSON;
    ChoiceBenefitModel choiceBenefitModel;

    public static final int firstState = 1;
    public static final int secondState = 2;
    public static final int thirdState = 3;

    private ObservableField<Integer> stateIndex;
    private ObservableField<String> newDialogue;
    
    public ChoiceBenefitViewModel(MainGameViewModel mainGameVM, CharacterViewModel characterVM, JSONObject encounter, Context context) throws JSONException {
        this.mainGameVM = mainGameVM;
        this.characterVM = characterVM;
        this.encounter = encounter;
        this.context = context;
        // holds dialogue object to be iterated through
        firstStateJSON = encounter.getJSONObject("dialogue");
        stateIndex = new ObservableField<>(1);
        newDialogue = new ObservableField<>();
        choiceBenefitModel = new ChoiceBenefitModel(context);
    }


    @Override
    public ObservableField<Integer> getStateIndex() {
        return stateIndex;
    }

    @Override
    public int getStateIndexValue() {
        Integer stateIndex = getStateIndex().get();
        if (stateIndex != null) return stateIndex;
        throw new NullPointerException();
    }

    @Override
    public void incrementStateIndex() {
        Integer state = stateIndex.get();
        if (state != null) {
            int newState = ++state;
            stateIndex.set(newState);
        }
    }

    @Override
    public ObservableField<String> getNewDialogue() {
        return newDialogue;
    }

    @Override
    public void setNewDialogue(String newDialogue) {
        this.newDialogue.set(newDialogue);
    }

    @Override
    public String getNewDialogueValue() {
        String newDialogue = getNewDialogue().get();
        if (newDialogue != null) return newDialogue;
        throw new NullPointerException();
    }

    @Override
    public void firstDialogueState() throws JSONException {
        setNewDialogue(firstStateJSON.getString("dialogue"));
        if (firstStateJSON.has("next")) {
            firstStateJSON = firstStateJSON.getJSONObject("next");
        } else {
            incrementStateIndex();
        }
    }

    // gain a random tangible item
    public Inventory getTangible() {
        return choiceBenefitModel.getNewInventory();
    }

    public void setPermIncrease() {
        Effect effect = choiceBenefitModel.getNewPermanentStat();
        if (effect.isTempStat()) {
            // add permanent stat
            characterVM.permIncreaseStat((TempStat) effect);
        } else {
            // otherwise add the permanent heath/mana increase
            characterVM.setPermBarIncr((TempBar) effect);
        }
    }

    public void setTempIncrease() {
        Effect effect = choiceBenefitModel.getNewTempStat();
        if (effect.isTempStat()) {
            // add permanent stat
            characterVM.addInputStat((TempStat) effect);
        } else {
            // otherwise add the permanent heath/mana increase
            characterVM.addTempHealthMana((TempBar) effect);
        }
    }

    // if space in inventory, return true and add inventory object to inventory, otherwise return false
    public boolean addNewInventory(Inventory inventoryToRetrieve) {
        if (inventoryToRetrieve.getType().equals(Inventory.TYPE_ABILITY)) {
            // if inventory is full, don't pick-up Ability
            return (characterVM.addAbility((Ability) inventoryToRetrieve));

        } else if (inventoryToRetrieve.getType().equals(Inventory.TYPE_ITEM)) {
            // if inventory is full, don't pick-up item
            return (characterVM.addItem((Item) inventoryToRetrieve)) ;

        } else {
            // if inventory is full, cant pick up weapon
            return (characterVM.addWeapon((Weapon) inventoryToRetrieve));
        }
    }

    // return toast message for being full on inventory space
    public String getToastString(Inventory inventoryToRetrieve) {
        if (inventoryToRetrieve.getType().equals(Inventory.TYPE_ITEM)) {
            return "You are full on Items";
        } else if (inventoryToRetrieve.getType().equals(Inventory.TYPE_ABILITY)) {
            return  "You are full on Ability Scrolls";
        } else {
            return "You are full on Weapons";
        }
    }
}
