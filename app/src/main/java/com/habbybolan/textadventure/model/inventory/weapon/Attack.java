package com.habbybolan.textadventure.model.inventory.weapon;

import android.database.Cursor;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.model.inventory.Inventory;
import com.habbybolan.textadventure.repository.database.DatabaseAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
/*
An attack that represents one of the two uses for a weapon
    // it is always a single target attack that costs no mana to use and has no cool down
    // each weapon is guaranteed to have one attack
 */

public class Attack implements Inventory {
    private String attackName = "";
    private String attackDescription = "";
    private int damageMin;
    private int damageMax;
    private boolean isRanged;

    private int attackID;
    public static final String table = "attacks";

    private int pictureResource;

    // todo: add weight and possible dot to attacks - maybe aoe?
    private int weaponWeight = 0; // affects the overall speed stat

    // constructor to read database for attack
    public Attack(int attackID, DatabaseAdapter mDbHelper) throws ExecutionException, InterruptedException {
        this.attackID = attackID;
        Cursor cursor = mDbHelper.getAttackCursorFromID(attackID);
        setVariables(cursor);
        setPictureResource();
    }

    private void setVariables(Cursor cursor) {
        // set up attack name
        int attackNameColID = cursor.getColumnIndex("attack_name");
        setAttackName(cursor.getString(attackNameColID));
        // set up attack damage min/max
        int damageMinColID = cursor.getColumnIndex("damage_min");
        setDamageMin(cursor.getInt(damageMinColID));
        int damageMaxColID = cursor.getColumnIndex("damage_max");
        setDamageMax(cursor.getInt(damageMaxColID));
        // set up ranged boolean
        int isRangedColID = cursor.getColumnIndex("is_ranged");
        setIsRanged(cursor.getInt(isRangedColID));
    }


    // setters
    public void setAttackName(String attackName) {
        this.attackName = attackName;
    }
    private void setDamageMin(int damageMin) {
        this.damageMin = damageMin;
    }
    private void setDamageMax(int damageMax) {
        this.damageMax = damageMax;
    }
    private void setIsRanged(int isRanged) {
        if (isRanged == 1)
            this.isRanged = true;
        else
            this.isRanged = false;

    }

    // getters
    public String getAttackName() {
        return attackName;
    }
    public String getAttackDescription() {
        return attackDescription;
    }
    public int getDamageMin() {
        return damageMin;
    }
    public int getDamageMax() {
        return damageMax;
    }
    public boolean getIsRanged() {
        return isRanged;
    }

    public int getAttackID() {
        return attackID;
    }

    @Override
    public String getType() {
        return TYPE_ATTACK;
    }

    @Override
    public String getName() {
        return getAttackName();
    }

    @Override
    public int getID() {
        return attackID;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject toJSON = new JSONObject();
        try {
            toJSON.put(INVENTORY_TYPE, TYPE_ATTACK);
            toJSON.put(ID, getID());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return toJSON;
    }

    @Override
    public void setPictureResource() {
        pictureResource = R.drawable.sword;
    }

    @Override
    public int getPictureResource() {
        return pictureResource;
    }
}
