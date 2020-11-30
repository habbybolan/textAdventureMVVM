package com.habbybolan.textadventure.model.inventory.weapon;

import android.database.Cursor;

import com.habbybolan.textadventure.R;

import org.json.JSONException;
import org.json.JSONObject;
/*
An attack that represents one of the two uses for a weapon
    // it is always a single target attack that costs no mana to use and has no cool down
    // each weapon is guaranteed to have one attack
 */

public class Attack extends WeaponAction {
    private String attackName = "";
    private String attackDescription = "Attack Description";
    private int damageMin;
    private int damageMax;
    private boolean isRanged = false;

    private int attackID;
    public static final String table = "attacks";

    private int pictureResource;

    // todo: add weight and possible dot to attacks - maybe aoe?
    private int weaponWeight = 0; // affects the overall speed stat

    // constructor to read database for attack
    public Attack(Cursor cursor, Weapon parentInventory)  {
        setVariables(cursor);
        this.parentWeapon = parentInventory;
        setPictureResource();
    }

    /**
     * Constructed from serialized attack.
     * Only use for getting info. Don't re-create an actual Attack using this.
     * @param stringAttack  The serialized Attack object
     */
    public Attack(String stringAttack) {
        try {
            JSONObject JSONAttack = new JSONObject(stringAttack);
            attackName = JSONAttack.getString(ATTACK_NAME);
            attackID = JSONAttack.getInt(ATTACK_ID);
            damageMin = JSONAttack.getInt(DAMAGE_MIN);
            damageMax = JSONAttack.getInt(DAMAGE_MAX);
            isRanged = JSONAttack.getBoolean(IS_RANGED);
            pictureResource = JSONAttack.getInt(IMAGE_RESOURCE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructed from serialized attack, and passed the parent weapon Attack is attached to.
     * @param stringAttack  The serialized Attack object
     * @param parentWeapon  The parent weapon to attach to Attack
     */
    Attack(String stringAttack, Weapon parentWeapon) {
        this.parentWeapon = parentWeapon;
        try {
            JSONObject JSONAttack = new JSONObject(stringAttack);
            attackName = JSONAttack.getString(ATTACK_NAME);
            attackID = JSONAttack.getInt(ATTACK_ID);
            damageMin = JSONAttack.getInt(DAMAGE_MIN);
            damageMax = JSONAttack.getInt(DAMAGE_MAX);
            isRanged = JSONAttack.getBoolean(IS_RANGED);
            pictureResource = JSONAttack.getInt(IMAGE_RESOURCE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a default attack used for the default weapon 'fists'.
     */
    public Attack(Weapon defaultParentWeapon) {
        parentWeapon = defaultParentWeapon;
        attackName = "Punch";
        damageMin = 1;
        damageMax = 3;
        setPictureResource();
    }

    private void setVariables(Cursor cursor) {
        int attackIDColID = cursor.getColumnIndex(ATTACK_ID);
        attackID = cursor.getInt(attackIDColID);
        // set up attack name
        int attackNameColID = cursor.getColumnIndex(ATTACK_NAME);
        setAttackName(cursor.getString(attackNameColID));
        // set up attack damage min/max
        int damageMinColID = cursor.getColumnIndex(DAMAGE_MIN);
        setDamageMin(cursor.getInt(damageMinColID));
        int damageMaxColID = cursor.getColumnIndex(DAMAGE_MAX);
        setDamageMax(cursor.getInt(damageMaxColID));
        // set up ranged boolean
        int isRangedColID = cursor.getColumnIndex(IS_RANGED);
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
    public JSONObject serializeToJSON() throws JSONException {
        JSONObject JSONInventory = new JSONObject();
        JSONInventory.put(TYPE, TYPE_ATTACK);
        JSONInventory.put(ATTACK_NAME, attackName);
        JSONInventory.put(INVENTORY_TYPE, TYPE_ATTACK);
        JSONInventory.put(ATTACK_ID, attackID);
        JSONInventory.put(DAMAGE_MIN, damageMin);
        JSONInventory.put(DAMAGE_MAX, damageMax);
        JSONInventory.put(IS_RANGED, isRanged);
        JSONInventory.put(DESCRIPTION, attackDescription);
        JSONInventory.put(IMAGE_RESOURCE, pictureResource);
        return JSONInventory;
    }

    @Override
    public void setPictureResource() {
        // todo: attack picture resource
        pictureResource = R.drawable.sword;
    }

    @Override
    public int getPictureResource() {
        return pictureResource;
    }

    @Override
    public boolean isAbility() {
        return false;
    }

    @Override
    public boolean isItem() {
        return false;
    }

    @Override
    public boolean isWeapon() {
        return false;
    }

    @Override
    public boolean isAttack() {
        return true;
    }

    @Override
    public boolean isSpecialAttack() {
        return false;
    }

    public static final String ATTACK_NAME = "attack_name";
    public static final String ATTACK_ID = "attack_id";
    public static final String DAMAGE_MIN = "damage_min";
    public static final String DAMAGE_MAX = "damage_max";
    public static final String IS_RANGED = "is_ranged";
    public static final String DESCRIPTION = "description";
    // image resource
    public static final String IMAGE_RESOURCE = "image_resource";


}
