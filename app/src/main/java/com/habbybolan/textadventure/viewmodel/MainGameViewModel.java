package com.habbybolan.textadventure.viewmodel;

import android.content.Context;
import android.view.View;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.habbybolan.textadventure.BR;
import com.habbybolan.textadventure.model.MainGameModel;
import com.habbybolan.textadventure.model.characterentity.Character;
import com.habbybolan.textadventure.model.encounter.RandomBenefitModel;
import com.habbybolan.textadventure.model.encounter.ShopModel;
import com.habbybolan.textadventure.model.locations.CombatDungeon;
import com.habbybolan.textadventure.model.locations.MultiDungeon;
import com.habbybolan.textadventure.model.locations.Outdoor;
import com.habbybolan.textadventure.repository.JsonAssetFileReader;
import com.habbybolan.textadventure.repository.SaveDataLocally;
import com.habbybolan.textadventure.viewmodel.characterEntityViewModels.CharacterViewModel;
import com.habbybolan.textadventure.viewmodel.encounters.EncounterViewModel;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Creates the logic for swapping between encounters and dealing with location changes from outdoors
 * to multi dungeon and combat dungeon.
 * Holds the JSONEncounter, all necessary JSON data for the current encounter being played.
 * Holds logic for swapping between the game fragment and character fragment.
 */
public class MainGameViewModel extends BaseObservable {

    // number of encounters the user has played
    private int encounterNumber = 0;
    private int turnCounter; // keeps track of the number of encounters/distance travelled

    private int gameFragmentVisible = View.VISIBLE; // visibility of the game fragment
    private int characterFragmentVisible = View.GONE; // visibility of the character fragment

    public static final String outdoorJSONFilename = "outdoor_encounters";
    public static final String multiDungeonJSONFilename = "multi_dungeon_encounters";
    public static final String combatDungeonJSONFilename = "combat_dungeon_encounters";
    public static final String breakJSONFilename = "break_encounters";

    private JSONObject JSONEncounter;

    // keeps track of the location
    private int location = FOREST;
    private String location_type = FOREST_TYPE;

    private int encounterLayout;

    // locations - int corresponding to the index of the location in a JSONArray in outdoor_encounters
    final public static int DESERT = 0;
    final public static String DESERT_TYPE = "desert";
    final public static int FOREST = 1;
    final public static String FOREST_TYPE = "forest";
    final public static int GRASSLAND = 2;
    final public static String GRASSLAND_TYPE = "grassland";
    final public static int SNOW = 3;
    final public static String SNOW_TYPE = "snow";
    // number of different locations
    final public static int numLocations = 4;

    // encounter types - int corresponding to the index of the location in a JSONArray in outdoor_encounters
    final public static String TYPE = "type";

    private final static int OUTDOOR_STATE = Character.OUTDOOR_STATE;
    private final static int MULTI_DUNGEON_STATE = Character.MULTI_DUNGEON_STATE;
    private final static int COMBAT_DUNGEON_STATE = Character.COMBAT_DUNGEON_STATE;

    final public static String COMBAT_DUNGEON_TYPE = "combat_dungeon";
    final public static String MULTI_DUNGEON_TYPE = "multi_dungeon";
    final public static String CHOICE_TYPE = "choice";
    final public static String COMBAT_TYPE = "combat";
    final public static String TRAP_TYPE = "trap";
    final public static String SHOP_TYPE = "shop";
    final public static String CHOICE_BENEFIT_TYPE = "choice_benefit";
    final public static String RANDOM_BENEFIT_TYPE = "random_benefit";
    final public static String QUEST_TYPE = "quest";
    final public static String CHECK_TYPE = "check";
    final public static String BREAK_TYPE = "break";

    private MainGameModel mainGameModel = new MainGameModel();


    private CharacterViewModel characterVM;
    private Context context;

    private JSONObject savedEncounter;

    private static MainGameViewModel instance = null;

    // returns true if viewModel is initiated
    public static boolean isInitiated() {
        return instance != null;
    }

    // uses singleton pattern to access in all encounter fragments
    public static MainGameViewModel getInstance() {
        if (instance == null) throw new AssertionError("Have to call init first");
        return instance;
    }

    // private constructor only accessed from initiation
    private MainGameViewModel(Context context, CharacterViewModel characterVM) {
        this.characterVM = characterVM;
        this.context = context;
    }

    // must be initialized before getting the instance
    public static MainGameViewModel init(Context context, CharacterViewModel characterVM) {
        if (instance != null) throw new AssertionError("Already Initialized");
        instance = new MainGameViewModel(context, characterVM);
        return instance;
    }

    // observer for when the encounter changes, starting a new one
    private ObservableField<String> encounterType = new ObservableField<>();
    public ObservableField<String> getEncounterType() {
        return encounterType;
    }
    private void setEncounterType(String encounterType) {
        this.encounterType.set(encounterType);
    }

    /**
     * Checks if there is a previously saved encounter to enter. If one exists, then goto that specific
         * saved encounter, otherwise create a new random encounter to enter.
         * @throws JSONException    If problem in JSON encounter formatting.
     */
    public void openGameEncounter() throws JSONException {
        SaveDataLocally save = new SaveDataLocally(context);
        JSONObject prevSave = save.readSavedEncounter();
        if (prevSave != null) {
            // if a saved encounter exists, then go into it
            savedEncounter = prevSave;
            JSONEncounter = prevSave.getJSONObject(EncounterViewModel.ENCOUNTER);
            setEncounterType(prevSave.getString(EncounterViewModel.ENCOUNTER_TYPE));
        } else {
            // otherwise, create a new encounter
            gotoNextRandomEncounter();
        }
    }

    /**
     *  Start a new random encounter once one has finished or a new game has started. Given the current state of the encounter
     *  stored in the character, it will start an encounter associated with the that state.
     */
    public void gotoNextRandomEncounter() {
        applyAfterEncounterActions();
        try {
            switch (characterVM.getEncounterState()) {
                case OUTDOOR_STATE:
                    if (mainGameModel.breakEncounterCheck()) {
                        // Enter break encounter
                        createNewBreakEncounter();
                    } else {
                        // otherwise, enter a random outdoor encounter
                        characterVM.incrementDistance();
                        createNewOutdoorEncounter();
                    }
                    break;
                case MULTI_DUNGEON_STATE:
                    checkContinueMultiDungeon();
                    break;
                case COMBAT_DUNGEON_STATE:
                    createNewCombatDungeonEncounter();
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * Starts a specified encounter from specifiedEncounter JSON.
     * @param specifiedEncounter    The encounter to start relating to the JSON.
     */
    public void gotoSpecifiedEncounter(JSONObject specifiedEncounter) throws JSONException {
        applyAfterEncounterActions();
        JSONEncounter = specifiedEncounter;
        String type = JSONEncounter.getString("type");
        setEncounterType(type);
    }

    /**
     * Called to signal that player entered multi dungeon
     */
    public void startMultiDungeon() throws JSONException {
        // set the size of the dungeon
        characterVM.setDungeonCounter(MultiDungeon.getMultiDungeonLength());
        characterVM.setStateToMultiDungeon();
        createNewMultiDungeonEncounter();
    }

    /**
     * Called to signal that player entered combat dungeon.
     */
    public void startCombatDungeon() throws JSONException {
        // set the size of the dungeon
        characterVM.setDungeonCounter(CombatDungeon.getCombatDungeonLength());
        characterVM.setStateToCombatDungeon();
        createNewCombatDungeonEncounter();
    }

    /**
     * Start fragment to check if player wants to continue in multi dungeon encounter, or leave
     * the dungeon and continue on with outdoor encounters. Called from an encounter view Model and startMultiDungeon.
     * Sets up the encounter object with type 'check' and a single dialogue line.
     */
    private void checkContinueMultiDungeon() throws JSONException {
        JSONObject check = new JSONObject();
        check.put(TYPE, MultiDungeon.CHECK_TYPE);
        JSONObject dialogueObj = new JSONObject();
        dialogueObj.put("dialogue", "Continue in the dungeon?");
        check.put("dialogue", dialogueObj);
        // create a check JSONObject that only holds
        //      {"type":"check",
        //      "dialogue":{"Continue in the dungeon?"}}
        // and set as encounter
        JSONEncounter = check;
        setEncounterType(MultiDungeon.CHECK_TYPE);
    }

    /**
     * Creates a new random outdoor encounter and saves it to JSONEncounter local field.
     */
    private void createNewOutdoorEncounter() throws JSONException {
        JsonAssetFileReader jsonAssetFileReader = new JsonAssetFileReader(context);
        // get a random encounter from jsonFileReader
        try {
            JSONEncounter = jsonAssetFileReader.getRandomOutdoorEncounter(location, location_type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String type = JSONEncounter.getString("type");
        setEncounterType(type);
    }

    private void createNewBreakEncounter() throws JSONException {
        JsonAssetFileReader jsonAssetFileReader = new JsonAssetFileReader(context);
        // get a random encounter from jsonFileReader
        try {
            JSONEncounter = jsonAssetFileReader.getRandomBreakEncounter();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String type = JSONEncounter.getString("type");
        setEncounterType(type);
    }

    /**
     * Creates a random_benefit_encounter with set JSON data.
     */
    private void createRewardEncounter() throws JSONException {
        // create and set the JSON data and update the new encounter type to start the encounter
        RandomBenefitModel rbModel = new RandomBenefitModel();
        String[] dialogue = {"1", "2", "You have reached the end of the dungeon. Claim your prize"};
        JSONEncounter = rbModel.createRandomBenefitEncounter(dialogue);
        setEncounterType(Outdoor.RANDOM_BENEFIT_TYPE);
    }

    /**
     * Continue with the multi dungeon encounters, finding a random multi dungeon encounter to enter.
     */
    public void createNewMultiDungeonEncounter() throws JSONException {
        if (mainGameModel.isDungeonOver(characterVM)) {
            // set the state to being outdoors
            characterVM.setStateToOutdoor();
            // if the dungeon counter reaches 0, then enter a reward state and leave the dungeon
            createRewardEncounter();
        } else {
            // otherwise, continue with multi dungeon encounters
            JsonAssetFileReader jsonAssetFileReader = new JsonAssetFileReader(context);
            // get a random encounter from jsonFileReader for multi dungeon
            try {
                JSONEncounter = jsonAssetFileReader.getRandomMultiDungeonEncounter();
            } catch (Exception e) {
                e.printStackTrace();
            }
            String type = JSONEncounter.getString(TYPE);
            setEncounterType(type);
        }
    }

    /**
     * Creates a new random dungeon encounter, finding a random combat dungeon encounter to enter.
     */
    private void createNewCombatDungeonEncounter() throws JSONException {
        if (mainGameModel.isDungeonOver(characterVM)) {
            // set the state to being outdoors
            characterVM.setStateToOutdoor();
            // if the dungeon counter reaches 0, then enter a reward state and leave the dungeon
            createRewardEncounter();
        } else {
            // otherwise, continue with multi dungeon encounters
            if (characterVM.getDungeonCounter() == characterVM.getDungeonLength()/2) {
                // if halfway through the dungeon, then create a shop encounter
                ShopModel shopModel = new ShopModel();
                String[] dialogue = {"A shop, wow."};
                JSONEncounter = shopModel.createShopEncounter(dialogue);
            } else {
                // otherwise, create a normal random combat encounter.
                JsonAssetFileReader jsonAssetFileReader = new JsonAssetFileReader(context);
                // get a random encounter from jsonFileReader for multi dungeon
                try {
                    JSONEncounter = jsonAssetFileReader.getRandomCombatDungeonEncounter();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // start the encounter given its type
            String type = JSONEncounter.getString(TYPE);
            setEncounterType(type);
        }
    }

    /**
     * Helper to apply necessary actions after an encounter ends, including:
     *  applying and decrementing dot effects applied to character
     *  decrementing special effects applied to character
     *  decrementing stat and tempExtra changes applied to character
     *  setting the savedEncounter to null
     */
    private void applyAfterEncounterActions() {
        characterVM.applyDots();
        savedEncounter = null;
        // todo: decrement stat and bar durations
        //characterVM.decrementStatChangeDuration();
        //characterVM.decrementTempExtraDuration();
        characterVM.decrSpecialDuration();
        classSpecificCheck();
    }

    /**
     * Run a check for any class specific checks that occur between encounters.
     */
    private void classSpecificCheck() {
    }


    @Bindable
    public int getGameFragmentVisible() {
        return gameFragmentVisible;
    }
    public void setGameState(int gameFragmentVisible) {
        this.gameFragmentVisible = gameFragmentVisible;
        notifyPropertyChanged(BR.gameFragmentVisible);
    }

    @Bindable
    public int getCharacterFragmentVisible() {
        return characterFragmentVisible;
    }
    public void setCharacterState(int characterFragmentVisible) {
        this.characterFragmentVisible = characterFragmentVisible;
        notifyPropertyChanged(BR.characterFragmentVisible);
    }

    // makes the game fragment the visible one
    public void gotoGameFragment() {
        if (gameFragmentVisible != View.VISIBLE) {
            setCharacterState(View.GONE);
            setGameState(View.VISIBLE);
        }
    }

    // makes the character fragment the visible one
    public void gotoCharacterFragment() {
        if (characterFragmentVisible != View.VISIBLE) {
            setCharacterState(View.VISIBLE);
            setGameState(View.GONE);
        }
    }

    public JSONObject getJSONEncounter() {
        return JSONEncounter;
    }

    public int getEncounterLayout() {
        return encounterLayout;
    }

    public JSONObject getSavedEncounter() {
        return savedEncounter;
    }

}
