package com.habbybolan.textadventure.repository.database;

import android.content.Context;
import android.database.Cursor;

import com.habbybolan.textadventure.model.characterentity.Enemy;
import com.habbybolan.textadventure.model.inventory.Ability;
import com.habbybolan.textadventure.model.inventory.weapon.Weapon;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class CreateEnemy extends LootInventory {

    public CreateEnemy(Context context) {
        super(context);
    }

    /**
     * Create an enemy given the type of enemy, its tier, and the number of stat points
     * @param type              The type of enemy, reference in the database
     * @param tier              The tier of the enemy, from 1-3
     * @param numStatPoints     The number of state points to give the enemy
     * @return                  The created enemy
     */
    public Enemy getEnemy(String type, int tier, int numStatPoints) {
        try {
            Cursor cursor = databaseAdapter.getEnemy(type);
            // holds the column of the next column to look at
            int colIndex;
            // enemy ID
            colIndex = cursor.getColumnIndex(databaseAdapter.ENEMY_ID);
            int id = cursor.getInt(colIndex);
            // if enemy has a weapon
            colIndex = cursor.getColumnIndex(Enemy.IS_WEAPON);
            boolean isWeapon = cursor.getInt(colIndex) == 1;
            // percentages
            colIndex = cursor.getColumnIndex(Enemy.STR_PERCENT);
            int strPercent = cursor.getInt(colIndex);
            colIndex = cursor.getColumnIndex(Enemy.INT_PERCENT);
            int intPercent = cursor.getInt(colIndex);
            colIndex = cursor.getColumnIndex(Enemy.CON_PERCENT);
            int colPercent = cursor.getInt(colIndex);
            colIndex = cursor.getColumnIndex(Enemy.SPD_PERCENT);
            int spdPercent = cursor.getInt(colIndex);
            ArrayList<Ability> abilities = databaseAdapter.getAbilitiesOfEnemy(id, tier);
            Weapon weapon = getRandomWeaponOfTier(tier);
            if (isWeapon) weapon = getRandomWeaponOfTier(1);
            return new Enemy(type, tier, isWeapon, strPercent, intPercent, colPercent, spdPercent, abilities, numStatPoints, weapon);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
