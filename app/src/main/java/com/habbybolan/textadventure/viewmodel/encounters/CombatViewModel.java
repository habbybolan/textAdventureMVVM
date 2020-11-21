package com.habbybolan.textadventure.viewmodel.encounters;

import android.app.Application;
import android.content.Context;

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
import com.habbybolan.textadventure.repository.SaveDataLocally;
import com.habbybolan.textadventure.repository.database.DatabaseAdapter;
import com.habbybolan.textadventure.viewmodel.characterEntityViewModels.CharacterEntityViewModel;
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
    private ArrayList<CharacterEntityViewModel> combatOrderCurr = new ArrayList<>();
    private ArrayList<CharacterEntityViewModel> combatOrderNext = new ArrayList<>();
    private ArrayList<CharacterEntityViewModel> combatOrderLast = new ArrayList<>();


    private ArrayList<EnemyViewModel> enemies = new ArrayList<>();

    private Action selectedAction;

    // combat View Model constructor
    public CombatViewModel(Application application) {
        super(application);
        combatModel = new CombatModel();
    }

    /**
     *  Check for escaping the combat encounter.
     */
    public void attemptInCombatRun() {
        if (combatModel.runCheck(characterVM.getCharacter())) {
            // speed check successful
            String failText = "You successfully escaped the encounter.";
            setNewDialogue(new Dialogue(failText));
            // goto end state
            gotoEndState();
        } else {
            // speed check not successful, enter combat
            String failText = "You were unsuccessful with your escape.";
            setNewDialogue(new Dialogue(failText));
            nextTurn();
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
        // ID associated with the enemy
        int ID = 2;
        DatabaseAdapter db = new DatabaseAdapter(application);
        for (int i = 0; i < typeArray.length(); i++) {
            Enemy enemy = db.getEnemy(typeArray.getString(i), 1, characterVM.getCharacter().getNumStatPoints());
            enemy.setID(ID);
            enemies.add(new EnemyViewModel(enemy));
            ID++;
        }
        createCombatOrderLists();
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
        combatOrderCurr.add(characterVM);
        combatOrderCurr.addAll(enemies);
        // order the curr combat list
        combatModel.sortCombatOrdering(0, combatOrderCurr);
        combatOrderNext.addAll(combatOrderCurr);
    }

    // go directly to the end state
    private void gotoEndState() {
        setStateIndex(sixthState);
    }

    /**
     * saves the encounter into a JSON String. Saved the encounter type, encounter JSON, state, dialogue, inventory reward, and enemies
     * @param dialogueList  All dialogue objects currently displayed in the dialogue recyclerViewer
     */
    @Override
    public void saveEncounter(ArrayList<DialogueType> dialogueList) {
        SaveDataLocally save = new SaveDataLocally(application);
        JSONObject encounterData = new JSONObject();
        try {
            encounterData.put(ENCOUNTER_TYPE, TYPE_COMBAT);
            encounterData.put(ENCOUNTER, encounter);
            encounterData.put(STATE, getStateIndexValue());
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
    private void storeEntityFromCombatOrdering(JSONArray jsonArray, ArrayList<CharacterEntityViewModel> combatOrderList) {
        JSONArray array = new JSONArray();
        for (CharacterEntityViewModel entity : combatOrderList) {
            // store the ID of the CharacterEntity
            array.put(entity.getCharacterEntity().getID());
        }
        jsonArray.put(array);
    }

    /**
     * Set up the saved dialogue, inventory reward if one exists, and the combat ordering.
     */
    @Override
    public void setSavedData() {
        try {
            if (getIsSaved()) {
                setDialogueList(mainGameVM);
                inventoryToRetrieve = setSavedInventory();
                retrieveEnemies();
                retrieveCombatOrdering();
            }
        } catch (JSONException e) {
            e.printStackTrace();
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
    private void addToCombatArray(ArrayList<CharacterEntityViewModel> combatList, int ID) {
        if (ID == 1)
            // if the ID is 1, then it must be player Character
            combatList.add(characterVM);
        else {
            // otherwise, find the enemy from the enemies arrayList with the respective ID
            for (EnemyViewModel enemyVM : enemies) {
                if (enemyVM.getEnemy().getID() == ID)
                    combatList.add(enemyVM);
            }
        }
    }

    /**
     * helper for applying the character action to a CharacterEntity that was chosen
     * @param target    The target characterEntity to use the character selectedAction on.
     */
    private void applyCharacterAction(CharacterEntityViewModel target) {
        String attackerString = "you";
        String action = selectedAction.getName();
        // apply the selection action
        switch (selectedAction.getType()) {
            case Inventory.TYPE_ABILITY:
                applyAbilityAction((Ability) selectedAction, target, characterVM);
                break;
            case Inventory.TYPE_ATTACK:
                applyAttackAction((Attack) selectedAction, target, characterVM);
                break;
            case Inventory.TYPE_S_ATTACK:
                applySpecialAttackAction((SpecialAttack) selectedAction, target, characterVM);
                break;
            case Inventory.TYPE_ITEM:
                applyItemCharacterAction((Item) selectedAction, target);
                break;
        }

        // add the combat dialogue to the RecyclerView
        if (target.getCharacterEntity().getIsCharacter()) {
            setNewCombatActionDialogue(new CombatActionDialogue(attackerString, "yourself", action));
        } else {
            Enemy enemy = (Enemy) target.getCharacterEntity();
            setNewCombatActionDialogue(new CombatActionDialogue(attackerString, enemy.getType(), action));
        }
        // un-select the selected action
        selectedAction = null;
        nextTurn();
    }

    /**
     *  apply the character action selected if valid, otherwise return false
     * @param target    The target characterEntity to apply selectedAction on.
     * @return          True if the selectedAction on target is valid, otherwise return false.
     */
    public boolean characterAction(CharacterEntityViewModel target) {
        if (isSelectedAction()) {
            // if the action is not out of cooldown, don't apply action
            if (!selectedAction.isActionReady()) return false;
            // if target is not alive
            if (!target.getCharacterEntity().getIsAlive()) return false;
            // cannot use a consumable item on an enemy, only action on self
            if (isValidAction(target.getCharacterEntity())) {
                applyCharacterAction(target);
                return true;
            }
        }
        return false;
    }

    /**
     *  Returns an error message for when action not selected or an action is invalid on a certain target. Only called if
     *  the action being used is invalid.
     * @param target    The target characterEntity to apply selectedAction on.
     * @return          The error message as a String to be displayed
     */
    public String getActionError(CharacterEntity target) {
        if (!isSelectedAction())
            return "Select an action";
        else if (!target.getIsAlive())
            return "selected target is dead";
        else if (!isValidAction(target))
            return "Action not possible";
        else if (!selectedAction.isActionReady()) {
            return "Action in cooldown";
        }
        // method should not have been called if no problem exists
        else throw new IllegalStateException();
    }

    /**
     *  Helper to use selected ability action on target from attacker.
     * @param ability       The ability action to be used.
     * @param target        The target to use the ability action on.
     * @param attacker      The one using the ability action on the target
     */
    private void applyAbilityAction(Ability ability, CharacterEntityViewModel target, CharacterEntityViewModel attacker) {
        target.applyAbility(ability, attacker.getCharacterEntity());
        ability.setActionUsed();
    }

    /**
     *  Helper to use selected attack action on target from attacker.
     * @param attack       The attack action to be used.
     * @param target       The target to use the attack action on.
     * @param attacker     The one using the attack action on the target
     */
    private void applyAttackAction(Attack attack, CharacterEntityViewModel target, CharacterEntityViewModel attacker) {
        target.applyAttack(attack, attacker.getCharacterEntity());
    }

    /**
     *  Helper to use selected special attack on target from attacker.
     * @param specialAttack     The special attack action to be used.
     * @param target            The target to use the special attack action on.
     * @param attacker          The one using the special attack action on the target
     */
    private void applySpecialAttackAction(SpecialAttack specialAttack, CharacterEntityViewModel target, CharacterEntityViewModel attacker) {
        target.applySpecialAttack(specialAttack, attacker.getCharacterEntity());
        specialAttack.setActionUsed();
    }

    /**
     *  Helper to use selected Item action on target.
     * @param item      The item to be used.
     * @param target    The target for the item to be used on.
     */
    private void applyItemCharacterAction(Item item, CharacterEntityViewModel target) {
        CharacterEntity attacker = characterVM.getCharacter();
        if (target.getIsCharacter()) {
            if (item.getIsConsumable()) characterVM.consumeItem(item);
            else characterVM.applyAbility(item.getAbility(), attacker);
        } else {
            target.applyAbility(item.getAbility(), attacker);
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
        EnemyViewModel enemy = (EnemyViewModel) combatOrderCurr.get(0);
        randomEnemyAction(enemy);
        nextTurn();
    }
    // apply a random enemy action
    private void randomEnemyAction(EnemyViewModel enemy) {
        CharacterEntityViewModel target = getRandomTarget();
        Inventory action = enemy.getEnemy().getRandomAction();
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
        String attackerString = enemy.getEnemy().getType();
        String targetString;
        if (target.getCharacterEntity().getIsCharacter()) targetString = "you";
        else {
            Enemy enemyTarget = (Enemy) target.getCharacterEntity();
            targetString = enemyTarget.getType();
        }
        setNewCombatActionDialogue(new CombatActionDialogue(attackerString, targetString, action.getName()));
    }
    // gets a random target to attack
    private CharacterEntityViewModel getRandomTarget() {
        CharacterEntityViewModel target;
        Random random = new Random();
        int targetType = random.nextInt(2);
        if (targetType == 0) target = characterVM;
        else target = getRandomEnemyTarget();
        return target;
    }
    // returns a random valid enemy target
    private EnemyViewModel getRandomEnemyTarget() {
        int numEnemyTargets = getNumEnemyTargets();
        Random random = new Random();
        // the nth alive enemy
        int aliveEnemyTarget = random.nextInt(numEnemyTargets); // *** IllegalArgumentException
        int index = 0;
        for (EnemyViewModel enemyVM : enemies) {
            if (enemyVM.getEnemy().getIsAlive()) {
                // if the enemy is the correct nth alive enemy
                if (index == aliveEnemyTarget) return enemyVM;
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
    private void beforeAction(CharacterEntityViewModel entity) {
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
        if (isCharacterTurn()) {
            // state 4 corresponds to character turn
            setStateIndex(fourthState);
        } else {
            // state 5 corresponds to enemy turn
            setStateIndex(fifthState);
        }
    }

    /**
     * Returns true if the next turn is an character turn.
     */
    private boolean isCharacterTurn() {
        return combatOrderCurr.get(0).getIsCharacter();
    }

    /**
     * Move the CharacterEntity fom the front of the combatOrderCurr list to back of combatOrderLast list to signal turn is over.
     * Apply all necessary effects on the current CharacterEntity, check if the combat is still ongoing, and signal the next UI state.
     */
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
                    setStateIndex(fourthState);
                } else {
                    // state 5 corresponds to enemy turn
                    setStateIndex(fifthState);
                }
            }
        }
    }

    /**
     * Find if combat is still ongoing. If combat is over, then signal to go to end of UI states.
     * @return  True if if all enemies are dead in enemies list.
     */
    private boolean isCombatInProgress() {
        for (EnemyViewModel enemyVM : enemies) {
            if (enemyVM.getEnemy().getIsAlive()) {
                return true;
            }
        }
        setReward();
        setStateIndex(sixthState);
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

    public ArrayList<CharacterEntityViewModel> getCombatOrderCurr() {
        return combatOrderCurr;
    }
    public ArrayList<CharacterEntityViewModel> getCombatOrderNext() {
        return combatOrderNext;
    }
    public ArrayList<CharacterEntityViewModel> getCombatOrderLast() {
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
        int expReward = combatModel.getExpReward(getEnemiesList());
        characterVM.addExp(expReward);
    }
    // gold reward
    private void getGoldReward() {
        int goldReward = combatModel.getGoldReward(getEnemiesList());
        characterVM.goldChange(goldReward);
    }
    private ArrayList<Enemy> getEnemiesList() {
        ArrayList<Enemy> enemiesList = new ArrayList<>();
        for (EnemyViewModel enemyVM : enemies) {
            enemiesList.add(enemyVM.getEnemy());
        }
        return enemiesList;
    }
    // Weapon/Ability/Item reward
    public Inventory getInventoryReward(Context context) {
        Inventory inventoryRewards = combatModel.getInventoryReward(getEnemiesList(), context);
        // todo: apply the Inventory rewards to character
        return inventoryRewards;
    }
    public int getSizeCombatOrderCurr() {
        return combatOrderCurr.size();
    }
    public int getSizeCombatOrderLast() {
        return combatOrderLast.size();
    }

}
