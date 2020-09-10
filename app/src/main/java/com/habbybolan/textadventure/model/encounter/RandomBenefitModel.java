package com.habbybolan.textadventure.model.encounter;

import android.content.Context;

import com.habbybolan.textadventure.model.inventory.Inventory;
import com.habbybolan.textadventure.repository.database.DatabaseAdapter;

import java.util.Random;
import java.util.concurrent.ExecutionException;

/*
holds the business logic for the Random Benefit encounter
 */
public class RandomBenefitModel {

    Context context;

    public RandomBenefitModel(Context context) {
        this.context = context;
    }

    // finds a random Inventory loot
    public Inventory getRandomInventory() {
        Random rand = new Random();
        DatabaseAdapter adapter = new DatabaseAdapter(context);
        // randomly choose Inventory object, Weapon/Item/Ability
        int val = rand.nextInt(3);
        Inventory inventory = null;
        try {
            switch (val) {
                case 0:
                    inventory = adapter.getRandomWeapons(1).get(0);
                    break;
                case 1:
                    inventory = adapter.getRandomAbilities(1).get(0);
                    break;
                case 2:
                    inventory = adapter.getRandomItems(1).get(0);
                    break;
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return inventory;
    }
}
