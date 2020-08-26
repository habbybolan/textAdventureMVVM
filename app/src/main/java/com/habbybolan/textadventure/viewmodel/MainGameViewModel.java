package com.habbybolan.textadventure.viewmodel;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;

import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.habbybolan.textadventure.BR;
import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.repository.JsonAssetFileReader;

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
    private Context context;
    private int turnCounter; // keeps track of the number of encounters/distance travelled

    private int gameFragmentVisible = View.VISIBLE; // visibility of the game fragment
    private int characterFragmentVisible = View.GONE; // visibility of the character fragment

    private static final String outdoorJSONFilename = "outdoor_encounters";
    public JSONObject JSONEncounter;

    // keeps track of the location
    private int location = FOREST;
    private String location_type = FOREST_TYPE;
    private Typeface font;

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
    final public static int BENEFIT = 6; // a random, guaranteed temporary/permanent benefit - stat/health increase, item, weapon
    final public static String BENEFIT_TYPE = "benefit";
    final public static int SMALL_BENEFIT = 7; // a random reward of either gold/Weapon/Item/Ability
    final public static String SMALL_BENEFIT_TYPE = "small_benefit";
    final public static int QUEST = 8; // temporary leave the "area" to finish a specific task for a reward
    final public static String QUEST_TYPE = "quest";

    public MainGameViewModel(Context context) {
        this.context = context;
        font = ResourcesCompat.getFont(context, R.font.press_start_2p);
        gotoNextEncounter();
    }

    // observer for when the encounter changes, starting a new one
    private ObservableField<String> encounterType = new ObservableField<>();
    @Bindable
    public ObservableField<String> getEncounterType() {
        return encounterType;
    }
    public void setEncounterType(String encounterType) {
        this.encounterType.set(encounterType);
        notifyPropertyChanged(BR.encounterType);
    }

    // called by outside this viewModel to start a new encounter
    public void gotoNextEncounter() {
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

    // set up the encounter given the JSON string
    public void setEncounter(String type) throws JSONException {
        //String type = encounter.getString("type");
        switch (type) {
            /*case DUNGEON_TYPE:
                isDungeon = true;
                DungeonEncounter dungeonEncounter = new DungeonEncounter(getContext(), character, damage);
                dungeonEncounter.setInitialChoice(encounter);
                break;
            case MULTI_LEVEL_TYPE:
                isMultiLevel = true;
                MultiLevelEncounter multiLevelEncounter = new MultiLevelEncounter(getContext(), character, damage, view, this, model);
                multiLevelEncounter.setInitialMultiLevel(encounter);
                break;
            case CHOICE_TYPE:
                ChoiceEncounter choiceEncounter = new ChoiceEncounter(getContext(), character, damage, view, this, model);
                choiceEncounter.setInitialChoice(encounter);
                break;
            case COMBAT_TYPE:
                CombatEncounter combatObject = new CombatEncounter(getContext(), character, damage, view, this);
                combatObject.setInitialCombat(encounter, view);
                break;*/
            case TRAP_TYPE:
                encounterLayout = R.layout.fragment_trap;
                break;
            /*case SHOP_TYPE:
                enterLeaveShop(encounter);
                break;
            case BENEFIT_TYPE:
                BenefitEncounter benefitObject = new BenefitEncounter(getContext(), character, model, view, this);
                benefitObject.setInitialBenefit(encounter.getString("dialogue"));
                break;
            case SMALL_BENEFIT_TYPE:
                SmallBenefitEncounter smallBenefitEncounter = new SmallBenefitEncounter(getContext(), character, model, view, this);
                smallBenefitEncounter.setInitialSmallBenefit(encounter.getString("dialogue"));
                break;
            case QUEST_TYPE:
                QuestEncounter questEncounter = new QuestEncounter(getContext(), character, damage, view, this, model);
                questEncounter.setInitialQuest(encounter.getJSONObject("encounter"));
                break;*/
            default: //  shouldn't reach here
                throw new IllegalArgumentException();
        }
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

    public Typeface getFont() {
        return font;
    }

}
