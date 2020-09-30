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
import com.habbybolan.textadventure.model.inventory.Action;
import com.habbybolan.textadventure.model.inventory.Inventory;
import com.habbybolan.textadventure.model.inventory.Item;
import com.habbybolan.textadventure.model.inventory.weapon.Attack;
import com.habbybolan.textadventure.model.inventory.weapon.SpecialAttack;
import com.habbybolan.textadventure.viewmodel.characterEntityViewModels.CharacterViewModel;
import com.habbybolan.textadventure.viewmodel.MainGameViewModel;
import com.habbybolan.textadventure.viewmodel.characterEntityViewModels.EnemyViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;
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


    private ArrayList<EnemyViewModel> enemies = new ArrayList<>();

    private Action selectedAction;

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
    public void setSelectedInventoryAction(Action selectedAction) {
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
            Enemy enemy = new Enemy(characterVM.getCharacter().getNumStatPoints(), typeArray.getString(i), difficultyArray.getInt(i), context);
            enemies.add(new EnemyViewModel(enemy));
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
        for (EnemyViewModel enemyVM : enemies) {
            combatOrderCurr.add(enemyVM.getEnemy());
        }
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

    // helper for applying the character action to a CharacterEntity that was chosen
    private void applyCharacterAction(CharacterEntity target) {
        String attackerString = "you";
        String action = selectedAction.getName();
        // apply the selection action
        switch (selectedAction.getType()) {
            case Inventory.TYPE_ABILITY:
                applyAbilityAction((Ability) selectedAction, target, characterVM.getCharacter());
                break;
            case Inventory.TYPE_ATTACK:
                applyAttackAction((Attack) selectedAction, target, characterVM.getCharacter());
                break;
            case Inventory.TYPE_S_ATTACK:
                applySpecialAttackAction((SpecialAttack) selectedAction, target, characterVM.getCharacter());
                break;
            case Inventory.TYPE_ITEM:
                applyItemCharacterAction((Item) selectedAction, target);
                break;
        }

        // add the combat dialogue to the RecyclerView
        if (target.getIsCharacter()) {
            setNewCombatActionDialogue(new CombatActionDialogue(attackerString, "yourself", action));
        } else {
            Enemy enemy = (Enemy) target;
            setNewCombatActionDialogue(new CombatActionDialogue(attackerString, enemy.getType(), action));
        }
        // un-select the selected action
        selectedAction = null;
        nextTurn();
    }

    // apply the character action selected if valid, otherwise return false
    public boolean characterAction(CharacterEntity entity) {
        if (isSelectedAction()) {
            // if the action is not out of cooldown, don't apply action
            if (!selectedAction.isActionReady()) return false;
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
        else if (!selectedAction.isActionReady()) {
            return "Action in cooldown";
        }
        // method should not have been called if no problem exists
        else throw new IllegalStateException();
    }

    // helper for applyCharacterAction to use selected ability action
    private void applyAbilityAction(Ability ability, CharacterEntity target, CharacterEntity attacker) {
        if (target.getIsCharacter()) characterVM.applyAbility(ability, attacker);
        else {
            Enemy enemy = (Enemy) target;
            enemy.applyAbility(ability, attacker);
        }
        ability.setActionUsed();
    }

    // helper for applyCharacterAction to use selected attack
    private void applyAttackAction(Attack attack, CharacterEntity target, CharacterEntity attacker) {
        if (target.getIsCharacter()) characterVM.applyAttack(attack, attacker);
        else {
            Enemy enemy = (Enemy) target;
            enemy.applyAttack(attack, attacker);
        }
    }
    // helper for applyCharacterAction to use selected special attack
    private void applySpecialAttackAction(SpecialAttack specialAttack, CharacterEntity target, CharacterEntity attacker) {
        if (target.getIsCharacter()) characterVM.applySpecialAttack(specialAttack, attacker);
        else {
            Enemy enemy = (Enemy) target;
            enemy.applySpecialAttack(specialAttack, attacker);
        }
        specialAttack.setActionUsed();
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
        item.setActionUsed();
    }

    // returns false if the item doesn't have an ability associated with it and it's being used on an enemy
    private boolean isValidItemAction(CharacterEntity target) {
        if (selectedAction.getType().equals(Inventory.TYPE_ITEM)) {
            Item item = (Item) selectedAction;
            // can't use an item on an enemy that has no ability
            return target.getIsCharacter() || item.getAbility() != null;
        }
        return true;
    }

    // create and apply the enemy action
    public void enemyAction() {
        Enemy enemy = (Enemy) combatOrderCurr.get(0);
        randomEnemyAction(enemy);
        nextTurn();
    }
    // apply a random enemy action
    private void randomEnemyAction(Enemy enemy) {
        CharacterEntity target = getRandomTarget();
        Inventory action = enemy.getRandomAction();
        switch (action.getType()) {
            case Inventory.TYPE_ABILITY:
                // action is ability
                applyAbilityAction((Ability) action, target, enemy);
                break;
            case Inventory.TYPE_ATTACK:
                applyAttackAction((Attack) action, target, enemy);
                // action is attack
                break;
            case Inventory.TYPE_S_ATTACK:
                applySpecialAttackAction((SpecialAttack) action, target, enemy);
                // action is special attack
                break;
            default:
                throw new IllegalStateException();
        }
        // dialogue showing the enemy action
        String attackerString = enemy.getType();
        String targetString;
        if (target.getIsCharacter()) targetString = "you";
        else {
            Enemy enemyTarget = (Enemy) target;
            targetString = enemyTarget.getType();
        }
        setNewCombatActionDialogue(new CombatActionDialogue(attackerString, targetString, action.getName()));
    }
    // gets a random target to attack
    private CharacterEntity getRandomTarget() {
        CharacterEntity target;
        Random random = new Random();
        int targetType = random.nextInt(2);
        if (targetType == 0) target = characterVM.getCharacter();
        else target = getRandomEnemyTarget();
        return target;
    }
    // returns a random valid enemy target
    private Enemy getRandomEnemyTarget() {
        int numEnemyTargets = getNumEnemyTargets();
        Random random = new Random();
        // the nth alive enemy
        int aliveEnemyTarget = random.nextInt(numEnemyTargets);
        int index = 0;
        for (EnemyViewModel enemyVM : enemies) {
            if (enemyVM.getEnemy().getIsAlive()) {
                // if the enemy is the correct nth alive enemy
                if (index == aliveEnemyTarget) return enemyVM.getEnemy();
                else index++;
            }
        }
        // shouldn't reach here
        throw new IllegalStateException();
    }
    // returns the number of alive enemies and character
    private int getNumEnemyTargets() {
        int numTargets = 0;
        for (EnemyViewModel enemyVM : enemies) {
            if (enemyVM.getEnemy().getIsAlive()) numTargets++;
        }
        return numTargets;
    }

    // helper for applying things at the beginning of entity's turn before action performed
    private void beforeAction(CharacterEntity entity) {
        entity.decrCooldowns();
        if (entity.getIsCharacter()) {
            characterVM.applyDots();
            characterVM.decrSpecialDuration();
            characterVM.decrementTempStatDuration();
            characterVM.decrementTempExtraDuration();
        } else {
            entity.applyDots();
            entity.decrSpecialDuration();
            entity.decrementTempStatDuration();
            entity.decrementTempExtraDuration();
        }

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

    // return true if the next turn is an Enemy's turn
    public boolean isCharacterTurn() {
        return combatOrderCurr.get(0).getIsCharacter();
    }

    // moves the entity at front of combatOrderCurr list to back of combatOrderLast list
        // applies all necessary effects on entity, checks if combat still going, and calls the next UI state
    private void nextTurn() {
        // check if still in combat before action
        if (!isCombatInProgress()) {
            stateIndex.set(sixthState);
        } else {
            // in combat, find next characterEntity's turn
            do {
                // loop until you find a characterEntity in the combat ordering that is still alive
                combatModel.moveEntityToBackOfCombatOrder(combatOrderCurr, combatOrderNext, combatOrderLast);
                beforeAction(combatOrderCurr.get(0));
            } while (!combatOrderCurr.get(0).getIsAlive());

            if (isCharacterTurn()) {
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
        for (EnemyViewModel enemyVM : enemies) {
            if (!enemyVM.getEnemy().getIsAlive()) return false;
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

    public ArrayList<EnemyViewModel> getEnemies() {
        return enemies;
    }

    // return true if an action is selected
    public boolean isSelectedAction() {
        return selectedAction != null;
    }
}
