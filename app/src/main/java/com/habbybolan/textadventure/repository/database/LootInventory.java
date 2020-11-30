package com.habbybolan.textadventure.repository.database;

import android.content.Context;
import android.database.Cursor;

import com.habbybolan.textadventure.model.inventory.Ability;
import com.habbybolan.textadventure.model.inventory.Item;
import com.habbybolan.textadventure.model.inventory.weapon.Attack;
import com.habbybolan.textadventure.model.inventory.weapon.SpecialAttack;
import com.habbybolan.textadventure.model.inventory.weapon.Weapon;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;

/**
 * Bridge between getting random/specific loot from the database. Any database access for loot goes through this class.
 * MUST CALL closeDatabase() WHEN NOT USING ANYMORE.
 */
public class LootInventory {
    // todo: loot inventory

    DatabaseAdapter databaseAdapter;
    public LootInventory(Context context) {
        databaseAdapter = new DatabaseAdapter(context);
        databaseAdapter.open();
    }


    /**
     * Get a random tier scaled towards the tierScaled value.
     * @param tierScaled    The tier to scale the random returned tier towards
     * @return              The random tier
     */
    private int getTier(int tierScaled) {
        // tier cutoffs
        int tier1Cutoff;
        int tier2Cutoff;
        // set the cutoffs for the tiers given the tierScaled to scale the chance of the scale towards.
        switch(tierScaled) {
            case 1:
                tier1Cutoff = 70;
                tier2Cutoff = tier1Cutoff + 20;
                break;
            case 2:
                tier1Cutoff = 40;
                tier2Cutoff = tier1Cutoff + 50;
                break;
            case 3:
                tier1Cutoff = 30;
                tier2Cutoff = tier1Cutoff + 40;
                break;
            default:
                throw new IllegalArgumentException(tierScaled + " is not a valid tier. Tier is from 1-3.");
        }
        Random random = new Random();
        int randPercent = random.nextInt(100);
        // find the tier to return given the randPercent value
        if (randPercent < tier1Cutoff) {
            return 1;
        } else if (randPercent < tier2Cutoff) {
            return 2;
        } else {
            return 3;
        }
    }

    /**
     * Get a number of random abilities scaled towards tierScaled.
     * @param tierScaled    The tier to scale the ability rarity to. Values 1-3
     * @param numAbilities  The number of random abilities to get
     * @return              A number of random abilities scaled towards tierScaled.
     */
    public ArrayList<Ability> getRandomAbilities(int tierScaled, int numAbilities) {
        ArrayList<Ability> abilities = new ArrayList<>();
        for (int i = 0; i < numAbilities; i++) {
            abilities.add(getRandomAbility(tierScaled));
        }
        return abilities;
    }

    /**
     * Get a random ability from the database, scaled towards the tier specified.
     * @param tierScaled  The tier to scale the ability rarity to. Values 1-3
     * @return      The ability retrieved from the database and constructed
     */
    public Ability getRandomAbility(int tierScaled) {
        int tier = getTier(tierScaled);
        return getRandomAbilityOfTier(tier);
    }

    public Ability getRandomAbilityOfTier(int tier) {
        try {
            return databaseAdapter.getRandomAbilityOfTier(tier);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public Ability getAbilityFromID(int id) {
        try {
            Cursor cursor = databaseAdapter.getAbilityCursorFromID(id);
            return new Ability(cursor);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public Ability getSpecialAttackAbilityFromID(int id) {
        try {
            Cursor cursor = databaseAdapter.getSpecialAttackAbilityCursorFromID(id);
            return new Ability(cursor);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * Get a number of random Items scaled towards tierScaled.
     * @param tierScaled    The tier to scale the Item rarity to. Values 1-3
     * @param numItems      The number of random Items to get
     * @return              A number of random Items scaled towards tierScaled.
     */
    public ArrayList<Item> getRandomItems(int tierScaled, int numItems) {
        ArrayList<Item> items = new ArrayList<>();
        for (int i = 0; i < numItems; i++) {
            items.add(getRandomItem(tierScaled));
        }
        return items;
    }

    /**
     * Get a random Item from the database, scaled towards the tier specified.
     * @param tierScaled  The tier to scale the Item rarity to. Values 1-3
     * @return      The item retrieved from the database and constructed
     */
    public Item getRandomItem(int tierScaled) {
        int tier = getTier(tierScaled);
        return getRandomItemOfTier(tier);
    }

    public Item getRandomItemOfTier(int tier) {
        try {
            Cursor cursor = databaseAdapter.getRandomItemOfTier(tier);
            return new Item(cursor, this);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public Item getItemFromID(int id) {
        try {
            Cursor cursor = databaseAdapter.getItemCursorFromID(id);
            return new Item(cursor, this);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     * Get a number of random Weapons scaled towards tierScaled.
     * @param tierScaled    The tier to scale the Weapon rarity to. Values 1-3
     * @param numWeapons    The number of random Weapons to get
     * @return              A number of random Weapons scaled towards tierScaled.
     */
    public ArrayList<Weapon> getRandomWeapons(int tierScaled, int numWeapons) {
        ArrayList<Weapon> weapons = new ArrayList<>();
        for (int i = 0; i < numWeapons; i++) {
            weapons.add(getRandomWeapon(tierScaled));
        }
        return weapons;
    }

    /**
     * Get a random Weapon from the database, scaled towards the tier specified.
     * @param tierScaled  The tier to scale the Weapon rarity to. Values 1-3
     * @return      The Weapon retrieved from the database and constructed
     */
    public Weapon getRandomWeapon(int tierScaled) {
        int tier = getTier(tierScaled);
        return getRandomWeaponOfTier(tier);
    }

    public Weapon getRandomWeaponOfTier(int tier) {
        try {
            Cursor cursor = databaseAdapter.getRandomWeaponOfTier(tier);
            return new Weapon(cursor, this);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public Weapon getWeaponFromID(int id) {
        try {
            Cursor cursor = databaseAdapter.getWeaponCursorFromID(id);
            return new Weapon(cursor, this);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public Attack getAttackCursorFromID(int id, Weapon weapon) {
        try {
            Cursor cursor = databaseAdapter.getAttackCursorFromID(id);
            return new Attack(cursor, weapon);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public SpecialAttack getSpecialAttackCursorFromID(int id, Weapon weapon) {
        try {
            Cursor cursor = databaseAdapter.getSpecialAttackCursorFromID(id);
            return new SpecialAttack(cursor, this, weapon);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public void closeDatabase() {
        databaseAdapter.close();
    }
}
