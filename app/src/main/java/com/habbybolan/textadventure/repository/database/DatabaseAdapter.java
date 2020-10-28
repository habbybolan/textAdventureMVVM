package com.habbybolan.textadventure.repository.database;

import android.content.Context;
import android.database.Cursor;
import android.database.MergeCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.habbybolan.textadventure.model.characterentity.Enemy;
import com.habbybolan.textadventure.model.inventory.Ability;
import com.habbybolan.textadventure.model.inventory.Item;
import com.habbybolan.textadventure.model.inventory.weapon.Attack;
import com.habbybolan.textadventure.model.inventory.weapon.SpecialAttack;
import com.habbybolan.textadventure.model.inventory.weapon.Weapon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

public class DatabaseAdapter {
    protected static final String TAG = "DataAdapter";

    private final Context mContext;
    private SQLiteDatabase mDb;
    private DataBaseHelper mDbHelper;

    private String WEAPON_ID = "weapon_id";
    private String ATTACK_ID = "attack_id";
    private String S_ATTACK_ID = "s_attack_id";
    private String ABILITY_ID = "ability_id";
    private String ITEM_ID = "item_id";

    private String ENEMY_ID = "enemy_id";
    private String ENEMY_TYPE = "type";
    private String ENEMY_ABILITY_ID = "enemy_ability_id";
    private String TIER = "tier";

    // TODO: establish the number of tiers weapons/abilities/items have
    private double WEAPON_TIER_1 = 0.70;
    private double WEAPON_TIER_2 = 0.20;
    private double WEAPON_TIER_3 = 0.10;

    private double ABILITY_TIER_1 = 0.70;
    private double ABILITY_TIER_2 = 0.20;
    private double ABILITY_TIER_3 = 0.10;

    private double ITEM_TIER_1 = 0.70;
    private double ITEM_TIER_2 = 0.20;
    private double ITEM_TIER_3 = 0.10;

    private final int LOOT_POOL_SIZE = 10;

    public DatabaseAdapter(Context context) {
        this.mContext = context;
        mDbHelper = new DataBaseHelper(mContext);
        createDatabase();
        open();
    }

    private void createDatabase() throws SQLException {
        try {
            mDbHelper.createDataBase();
        } catch (IOException mIOException) {
            Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
    }

    private void open() throws SQLException {
        try {
            mDbHelper.openDataBase();
            mDbHelper.close();
            mDb = mDbHelper.getReadableDatabase();
        } catch (SQLException mSQLException) {
            Log.e(TAG, "open >>"+ mSQLException.toString());
            throw mSQLException;
        }
    }

    public void close() {
        mDbHelper.close();
    }

    public Cursor getData(String table) throws ExecutionException, InterruptedException {
        String sql ="SELECT * FROM " + table;
        return new queryDatabase().execute(sql).get();
    }

    public class queryDatabase extends AsyncTask<String, Void, Cursor> {
        Cursor mCur;
        @Override
        protected Cursor doInBackground(String ... query) {
            try {
                mCur = mDb.rawQuery(query[0], null, null);
                if (mCur != null) {
                    mCur.moveToNext();
                }
                return mCur;
            } catch (SQLException mSQLException) {
                Log.e(TAG, "sql database access fail >>"+ mSQLException.toString());
                throw mSQLException;
            }
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);
            close();
        }
    }
    // get a weapon of a specific tier
    public Weapon getRandomWeaponOfTier(int tier) throws ExecutionException, InterruptedException {
        open();
        String table = Weapon.table;
        int limit = 1;
        String sql = "SELECT * FROM " + table + " WHERE " + "tier" + " = " + tier + " ORDER BY RANDOM() LIMIT " + limit;
        Cursor weaponCursor = new queryDatabase().execute(sql).get();
        Weapon weapon =  new Weapon(weaponCursor, this);
        close();
        return weapon;
    }

    // get numWeapons random weapon of weighted-random tier
    public ArrayList<Weapon> getRandomWeapons(int numWeapons) throws ExecutionException, InterruptedException {
        open();
        // create a loot pool of 10 items, where numWeapons Weapons will be picked from it

        // get some tier 1 weapon, specified by WEAPON_TIER_1
        double numTier1D = LOOT_POOL_SIZE*WEAPON_TIER_1;
        int numTier1 = (int)numTier1D;
        Cursor cursorTier1 = getDataMultipleRandom(Weapon.table, numTier1, 1);
        // get some tier 2 weapon, specified by WEAPON_TIER_2
        double numTier2D = LOOT_POOL_SIZE*WEAPON_TIER_2;
        int numTier2 = (int)numTier2D;
        Cursor cursorTier2 = getDataMultipleRandom(Weapon.table, numTier2, 2);
        // get some tier 3 weapon, specified by WEAPON_TIER_3
        double numTier3D = LOOT_POOL_SIZE*WEAPON_TIER_3;
        int numTier3 = (int)numTier3D;
        Cursor cursorTier3 = getDataMultipleRandom(Weapon.table, numTier3, 3);

        // merge all the cursor tiers into one tier
        Cursor[] cursorArray = new Cursor[] {cursorTier1, cursorTier2, cursorTier3};
        MergeCursor cursor = new MergeCursor(cursorArray);

        // array of weapons to return
        ArrayList<Weapon> weapons = new ArrayList<>();
        // used to get random cursors without repeat of the random value previously chosen
        ArrayList<Integer> possibleCursorIndices = new ArrayList<>();
        for (int i = 0; i < LOOT_POOL_SIZE; i++)
            possibleCursorIndices.add(i);
        Collections.shuffle(possibleCursorIndices); // randomize the ArrayList<Weapon>
        for (int i = 0; i < numWeapons; i++) {
            cursor.moveToPosition(possibleCursorIndices.get(i));
            weapons.add(new Weapon(cursor, this));
        }
        close();
        return weapons;
    }

    // return the cursor of a weapon id
    public Cursor getWeaponCursorFromID(int id) throws ExecutionException, InterruptedException {
        String table = Weapon.table;
        String sql = "SELECT * FROM " + table + " WHERE " + WEAPON_ID + " = " + id + " ORDER BY RANDOM() LIMIT " + 1;
        return new queryDatabase().execute(sql).get();
    }

    public Cursor getAttackCursorFromID(int id) throws ExecutionException, InterruptedException {
        String table = Attack.table;
        String sql = "SELECT * FROM " + table + " WHERE " + ATTACK_ID + " = " + id + " ORDER BY RANDOM() LIMIT " + 1;
        return new queryDatabase().execute(sql).get();
    }

    public Cursor getSpecialAttackCursorFromID(int id) throws ExecutionException, InterruptedException {
        String table = SpecialAttack.table;
        String sql = "SELECT * FROM " + table + " WHERE " + S_ATTACK_ID + " = " + id + " ORDER BY RANDOM() LIMIT " + 1;
        return new queryDatabase().execute(sql).get();
    }

    // retrieve an ability of specific tier
    public Ability getRandomAbilityOfTier(int tier) throws ExecutionException, InterruptedException {
        open();
        String table = Ability.table;
        String sql ="SELECT * FROM " + table + " WHERE " + "tier" + " = " + tier + " ORDER BY RANDOM() LIMIT 1";
        Cursor abilityCursor = new queryDatabase().execute(sql).get();
        Ability ability =  new Ability(abilityCursor);
        abilityCursor.close();
        close();
        return ability;
    }

    // retrieve numAbilities random abilities
    public ArrayList<Ability> getRandomAbilities(int numAbilities) throws ExecutionException, InterruptedException {
        open();
        // create a loot pool of 10 items, where numWeapons Weapons will be picked from it
        // get some tier 1 ability, specified by ABILITY_TIER_1
        double numTier1D = LOOT_POOL_SIZE*ABILITY_TIER_1;
        int numTier1 = (int)numTier1D;
        Cursor cursorTier1 = getDataMultipleRandom(Ability.table, numTier1, 1);
        // get some tier 2 ability, specified by ABILITY_TIER_2
        double numTier2D = LOOT_POOL_SIZE*ABILITY_TIER_2;
        int numTier2 = (int)numTier2D;
        Cursor cursorTier2 = getDataMultipleRandom(Ability.table, numTier2, 2);
        // get some tier 3 ability, specified by ABILITY_TIER_3
        double numTier3D = LOOT_POOL_SIZE*ABILITY_TIER_3;
        int numTier3 = (int)numTier3D;
        Cursor cursorTier3 = getDataMultipleRandom(Ability.table, numTier3, 3);

        // merge all the cursor tiers into one tier
        Cursor[] cursorArray = new Cursor[] {cursorTier1, cursorTier2, cursorTier3};
        MergeCursor cursor = new MergeCursor(cursorArray);

        // array of abilities to return
        ArrayList<Ability> abilities = new ArrayList<>();
        // used to get random cursors without repeat of the random value previously chosen
        ArrayList<Integer> possibleCursorIndices = new ArrayList<>();
        for (int i = 0; i < LOOT_POOL_SIZE; i++) possibleCursorIndices.add(i);
        Collections.shuffle(possibleCursorIndices); // randomize the ArrayList<Ability>
        for (int i = 0; i < numAbilities; i++) {
            cursor.moveToPosition(possibleCursorIndices.get(i));
            abilities.add(new Ability(cursor));
        }
        cursor.close();
        close();
        return abilities;
    }

    // return the cursor of an ability id
    public Cursor getAbilityCursorFromID(int id) throws ExecutionException, InterruptedException {
        String table = Ability.table;
        String sql = "SELECT * FROM " + table + " WHERE " + ABILITY_ID + " = " + id + " ORDER BY RANDOM() LIMIT " + 1;
        return new queryDatabase().execute(sql).get();
    }

    private Cursor getDataMultipleRandom(String table, int limit, int tier) throws ExecutionException, InterruptedException {
        open();
        String sql = "SELECT * FROM " + table + " WHERE " + "tier" + " = " + 1 + " ORDER BY RANDOM() LIMIT " + limit;
        return new queryDatabase().execute(sql).get();
    }

    // retrieve a random item of specific tier
    public Item getRandomItemOfTier(int tier) throws ExecutionException, InterruptedException {
        open();
        String table = Item.table;
        String sql ="SELECT * FROM " + table + " WHERE " + "tier" + " = " + tier + " ORDER BY RANDOM() LIMIT 1";
        Cursor itemCursor = new queryDatabase().execute(sql).get();
        Item item =  new Item(itemCursor, this);
        itemCursor.close();
        close();
        return item;
    }
    // retrieve numItems random items
    public ArrayList<Item> getRandomItems(int numItems) throws ExecutionException, InterruptedException {
        open();
        // create a loot pool of 10 items, where numItems items will be picked from it
        // get some tier 1 item, specified by ITEM_TIER_1
        double numTier1D = LOOT_POOL_SIZE*ITEM_TIER_1;
        int numTier1 = (int)numTier1D;
        Cursor cursorTier1 = getDataMultipleRandom(Item.table, numTier1, 1);
        // get some tier 2 item, specified by ITEM_TIER_2
        double numTier2D = LOOT_POOL_SIZE*ITEM_TIER_2;
        int numTier2 = (int)numTier2D;
        Cursor cursorTier2 = getDataMultipleRandom(Item.table, numTier2, 2);
        // get some tier 3 item, specified by ITEM_TIER_3
        double numTier3D = LOOT_POOL_SIZE*ITEM_TIER_3;
        int numTier3 = (int)numTier3D;
        Cursor cursorTier3 = getDataMultipleRandom(Item.table, numTier3, 3);

        // merge all the cursor tiers into one tier
        Cursor[] cursorArray = new Cursor[] {cursorTier1, cursorTier2, cursorTier3};
        MergeCursor cursor = new MergeCursor(cursorArray);

        // array of weapons to return
        ArrayList<Item> items = new ArrayList<>();
        // used to get random cursors without repeat of the random value previously chosen
        ArrayList<Integer> possibleCursorIndices = new ArrayList<>();
        for (int i = 0; i < LOOT_POOL_SIZE; i++) possibleCursorIndices.add(i);
        Collections.shuffle(possibleCursorIndices); // randomize the ArrayList<Item>
        for (int i = 0; i < numItems; i++) {
            cursor.moveToPosition(possibleCursorIndices.get(i));
            items.add(new Item(cursor, this));
        }
        cursor.close();
        close();
        return items;
    }

    // return the cursor of an item id
    public Cursor getItemCursorFromID(int id) throws ExecutionException, InterruptedException {
        String table = Item.table;
        String sql = "SELECT * FROM " + table + " WHERE " + ITEM_ID + " = " + id + " ORDER BY RANDOM() LIMIT " + 1;
        return new queryDatabase().execute(sql).get();
    }

    /**
     * Retrieves the Enemy given a type and tier from the database table enemy.
     * @param type              The type of the enemy, used as a primary key in the database table enemy.
     * @param tier              The tier from 1-3, determining the strength of the enemy, 1 being weakest, 3 being strongest.
     * @param numStatPoints     The number of stat points to be distributed to the enemy.
     * @return                  The Enemy object created from the info retrieved from the database table.
     */
    public Enemy getEnemy(String type, int tier, int numStatPoints) throws ExecutionException, InterruptedException {
        open();
        String sql = "SELECT * FROM " + Enemy.TABLE_ENEMY + " WHERE " + "type" + " = " + "\"" + type + "\"";
        Cursor cursor = new queryDatabase().execute(sql).get();
        // holds the column of the next column to look at
        int colIndex;
        // enemy ID
        colIndex = cursor.getColumnIndex(ENEMY_ID);
        int id = cursor.getInt(colIndex);
        // if enemy has a weapon
        colIndex = cursor.getColumnIndex(Enemy.IS_WEAPON);
        boolean isWeapon = cursor.getInt(colIndex) == 1;
        // percentages
        colIndex = cursor.getColumnIndex(Enemy.STR_PERCENT);
        int strPercent = cursor.getInt(colIndex);
        colIndex = cursor.getColumnIndex(Enemy.INT_PERCENT);
        int intPercent  = cursor.getInt(colIndex);
        colIndex = cursor.getColumnIndex(Enemy.CON_PERCENT);
        int colPercent = cursor.getInt(colIndex);
        colIndex = cursor.getColumnIndex(Enemy.SPD_PERCENT);
        int spdPercent = cursor.getInt(colIndex);
        ArrayList<Ability> abilities = getAbilitiesOfEnemy(id, tier);
        Weapon weapon = null;
        if (isWeapon) weapon = getRandomWeaponOfTier(1);
        return new Enemy(type, tier, isWeapon, strPercent, intPercent, colPercent, spdPercent, abilities, numStatPoints, weapon);
    }

    /**
     * Retrieves the abilities associated with the Enemy, given its ID and tier, by looking at the bridge table enemy_ability_bridge in the database
     * and finding all abilities associated with those 2 keys.
     * @param id        The ID of the enemy.
     * @param tier      The tier of the enemy.
     */
    private ArrayList<Ability> getAbilitiesOfEnemy(int id, int tier) throws ExecutionException, InterruptedException {
        ArrayList<Ability> abilities = new ArrayList<>();
        String sql = "SELECT * FROM " + Enemy.TABLE_ENEMY_ABILITY_BRIDGE + " WHERE " + ENEMY_ID + " = " + id + " AND " + TIER + " = " + tier;
        Cursor cursor = new queryDatabase().execute(sql).get();
        do {
            // get the specific ability ID from the bridge table that cursor is pointing to, and query for that ability in enemy_ability table.
            int abilityIdColIndex = cursor.getColumnIndex(Enemy.ENEMY_ABILITY_ID);
            int ability_id = cursor.getInt(abilityIdColIndex);
            // query a single ability given enemy_ability primary key ability_id
            sql = "SELECT * FROM " + Enemy.TABLE_ENEMY_ABILITY + " WHERE " + ABILITY_ID + " = " + ability_id;
            Cursor cursorAbility = new queryDatabase().execute(sql).get();
            abilities.add(new Ability(cursorAbility));
        } while (cursor.moveToNext());
        return abilities;
    }

}
