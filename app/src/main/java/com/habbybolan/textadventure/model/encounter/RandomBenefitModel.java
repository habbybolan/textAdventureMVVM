package com.habbybolan.textadventure.model.encounter;

import android.app.Application;
import android.content.Context;

import com.habbybolan.textadventure.model.inventory.Inventory;
import com.habbybolan.textadventure.model.locations.Outdoor;
import com.habbybolan.textadventure.repository.database.LootInventory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;
import java.util.Stack;

/*
holds the business logic for the Random Benefit encounter
 */
public class RandomBenefitModel extends EncounterModel {

    String[] dialogue;
    // default values if not specifically set
    int tier = 0;
    int numRewards = 1;
    boolean isExp = false;
    boolean isGold = false;

    public static final String TYPE = "type";
    public static final String DIALOGUE = "dialogue";
    public static final String TIER = "tier";
    public static final String NUM_REWARDS = "numRewards";
    public static final String IS_EXP = "isExp";
    public static final String IS_GOLD = "isGold";


    /**
     * Constructor created through RandomBenefitModelBuilder.
     * @param dialogue  The lines of dialogue at the beginning of the random benefit encounter.
     */
    RandomBenefitModel(String[] dialogue) {
        this.dialogue = dialogue;
    }

    // finds a random Inventory loot
    private static Inventory getRandomInventory(Context context) {
        Random rand = new Random();
        LootInventory lootInventory = new LootInventory(context);
        // randomly choose Inventory object, Weapon/Item/Ability
        int val = rand.nextInt(3);
        Inventory inventory = null;
        switch (val) {
            case 0:
                inventory = lootInventory.getRandomWeapon(1);
                break;
            case 1:
                inventory = lootInventory.getRandomAbility(1);
                break;
            case 2:
                inventory = lootInventory.getRandomItem(1);
                break;
        }
        lootInventory.closeDatabase();
        return inventory;

    }

    /**
     * Creates a Random Benefit encounter with specified dialogue, loot rarity, loot amount, and if exp and/or gold is rewarded.
     * This is used for creating a reward state, specifically for end-dungeon loot, boss killing, and combat encounters.
     * @return              The JSONObject encounter of the Random Benefit Encounter.
     */
    public JSONObject createRandomBenefitEncounter() throws JSONException {
        JSONObject randomBenefitJSON = new JSONObject();
        randomBenefitJSON.put(TYPE, Outdoor.RANDOM_BENEFIT_TYPE);
        randomBenefitJSON.put(DIALOGUE, createDialogue(dialogue));
        randomBenefitJSON.put(TIER, tier);
        randomBenefitJSON.put(NUM_REWARDS, numRewards);
        randomBenefitJSON.put(IS_EXP, isExp);
        randomBenefitJSON.put(IS_GOLD, isGold);
        return randomBenefitJSON;
    }

    /**
     * Get an exp amount to return given the tier and the distance the player character has travelled.
     * @param encounter     The encounter JSON holding the tier
     * @param distance      The distance the player character has travelled
     * @return              The exp reward scaled off of the distance and tier.
     */
    public static int getExpReward(JSONObject encounter, int distance) throws JSONException {
        if (encounter.has(IS_EXP)) {
            // todo: scale the exp correctly
            int tier = encounter.getInt(TIER);
            return distance + tier;
        } else
            return 0;
    }

    /**
     * Get the gold amount to return given the tier and distance the player has travelled.
     * @param encounter     The encounter JSON holding tier value
     * @param distance      The distance the player character has travelled
     * @return              The gold reward scaled off of the tier and distance.
     */
    public static int getGoldReward(JSONObject encounter, int distance) throws JSONException {
        if (encounter.has(IS_GOLD)) {
            // todo: scale the gold correctly
            int tier = encounter.getInt(TIER);
            return distance + tier;
        } else
            return 0;
    }

    /**
     * Get a number of inventory rewards specified in encounter JSONObject, scaled by tier and distance.
     * @param encounter     The JSON encounter that holds the tier and number of inventory rewards.
     * @param distance      The distance the player character has travelled
     * @return              a number of inventory rewards, specified by encounter.
     */
    public static Stack<Inventory> getInventoryRewards(Application application, JSONObject encounter, int distance) throws JSONException {
        Stack<Inventory> inventoryRewards = new Stack<>();
        int numRewards = encounter.getInt(NUM_REWARDS);
        for (int i = 0; i < numRewards; i++) {
            inventoryRewards.add(getRandomInventory(application));
        }
        return inventoryRewards;
    }
}
