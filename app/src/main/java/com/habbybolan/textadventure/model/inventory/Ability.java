package com.habbybolan.textadventure.model.inventory;

import android.database.Cursor;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.repository.database.DatabaseAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class Ability extends Action {

    // ability
    private String abilityName = "";
    private String abilityDescription = "";

    // direct damage
    private int minDamage = 0;
    private int maxDamage = 0;
    // aoe
    private int damageAoe = 0; // number of enemies hit by splash damage
    private int splashMin = 0;
    private int splashMax = 0;
    private int specialAoe = 0; // numbers of enemies hit by special ability aoe

    // specials
    private boolean isConfuse = false; // gives confusion to target, making them attack random
    private boolean isStun = false; // makes the target skip turns
    private boolean isRevive = false; //  revives a target
    private boolean isInvincibility = false; // makes the target invincible
    private boolean isSilence = false; // makes the target unable to use abilities
    private boolean isInvisible = false;

    // if it's a buff (for teammates) or not
    private boolean isBuff = false;

    // damage over times
    private boolean dotDuration = false;
    private boolean isFire = false; // applies fire
    private boolean isPoison = false; // applies poison
    private boolean isFrostBurn = false; // applies frost burn
    private boolean isBleed = false; // applies bleed
    private boolean isHealDot = false; // health restored after each turn
    private boolean isManaDot = false; // mana restored after each turn

    // bars
    private int healMin = 0;
    private int healMax = 0;
    private int manaMin = 0;
    private int manaMax = 0;

    // extra health
    private int tempExtraHealth = 0;

    // time the ability lasts
    private int duration = 0;

    // ability tier
    private int tier = 0;
    // number of ability tiers
    public final static int NUM_TIERS = 3;

    // increase stats temporarily
    private int strIncrease = 0;
    private int intIncrease = 0;
    private int conIncrease = 0;
    private int spdIncrease = 0;
    private int evadeIncrease = 0;
    private int blockIncrease = 0;

    // decrease target stats temporarily
    private int statDebuffDuration = 0;
    private int strDecrease = 0;
    private int intDecrease = 0;
    private int conDecrease = 0;
    private int spdDecrease = 0;
    private int evadeDecrease = 0;
    private int blockDecrease = 0;

    private int pictureResource;

    // cost to use ability in mana
    private int cost = 0;

    // ID
    private int abilityID;
    static public final String table = "abilities";


    // constructor to read database for ability
    public Ability(int abilityID, DatabaseAdapter mDbHelper) throws ExecutionException, InterruptedException {
        this.abilityID = abilityID;
        Cursor cursor = mDbHelper.getAbilityCursorFromID(abilityID);
        setVariables(cursor);
        setPictureResource();
    }

    // constructor to read database for ability
    public Ability(Cursor cursor, int abilityID) throws ExecutionException, InterruptedException {
        this.abilityID = abilityID;
        setVariables(cursor);
        setPictureResource();
    }

    public Ability(String stringAbility) {
        try {
            JSONObject JSONAbility = new JSONObject(stringAbility);
            abilityName = JSONAbility.getString(ABILITY_NAME);
            abilityID = JSONAbility.getInt(ABILITY_ID);
            // cooldown
            cooldownCurr = JSONAbility.getInt(COOLDOWN_CURR);
            cooldownMax = JSONAbility.getInt(COOLDOWN_MAX);
            // damage
            minDamage = JSONAbility.getInt(DAMAGE_MIN);
            maxDamage = JSONAbility.getInt(DAMAGE_MAX);
            damageAoe = JSONAbility.getInt(DAMAGE_AOE);
            splashMin = JSONAbility.getInt(SPLASH_DAMAGE_MIN);
            splashMax = JSONAbility.getInt(SPLASH_DAMAGE_MAX);
            // specials
            specialAoe = JSONAbility.getInt(SPECIAL_AOE);
            isStun = JSONAbility.getBoolean(IS_STUN);
            isConfuse = JSONAbility.getBoolean(IS_CONFUSE);
            isInvisible = JSONAbility.getBoolean(IS_INVISIBLE);
            isInvincibility = JSONAbility.getBoolean(IS_INVINCIBILITY);
            isSilence = JSONAbility.getBoolean(IS_SILENCE);
            isRevive = JSONAbility.getBoolean(IS_REVIVE);
            // buff
            isBuff = JSONAbility.getBoolean(IS_BUFF);
            // dot
            isFire = JSONAbility.getBoolean(IS_FIRE);
            isBleed = JSONAbility.getBoolean(IS_BLEED);
            isFrostBurn = JSONAbility.getBoolean(IS_FROST_BURN);
            isHealDot = JSONAbility.getBoolean(IS_HEAL_DOT);
            isManaDot = JSONAbility.getBoolean(IS_MANA_DOT);
            // bars
            healMin = JSONAbility.getInt(HEAL_MIN);
            healMax = JSONAbility.getInt(HEAL_MAX);
            manaMin = JSONAbility.getInt(MANA_MIN);
            manaMax = JSONAbility.getInt(MANA_MAX);
            // temp stat increase
            strIncrease = JSONAbility.getInt(STR_INCREASE);
            intIncrease = JSONAbility.getInt(INT_INCREASE);
            conIncrease = JSONAbility.getInt(CON_INCREASE);
            spdIncrease = JSONAbility.getInt(SPD_INCREASE);
            evadeIncrease = JSONAbility.getInt(EVASION_INCREASE);
            blockIncrease = JSONAbility.getInt(BLOCK_INCREASE);
            // temp stat decrease
            strDecrease = JSONAbility.getInt(STR_DECREASE);
            intDecrease = JSONAbility.getInt(INT_DECREASE);
            conDecrease = JSONAbility.getInt(CON_DECREASE);
            spdDecrease = JSONAbility.getInt(SPD_DECREASE);
            evadeDecrease = JSONAbility.getInt(EVASION_DECREASE);
            blockDecrease = JSONAbility.getInt(BLOCK_DECREASE);
            // ability duration
            duration = JSONAbility.getInt(DURATION);
            // temp extra
            tempExtraHealth = JSONAbility.getInt(TEMP_EXTRA_HEALTH);
            // tier
            tier = JSONAbility.getInt(TIER);
            abilityDescription = JSONAbility.getString(DESCRIPTION);
            pictureResource = JSONAbility.getInt(IMAGE_RESOURCE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        else if (o.getClass() != getClass()) return false;
        else if (o == this) return true;
        else return (checkAbilityEquality((Ability) o));
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

    // helper for checking if 2 ability scrolls are equal
    private boolean checkAbilityEquality(Ability ability) {
        return ability.getName().equals(getName());
    }

    private void setVariables(Cursor cursor) throws ExecutionException, InterruptedException {
        // set ability name
        int nameOfAbilityColIndex = cursor.getColumnIndex(ABILITY_NAME);
        setAbilityName(cursor.getString(nameOfAbilityColIndex));
        // set cooldown
        int cooldownColIndex = cursor.getColumnIndex(COOLDOWN);
        cooldownMax = (cursor.getInt(cooldownColIndex));
        cooldownCurr = 0;
        // set direct damage
        int damageMinColIndex = cursor.getColumnIndex(DAMAGE_MIN);
        setMinDamage(cursor.getInt(damageMinColIndex));
        int damageMaxColIndex = cursor.getColumnIndex(DAMAGE_MAX);
        setMaxDamage(cursor.getInt(damageMaxColIndex));
        // set aoe
        int damageAoeColIndex = cursor.getColumnIndex(DAMAGE_AOE);
        setDamageAoe(cursor.getInt(damageAoeColIndex));
        int splashMinColIndex = cursor.getColumnIndex(SPLASH_DAMAGE_MIN);
        setSplashMin(cursor.getInt(splashMinColIndex));
        int splashMaxColIndex = cursor.getColumnIndex(SPLASH_DAMAGE_MAX);
        setSplashMax(cursor.getInt(splashMaxColIndex));
        int specialAoeColIndex = cursor.getColumnIndex(SPECIAL_AOE);
        setSpecialAoe(cursor.getInt(specialAoeColIndex));
        // set specials
        int isStunColIndex = cursor.getColumnIndex(IS_STUN);
        setIsStun(cursor.getInt(isStunColIndex)==1);
        int isConfuseColIndex = cursor.getColumnIndex(IS_CONFUSE);
        setIsConfuse(cursor.getInt(isConfuseColIndex)==1);
        int isReviveColIndex = cursor.getColumnIndex(IS_REVIVE);
        setIsRevive(cursor.getInt(isReviveColIndex)==1);
        int isInvincibilityColIndex = cursor.getColumnIndex(IS_INVINCIBILITY);
        setIsInvincibility(cursor.getInt(isInvincibilityColIndex)==1);
        int isSilenceColIndex = cursor.getColumnIndex(IS_SILENCE);
        setIsSilence(cursor.getInt(isSilenceColIndex)==1);
        int isInvisibleColIndex = cursor.getColumnIndex(IS_INVISIBLE);
        setIsInvisible(cursor.getInt(isInvisibleColIndex)==1);
        // set if ability is a buff
        int isBuffColIndex = cursor.getColumnIndex(IS_BUFF);
        setIsBuff(cursor.getInt(isBuffColIndex)==1);
        int isFireColIndex = cursor.getColumnIndex(IS_FIRE);
        setIsFire(cursor.getInt(isFireColIndex)==1);
        int isPoisonColIndex = cursor.getColumnIndex(IS_POISON);
        setIsPoison(cursor.getInt(isPoisonColIndex)==1);
        int isBleedColIndex = cursor.getColumnIndex(IS_BLEED);
        setIsBleed(cursor.getInt(isBleedColIndex)==1);
        int isFrostBurnColIndex = cursor.getColumnIndex(IS_FROST_BURN);
        setIsFrostBurn(cursor.getInt(isFrostBurnColIndex)==1);
        int healOverTimeColIndex = cursor.getColumnIndex(IS_HEAL_DOT);
        setIsHealDot(cursor.getInt(healOverTimeColIndex)==1);
        int manaOverTimeColIndex = cursor.getColumnIndex(IS_MANA_DOT);
        setIsManaDot(cursor.getInt(manaOverTimeColIndex)==1);
        // bars
        int healMinColIndex = cursor.getColumnIndex(HEAL_MIN);
        setHealMin(cursor.getInt(healMinColIndex));
        int healMaxColIndex = cursor.getColumnIndex(HEAL_MAX);
        setHealMax(cursor.getInt(healMaxColIndex));
        int manaMinColIndex = cursor.getColumnIndex(MANA_MIN);
        setManaMin(cursor.getInt(manaMinColIndex));
        int manaMaxColIndex = cursor.getColumnIndex(MANA_MAX);
        setManaMax(cursor.getInt(manaMaxColIndex));
        // temp stat increases
        int strIncreaseColIndex = cursor.getColumnIndex(STR_INCREASE);
        setStrIncrease(cursor.getInt(strIncreaseColIndex));
        int intIncreaseColIndex = cursor.getColumnIndex(INT_INCREASE);
        setIntIncrease(cursor.getInt(intIncreaseColIndex));
        int conIncreaseColIndex = cursor.getColumnIndex(CON_INCREASE);
        setConIncrease(cursor.getInt(conIncreaseColIndex));
        int spdIncreaseColIndex = cursor.getColumnIndex(SPD_INCREASE);
        setSpdIncrease(cursor.getInt(spdIncreaseColIndex));
        int blockIncreaseColIndex = cursor.getColumnIndex(BLOCK_INCREASE);
        setBlockIncrease(cursor.getInt(blockIncreaseColIndex));
        int evadeIncreaseColIndex = cursor.getColumnIndex(EVASION_INCREASE);
        setEvadeIncrease(cursor.getInt(evadeIncreaseColIndex));
        int strDecreaseColIndex = cursor.getColumnIndex(STR_DECREASE);
        setStrDecrease(cursor.getInt(strDecreaseColIndex));
        int intDecreaseColIndex = cursor.getColumnIndex(INT_DECREASE);
        setIntDecrease(cursor.getInt(intDecreaseColIndex));
        int conDecreaseColIndex = cursor.getColumnIndex(CON_DECREASE);
        setConDecrease(cursor.getInt(conDecreaseColIndex));
        int spdDecreaseColIndex = cursor.getColumnIndex(SPD_DECREASE);
        setSpdDecrease(cursor.getInt(spdDecreaseColIndex));
        int blockDecreaseColIndex = cursor.getColumnIndex(BLOCK_DECREASE);
        setBlockDecrease(cursor.getInt(blockDecreaseColIndex));
        int evadeDecreaseColIndex = cursor.getColumnIndex(EVASION_INCREASE);
        setEvadeDecrease(cursor.getInt(evadeDecreaseColIndex));
        // set number of turns an ability lasts for
        int turnsLeftColIndex = cursor.getColumnIndex(DURATION);
        setDuration(cursor.getInt(turnsLeftColIndex));
        // set temporary amount of extra health
        int tempExtraHealthColIndex = cursor.getColumnIndex(TEMP_EXTRA_HEALTH);
        setTempExtraHealth(cursor.getInt(tempExtraHealthColIndex));
        // tier
        int tierColIndex = cursor.getColumnIndex(TIER);
        setTier(cursor.getInt(tierColIndex));
    }



    // setters
    public void setAbilityName(String abilityName) {
        this.abilityName = abilityName;
    }

    public void setMinDamage(int minDamage) {
        this.minDamage = minDamage;
    }

    public void setMaxDamage(int maxDamage) {
        this.maxDamage = maxDamage;
    }

    public void setDamageAoe(int damageAoe) {
        this.damageAoe = damageAoe;
    }

    public void setSplashMin(int splashMin) {
        this.splashMin = splashMin;
    }

    public void setSplashMax(int splashMax) {
        this.splashMax = splashMax;
    }

    public void setSpecialAoe(int specialAoe) {
        this.specialAoe = specialAoe;
    }

    public void setIsConfuse(boolean isConfuse) {
        this.isConfuse = isConfuse;
    }

    public void setIsStun(boolean isStun) {
        this.isStun = isStun;
    }

    public void setIsRevive(boolean isRevive) {
        this.isRevive = isRevive;
    }

    public void setIsInvincibility(boolean isInvincibility) {
        this.isInvincibility = isInvincibility;
    }

    public void setIsSilence(boolean isSilence) {
        this.isSilence = isSilence;
    }

    public void setIsInvisible(boolean isInvisible) {
        this.isInvisible = isInvisible;
    }

    public void setIsBuff(boolean isBuff) {
        this.isBuff = isBuff;
    }

    public void setDotDuration(boolean dotDuration) {
        this.dotDuration = dotDuration;
    }

    public void setIsFire(boolean isFire) {
        this.isFire = isFire;
    }

    public void setIsPoison(boolean isPoison) {
        this.isPoison = isPoison;
    }

    public void setIsFrostBurn(boolean isFrostBurn) {
        this.isFrostBurn = isFrostBurn;
    }

    public void setIsBleed(boolean isBleed) {
        this.isBleed = isBleed;
    }

    public void setIsHealDot(boolean healOverTime) {
        this.isHealDot = healOverTime;
    }

    public void setIsManaDot(boolean manaOverTime) {
        this.isManaDot = manaOverTime;
    }

    public void setHealMin(int healMin) {
        this.healMin = healMin;
    }

    public void setHealMax(int healMax) {
        this.healMax = healMax;
    }

    public void setManaMin(int manaMin) {
        this.manaMin = manaMin;
    }

    public void setManaMax(int manaMax) {
        this.manaMax = manaMax;
    }

    public void setStrIncrease(int strIncrease) {
        this.strIncrease = strIncrease;
    }

    public void setIntIncrease(int intIncrease) {
        this.intIncrease = intIncrease;
    }

    public void setConIncrease(int conIncrease) {
        this.conIncrease = conIncrease;
    }

    public void setSpdIncrease(int spdIncrease) {
        this.spdIncrease = spdIncrease;
    }

    public void setEvadeIncrease(int evadeIncrease) {
        this.evadeIncrease = evadeIncrease;
    }

    public void setBlockIncrease(int blockIncrease) {
        this.blockIncrease = blockIncrease;
    }

    public void setStatDebuffDuration(int statDebuffDuration) {
        this.statDebuffDuration = statDebuffDuration;
    }

    public void setStrDecrease(int strDecrease) {
        this.strDecrease = strDecrease;
    }

    public void setIntDecrease(int intDecrease) {
        this.intDecrease = intDecrease;
    }

    public void setConDecrease(int conDecrease) {
        this.conDecrease = conDecrease;
    }

    public void setSpdDecrease(int spdDecrease) {
        this.spdDecrease = spdDecrease;
    }

    public void setEvadeDecrease(int evadeDecrease) {
        this.evadeDecrease = evadeDecrease;
    }

    public void setBlockDecrease(int blockDecrease) {
        this.blockDecrease = blockDecrease;
    }

    public void setDuration(int durationLeft) {
        this.duration = durationLeft;
    }

    public void setTempExtraHealth(int tempExtraHealth) {
        this.tempExtraHealth = tempExtraHealth;
    }

    private void setTier(int tier) {
        this.tier = tier;
    }

    public int getCooldownCurr() {
        return cooldownCurr;
    }
    public int getMinDamage() {
        return minDamage;
    }
    public int getMaxDamage() {
        return maxDamage;
    }
    public int getDamageAoe() {
        return damageAoe;
    }
    public int getSplashMin() {
        return splashMin;
    }
    public int getSplashMax() {
        return splashMax;
    }
    public int getSpecialAoe() {
        return specialAoe;
    }
    public boolean getIsConfuse() {
        return isConfuse;
    }
    public boolean getIsStun() {
        return isStun;
    }
    public boolean getIsRevive() {
        return isRevive;
    }
    public boolean getIsInvincibility() {
        return isInvincibility;
    }
    public boolean getIsSilence() {
        return isSilence;
    }
    public boolean getIsInvisible() {
        return isInvisible;
    }
    public boolean getIsBuff() {
        return isBuff;
    }
    public boolean getDotDuration() {
        return dotDuration;
    }
    public boolean getIsFire() {
        return isFire;
    }
    public boolean getIsPoison() {
        return isPoison;
    }
    public boolean getIsFrostBurn() {
        return isFrostBurn;
    }
    public boolean getIsBleed() {
        return isBleed;
    }
    public boolean getIsHealDot() {
        return isHealDot;
    }
    public boolean getIsManaDot() {
        return isManaDot;
    }
    public int getHealMin() {
        return healMin;
    }
    public int getHealMax() {
        return healMax;
    }
    public int getManaMin() {
        return manaMin;
    }
    public int getManaMax() {
        return manaMax;
    }
    public int getStrIncrease() {
        return strIncrease;
    }
    public int getIntIncrease() {
        return intIncrease;
    }
    public int getConIncrease() {
        return conIncrease;
    }
    public int getSpdIncrease() {
        return spdIncrease;
    }
    public int getEvadeIncrease() {
        return evadeIncrease;
    }
    public int getBlockIncrease() {
        return blockIncrease;
    }
    public int getStatDebuffDuration() {
        return statDebuffDuration;
    }
    public int getStrDecrease() {
        return strDecrease;
    }
    public int getIntDecrease() {
        return intDecrease;
    }
    public int getConDecrease() {
        return conDecrease;
    }
    public int getSpdDecrease() {
        return spdDecrease;
    }
    public int getEvadeDecrease() {
        return evadeDecrease;
    }
    public int getBlockDecrease() {
        return blockDecrease;
    }
    public int getDuration() {
        return duration;
    }
    public int getTempExtraHealth() {
        return tempExtraHealth;
    }
    public int getAbilityID() {
        return abilityID;
    }
    public int getTier() {
        return tier;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject toJSON = new JSONObject();
        try {
            toJSON.put(INVENTORY_TYPE, TYPE_ABILITY);
            toJSON.put(ID, getID());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return toJSON;
    }

    @Override
    public JSONObject serializeToJSON() throws JSONException {
        JSONObject JSONInventory = new JSONObject();
        JSONInventory.put(ABILITY_NAME, abilityName);
        JSONInventory.put(ABILITY_ID, abilityID);
        // cooldown
        JSONInventory.put(COOLDOWN_CURR, cooldownCurr);
        JSONInventory.put(COOLDOWN_MAX, cooldownMax);
        // damage
        JSONInventory.put(DAMAGE_MIN, minDamage);
        JSONInventory.put(DAMAGE_MAX, maxDamage);
        JSONInventory.put(DAMAGE_AOE, damageAoe);
        JSONInventory.put(SPLASH_DAMAGE_MIN, splashMin);
        JSONInventory.put(SPLASH_DAMAGE_MAX, splashMax);
        // specials
        JSONInventory.put(SPECIAL_AOE, specialAoe);
        JSONInventory.put(IS_STUN, isStun);
        JSONInventory.put(IS_CONFUSE, isConfuse);
        JSONInventory.put(IS_INVINCIBILITY, isInvincibility);
        JSONInventory.put(IS_INVISIBLE, isInvisible);
        JSONInventory.put(IS_SILENCE, isSilence);
        JSONInventory.put(IS_REVIVE, isRevive);
        // is buff
        JSONInventory.put(IS_BUFF, isBuff);
        // DOT
        JSONInventory.put(IS_FIRE, isFire);
        JSONInventory.put(IS_BLEED, isBleed);
        JSONInventory.put(IS_FROST_BURN, isFrostBurn);
        JSONInventory.put(IS_HEAL_DOT, isHealDot);
        JSONInventory.put(IS_MANA_DOT, isManaDot);
        // bars
        JSONInventory.put(HEAL_MIN, healMin);
        JSONInventory.put(HEAL_MAX, healMax);
        JSONInventory.put(MANA_MIN, manaMin);
        JSONInventory.put(MANA_MAX, manaMax);
        // temp Stat increase
        JSONInventory.put(STR_INCREASE, strIncrease);
        JSONInventory.put(INT_INCREASE, intIncrease);
        JSONInventory.put(CON_INCREASE, conIncrease);
        JSONInventory.put(SPD_INCREASE, spdIncrease);
        JSONInventory.put(BLOCK_INCREASE, blockIncrease);
        JSONInventory.put(EVASION_INCREASE, evadeIncrease);
        // temp stat decreases
        JSONInventory.put(STR_DECREASE, strDecrease);
        JSONInventory.put(INT_DECREASE, intDecrease);
        JSONInventory.put(CON_DECREASE, conDecrease);
        JSONInventory.put(SPD_DECREASE, spdDecrease);
        JSONInventory.put(BLOCK_DECREASE, blockDecrease);
        JSONInventory.put(EVASION_DECREASE, evadeDecrease);
        // ability duration
        JSONInventory.put(DURATION, duration);
        // temp extra
        JSONInventory.put(TEMP_EXTRA_HEALTH, tempExtraHealth);
        // tier
        JSONInventory.put(TIER, tier);
        JSONInventory.put(DESCRIPTION, abilityDescription);
        JSONInventory.put(IMAGE_RESOURCE, pictureResource);
        return JSONInventory;
    }

    @Override
    public int getID() {
        return abilityID;
    }
    @Override
    public String getType() {
        return Inventory.TYPE_ABILITY;
    }
    @Override
    public String getName() {
        return abilityName;
    }

    public static final String ABILITY_NAME = "ability_name";
    public static final String ABILITY_ID = "ability_id";
    public static final String COOLDOWN = "cooldown";
    public static final String COOLDOWN_CURR = "cooldown_curr";
    public static final String COOLDOWN_MAX = "cooldown_max";
    public static final String DAMAGE_MIN = "damage_min";
    public static final String DAMAGE_MAX = "damage_max";
    public static final String DAMAGE_AOE = "damage_aoe";
    public static final String SPLASH_DAMAGE_MIN = "splash_damage_min";
    public static final String SPLASH_DAMAGE_MAX = "splash_damage_max";
    public static final String SPECIAL_AOE = "special_aoe";
    public static final String IS_STUN = "is_stun";
    public static final String IS_CONFUSE = "is_confuse";
    public static final String IS_INVINCIBILITY = "is_invincibility";
    public static final String IS_INVISIBLE = "is_invisible";
    public static final String IS_SILENCE = "is_silence";
    public static final String IS_REVIVE = "is_revive";
    public static final String IS_BUFF = "is_buff";
    public static final String DOT_DURATION = "dot_duration";
    public static final String IS_FIRE = "is_fire";
    public static final String IS_POISON = "is_poison";
    public static final String IS_BLEED = "is_bleed";
    public static final String IS_FROST_BURN = "is_frost_burn";
    public static final String IS_HEAL_DOT = "heal_over_time";
    public static final String IS_MANA_DOT = "mana_over_time";
    public static final String HEAL_MIN = "heal_min";
    public static final String HEAL_MAX = "heal_max";
    public static final String MANA_MIN = "mana_min";
    public static final String MANA_MAX = "mana_max";
    public static final String STR_INCREASE = "str_increase";
    public static final String INT_INCREASE = "int_increase";
    public static final String CON_INCREASE = "con_increase";
    public static final String SPD_INCREASE = "spd_increase";
    public static final String BLOCK_INCREASE = "block_increase";
    public static final String EVASION_INCREASE = "evade_increase";
    public static final String STR_DECREASE = "str_decrease";
    public static final String INT_DECREASE = "int_decrease";
    public static final String CON_DECREASE = "con_decrease";
    public static final String SPD_DECREASE = "spd_decrease";
    public static final String BLOCK_DECREASE = "block_decrease";
    public static final String EVASION_DECREASE = "evade_decrease";
    public static final String DURATION = "length";
    public static final String TEMP_EXTRA_HEALTH = "temp_extra_health";
    public static final String TIER = "tier";
    public static final String DESCRIPTION = "description";
    // image resource
    public static final String IMAGE_RESOURCE = "image_resource";

}
