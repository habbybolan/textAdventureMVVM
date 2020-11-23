package com.habbybolan.textadventure.viewmodel.encounters;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.habbybolan.textadventure.model.dialogue.CombatActionDialogue;
import com.habbybolan.textadventure.model.dialogue.Dialogue;
import com.habbybolan.textadventure.model.dialogue.DialogueType;
import com.habbybolan.textadventure.model.dialogue.EffectDialogue;
import com.habbybolan.textadventure.model.dialogue.ExpDialogue;
import com.habbybolan.textadventure.model.dialogue.GoldDialogue;
import com.habbybolan.textadventure.model.dialogue.HealthDialogue;
import com.habbybolan.textadventure.model.dialogue.InventoryDialogue;
import com.habbybolan.textadventure.model.dialogue.ManaDialogue;
import com.habbybolan.textadventure.model.dialogue.StatDialogue;
import com.habbybolan.textadventure.model.dialogue.TempStatDialogue;
import com.habbybolan.textadventure.model.inventory.Inventory;
import com.habbybolan.textadventure.model.inventory.InventoryFactory;
import com.habbybolan.textadventure.view.dialogueAdapter.DialogueRecyclerView;
import com.habbybolan.textadventure.viewmodel.MainGameViewModel;
import com.habbybolan.textadventure.viewmodel.characterEntityViewModels.CharacterViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Stack;

/**
 * ViewModel that all encounter viewModels extends.
 * Implements all of the shared functionality between the encounter view models,
 * including dialogue shown (and first dialogue state), state changes, and saving methods to implement
 */
public abstract class EncounterViewModel extends AndroidViewModel {

    // key values for JSON to save encounter
    public static String ENCOUNTER_TYPE = "encounter_type";
    public static String ENCOUNTER = "encounter";
    static String STATE = "state";
    static String DIALOGUE_REMAINING = "dialogue_remaining";
    static String INVENTORY = "inventory";
    static String DIALOGUE_ADDED = "dialogue_added";
    static String COMBAT_ORDER = "combat_order";
    static String ENEMIES = "enemies";
    static String BUY_LIST = "buy_list";
    static String BUY_INVENTORY = "buy_inventory";
    static String BUY_COST = "buy_cost";


    // types of encounters that are values for the key ENCOUNTER_TYPE
    static String TYPE_CHOICE_BENEFIT = MainGameViewModel.CHOICE_BENEFIT_TYPE;
    static String TYPE_RANDOM_BENEFIT = MainGameViewModel.RANDOM_BENEFIT_TYPE;
    static String TYPE_TRAP = MainGameViewModel.TRAP_TYPE;
    static String TYPE_COMBAT = MainGameViewModel.COMBAT_TYPE;
    static String TYPE_SHOP = MainGameViewModel.SHOP_TYPE;
    static String TYPE_CHOICE = MainGameViewModel.CHOICE_TYPE;

    Application application;
    MainGameViewModel mainGameVM = MainGameViewModel.getInstance();
    CharacterViewModel characterVM = CharacterViewModel.getInstance();
    JSONObject encounter;

    // states
    private final MutableLiveData<Integer> stateIndex = new MutableLiveData<>(0);

    public EncounterViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        encounter = mainGameVM.getEncounter().getEncounterJSON();
        try {
            setDialogueRemainingInDialogueState(encounter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public LiveData<Integer> getStateIndex() {
        return stateIndex;
    }

    void setStateIndex(int stateIndex) {
        this.stateIndex.setValue(stateIndex);
    }

    /**
     * Get the value of the stateIndex liveData
     * @return  The int value of the stateIndex liveData
     */
    public int getStateIndexValue() {
        if (stateIndex.getValue() != null)
            return stateIndex.getValue();
        // stateIndex liveData should not be returning null
        throw new IllegalArgumentException();

    }

    // increment the state index to next state
    public void incrementStateIndex() {
        int state = getStateIndexValue();
        stateIndex.setValue(++state);
    }

    // goto the the saved state if one is saved, otherwise start at beginning
    public void gotoBeginningState() throws JSONException {
        MainGameViewModel mainGameVM = MainGameViewModel.getInstance();
        if (!mainGameVM.getIsSaved()) // if no save, then go to dialogue state normally
            stateIndex.setValue(1);
        else { // there is a save, goto saved state
            stateIndex.setValue(mainGameVM.getSavedEncounter().getInt("state"));
        }
    }

    // dialogue

    private ObservableField<Dialogue> newDialogue = new ObservableField<>();
    // observed for changes to add to RecyclerViewer
    public ObservableField<Dialogue> getNewDialogue() {
        return newDialogue;
    }
    // sets the new dialogue value inside dialogue RV
    void setNewDialogue(Dialogue newDialogue) {
        this.newDialogue.set(newDialogue);
    }
    // get the observable NewDialogue
    public Dialogue getNewDialogueValue() {
        Dialogue newDialogue = getNewDialogue().get();
        if (newDialogue != null) return newDialogue;
        throw new NullPointerException();
    }
    private JSONObject firstStateJSON = new JSONObject();
    private void setFirstStateJSON(JSONObject firstStateJSON) {
        this.firstStateJSON = firstStateJSON;
    }

    /**
     * Add the new dialogue to the dialogue recyclerView and set up the next dialogue line if any exists.
     * @throws JSONException    if formatting error in the dialogue object
     */
    public void firstDialogueState() throws JSONException {
        if (firstStateJSON.has("dialogue"))
            setNewDialogue(new Dialogue(firstStateJSON.getString("dialogue")));
        if (firstStateJSON.has("next")) {
            firstStateJSON = firstStateJSON.getJSONObject("next");
        } else {
            firstStateJSON = null;
            incrementStateIndex();
        }
    }
    public JSONObject getFirstStateJSON() {
        return firstStateJSON;
    }

    /**
     * Sets the remaining dialogue for the first dialogue state. If the encounter is just created, it will
     * contain all of the dialogue. Otherwise, it will contain the remaining dialogue if any.
     * @param encounter         The encounter JSONObject gathered from outdoor_encounters asset String file
     * @throws JSONException    if problem with JSON string formatting
     */
    private void setDialogueRemainingInDialogueState(JSONObject encounter) throws JSONException {
        MainGameViewModel mainGameVM = MainGameViewModel.getInstance();
        if (mainGameVM.getIsSaved()) {
            // first state dialogue to be iterated over
            if (mainGameVM.getSavedEncounter().has(DIALOGUE_REMAINING))
                setFirstStateJSON(mainGameVM.getSavedEncounter().getJSONObject(DIALOGUE_REMAINING));
        } else {
            // no saved game, so get the full dialogue object to iterate through
            setFirstStateJSON(encounter.getJSONObject("dialogue"));
        }
    }

        // dialogueList

    private ArrayList<DialogueType> dialogueList = new ArrayList<>();

    /**
     * set the saved dialogue list of previously added dialogue to RV
     * @param mainGameVM        The view model that holds the saved encounter data
     * @throws JSONException    if problem with JSON formatting of saved encounter String
     */
    void setDialogueList(MainGameViewModel mainGameVM) throws JSONException {
        JSONArray dialogue = mainGameVM.getSavedEncounter().getJSONArray(DIALOGUE_ADDED);
        dialogueList = JSONArrayIntoDialogueTypesList(dialogue);
    }
    public ArrayList<DialogueType> getDialogueList() {
        return dialogueList;
    }

    /**
     *  Helper to convert a JSONArray of DialogueTypes strings to a list of DialogueTypes.
     * @param jsonArray         The JSON String to be converted into a DialogueType object
     * @return                  The converted DialogueType
     * @throws JSONException    if formatting error in jsonObject JSON string
     */
    private ArrayList<DialogueType> JSONArrayIntoDialogueTypesList(JSONArray jsonArray) throws JSONException {
        ArrayList<DialogueType> dialogueList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            DialogueType dialogueType = JSONObjectToDialogueType(jsonArray.getJSONObject(i));
            dialogueList.add(dialogueType);
        }
        return dialogueList;
    }

    /**
     *  Helper for turning a JSONObject of DialogueType into DialogueType.
     * @param jsonObject        The JSON String of the DialogueType
     * @return                  The DialogueType created from the jsonObject string data
     * @throws JSONException    if formatting error in jsonObject JSON string
     */
    private DialogueType JSONObjectToDialogueType(JSONObject jsonObject) throws JSONException {
        String type = jsonObject.getString(DialogueType.DIALOGUE_TYPE);
        switch (type) {
            case DialogueType.TYPE_DIALOGUE:
                return new Dialogue(jsonObject.getString(DialogueType.DIALOGUE));
            case DialogueType.TYPE_EFFECT:
                return new EffectDialogue(jsonObject.getString(DialogueType.TYPE),
                        jsonObject.getInt(DialogueType.DURATION),
                        jsonObject.getInt(DialogueType.IMAGE_RESOURCE),
                        jsonObject.getBoolean(DialogueType.IS_INDEFINITE));
            case DialogueType.TYPE_HEALTH:
                return new HealthDialogue(jsonObject.getInt(DialogueType.AMOUNT));
            case DialogueType.TYPE_MANA:
                return new ManaDialogue(jsonObject.getInt(DialogueType.AMOUNT));
            case DialogueType.TYPE_INVENTORY:
                return new InventoryDialogue(jsonObject.getString(DialogueType.NAME),
                        jsonObject.getInt(DialogueType.IMAGE_RESOURCE),
                        jsonObject.getString(DialogueType.TYPE),
                        jsonObject.getBoolean(DialogueType.IS_ADDED));
            case DialogueType.TYPE_STAT:
                return new StatDialogue(jsonObject.getString(DialogueType.TYPE), jsonObject.getInt(DialogueType.AMOUNT));
            case DialogueType.TYPE_TEMP_STAT:
                return new TempStatDialogue(jsonObject.getString(DialogueType.TYPE), jsonObject.getInt(DialogueType.AMOUNT), jsonObject.getInt(DialogueType.DURATION));
            case DialogueType.TYPE_COMBAT_ACTION:
                return new CombatActionDialogue(jsonObject.getString(DialogueType.ATTACKER), jsonObject.getString(DialogueType.TARGET), jsonObject.getString(DialogueType.ACTION));
            case DialogueType.TYPE_GOLD:
                return new GoldDialogue(jsonObject.getInt(DialogueType.AMOUNT));
            case DialogueType.TYPE_EXP:
                return new ExpDialogue(jsonObject.getInt(DialogueType.AMOUNT));
            default:
                throw new IllegalArgumentException(type + " is not a type listed in the switch.");
        }
    }

    /**
     * Get the saved Inventory Object from the json if there is one.
     * @return                  The saved inventory Object if one exists, otherwise return null.
     * @throws JSONException    For JSON formatting error in serialized Inventory object.
     */
    Inventory setSingleSavedInventory() throws JSONException {
        MainGameViewModel mainGameVM = MainGameViewModel.getInstance();
        // check if an inventory value has been stored
        if (mainGameVM.getSavedEncounter().has(INVENTORY)) {
            String inventory = mainGameVM.getSavedEncounter().getString(INVENTORY);
            return InventoryFactory.createInventory(inventory);
        } else {
            return null;
        }
    }

    /**
     * Get the array of saved inventory objects from JSON if it exists
     * @return                  The saved inventory Object if one exists, otherwise return null.
     * @throws JSONException    For JSON formatting error in serialized Inventory object.
     */
    Stack<Inventory> setMultiSavedInventory() throws JSONException {
        Stack<Inventory> inventoryReward = new Stack<>();
        MainGameViewModel mainGameVM = MainGameViewModel.getInstance();
        // check if an inventory value has been stored
        if (mainGameVM.getSavedEncounter().has(INVENTORY)) {
            JSONArray inventoryArray = mainGameVM.getSavedEncounter().getJSONArray(INVENTORY);
            for (int i = 0; i < inventoryArray.length(); i++) {
                inventoryReward.add(InventoryFactory.createInventory(inventoryArray.getString(i)));
            }
        } else {
            return null;
        }
        return inventoryReward;
    }

    /**
     * Save the player character and the encounter the player is currently in.
     * @param rv            The DialogueRecyclerView holding all dialogue data.
     */
    public void saveGame(DialogueRecyclerView rv) {
        CharacterViewModel.getInstance().saveCharacter();
        saveEncounter(rv.getDialogueList());
    }
    public boolean getIsSaved() {
        return mainGameVM.getIsSaved();
    }
    // saved the necessary data of fragment to retrieve it
    abstract void saveEncounter(ArrayList<DialogueType> dialogueList);
    public abstract void setSavedData();
}
