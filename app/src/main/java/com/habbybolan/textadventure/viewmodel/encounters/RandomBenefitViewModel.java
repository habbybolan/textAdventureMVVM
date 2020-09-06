package com.habbybolan.textadventure.viewmodel.encounters;

import android.content.Context;

import androidx.databinding.BaseObservable;
import androidx.databinding.ObservableField;

import com.habbybolan.textadventure.model.inventory.Inventory;
import com.habbybolan.textadventure.repository.database.DatabaseAdapter;
import com.habbybolan.textadventure.viewmodel.CharacterViewModel;
import com.habbybolan.textadventure.viewmodel.MainGameViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;
import java.util.concurrent.ExecutionException;


public class RandomBenefitViewModel extends BaseObservable implements EncounterViewModel {


    private MainGameViewModel mainGameVM;
    private CharacterViewModel characterVM;
    private JSONObject encounter;
    private Context context;
    private JSONObject firstStateJSON;

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
        Random rand = new Random();
        DatabaseAdapter adapter = new DatabaseAdapter(context);
        // randomly choose Inventory object, Weapon/Item/Ability
        int val = rand.nextInt(3);
        val = 1;
        Inventory inventory = null;
        try {
            switch (val) {
                case 0:
                    inventory = adapter.getRandomWeapons(1).get(0);
                    break;
                case 1:
                    inventory = adapter.getRandomAbilities(1).get(0);
                    break;
                case 2:
                    inventory = adapter.getRandomItems(1).get(0);
                    break;
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return inventory;
    }
}
