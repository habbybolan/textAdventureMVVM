package com.habbybolan.textadventure.viewmodel.encounters;

import android.content.Context;

import androidx.databinding.BaseObservable;
import androidx.databinding.ObservableField;

import com.habbybolan.textadventure.model.dialogue.DialogueType;
import com.habbybolan.textadventure.model.encounter.RandomBenefitModel;
import com.habbybolan.textadventure.model.inventory.Ability;
import com.habbybolan.textadventure.model.inventory.Inventory;
import com.habbybolan.textadventure.model.inventory.Item;
import com.habbybolan.textadventure.model.inventory.weapon.Weapon;
import com.habbybolan.textadventure.viewmodel.CharacterViewModel;
import com.habbybolan.textadventure.viewmodel.MainGameViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class RandomBenefitViewModel extends BaseObservable implements EncounterViewModel {


    private MainGameViewModel mainGameVM;
    private CharacterViewModel characterVM;
    private JSONObject encounter;
    private Context context;
    private JSONObject firstStateJSON;
    private RandomBenefitModel randomBenefitModel;

    public static final int firstState = 1;
    public static final int secondState = 2;
    public static final int thirdState = 3;

    private ObservableField<Integer> stateIndex;
    private ObservableField<String> newDialogue;

    public RandomBenefitViewModel(MainGameViewModel mainGameVM, CharacterViewModel characterVM, JSONObject encounter, Context context) throws JSONException {
        this.mainGameVM = mainGameVM;
        this.characterVM = characterVM;
        this.encounter = encounter;
        this.context = context;
        // holds dialogue object to be iterated through
        firstStateJSON = encounter.getJSONObject("dialogue");
        stateIndex = new ObservableField<>(1);
        newDialogue = new ObservableField<>();
        randomBenefitModel = new RandomBenefitModel(context);
    }

    @Override
    public ObservableField<Integer> getStateIndex() {
        return stateIndex;
    }
    public int getStateIndexValue() {
        Integer stateIndex = getStateIndex().get();
        if (stateIndex != null) return stateIndex;
        throw new NullPointerException();
    }
    // increment the state index to next state
    @Override
    public void incrementStateIndex() {
        Integer state = stateIndex.get();
        if (state != null) {
            int newState = ++state;
            stateIndex.set(newState);
        }
    }

    @Override
    public void saveEncounter(ArrayList<DialogueType> dialogueList) {
        // TODO: Save random Benefit encounter
    }

    // updates when a new bit of dialogue needs to be shown
    @Override
    public ObservableField<String> getNewDialogue() {
        return newDialogue;
    }
    public String getNewDialogueValue() {
        String newDialogue = getNewDialogue().get();
        if (newDialogue != null) return newDialogue;
        throw new NullPointerException();
    }
    @Override
    public void setNewDialogue(String newDialogue) {
        this.newDialogue.set(newDialogue);
    }



    // show the next dialogue snippet in the first state, called whenever dynamic button clicked
    @Override
    public void firstDialogueState() throws JSONException {
        setNewDialogue(firstStateJSON.getString("dialogue"));
        if (firstStateJSON.has("next")) {
            firstStateJSON = firstStateJSON.getJSONObject("next");
        } else {
            incrementStateIndex();
        }
    }

    // finds a random Inventory loot
    public Inventory checkState() {
        return randomBenefitModel.getRandomInventory();
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
