package com.habbybolan.textadventure.viewmodel.encounters;

import android.content.Context;
import android.os.Handler;
import android.view.View;

import androidx.databinding.ObservableField;

import com.habbybolan.textadventure.model.characterentity.CharacterEntity;
import com.habbybolan.textadventure.model.characterentity.Enemy;
import com.habbybolan.textadventure.model.dialogue.CombatActionDialogue;
import com.habbybolan.textadventure.model.dialogue.Dialogue;
import com.habbybolan.textadventure.model.dialogue.DialogueType;
import com.habbybolan.textadventure.model.encounter.CombatModel;
import com.habbybolan.textadventure.model.inventory.Ability;
import com.habbybolan.textadventure.model.inventory.Inventory;
import com.habbybolan.textadventure.model.inventory.Item;
import com.habbybolan.textadventure.model.inventory.weapon.Weapon;
import com.habbybolan.textadventure.viewmodel.CharacterViewModel;
import com.habbybolan.textadventure.viewmodel.MainGameViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
/*
View model to hold data of the combat encounter
 */
public class CombatViewModel extends EncounterViewModel {
    private MainGameViewModel mainGameVM;
    private CharacterViewModel characterVM;
    private JSONObject encounter;
    private Context context;
    private CombatModel combatModel;

    public static final int firstState = 1;
    public static final int secondState = 2;
    public static final int thirdState = 3;
    public static final int fourthState = 4;
    public static final int fifthState = 5;
    public static final int sixthState = 6;
    public static final int seventhState = 7;


    private Inventory inventoryToRetrieve = null;

    private int weaponListVisibility = View.VISIBLE;
    private int abilityListVisibility = View.GONE;
    private int itemListVisibility = View.GONE;

    // if not empty, then holds the current combat ordering - order of who to attack next based
    //      on the stat speed for player character and enemies
    private ArrayList<CharacterEntity> combatOrderCurr = new ArrayList<>();
    private ArrayList<CharacterEntity> combatOrderNext = new ArrayList<>();
    private ArrayList<CharacterEntity> combatOrderLast = new ArrayList<>();


    private ArrayList<Enemy> enemies = new ArrayList<>();

    private Inventory selectedAction;

    // combat View Model constructor
    public CombatViewModel(JSONObject encounter, Context context) throws JSONException {
        setDialogueRemainingInDialogueState(encounter);
        mainGameVM = MainGameViewModel.getInstance();
        characterVM = CharacterViewModel.getInstance();
        this.encounter = encounter;
        this.context = context;
        combatModel = new CombatModel();
    }

    // attempt to escape the combat encounter
        // if successful, goto end state and display text you escaped
    public void attemptRun() {
        int speed = characterVM.getSpeed();
        if (combatModel.runSpeedCheck(speed)) {
            // speed check successful
            String failText = "You successfully escaped the encounter.";
            setNewDialogue(new Dialogue(failText));
            // goto end state
            gotoEndState();
        } else {
            // speed check not successful, enter combat
            incrementStateIndex();
        }
    }

    // save the selected inventory object to be used for attacking an enemy
    public void setSelectedInventoryAction(Inventory selectedAction) {
        this.selectedAction = selectedAction;
    }

    // set selectedAction to null if the selected action associated to the inventory object is removed
    public void checkIfRemovedInventoryIsSelected(Inventory removed) {
        selectedAction = removed.equals(selectedAction) ? null : selectedAction;
    }

    // creates the enemies and sets up the data for entering combat
    public void createCombat() throws JSONException, ExecutionException, InterruptedException {
        // create the enemies
        JSONObject fightObject = encounter.getJSONObject("fight");
        JSONArray typeArray = fightObject.getJSONArray("type");
        JSONArray difficultyArray = fightObject.getJSONArray("difficulty");
        for (int i = 0; i < typeArray.length(); i++) {
            enemies.add(new Enemy(characterVM.getCharacter().getNumStatPoints(), typeArray.getString(i), difficultyArray.getInt(i), context));
        }
        createCombatOrderLists();
    }

    // pause a separate thread to create a pause between enemy actions
    public void tempPause() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 2000);
    }

    public ObservableField<CombatActionDialogue> newCombatActionDialogue = new ObservableField<>();
    public ObservableField<CombatActionDialogue> getNewCombatActionDialogue() {
        return newCombatActionDialogue;
    }
    public void setNewCombatActionDialogue(CombatActionDialogue dialogue) {
        newCombatActionDialogue.set(dialogue);
    }
    public CombatActionDialogue getNewCombatActionDialogueValue() {
        CombatActionDialogue dialogue = getNewCombatActionDialogue().get();
        if (dialogue != null) return dialogue;
        throw new NullPointerException();
    }


    // ***COMBAT ORDERING***

    // set up the initial combat ordering and sort characterEntities by speed stat
    // set up the layouts
    public void createCombatOrderLists() {
        combatOrderCurr.add(characterVM.getCharacter());
        combatOrderCurr.addAll(enemies);
        // order the curr combat list
        combatModel.sortCombatOrdering(0, combatOrderCurr);
        combatOrderNext.addAll(combatOrderCurr);
    }

    // go directly to the end state
    public void gotoEndState() {
        stateIndex.set(seventhState);
    }

    @Override
    public void saveEncounter(ArrayList<DialogueType> dialogueList) {
        // todo:
    }

    @Override
    public void setSavedData() throws JSONException, ExecutionException, InterruptedException {
        // todo:
    }

    // apply the character action to a CharacterEntity that was chosen
    public void applyCharacterAction(CharacterEntity target) {
        String attackerString = "you";
        int amount = 0;
        String action = selectedAction.getName();
        // apply the selection action
        switch (selectedAction.getType()) {
            case Inventory.TYPE_ABILITY:
                applyAbilityCharacterAction((Ability) selectedAction, target);
                break;
            case Inventory.TYPE_WEAPON:
                applyWeaponCharacterAction((Weapon) selectedAction, target);
                break;
            case Inventory.TYPE_ITEM:
                applyItemCharacterAction((Item) selectedAction, target);
                break;
        }

        // add the combat dialogue to the RecyclerView
        if (target.getIsCharacter()) {
            setNewCombatActionDialogue(new CombatActionDialogue(attackerString, "yourself", action, amount));
        } else {
            Enemy enemy = (Enemy) target;
            setNewCombatActionDialogue(new CombatActionDialogue(attackerString, enemy.getType(), action, amount));
        }
        // un-select the selected action
        selectedAction = null;
        nextTurn();
    }

    // apply the character action selected if valid, otherwise return false
    public boolean characterAction(CharacterEntity entity) {
        if (isSelectedAction()) {
            // cannot use a consumable item on an enemy, only action on self
            if (isValidItemAction(entity)) {
                applyCharacterAction(entity);
                return true;
            }
        }
        return false;
    }

    // returns an error message for when action not selected or an action is invalid on a certain target
    public String getActionError(CharacterEntity entity) {
        if (!isSelectedAction())
            return "Select an action";
        else if (!isValidItemAction(entity))
            return "Action not possible";
        // method should not have been called if no problem exists
        else throw new IllegalStateException();
    }

    // helper for applyCharacterAction to use selected ability action
    private void applyAbilityCharacterAction(Ability ability, CharacterEntity target) {

        CharacterEntity attacker = characterVM.getCharacter();
        if (target.getIsCharacter()) characterVM.applyAbility(ability, attacker);
        else {
            Enemy enemy = (Enemy) target;
            enemy.applyAbility(ability, attacker);
        }
    }
    // helper for applyCharacterAction to use selected weapon attack or special attack action
    private void applyWeaponCharacterAction(Weapon weapon, CharacterEntity target) {
        CharacterEntity attacker = characterVM.getCharacter();
        // todo:
    }
    // helper for applyCharacterAction to use selected Item action
    private void applyItemCharacterAction(Item item, CharacterEntity target) {
        CharacterEntity attacker = characterVM.getCharacter();
        if (target.getIsCharacter()) {
            if (item.getIsConsumable()) characterVM.consumeItem(item);
            else characterVM.applyAbility(item.getAbility(), attacker);
        } else {
            Enemy enemy = (Enemy) target;
            enemy.applyAbility(item.getAbility(), attacker);
        }
    }

    // returns false if the item doesn't have an ability associated with it and it's being used on an enemy
    public boolean isValidItemAction(CharacterEntity target) {
        if (selectedAction.getType().equals(Inventory.TYPE_ITEM)) {
            Item item = (Item) selectedAction;
            // can't use an item on an enemy that has no ability
            return target.getIsCharacter() || item.getAbility() != null;
        }
        return true;
    }

    // create and apply the enemy action
    public void applyEnemyAction() {
        int amount = 0;
        String action = "action";
        Enemy enemy = (Enemy) combatOrderCurr.get(0);
        String attacker = enemy.getType();
        setNewCombatActionDialogue(new CombatActionDialogue(attacker, "somebody", action, amount));
        nextTurn();
    }

    // sets the first turn, calling the state that corresponds to it
    public void setFirstTurn() {
        if (combatOrderCurr.get(0).getIsCharacter()) {
            // state 4 corresponds to character turn
            stateIndex.set(fourthState);
        } else {
            // state 5 corresponds to enemy turn
            stateIndex.set(fifthState);
        }
    }

    public boolean isEnemyTurn() {
        return combatOrderCurr.get(0).getIsCharacter();
    }

    // moves the entity at front of combatOrderCurr list to back of combatOrderLast list
        // applies all necessary effects on entity, checks if combat still going, and calls the next UI state
    public void nextTurn() {
        combatOrderCurr.get(0).applyDots();
        // check if still in combat before action
        if (!isCombatInProgress()) {
            stateIndex.set(sixthState);
        } else {
            combatModel.moveEntityToBackOfCombatOrder(combatOrderCurr, combatOrderNext, combatOrderLast);
            if (combatOrderCurr.get(0).getIsCharacter()) {
                // state 4 corresponds to character turn
                stateIndex.set(fourthState);
            } else {
                // state 5 corresponds to enemy turn
                stateIndex.set(fifthState);
            }
        }
    }

    // returns true if all of the enemies are alive, otherwise return false
    private boolean isCombatInProgress() {
        for (Enemy enemy : enemies) {
            if (!enemy.getIsAlive()) return false;
        }
        return true;
    }

    public ArrayList<CharacterEntity> getCombatOrderCurr() {
        return combatOrderCurr;
    }
    public ArrayList<CharacterEntity> getCombatOrderNext() {
        return combatOrderNext;
    }
    public ArrayList<CharacterEntity> getCombatOrderLast() {
        return combatOrderLast;
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    // return true if an action is selected
    public boolean isSelectedAction() {
        return selectedAction != null;
    }
}
