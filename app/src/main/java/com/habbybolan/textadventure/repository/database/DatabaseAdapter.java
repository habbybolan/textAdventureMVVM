package com.habbybolan.textadventure.repository.database;

import android.content.Context;
import android.database.Cursor;
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
import java.util.concurrent.ExecutionException;

public class DatabaseAdapter {
    protected static final String TAG = "DatabaseAdapter";

    private final Context mContext;
    private SQLiteDatabase mDb;
    private DataBaseHelper mDbHelper;

    String WEAPON_ID = "weapon_id";
    String ATTACK_ID = "attack_id";
    String S_ATTACK_ID = "s_attack_id";
    String ABILITY_ID = "ability_id";
    String ITEM_ID = "item_id";

    String ENEMY_ID = "enemy_id";
    String ENEMY_TYPE = "type";
    String ENEMY_ABILITY_ID = "enemy_ability_id";
    String TIER = "tier";

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

    void open() throws SQLException {
        try {
            mDbHelper.openDataBase();
            mDbHelper.close();
            mDb = mDbHelper.getReadableDatabase();
        } catch (SQLException mSQLException) {
            Log.e(TAG, "open: "+ mSQLException.toString());
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
        }
    }
    // get a weapon of a specific tier
    Cursor getRandomWeaponOfTier(int tier) throws ExecutionException, InterruptedException {
        String table = Weapon.table;
        int limit = 1;
        String sql = "SELECT * FROM " + table + " WHERE " + "tier" + " = " + tier + " ORDER BY RANDOM() LIMIT " + limit;
        return new queryDatabase().execute(sql).get();
    }

    /**
     * Get the Weapon from a specific id.
     * @param id    The id of the Weapon to retrieve from database
     * @return      return the cursor of an Weapon id
     */
    Cursor getWeaponCursorFromID(int id) throws ExecutionException, InterruptedException {
        String table = Weapon.table;
        String sql = "SELECT * FROM " + table + " WHERE " + WEAPON_ID + " = " + id + " ORDER BY RANDOM() LIMIT " + 1;
        return new queryDatabase().execute(sql).get();
    }

    Cursor getAttackCursorFromID(int id) throws ExecutionException, InterruptedException {
        String table = Attack.table;
        String sql = "SELECT * FROM " + table + " WHERE " + ATTACK_ID + " = " + id + " ORDER BY RANDOM() LIMIT " + 1;
        return new queryDatabase().execute(sql).get();
    }

    Cursor getSpecialAttackCursorFromID(int id) throws ExecutionException, InterruptedException {
        String table = SpecialAttack.table;
        String sql = "SELECT * FROM " + table + " WHERE " + S_ATTACK_ID + " = " + id + " ORDER BY RANDOM() LIMIT " + 1;
        return new queryDatabase().execute(sql).get();
    }

    // retrieve an ability of specific tier
    Ability getRandomAbilityOfTier(int tier) throws ExecutionException, InterruptedException {
        String table = Ability.table;
        String sql ="SELECT * FROM " + table + " WHERE " + "tier" + " = " + tier + " ORDER BY RANDOM() LIMIT 1";
        Cursor abilityCursor = new queryDatabase().execute(sql).get();
        Ability ability =  new Ability(abilityCursor);
        abilityCursor.close();
        return ability;
    }

    /**
     * Get the Ability from a specific id.
     * @param id    The id of the Ability to retrieve from database
     * @return      return the cursor of an Ability id
     */
    Cursor getAbilityCursorFromID(int id) throws ExecutionException, InterruptedException {
        String table = Ability.table;
        String sql = "SELECT * FROM " + table + " WHERE " + ABILITY_ID + " = " + id + " ORDER BY RANDOM() LIMIT " + 1;
        return new queryDatabase().execute(sql).get();
    }

    /**
     * Get the Special Attack Ability from a specific id.
     * @param id    The id of the Special attack ability
     * @return      The cursor of the special attack ability
     */
    Cursor getSpecialAttackAbilityCursorFromID(int id) throws ExecutionException, InterruptedException {
        String table = SpecialAttack.ability_table;
        String sql = "SELECT * FROM " + table + " WHERE " + ABILITY_ID + " = " + id + " ORDER BY RANDOM() LIMIT " + 1;
        return new queryDatabase().execute(sql).get();
    }

    private Cursor getDataMultipleRandom(String table, int limit, int tier) throws ExecutionException, InterruptedException {
        String sql = "SELECT * FROM " + table + " WHERE " + "tier" + " = " + 1 + " ORDER BY RANDOM() LIMIT " + limit;
        return new queryDatabase().execute(sql).get();
    }

    // retrieve a random item of specific tier
    Cursor getRandomItemOfTier(int tier) throws ExecutionException, InterruptedException {
        String table = Item.table;
        String sql ="SELECT * FROM " + table + " WHERE " + "tier" + " = " + tier + " ORDER BY RANDOM() LIMIT 1";
        return new queryDatabase().execute(sql).get();
    }

    /**
     * Get the item from a specific id.
     * @param id    The id of the item to retrieve from database
     * @return      return the cursor of an item id
     */
    Cursor getItemCursorFromID(int id) throws ExecutionException, InterruptedException {
        String table = Item.table;
        String sql = "SELECT * FROM " + table + " WHERE " + ITEM_ID + " = " + id + " ORDER BY RANDOM() LIMIT " + 1;
        return new queryDatabase().execute(sql).get();
    }

    /**
     * Retrieves the cursor associated with an enemy inside the database
     * @param type              The type of the enemy, used as a primary key in the database table enemy.
     * @return                  The Enemy object created from the info retrieved from the database table.
     */
    Cursor getEnemy(String type) throws ExecutionException, InterruptedException {
        String sql = "SELECT * FROM " + Enemy.TABLE_ENEMY + " WHERE " + "type" + " = " + "\"" + type + "\"";
        return new queryDatabase().execute(sql).get();
    }

    /**
     * Retrieves the abilities associated with the Enemy, given its ID and tier, by looking at the bridge table enemy_ability_bridge in the database
     * and finding all abilities associated with those 2 keys.
     * @param id        The ID of the enemy.
     * @param tier      The tier of the enemy.
     */
    ArrayList<Ability> getAbilitiesOfEnemy(int id, int tier) throws ExecutionException, InterruptedException {
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
