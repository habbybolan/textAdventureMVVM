package com.habbybolan.textadventure.model.inventory.weapon;

import android.database.Cursor;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.model.inventory.Ability;
import com.habbybolan.textadventure.repository.database.LootInventory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/*
A special attack that represents one of two uses for a weapon
    // attacks can be abilities, or similar to attacks with a bit more effects, but has a cool down
    // each weapon is guaranteed to have one special attack
 */
public class SpecialAttack extends WeaponAction {

    private String specialAttackName = "";
    private String specialAttackDescription = "Special Attack Description";
    private Ability ability = null;
    private boolean isRanged = false;
    private int aoe;
    private int splashDamageMin;
    private int splashDamageMax;
    private int damageMin;
    private int damageMax;

    private int specialAttackID;

    public static final String table = "s_attacks";

    private int pictureResource;

    // damage with attack
    private int attackDamage = 0;
    private int attackDamagePerTurn = 0;
    private int attackTurnsForDot = 0;

    private int weaponWeight = 0; // affects the overall speed stat

    // constructor where database opened and closed elsewhere
    public SpecialAttack(Cursor cursor, LootInventory lootInventory, Weapon parentInventory) throws ExecutionException, InterruptedException {
        setVariables(cursor, lootInventory);
        this.parentWeapon = parentInventory;
        setPictureResource();
    }

    /**
     * Constructed from serialized special attack.
     * Only use for getting info. Don't re-create an actual special Attack using this.
     * @param stringSpecialAttack  The serialized Special Attack object
     */
    public SpecialAttack(String stringSpecialAttack) {
        try {
            JSONObject JSONSpecialAttack = new JSONObject(stringSpecialAttack);
            specialAttackName = JSONSpecialAttack.getString(S_ATTACK_NAME);
            specialAttackID = JSONSpecialAttack.getInt(S_ATTACK_ID);
            // ability
            if (JSONSpecialAttack.has(ABILITY))
                ability = new Ability(JSONSpecialAttack.getJSONObject(ABILITY).toString());
            // damage
            damageMin = JSONSpecialAttack.getInt(DAMAGE_MIN);
            damageMax = JSONSpecialAttack.getInt(DAMAGE_MAX);
            isRanged = JSONSpecialAttack.getBoolean(IS_RANGED);
            aoe = JSONSpecialAttack.getInt(AOE);
            splashDamageMin = JSONSpecialAttack.getInt(SPLASH_DAMAGE_MIN);
            splashDamageMax = JSONSpecialAttack.getInt(SPLASH_DAMAGE_MAX);
            // cooldown
            cooldownCurr = JSONSpecialAttack.getInt(COOLDOWN_CURR);
            cooldownMax = JSONSpecialAttack.getInt(COOLDOWN_MAX);
            specialAttackDescription = JSONSpecialAttack.getString(DESCRIPTION);
            pictureResource = JSONSpecialAttack.getInt(IMAGE_RESOURCE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

     /**
     * Constructed from serialized special attack, and passed the parent weapon Special Attack is attached to.
     * @param stringSpecialAttack  The serialized Special Attack object
     * @param parentWeapon         The parent weapon to attach to Special Attack
     */
    SpecialAttack(String stringSpecialAttack, Weapon parentWeapon) {
        this.parentWeapon = parentWeapon;
        try {
            JSONObject JSONSpecialAttack = new JSONObject(stringSpecialAttack);
            specialAttackName = JSONSpecialAttack.getString(S_ATTACK_NAME);
            specialAttackID = JSONSpecialAttack.getInt(S_ATTACK_ID);
            // ability
            if (JSONSpecialAttack.has(ABILITY))
                ability = new Ability(JSONSpecialAttack.getJSONObject(ABILITY).toString());
            // damage
            damageMin = JSONSpecialAttack.getInt(DAMAGE_MIN);
            damageMax = JSONSpecialAttack.getInt(DAMAGE_MAX);
            isRanged = JSONSpecialAttack.getBoolean(IS_RANGED);
            aoe = JSONSpecialAttack.getInt(AOE);
            splashDamageMin = JSONSpecialAttack.getInt(SPLASH_DAMAGE_MIN);
            splashDamageMax = JSONSpecialAttack.getInt(SPLASH_DAMAGE_MAX);
            // cooldown
            cooldownCurr = JSONSpecialAttack.getInt(COOLDOWN_CURR);
            cooldownMax = JSONSpecialAttack.getInt(COOLDOWN_MAX);
            specialAttackDescription = JSONSpecialAttack.getString(DESCRIPTION);
            pictureResource = JSONSpecialAttack.getInt(IMAGE_RESOURCE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a default special attack used for the default weapon 'fists'.
     */
    public SpecialAttack() {
        specialAttackName = "Upper Cut";
        damageMin = 2;
        damageMax = 3;
        cooldownMax = 2;
        setPictureResource();
    }

    private void setVariables(Cursor cursor, LootInventory lootInventory) throws ExecutionException, InterruptedException {
        // set up special attack name
        int specialAttackNameColID = cursor.getColumnIndex(S_ATTACK_NAME);
        setSpecialAttackName(cursor.getString(specialAttackNameColID));
        // set ability id - NULL if none
        int abilityColID = cursor.getColumnIndex(ABILITY_ID);
        int abilityID = cursor.getInt(abilityColID);
        if (cursor.getInt(abilityColID) != 0) {
            setAbility(lootInventory.getAbilityFromID(abilityID));
        }
        // set up ranged boolean
        int isRangedColID = cursor.getColumnIndex(IS_RANGED);
        setIsRanged(cursor.getInt(isRangedColID));
        // set up aoe (number of extra enemies hit with splash damage)
        int intAoeColID = cursor.getColumnIndex(AOE);
        setAoe(cursor.getInt(intAoeColID));
        // set up splash damage min/max
        int splashDamageMinColID = cursor.getColumnIndex(SPLASH_DAMAGE_MIN);
        setSplashDamageMin(cursor.getInt(splashDamageMinColID));
        int splashDamageMaxColID = cursor.getColumnIndex(SPLASH_DAMAGE_MAX);
        setSplashDamageMax(cursor.getInt(splashDamageMaxColID));
        // set up damage min/max
        int damageMinColID = cursor.getColumnIndex(DAMAGE_MIN);
        setDamageMin(cursor.getInt(damageMinColID));
        int damageMaxColID = cursor.getColumnIndex(DAMAGE_MAX);
        setDamageMax(cursor.getInt(damageMaxColID));
        // set cooldowns
        int cooldownColID = cursor.getColumnIndex(COOLDOWN);
        cooldownMax = (cursor.getInt(cooldownColID)) ;
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
    public JSONObject serializeToJSON() throws JSONException {
        JSONObject JSONInventory = new JSONObject();
        JSONInventory.put(TYPE, TYPE_S_ATTACK);
        JSONInventory.put(S_ATTACK_NAME, specialAttackName);
        JSONInventory.put(S_ATTACK_ID, specialAttackID);
        JSONInventory.put(INVENTORY_TYPE, TYPE_S_ATTACK);
        // ability
        if (ability != null) {
            JSONObject JSONAbility = ability.serializeToJSON();
            JSONInventory.put(ABILITY, JSONAbility);
        }
        // damage
        JSONInventory.put(IS_RANGED, isRanged);
        JSONInventory.put(AOE, aoe);
        JSONInventory.put(SPLASH_DAMAGE_MIN, splashDamageMin);
        JSONInventory.put(SPLASH_DAMAGE_MAX, splashDamageMax);
        JSONInventory.put(DAMAGE_MIN, damageMin);
        JSONInventory.put(DAMAGE_MAX, damageMax);
        // cooldown
        JSONInventory.put(COOLDOWN_CURR, cooldownCurr);
        JSONInventory.put(COOLDOWN_MAX, cooldownMax);
        JSONInventory.put(DESCRIPTION, specialAttackDescription);
        JSONInventory.put(IMAGE_RESOURCE, pictureResource);
        return JSONInventory;
    }

    @Override
    public void setPictureResource() {
        // todo: special attack picture resource
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
        return false;
    }

    @Override
    public boolean isSpecialAttack() {
        return true;
    }

    public static final String S_ATTACK_NAME = "s_attack_name";
    public static final String S_ATTACK_ID = "s_attack_id";
    public static final String ABILITY = "ability";
    public static final String ABILITY_ID = "ability_id";
    public static final String IS_RANGED = "is_ranged";
    public static final String AOE = "aoe";
    public static final String SPLASH_DAMAGE_MIN = "splash_min";
    public static final String SPLASH_DAMAGE_MAX  = "splash_max";
    public static final String DAMAGE_MIN = "damage_min";
    public static final String DAMAGE_MAX = "damage_max";
    public static final String COOLDOWN = "cooldown";
    public static final String COOLDOWN_CURR = "cooldown_curr";
    public static final String COOLDOWN_MAX = "cooldown_max";
    public static final String DESCRIPTION = "description";
    // image resource
    public static final String IMAGE_RESOURCE = "image_resource";
}
