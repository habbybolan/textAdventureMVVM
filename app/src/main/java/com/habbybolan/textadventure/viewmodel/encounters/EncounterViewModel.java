package com.habbybolan.textadventure.viewmodel.encounters;

import androidx.databinding.ObservableField;

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
import com.habbybolan.textadventure.model.inventory.Ability;
import com.habbybolan.textadventure.model.inventory.Inventory;
import com.habbybolan.textadventure.model.inventory.Item;
import com.habbybolan.textadventure.model.inventory.weapon.Weapon;
import com.habbybolan.textadventure.view.dialogueAdapter.DialogueRecyclerView;
import com.habbybolan.textadventure.viewmodel.MainGameViewModel;
import com.habbybolan.textadventure.viewmodel.characterEntityViewModels.CharacterViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/*
View Model that all encounter view models extend
 - implements all of the shared functionality between the encounter view models,
    including dialogue shown (and first dialogue state), state changes, and saving methods to implement
 */
public abstract class EncounterViewModel {

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

    // states

    ObservableField<Integer> stateIndex = new ObservableField<>();
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
    public int getStateIndexValue() {
        Integer stateIndex = getStateIndex().get();
        if (stateIndex != null) return stateIndex;
        throw new NullPointerException();
    }
    // goto the the saved state if one is saved, otherwise start at beginning
    public void gotoBeginningState() throws JSONException {
        MainGameViewModel mainGameVM = MainGameViewModel.getInstance();
        if (!isSaved) // if no save, then go to dialogue state normally
            stateIndex.set(1);
        else { // there is a save, goto saved state
            stateIndex.set(mainGameVM.getSavedEncounter().getInt("state"));
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
    void setDialogueRemainingInDialogueState(JSONObject encounter) throws JSONException {
        MainGameViewModel mainGameVM = MainGameViewModel.getInstance();
        if (mainGameVM.getSavedEncounter() != null) {
            setIsSaved(true);
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
        switch (jsonObject.getString(DialogueType.DIALOGUE_TYPE)) {
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
                throw new IllegalArgumentException("Incorrect dialogue key");
        }
    }

    // saved encounter

    /**
     * get the saved Inventory Object from the json
     * @return                  The saved inventory Object if one exists, otherwise return null.
     * @throws JSONException    For JSON formatting error in serialized Inventory object.
     */
    Inventory setSavedInventory() throws JSONException {
        MainGameViewModel mainGameVM = MainGameViewModel.getInstance();
        // check if an inventory value has been stored
        if (mainGameVM.getSavedEncounter().has(INVENTORY)) {
            JSONObject inventory = mainGameVM.getSavedEncounter().getJSONObject(INVENTORY);
            if (inventory.getString(Inventory.TYPE).equals(Inventory.TYPE_ABILITY)) {
                // saved Inventory object is an Ability
                return new Ability(inventory.toString());

            } else if (inventory.getString(Inventory.TYPE).equals(Inventory.TYPE_ITEM)) {
                // saved Inventory object is an Item
                return new Item(inventory.toString());

            } else if (inventory.getString(Inventory.TYPE).equals(Inventory.TYPE_WEAPON)) {
                // otherwise, saved Inventory object is a Weapon
                return new Weapon(inventory.toString());
            } else {
                throw new IllegalArgumentException(inventory.getString(Inventory.TYPE) + " is not a correct Inventory type");
            }
        } else {
            return null;
        }
    }

    /**
     * Save the player character and the encounter the player is currently in.
     * @param rv            The DialogueRecyclerView holding all dialogue data.
     */
    public void saveGame(DialogueRecyclerView rv) {
        CharacterViewModel.getInstance().saveCharacter();
        saveEncounter(rv.getDialogueList());
    }

    private boolean isSaved = false;
    public void setIsSaved(boolean isSaved) {
        this.isSaved = isSaved;
    }
    public boolean getIsSaved() {
        return isSaved;
    }
    // saved the necessary data of fragment to retrieve it
    abstract void saveEncounter(ArrayList<DialogueType> dialogueList);
    public abstract void setSavedData();
}
