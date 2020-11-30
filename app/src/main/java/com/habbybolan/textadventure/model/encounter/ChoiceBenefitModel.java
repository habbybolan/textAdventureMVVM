package com.habbybolan.textadventure.model.encounter;

import android.content.Context;

import com.habbybolan.textadventure.model.effects.Effect;
import com.habbybolan.textadventure.model.effects.TempBarFactory;
import com.habbybolan.textadventure.model.effects.TempStat;
import com.habbybolan.textadventure.model.inventory.InventoryEntity;
import com.habbybolan.textadventure.repository.database.LootInventory;

import java.util.Random;

public class ChoiceBenefitModel {

    private Context context;
    public ChoiceBenefitModel(Context context) {
        this.context = context;
    }

    public int getGoldRewardAmount() {
        Random rand = new Random();
        // amount of gold between MIN_GOLD_AMOUNT and MAX_GOLD_AMOUNT
        int MAX_GOLD_REWARD = 45;
        int MIN_GOLD_REWARD = 5;
        return rand.nextInt(MAX_GOLD_REWARD - MIN_GOLD_REWARD) + MIN_GOLD_REWARD;
    }

    // return a new TempStat or TempBar to apply to character
    public Effect getNewTempStat() {
        Random rand = new Random();
        int numBenefits = 6;
        int benefit = rand.nextInt(numBenefits);
        switch(benefit) {
            case 0: // STR
                return new TempStat(TempStat.STR, 10, 30);
            case 1: // INT
                return new TempStat(TempStat.INT, 10, 30);
            case 2: // CON
                return new TempStat(TempStat.CON, 10, 30);
            case 3: // SPD
                return new TempStat(TempStat.SPD, 10, 30);
            case 4: // Health
                return TempBarFactory.createTempHealth(10, 50);
            default:
                return TempBarFactory.createTempMana(10, 50);
        }
    }

    // return a new String type for a stat to increase permanently
    public Effect getNewPermanentStat() {
        Random rand = new Random();
        int numBenefits = 6;
        int benefit = rand.nextInt(numBenefits);
        switch(benefit) {
            case 0: // STR
                return new TempStat(TempStat.STR, 5);
            case 1: // INT
                return new TempStat(TempStat.INT, 5);
            case 2: // CON
                return new TempStat(TempStat.CON, 5);
            case 3: // SPD
                return new TempStat(TempStat.SPD, 5);
            case 4: // Health
                return TempBarFactory.createIndefiniteHealth(10);
            default:
                return TempBarFactory.createIndefiniteMana(10);
        }
    }

    // return new Inventory object to add to character inventory
    public InventoryEntity getNewInventory() {
        Random rand = new Random();
        LootInventory lootInventory = new LootInventory(context);
        // randomly choose Inventory object, Weapon/Item/Ability
        int val = rand.nextInt(3);
        InventoryEntity inventoryEntity = null;
        switch (val) {
            case 0:
                inventoryEntity = lootInventory.getRandomWeapon(1);
                break;
            case 1:
                inventoryEntity = lootInventory.getRandomAbility(1);
                break;
            case 2:
                inventoryEntity = lootInventory.getRandomItem(1);
                break;
        }
        return inventoryEntity;
    }
}
