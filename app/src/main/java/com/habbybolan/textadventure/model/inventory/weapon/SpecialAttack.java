package com.habbybolan.textadventure.model.inventory.weapon;

import android.database.Cursor;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.model.inventory.Ability;
import com.habbybolan.textadventure.model.inventory.Inventory;
import com.habbybolan.textadventure.repository.database.DatabaseAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/*
A special attack that represents one of two uses for a weapon
    // attacks can be abilities, or similar to attacks with a bit more effects, but has a cool down
    // each weapon is guaranteed to have one special attack
 */
public class SpecialAttack implements Inventory {

    private String specialAttackName = "";
    private String specialAttackDescription = "";
    private Ability ability = null;
    private boolean isRanged;
    private int aoe;
    private int splashDamageMin;
    private int splashDamageMax;
    private int damageMin;
    private int damageMax;
    private int cooldownMax;
    private int cooldownCurr;

    private int specialAttackID;

    public static final String table = "s_attacks";

    private int pictureResource;

    // damage with attack
    private int attackDamage = 0;
    private int attackDamagePerTurn = 0;
    private int attackTurnsForDot = 0;

    private int weaponWeight = 0; // affects the overall speed stat

    // constructor where database opened and closed elsewhere
    public SpecialAttack(int specialAttackID, DatabaseAdapter mDbHelper) throws ExecutionException, InterruptedException {
        this.specialAttackID = specialAttackID;
        Cursor cursor = mDbHelper.getSpecialAttackCursorFromID(specialAttackID);
        setVariables(cursor, mDbHelper);
        setPictureResource();
    }

    private void setVariables(Cursor cursor, DatabaseAdapter mDbHelper) throws ExecutionException, InterruptedException {
        // set up special attack name
        int specialAttackNameColID = cursor.getColumnIndex("s_attack_name");
        setSpecialAttackName(cursor.getString(specialAttackNameColID));
        // set ability id - NULL if none
        int abilityColID = cursor.getColumnIndex("ability_id");
        int abilityID = cursor.getInt(abilityColID);
        if (cursor.getInt(abilityColID) != 0) {
            setAbility(new Ability(abilityID, mDbHelper));
        }
        // set up ranged boolean
        int isRangedColID = cursor.getColumnIndex("is_ranged");
        setIsRanged(cursor.getInt(isRangedColID));
        // set up aoe (number of extra enemies hit with splash damage)
        int intAoeColID = cursor.getColumnIndex("aoe");
        setAoe(cursor.getInt(intAoeColID));
        // set up splash damage min/max
        int splashDamageMinColID = cursor.getColumnIndex("splash_min");
        setSplashDamageMin(cursor.getInt(splashDamageMinColID));
        int splashDamageMaxColID = cursor.getColumnIndex("splash_max");
        setSplashDamageMax(cursor.getInt(splashDamageMaxColID));
        // set up damage min/max
        int damageMinColID = cursor.getColumnIndex("damage_min");
        setDamageMin(cursor.getInt(damageMinColID));
        int damageMaxColID = cursor.getColumnIndex("damage_max");
        setDamageMax(cursor.getInt(damageMaxColID));
        // set cooldowns
        int cooldownColID = cursor.getColumnIndex("cooldown");
        setCooldownMax(cursor.getInt(cooldownColID));
        setCooldownCurr(0);
        cursor.close();
    }


    // setters
    public void setSpecialAttackName(String specialAttackName) {
        this.specialAttackName = specialAttackName;
    }
    public void setAbility(Ability ability) {
        this.ability = ability;
    }
    private void setIsRanged(int isRanged) {
        if (isRanged == 1)
            this.isRanged = true;
        else
            this.isRanged = false;

    }
    private void setAoe(int aoe) {
        this.aoe = aoe;
    }
    private void setSplashDamageMin(int splashDamageMin) {
        this.splashDamageMin = splashDamageMin;
    }
    private void setSplashDamageMax(int splashDamageMax) {
        this.splashDamageMax = splashDamageMax;
    }
    private void setDamageMin(int damageMin) {
        this.damageMin = damageMin;
    }
    private void setDamageMax(int damageMax) {
        this.damageMax = damageMax;
    }

    private void setCooldownMax(int cooldownMax) {
        this.cooldownMax = cooldownMax;
    }
    public void setCooldownCurr(int cooldownCurr) {
        this.cooldownCurr = cooldownCurr;
    }

    // getters
    public String getSpecialAttackName() {
        return specialAttackName;
    }
    public String getAttackDescription() {
        return specialAttackDescription;
    }
    public Ability getAbility() {
        return ability;
    }
    public boolean getIsRanged() {
        return isRanged;
    }
    public int getAoe() {
        return aoe;
    }
    public int getSplashDamageMin() {
        return splashDamageMin;
    }
    public int getSplashDamageMax() {
        return splashDamageMax;
    }
    public int getDamageMin() {
        return damageMin;
    }
    public int getDamageMax() {
        return damageMax;
    }
    public int getCooldownMax() {
        return cooldownMax;
    }
    public int getCooldownCurr() {
        return cooldownCurr;
    }

    public int getSpecialAttackID() {
        return specialAttackID;
    }

    @Override
    public String getType() {
        return TYPE_S_ATTACK;
    }

    @Override
    public String getName() {
        return specialAttackName;
    }

    @Override
    public int getID() {
        return specialAttackID;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject toJSON = new JSONObject();
        try {
            toJSON.put(INVENTORY_TYPE, TYPE_S_ATTACK);
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
