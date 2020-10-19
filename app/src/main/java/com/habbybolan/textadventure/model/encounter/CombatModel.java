package com.habbybolan.textadventure.model.encounter;

import android.content.Context;

import com.habbybolan.textadventure.model.characterentity.CharacterEntity;
import com.habbybolan.textadventure.model.inventory.Ability;
import com.habbybolan.textadventure.model.inventory.Inventory;
import com.habbybolan.textadventure.model.inventory.Item;
import com.habbybolan.textadventure.model.inventory.weapon.Weapon;
import com.habbybolan.textadventure.repository.JsonAssetFileReader;
import com.habbybolan.textadventure.repository.database.DatabaseAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;

/*
model that does the business logic of the combat encounter
 */
public class CombatModel {

    // a speed check that returns true if speed * multiplier is above a random value
    public boolean runSpeedCheck(int speed) {
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

    // take characterEntity from combatOrderCurr and add to combatOrderLast
        // if combatOrderCurr is empty, then move all from combatOrder
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
    public int getExpReward(JSONObject encounter) {
        int difficulty = getMeanDifficulty(encounter);
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
    public int getGoldReward(JSONObject encounter) {
        int difficulty = getMeanDifficulty(encounter);
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
    public Inventory getInventoryReward(JSONObject encounter, Context context) {
        int difficulty = getMeanDifficulty(encounter);
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
    private int getMeanDifficulty(JSONObject encounter) {
        int val = 0;
        try {
            JSONArray tierArray = encounter.getJSONObject(JsonAssetFileReader.FIGHT).getJSONArray(JsonAssetFileReader.DIFFICULTY);
            for (int i = 0; i < tierArray.length(); i++)
                val += tierArray.getInt(i);
            return val/tierArray.length();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return val;
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
