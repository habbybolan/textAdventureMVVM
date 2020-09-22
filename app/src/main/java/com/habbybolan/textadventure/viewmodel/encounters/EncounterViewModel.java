package com.habbybolan.textadventure.viewmodel.encounters;

import androidx.databinding.ObservableField;

import com.habbybolan.textadventure.model.dialogue.Dialogue;
import com.habbybolan.textadventure.model.dialogue.DialogueType;
import com.habbybolan.textadventure.model.dialogue.EffectDialogue;
import com.habbybolan.textadventure.model.dialogue.HealthDialogue;
import com.habbybolan.textadventure.model.dialogue.InventoryDialogue;
import com.habbybolan.textadventure.model.dialogue.ManaDialogue;
import com.habbybolan.textadventure.model.dialogue.StatDialogue;
import com.habbybolan.textadventure.model.dialogue.TempStatDialogue;
import com.habbybolan.textadventure.viewmodel.MainGameViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/*
View Model that all encounter view models extend
 - implements all of the shared functionality between the encounter view models,
    including dialogue shown (and first dialogue state), state changes, and saving methods to implement
 */
public abstract class EncounterViewModel {

    // key values for JSON to save encounter
    public static String ENCOUNTER_TYPE = "encounter_type";
    public static String ENCOUNTER = "encounter";
    public static String STATE = "state";
    public static String DIALOGUE_REMAINING = "dialogue_remaining";
    public static String INVENTORY = "inventory";
    public static String DIALOGUE_ADDED = "dialogue_added";

    // types of encounters that are values for the key ENCOUNTER_TYPE
    public static String TYPE_CHOICE_BENEFIT = MainGameViewModel.CHOICE_BENEFIT_TYPE;
    public static String TYPE_RANDOM_BENEFIT = MainGameViewModel.RANDOM_BENEFIT_TYPE;
    public static String TYPE_TRAP = MainGameViewModel.TRAP_TYPE;

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
    public void gotoBeginningState(MainGameViewModel mainGameVM) throws JSONException {
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
    public void setFirstStateJSON(JSONObject firstStateJSON) {
        this.firstStateJSON = firstStateJSON;
    }
    public void firstDialogueState() throws JSONException {
        if (firstStateJSON.has("dialogue")) setNewDialogue(new Dialogue(firstStateJSON.getString("dialogue")));
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
    public void setDialogueRemainingInDialogueState(JSONObject encounter) throws JSONException {
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

    protected ArrayList<DialogueType> dialogueList = new ArrayList<>();
    // set the saved dialogue list of previously added dialogue to RV
    void setDialogueList(MainGameViewModel mainGameVM) throws JSONException {
        JSONArray dialogue = mainGameVM.getSavedEncounter().getJSONArray(DIALOGUE_ADDED);
        dialogueList = JSONArrayIntoDialogueTypesList(dialogue);
    }
    public ArrayList<DialogueType> getDialogueList() {
        return dialogueList;
    }
    // helper to convert a JSONArray of DialogueTypes strings to a list of DialogueTypes
    private ArrayList<DialogueType> JSONArrayIntoDialogueTypesList(JSONArray jsonArray) throws JSONException {
        ArrayList<DialogueType> dialogueList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            DialogueType dialogueType = JSONObjectToDialogueType(jsonArray.getJSONObject(i));
            dialogueList.add(dialogueType);
        }
        return dialogueList;
    }
    // helper for turning a JSONObject of DialogueType into DialogueType
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
            default:
                // otherWise, TYPE_TEMP_STAT
                return new TempStatDialogue(jsonObject.getString(DialogueType.TYPE), jsonObject.getInt(DialogueType.AMOUNT), jsonObject.getInt(DialogueType.DURATION));
        }
    }

    // saved encounter

    private boolean isSaved = false;
    public void setIsSaved(boolean isSaved) {
        this.isSaved = isSaved;
    }
    public boolean getIsSaved() {
        return isSaved;
    }
    // saved the necessary data of fragment to retrieve it
    abstract void saveEncounter(ArrayList<DialogueType> dialogueList);
    abstract void setSavedData() throws JSONException, ExecutionException, InterruptedException;
}
