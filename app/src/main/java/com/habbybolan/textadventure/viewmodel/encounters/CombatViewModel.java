package com.habbybolan.textadventure.viewmodel.encounters;

import android.content.Context;
import android.os.Handler;

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
import com.habbybolan.textadventure.model.inventory.weapon.Weapon;
import com.habbybolan.textadventure.repository.SaveDataLocally;
import com.habbybolan.textadventure.viewmodel.MainGameViewModel;
import com.habbybolan.textadventure.viewmodel.characterEntityViewModels.CharacterViewModel;
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


    // inventory reward able to retrieve at the end of the encounter
    private Inventory inventoryToRetrieve = null;

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
        // ID associated with the enemy
        int ID = 2;
        for (int i = 0; i < typeArray.length(); i++) {
            Enemy enemy = new Enemy(characterVM.getCharacter().getNumStatPoints(), typeArray.getString(i), difficultyArray.getInt(i), ID, context);
            enemies.add(new EnemyViewModel(enemy));
            ID++;
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

    private ObservableField<CombatActionDialogue> newCombatActionDialogue = new ObservableField<>();
    public ObservableField<CombatActionDialogue> getNewCombatActionDialogue() {
        return newCombatActionDialogue;
    }
    private void setNewCombatActionDialogue(CombatActionDialogue dialogue) {
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
        stateIndex.set(sixthState);
    }

    /**
     * saves the encounter into a JSON String. Saved the encounter type, encounter JSON, state, dialogue, inventory reward, and enemies
     * @param dialogueList  All dialogue objects currently displayed in the dialogue recyclerViewer
     */
    @Override
    public void saveEncounter(ArrayList<DialogueType> dialogueList) {
        SaveDataLocally save = new SaveDataLocally(context);
        JSONObject encounterData = new JSONObject();
        try {
            encounterData.put(ENCOUNTER_TYPE, TYPE_COMBAT);
            encounterData.put(ENCOUNTER, encounter);
            encounterData.put(STATE, stateIndex.get());
            if (getFirstStateJSON() != null) encounterData.put(DIALOGUE_REMAINING, getFirstStateJSON());
            // convert the inventory to JSON and store if one exists
            if (inventoryToRetrieve != null) encounterData.put(INVENTORY, inventoryToRetrieve.serializeToJSON());
            // store all DialogueTypes converted to JSON
            JSONArray JSONDialogue = new JSONArray();
            for (DialogueType dialogueType : dialogueList) {
                JSONObject dialogueObject = dialogueType.toJSON();
                JSONDialogue.put(dialogueObject);
            }
            encounterData.put(DIALOGUE_ADDED, JSONDialogue);
            JSONArray JSONEnemies = new JSONArray();
            for (EnemyViewModel enemyVM : enemies) {
                JSONEnemies.put(enemyVM.getEnemy().serializeToJSON());
            }
            encounterData.put(ENEMIES, JSONEnemies);
            JSONArray combatOrderArray = saveCombatOrdering();
            encounterData.put(COMBAT_ORDER, combatOrderArray);

            save.saveEncounter(encounterData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * creates an ArrayList of 3 ArrayLists that hold the data for the combat ordering, related to combatOrderCurr,
     * combatOrderNext, combatOrderLast. Each enemy is set with an ID 2+ incrementally, and the player character
     * is represented with ID 1.
     * @return  the ArrayList holding the converted JSON data of the combat ordering lists
     */
    private JSONArray saveCombatOrdering() {
        JSONArray jsonArray = new JSONArray();
        storeEntityFromCombatOrdering(jsonArray, combatOrderCurr);
        storeEntityFromCombatOrdering(jsonArray, combatOrderNext);
        storeEntityFromCombatOrdering(jsonArray, combatOrderLast);
        return jsonArray;
    }

    /**
     * Helper for saveCombatOrdering that Creates a JSONArray of an individual combatOrderingList and stores into jsonArray
     * @param jsonArray     The array to store the newly created JSONArray for the combatOrderList
     */
    private void storeEntityFromCombatOrdering(JSONArray jsonArray, ArrayList<CharacterEntity> combatOrderList) {
        JSONArray array = new JSONArray();
        for (CharacterEntity entity : combatOrderList) {
            // store the ID of the CharacterEntity
            array.put(entity.getID());
        }
        jsonArray.put(array);
    }

    /**
     * set up the saved dialogue, inventory reward if one exists, and the combat ordering
     * @throws JSONException    problem reading saved JSON
     */
    @Override
    public void setSavedData() throws JSONException {
        if (getIsSaved()) {
            setDialogueList(mainGameVM);
            setSavedInventory();
            retrieveEnemies();
            retrieveCombatOrdering();
        }
    }

    /**
     * Called if there is a saved game. Recovers the saved Enemy objects inside JSON and places them inside enemies ArrayList.
     * Must be called before retrieveCombatOrdering as that method uses the retrieved enemies to order them into the combat order arrays
     */
    private void retrieveEnemies() throws JSONException {
        JSONArray enemiesArray = mainGameVM.getSavedEncounter().getJSONArray(ENEMIES);
        for (int i = 0; i < enemiesArray.length(); i++) {
            Enemy enemy = new Enemy(enemiesArray.getString(i));
            enemies.add(new EnemyViewModel(enemy));
        }
    }

    /**
     * Called if there is a saved game. Retrieves the combat ordering from the JSON file, filling in the arrays of CharacterEntities
     * combatOrderCurr, combatOrderNext and combatOrderLast that deals with the combat Ordering.
     * Only call this after retrieveEnemies.
     */
    private void retrieveCombatOrdering() throws JSONException {
        if (enemies.isEmpty()) throw new IllegalStateException("Call retrieveEnemies() before this");
        JSONArray combatOrderArrays = mainGameVM.getSavedEncounter().getJSONArray(COMBAT_ORDER);
        JSONArray combatArrayCurr = combatOrderArrays.getJSONArray(0);
        for (int i = 0; i < combatArrayCurr.length(); i++) {
            addToCombatArray(combatOrderCurr, combatArrayCurr.getInt(i));
        }
        JSONArray combatArrayNext = combatOrderArrays.getJSONArray(1);
        for (int i = 0; i < combatArrayNext.length(); i++) {
            addToCombatArray(combatOrderNext, combatArrayNext.getInt(i));
        }
        JSONArray combatArrayLast = combatOrderArrays.getJSONArray(2);
        for (int i = 0; i < combatArrayLast.length(); i++) {
            addToCombatArray(combatOrderLast, combatArrayLast.getInt(i));
        }
    }

    /**
     * Find the correct CharacterEntity given the ID to add to the end of the combatList.
     * @param combatList    One of the three combatOrderLists to put entity in. combatOrderCurr, combatOrderNext or combatOrderLast
     * @param ID            The ID associated with the CharacterEntity to put into the
     */
    private void addToCombatArray(ArrayList<CharacterEntity> combatList, int ID) {
        if (ID == 1)
            // if the ID is 1, then it must be player Character
            combatList.add(characterVM.getCharacter());
        else {
            // otherwise, find the enemy from the enemies arrayList with the respective ID
            for (EnemyViewModel enemyVM : enemies) {
                if (enemyVM.getEnemy().getID() == ID)
                    combatList.add(enemyVM.getEnemy());
            }
        }
    }


    // get the saved Inventory Object from the json
    private void setSavedInventory() throws JSONException {
        // check if an inventory value has been stored
        if (mainGameVM.getSavedEncounter().has(INVENTORY)) {
            JSONObject inventory = mainGameVM.getSavedEncounter().getJSONObject(INVENTORY);

            if (inventory.getString("type").equals("ability")) {
                // saved Inventory object is an Ability
                inventoryToRetrieve = new Ability(inventory.toString());

            } else if (inventory.getString("type").equals("item")) {
                // saved Inventory object is an Item
                inventoryToRetrieve = new Item(inventory.toString());

            } else {
                // otherwise, saved Inventory object is a Weapon
                inventoryToRetrieve = new Weapon(inventory.toString());
            }
        }
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
            // if target is not alive
            if (!entity.getIsAlive()) return false;
            // cannot use a consumable item on an enemy, only action on self
            if (isValidAction(entity)) {
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
        else if (!entity.getIsAlive())
            return "selected target is dead";
        else if (!isValidAction(entity))
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
    private boolean isValidAction(CharacterEntity target) {
        if (selectedAction.getType().equals(Inventory.TYPE_ITEM)) {
            Item item = (Item) selectedAction;
            // can't use an item on an enemy that has no ability
            return target.getIsCharacter() || item.getAbility() != null;
        }
        return true;
    }

    // Enemy


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
        int aliveEnemyTarget = random.nextInt(numEnemyTargets); // *** IllegalArgumentException
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
        // returns true if still in combat after applying effects
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
        if (isCombatInProgress()) {
            // in combat, find next characterEntity's turn
            do {
                // loop until you find a characterEntity in the combat ordering that is still alive
                combatModel.moveEntityToBackOfCombatOrder(combatOrderCurr, combatOrderNext, combatOrderLast);
                beforeAction(combatOrderCurr.get(0));
            } while (!combatOrderCurr.get(0).getIsAlive());

            // run this only if the next character is alive, otherwise the combat encounter is over
            if (isCombatInProgress()) {
                if (isCharacterTurn()) {
                    // state 4 corresponds to character turn
                    stateIndex.set(fourthState);
                } else {
                    // state 5 corresponds to enemy turn
                    stateIndex.set(fifthState);
                }
            }
        }
    }

    // returns true if all of the enemies are alive, otherwise return false
        // calls the end of combat state
    private boolean isCombatInProgress() {
        for (EnemyViewModel enemyVM : enemies) {
            if (enemyVM.getEnemy().getIsAlive()) {
                return true;
            }
        }
        setReward();
        stateIndex.set(sixthState);
        return false;
    }

    /**
     *  creates reward based on combat difficult, and creates button for Inventory reward
     */
    private void setReward() {
        getExpReward();
        getGoldReward();
        // todo: inventory reward?
        //Inventory inventoryReward = combatVM.getInventoryReward(getContext());
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

    // serialized an inventory object into a JSON String
    public String serializeInventory(Inventory object) {
        try {
            return object.serializeToJSON().toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException();
    }


    // rewards

    // exp reward
    private void getExpReward() {
        int expReward = combatModel.getExpReward(encounter);
        characterVM.addExp(expReward);
    }
    // gold reward
    private void getGoldReward() {
        int goldReward = combatModel.getGoldReward(encounter);
        characterVM.goldChange(goldReward);
    }
    // Weapon/Ability/Item reward
    public Inventory getInventoryReward(Context context) {
        Inventory inventoryRewards = combatModel.getInventoryReward(encounter, context);
        // todo: apply the Inventory rewards to character
        return inventoryRewards;
    }


}
