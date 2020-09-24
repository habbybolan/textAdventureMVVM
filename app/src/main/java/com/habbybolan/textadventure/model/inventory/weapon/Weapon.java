package com.habbybolan.textadventure.model.inventory.weapon;

import android.database.Cursor;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.model.inventory.Inventory;
import com.habbybolan.textadventure.repository.database.DatabaseAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/*
Sets up a weapon for a CharacterEntity with an Attack and Special Attack
 */

public class Weapon implements Inventory {
    private String description = "blahblahblah"; //  weapon description
    private String weaponName = ""; // weapon name
    private int tier; //  the quality of a weapon - higher the tier, the better
    static final private int numberOfWeapons = 1;
    private Attack attack; // the weapon's normal attack
    private SpecialAttack specialAttack; // the weapon's special attack
    static public final String table = "weapons";
    private int weaponID;

    private int pictureResource;

    // Constructor where database opened and closed elsewhere
    public Weapon(int weaponID, DatabaseAdapter mDbHelper) throws ExecutionException, InterruptedException {
        this.weaponID = weaponID;
        Cursor cursor = mDbHelper.getWeaponCursorFromID(weaponID);
        setVariables(mDbHelper, cursor);
        setPictureResource();
    }

    // Constructor where database opened and closed elsewhere
    public Weapon(Cursor cursor, DatabaseAdapter mDbHelper, int weaponID) throws ExecutionException, InterruptedException {
        this.weaponID = weaponID;
        setVariables(mDbHelper, cursor);
        setPictureResource();
    }

    // sets the picture resource
    @Override
    public void setPictureResource() {
        // todo: get pic based on tier and type
        pictureResource = R.drawable.sword;
    }

    @Override
    public int getPictureResource() {
        return pictureResource;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        else if (o.getClass() != getClass()) return false;
        else if (o == this) return true;
        else return (checkWeaponEquality((Weapon) o));
    }

    // helper for checking if 2 ability scrolls are equal
    private boolean checkWeaponEquality(Weapon weapon) {
        return weapon.getName().equals(getName());
    }

    // sets all the variables that describe the weapon
    private void setVariables(DatabaseAdapter mDbHelper, Cursor cursor) throws ExecutionException, InterruptedException {
        // set up the weapon name
        int weaponNameColIndex = cursor.getColumnIndex("weapon_name");
        setWeaponName(cursor.getString(weaponNameColIndex));
        // set up the attack id
        int attackColID = cursor.getColumnIndex("attack_id");
        Attack attack = new Attack(cursor.getInt(attackColID), mDbHelper);
        setAttack(attack);
        // set up the special attack id
        int specialAttackColID = cursor.getColumnIndex("s_attack_id");
        SpecialAttack specialAttack = new SpecialAttack(cursor.getInt(specialAttackColID), mDbHelper);
        setSpecialAttack(specialAttack);
        // set up the weapon tier (weapon rarity)
        int tierColID = cursor.getColumnIndex("tier");
        setTier(cursor.getInt(tierColID));
    }



    // setter methods
    public void setWeaponName(String weaponName) {
        this.weaponName = weaponName;
    }
    public void setAttack(Attack attack) {
        this.attack = attack;
    }
    public void setSpecialAttack(SpecialAttack specialAttack) {
        this.specialAttack = specialAttack;
    }
    public void setTier(int tier) {
        this.tier = tier;
    }

    // getter methods
    public String getDescription() {
        return description;
    }
    public Attack getAttack() {
        return attack;
    }
    public SpecialAttack getSpecialAttack() {
        return specialAttack;
    }
    public int getTier() {
        return tier;
    }
    public int getWeaponID() {
        return weaponID;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject toJSON = new JSONObject();
        try {
            toJSON.put(INVENTORY_TYPE, TYPE_WEAPON);
            toJSON.put(ID, getID());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return toJSON;
    }
    @Override
    public int getID() {
        return weaponID;
    }
    @Override
    public String getType() {
        return Inventory.TYPE_WEAPON;
    }
    @Override
    public String getName() {
        return weaponName;
    }
}
