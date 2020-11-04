package com.habbybolan.textadventure.model.encounter;

import android.content.Context;

import com.habbybolan.textadventure.model.characterentity.Character;
import com.habbybolan.textadventure.model.characterentity.CharacterEntity;
import com.habbybolan.textadventure.model.characterentity.Enemy;
import com.habbybolan.textadventure.model.inventory.Ability;
import com.habbybolan.textadventure.model.inventory.Inventory;
import com.habbybolan.textadventure.model.inventory.Item;
import com.habbybolan.textadventure.model.inventory.weapon.Weapon;
import com.habbybolan.textadventure.repository.database.DatabaseAdapter;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;

/*
model that does the business logic of the combat encounter
 */
public class CombatModel {

    // a speed check that returns true if speed * multiplier is above a random value
    public boolean runCheck(Character character) {
        int speed = character.getSpeed();
        int RUN_SPEED_MULTIPLIER = 5;
        Random rand = new Random();
        int numToCheck = rand.nextInt(100);
        return speed * RUN_SPEED_MULTIPLIER > numToCheck;
    }

    // helper for setCombatOrdering - bubble sort to sort both lists
    // index = 0 if setting up initial combat ordering, 1 if re-sorting the combat ordering
    public void sortCombatOrdering(int index, ArrayList<CharacterEntity> combatOrder) {
        // bubble sort combat ordering
        for (int i = index; i < combatOrder.size() - 1; i++) {
            for (int j = index; j < combatOrder.size() - i - 1 + index; j++) {
                if (combatOrder.get(j).getSpeed() < combatOrder.get(j + 1).getSpeed()) {
                    CharacterEntity temp = combatOrder.get(j + 1);
                    combatOrder.set(j + 1, combatOrder.get(j));
                    combatOrder.set(j, temp);
                }
            }
        }
    }

    /**
     * Remove the front item of the arrayList combatOrderCurr and add the removed item to end of combatOrderLast.
     * If combatOrderCurr is empty, then add all items from combatOrderNext to combatOrderCurr and clear combatOrderLast.
     * @param combatOrderCurr       The current 'round' of characterEntities in the combat order list. The first arrayList on the combat Order.
     * @param combatOrderNext       The next 'round' of characterEntities in the combat order list. The second arrayList on the combat Order.
     * @param combatOrderLast       The last 'round' of characterEntities in the combat order list. The third arrayList on the combat Order.
     */
    public void moveEntityToBackOfCombatOrder(ArrayList<CharacterEntity> combatOrderCurr, ArrayList<CharacterEntity> combatOrderNext, ArrayList<CharacterEntity> combatOrderLast) {
        CharacterEntity tempEntity = combatOrderCurr.get(0);
        combatOrderCurr.remove(0);
        combatOrderLast.add(tempEntity);

        if (combatOrderCurr.isEmpty()) {
            combatOrderCurr.addAll(combatOrderNext);
            combatOrderLast.clear();
        }
    }

    // rewards

    // exp reward
    public int getExpReward(ArrayList<Enemy> enemies) {
        int difficulty = getMeanDifficulty(enemies);
        Random rand = new Random();
        switch (difficulty) {
            case 1:
                return rand.nextInt(TIER_1_MAX_GOLD-TIER_1_MIN_GOLD)+TIER_1_MIN_GOLD;
            case 2:
                return rand.nextInt(TIER_2_MAX_GOLD-TIER_2_MIN_GOLD)+TIER_2_MIN_GOLD;
            case 3:
                return rand.nextInt(TIER_3_MAX_GOLD-TIER_3_MIN_GOLD)+TIER_3_MIN_GOLD;
            default:
                throw new IllegalArgumentException();
        }
    }
    // gold reward
    public int getGoldReward(ArrayList<Enemy> enemies) {
        int difficulty = getMeanDifficulty(enemies);
        Random rand = new Random();
        switch (difficulty) {
            case 1:
                return rand.nextInt(TIER_1_MAX_EXP-TIER_1_MIN_EXP)+TIER_1_MIN_EXP;
            case 2:
                return rand.nextInt(TIER_2_MAX_EXP-TIER_2_MIN_EXP)+TIER_2_MIN_EXP;
            case 3:
                return rand.nextInt(TIER_3_MAX_EXP-TIER_3_MIN_EXP)+TIER_3_MIN_EXP;
            default:
                throw new IllegalArgumentException();
        }
    }
    // Weapon/Ability/Item reward
    public Inventory getInventoryReward(ArrayList<Enemy> enemies, Context context) {
        int difficulty = getMeanDifficulty(enemies);
        Random rand = new Random();
        int inventoryRand = rand.nextInt(3);
        DatabaseAdapter db = new DatabaseAdapter(context);
        try {
            switch (inventoryRand) {
                case 0:
                    // Weapon
                    return getWeaponReward(difficulty, db);
                case 1:
                    // Ability
                    return getAbilityReward(difficulty, db);
                case 2:
                    // Item
                    return getItemReward(difficulty, db);
                default:
                    throw new IllegalArgumentException();
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException();
    }

    private final int TIER_CUTOFF = 2;

    // returns a weapon reward
    private Weapon getWeaponReward(int difficulty, DatabaseAdapter db) throws ExecutionException, InterruptedException {
        Random rand = new Random();
        int val = rand.nextInt(10);
        if (difficulty == 1) {
            // difficulty 1
            if (val > TIER_CUTOFF)
                return db.getRandomWeaponOfTier(difficulty);
            else
                return db.getRandomWeaponOfTier(difficulty + 1);
        } else if (difficulty == 2) {
            // difficulty 2
            if (val > TIER_CUTOFF)
                return db.getRandomWeaponOfTier(difficulty);
            else
                if (rand.nextInt(2) == 0)
                    return db.getRandomWeaponOfTier(difficulty-1);
                else
                    return db.getRandomWeaponOfTier(difficulty+1);
        } else {
            // difficulty 3
            if (val > TIER_CUTOFF)
                return db.getRandomWeaponOfTier(difficulty);
            else
                return db.getRandomWeaponOfTier(difficulty - 1);
        }
    }

    // returns a weapon reward
    private Ability getAbilityReward(int difficulty, DatabaseAdapter db) throws ExecutionException, InterruptedException {
        Random rand = new Random();
        int val = rand.nextInt(10);
        if (difficulty == 1) {
            // difficulty 1
            if (val > TIER_CUTOFF)
                return db.getRandomAbilityOfTier(difficulty);
            else
                return db.getRandomAbilityOfTier(difficulty + 1);
        } else if (difficulty == 2) {
            // difficulty 2
            if (val > TIER_CUTOFF)
                return db.getRandomAbilityOfTier(difficulty);
            else
            if (rand.nextInt(2) == 0)
                return db.getRandomAbilityOfTier(difficulty-1);
            else
                return db.getRandomAbilityOfTier(difficulty+1);
        } else {
            // difficulty 3
            if (val > TIER_CUTOFF)
                return db.getRandomAbilityOfTier(difficulty);
            else
                return db.getRandomAbilityOfTier(difficulty - 1);
        }
    }

    // returns a weapon reward
    private Item getItemReward(int difficulty, DatabaseAdapter db) throws ExecutionException, InterruptedException {
        Random rand = new Random();
        int val = rand.nextInt(10);
        if (difficulty == 1) {
            // difficulty 1
            if (val > TIER_CUTOFF)
                return db.getRandomItemOfTier(difficulty);
            else
                return db.getRandomItemOfTier(difficulty + 1);
        } else if (difficulty == 2) {
            // difficulty 2
            if (val > TIER_CUTOFF)
                return db.getRandomItemOfTier(difficulty);
            else
            if (rand.nextInt(2) == 0)
                return db.getRandomItemOfTier(difficulty-1);
            else
                return db.getRandomItemOfTier(difficulty+1);
        } else {
            // difficulty 3
            if (val > TIER_CUTOFF)
                return db.getRandomItemOfTier(difficulty);
            else
                return db.getRandomItemOfTier(difficulty - 1);
        }
    }

    // gets the JSONArray for the difficulty of all enemies and returns the mean difficulty as int
    private int getMeanDifficulty(ArrayList<Enemy> enemies) {
        int val = 0;
        for (int i = 0; i < enemies.size(); i++)
            val += enemies.get(i).getDifficulty();
        return val/enemies.size();
    }

    // parsing enemy into JSON

    // GOLD
    private final int TIER_1_MIN_GOLD = 1;
    private final int TIER_1_MAX_GOLD = 2;
    private final int TIER_2_MIN_GOLD = 2;
    private final int TIER_2_MAX_GOLD = 3;
    private final int TIER_3_MIN_GOLD = 3;
    private final int TIER_3_MAX_GOLD = 4;
    // EXP
    private final int TIER_1_MIN_EXP = 1;
    private final int TIER_1_MAX_EXP = 2;
    private final int TIER_2_MIN_EXP = 2;
    private final int TIER_2_MAX_EXP = 3;
    private final int TIER_3_MIN_EXP = 3;
    private final int TIER_3_MAX_EXP = 4;
}
