package com.habbybolan.textadventure.repository.database;

import android.content.Context;
import android.database.Cursor;
import android.database.MergeCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

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
        int weaponColID = weaponCursor.getColumnIndex(WEAPON_ID);
        int weaponID = weaponCursor.getInt(weaponColID);
        Weapon weapon =  new Weapon(weaponCursor, this, weaponID);
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
        for (int i = 0; i < LOOT_POOL_SIZE; i++) possibleCursorIndices.add(i);
        Collections.shuffle(possibleCursorIndices); // randomize the ArrayList<Weapon>
        for (int i = 0; i < numWeapons; i++) {
            cursor.moveToPosition(possibleCursorIndices.get(i));
            int weaponColID = cursor.getColumnIndex(WEAPON_ID);
            int weaponID = cursor.getInt(weaponColID);
            weapons.add(new Weapon(cursor, this, weaponID));
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
        int abilityColID = abilityCursor.getColumnIndex(WEAPON_ID);
        int abilityID = abilityCursor.getInt(abilityColID);
        Ability ability =  new Ability(abilityCursor, abilityID);
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
            int abilityColID = cursor.getColumnIndex(ABILITY_ID);
            int abilityID = cursor.getInt(abilityColID);
            abilities.add(new Ability(cursor, abilityID));
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
        int itemColID = itemCursor.getColumnIndex(ITEM_ID);
        int itemID = itemCursor.getInt(itemColID);
        Item item =  new Item(itemCursor, this, itemID);
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
            int itemColID = cursor.getColumnIndex(ITEM_ID);
            int itemID = cursor.getInt(itemColID);
            items.add(new Item(cursor, this, itemID));
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

}
