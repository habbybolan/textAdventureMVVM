package com.habbybolan.textadventure.viewmodel;

import android.content.Context;
import android.view.View;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.habbybolan.textadventure.BR;
import com.habbybolan.textadventure.repository.JsonAssetFileReader;
import com.habbybolan.textadventure.repository.SaveDataLocally;
import com.habbybolan.textadventure.viewmodel.encounters.EncounterViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/*
Holds encounter information to deal with flow and creation of encounters
   saves and retrieves encounters
 */
public class MainGameViewModel extends BaseObservable {

    // number of encounters the user has played
    private int encounterNumber = 0;
    private int turnCounter; // keeps track of the number of encounters/distance travelled

    private int gameFragmentVisible = View.VISIBLE; // visibility of the game fragment
    private int characterFragmentVisible = View.GONE; // visibility of the character fragment

    private static final String outdoorJSONFilename = "outdoor_encounters";
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
    final public static int DUNGEON = 0;
    final public static String DUNGEON_TYPE = "dungeon";
    final public static int MULTI_LEVEL = 1; // enter into a multi-level encounter
    final public static String MULTI_LEVEL_TYPE = "multi_level";
    final public static int CHOICE = 2; // a choice to interact or not, leading to a semi-random encounter based on type of choice
    final public static String CHOICE_TYPE = "choice";
    final public static int COMBAT = 3; // enter combat
    final public static String COMBAT_TYPE = "combat";
    final public static int TRAP = 4; // enter a trap - take damage if fail to escape
    final public static String TRAP_TYPE = "trap";
    final public static int SHOP = 5; // a shop that sells an assortment of random items/weapons/abilities
    final public static String SHOP_TYPE = "shop";
    final public static int CHOICE_BENEFIT = 6; // a random, guaranteed temporary/permanent benefit - stat/health increase, item, weapon
    final public static String CHOICE_BENEFIT_TYPE = "choice_benefit";
    final public static int RANDOM_BENEFIT = 7; // a random reward of either gold/Weapon/Item/Ability
    final public static String RANDOM_BENEFIT_TYPE = "random_benefit";
    final public static int QUEST = 8; // temporary leave the "area" to finish a specific task for a reward
    final public static String QUEST_TYPE = "quest";

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
    public void setEncounterType(String encounterType) {
        this.encounterType.set(encounterType);
    }

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
            gotoNextEncounter();
        }
    }

    // called by outside this viewModel to start a new encounter
    public void gotoNextEncounter() {
        characterVM.applyDots();
        savedEncounter = null;
        // todo: decrement stat and bar durations
        //characterVM.decrementStatChangeDuration();
        //characterVM.decrementTempExtraDuration();
        characterVM.decrSpecialDuration();
        try {
            createNewEncounter(context);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // creates a new encounter and saves it to JSONEncounter local field
    public void createNewEncounter(Context context) throws JSONException {
        JsonAssetFileReader jsonAssetFileReader = new JsonAssetFileReader(context, outdoorJSONFilename);
        jsonAssetFileReader.loadJSONFromAssets();

        // get a random encounter from jsonFileReader
        try {
            JSONEncounter = jsonAssetFileReader.getRandomEncounter(location, location_type);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        /*
        String jsonString = JSONEncounter.toString();
        // save the new encounter object to filePrevEncounters local file
        SaveDataLocally save = new SaveDataLocally(context);
        save.saveEncounter(context, jsonString);
        */
        String type = JSONEncounter.getString("type");
        setEncounterType(type);
    }

    @Bindable
    public String getTurnCounter() {
        return String.valueOf(turnCounter);
    }
    public void incrementTurnCounter() {
        turnCounter++;
        notifyPropertyChanged(BR.turnCounter);
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
