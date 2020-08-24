package com.habbybolan.textadventure.model.inventory;

import android.database.Cursor;

import com.habbybolan.textadventure.repository.database.DatabaseAdapter;

import java.util.concurrent.ExecutionException;

public class Ability implements Inventory{

    // ability
    String abilityName;
    String abilityDescription;
    // cooldowns
    private int cooldownMax = 0;
    private int cooldownLeft = 0;

    // direct damage
    private int minDamage = 0;
    private int maxDamage = 0;
    // aoe
    private int damageAoe = 0; // number of enemies hit by splash damage
    private int splashMin = 0;
    private int splashMax = 0;
    private int specialAoe = 0; // numbers of enemies hit by special ability aoe

    // specials
    private int isConfuse = 0; // gives confusion to target, making them attack random
    private int isStun = 0; // makes the target skip turns
    private int isRevive = 0; //  revives a target
    private int isInvincibility = 0; // makes the target invincible
    private int isSilence = 0; // makes the target unable to use abilities
    private int isInvisible = 0;

    // if it's a buff (for teammates) or not
    private int isBuff = 0;

    // damage over times
    private int dotDuration = 0;
    private int isFire = 0; // applies fire
    private int isPoison = 0; // applies poison
    private int isFrostBurn = 0; // applies frost burn
    private int isBleed = 0; // applies bleed
    private int isHealDot = 0; // health restored after each turn
    private int isManaDot = 0; // mana restored after each turn

    // bars
    private int healMin = 0;
    private int healMax = 0;
    private int manaMin = 0;
    private int manaMax = 0;

    // extra health
    private int tempExtraHealth = 0;

    // time the ability lasts
    private int durationLeft = 0;

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

    // cost to use ability in mana
    private int cost = 0;

    // ID
    private int abilityID;
    static public final String table = "abilities";


    // constructor to read database for ability
    public Ability(int abilityID, DatabaseAdapter mDbHelper) throws ExecutionException, InterruptedException {
        this.abilityID = abilityID;
        setVariables(mDbHelper, abilityID);
    }

    private void setVariables(DatabaseAdapter mDbHelper, int abilityID) throws ExecutionException, InterruptedException {
        Cursor cursor = mDbHelper.getData(table);
        cursor.moveToPosition(abilityID - 1);
        // set ability name
        int nameOfAbilityColIndex = cursor.getColumnIndex("ability_name");
        setAbilityName(cursor.getString(nameOfAbilityColIndex));
        // set cooldown
        int cooldownColIndex = cursor.getColumnIndex("cooldown");
        setcooldownMax(cursor.getInt(cooldownColIndex));
        setCooldownCurr(0);
        // set direct damage
        int damageMinColIndex = cursor.getColumnIndex("damage_min");
        setMinDamage(cursor.getInt(damageMinColIndex));
        int damageMaxColIndex = cursor.getColumnIndex("damage_max");
        setMaxDamage(cursor.getInt(damageMaxColIndex));
        // set aoe
        int damageAoeColIndex = cursor.getColumnIndex("damage_aoe");
        setDamageAoe(cursor.getInt(damageAoeColIndex));
        int splashMinColIndex = cursor.getColumnIndex("splash_damage_min");
        setSplashMin(cursor.getInt(splashMinColIndex));
        int splashMaxColIndex = cursor.getColumnIndex("splash_damage_max");
        setSplashMax(cursor.getInt(splashMaxColIndex));
        int specialAoeColIndex = cursor.getColumnIndex("special_aoe");
        setSpecialAoe(cursor.getInt(specialAoeColIndex));
        // set specials
        int isStunColIndex = cursor.getColumnIndex("is_stun");
        setIsStun(cursor.getInt(isStunColIndex));
        int isConfuseColIndex = cursor.getColumnIndex("is_confuse");
        setIsConfuse(cursor.getInt(isConfuseColIndex));
        int isReviveColIndex = cursor.getColumnIndex("is_revive");
        setIsRevive(cursor.getInt(isReviveColIndex));
        int isInvincibilityColIndex = cursor.getColumnIndex("is_invincibility");
        setIsInvincibility(cursor.getInt(isInvincibilityColIndex));
        int isSilenceColIndex = cursor.getColumnIndex("is_silence");
        setIsSilence(cursor.getInt(isSilenceColIndex));
        int isInvisibleColIndex = cursor.getColumnIndex("is_invisible");
        setIsInvisible(cursor.getInt(isInvisibleColIndex));
        // set if ability is a buff
        int isBuffColIndex = cursor.getColumnIndex("is_buff");
        setIsBuff(cursor.getInt(isBuffColIndex));
        // DOT
        int dotDurationColIndex = cursor.getColumnIndex("dot_duration");
        setDotDuration(cursor.getInt(dotDurationColIndex));
        int isFireColIndex = cursor.getColumnIndex("is_fire");
        setIsFire(cursor.getInt(isFireColIndex));
        int isPoisonColIndex = cursor.getColumnIndex("is_poison");
        setIsPoison(cursor.getInt(isPoisonColIndex));
        int isBleedColIndex = cursor.getColumnIndex("is_bleed");
        setIsBleed(cursor.getInt(isBleedColIndex));
        int isFrostBurnColIndex = cursor.getColumnIndex("is_frost_burn");
        setIsFrostBurn(cursor.getInt(isFrostBurnColIndex));
        int healOverTimeColIndex = cursor.getColumnIndex("heal_over_time");
        setIsHealDot(cursor.getInt(healOverTimeColIndex));
        int manaOverTimeColIndex = cursor.getColumnIndex("mana_over_time");
        setIsManaDot(cursor.getInt(manaOverTimeColIndex));
        // bars
        int healMinColIndex = cursor.getColumnIndex("heal_min");
        setHealMin(cursor.getInt(healMinColIndex));
        int healMaxColIndex = cursor.getColumnIndex("heal_max");
        setHealMax(cursor.getInt(healMaxColIndex));
        int manaMinColIndex = cursor.getColumnIndex("mana_min");
        setManaMin(cursor.getInt(manaMinColIndex));
        int manaMaxColIndex = cursor.getColumnIndex("mana_max");
        setManaMax(cursor.getInt(manaMaxColIndex));
        // temp stat increases
        int strIncreaseColIndex = cursor.getColumnIndex("str_increase");
        setStrIncrease(cursor.getInt(strIncreaseColIndex));
        int intIncreaseColIndex = cursor.getColumnIndex("int_increase");
        setIntIncrease(cursor.getInt(intIncreaseColIndex));
        int conIncreaseColIndex = cursor.getColumnIndex("con_increase");
        setConIncrease(cursor.getInt(conIncreaseColIndex));
        int spdIncreaseColIndex = cursor.getColumnIndex("spd_increase");
        setSpdIncrease(cursor.getInt(spdIncreaseColIndex));
        int blockIncreaseColIndex = cursor.getColumnIndex("block_increase");
        setBlockIncrease(cursor.getInt(blockIncreaseColIndex));
        int evadeIncreaseColIndex = cursor.getColumnIndex("evade_increase");
        setEvadeIncrease(cursor.getInt(evadeIncreaseColIndex));
        // temp stat decreases
        int statDebuffDurationColIndex = cursor.getColumnIndex("stat_debuff_duration");
        setStatDebuffDuration(cursor.getInt(statDebuffDurationColIndex));
        int strDecreaseColIndex = cursor.getColumnIndex("str_decrease");
        setStrDecrease(cursor.getInt(strDecreaseColIndex));
        int intDecreaseColIndex = cursor.getColumnIndex("int_decrease");
        setIntDecrease(cursor.getInt(intDecreaseColIndex));
        int conDecreaseColIndex = cursor.getColumnIndex("con_decrease");
        setConDecrease(cursor.getInt(conDecreaseColIndex));
        int spdDecreaseColIndex = cursor.getColumnIndex("spd_decrease");
        setSpdDecrease(cursor.getInt(spdDecreaseColIndex));
        int blockDecreaseColIndex = cursor.getColumnIndex("block_decrease");
        setBlockDecrease(cursor.getInt(blockDecreaseColIndex));
        int evadeDecreaseColIndex = cursor.getColumnIndex("evade_decrease");
        setEvadeDecrease(cursor.getInt(evadeDecreaseColIndex));
        // set number of turns an ability lasts for
        int turnsLeftColIndex = cursor.getColumnIndex("length");
        setDuration(cursor.getInt(turnsLeftColIndex));
        // set temporary amount of extra health
        int tempExtraHealthColIndex = cursor.getColumnIndex("temp_extra_health");
        setTempExtraHealth(cursor.getInt(tempExtraHealthColIndex));
        // tier
        int tierColIndex = cursor.getColumnIndex("tier");
        setTier(cursor.getInt(tierColIndex));
        cursor.close();
    }


    /*
    static public int getRandAbility() {
        Random rand = new Random();
        return rand.nextInt(numAbilities);
    }*/


    // setters
    public void setAbilityName(String abilityName) {
        this.abilityName = abilityName;
    }

    public void setcooldownMax(int cooldownMax) {
        this.cooldownMax = cooldownMax;
    }

    public void setCooldownCurr(int cooldownLeft) {
        this.cooldownLeft = cooldownLeft;
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

    public void setIsConfuse(int isConfuse) {
        this.isConfuse = isConfuse;
    }

    public void setIsStun(int isStun) {
        this.isStun = isStun;
    }

    public void setIsRevive(int isRevive) {
        this.isRevive = isRevive;
    }

    public void setIsInvincibility(int isInvincibility) {
        this.isInvincibility = isInvincibility;
    }

    public void setIsSilence(int isSilence) {
        this.isSilence = isSilence;
    }

    public void setIsInvisible(int isInvisible) {
        this.isInvisible = isInvisible;
    }

    public void setIsBuff(int isBuff) {
        this.isBuff = isBuff;
    }

    public void setDotDuration(int dotDuration) {
        this.dotDuration = dotDuration;
    }

    public void setIsFire(int isFire) {
        this.isFire = isFire;
    }

    public void setIsPoison(int isPoison) {
        this.isPoison = isPoison;
    }

    public void setIsFrostBurn(int isFrostBurn) {
        this.isFrostBurn = isFrostBurn;
    }

    public void setIsBleed(int isBleed) {
        this.isBleed = isBleed;
    }

    public void setIsHealDot(int healOverTime) {
        this.isHealDot = healOverTime;
    }

    public void setIsManaDot(int manaOverTime) {
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
        this.durationLeft = durationLeft;
    }

    public void setTempExtraHealth(int tempExtraHealth) {
        this.tempExtraHealth = tempExtraHealth;
    }

    private void setTier(int tier) {
        this.tier = tier;
    }

    // getters
    public int getCooldownMax() {
        return cooldownMax;
    }
    public int getCooldownLeft() {
        return cooldownLeft;
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
    public int getIsConfuse() {
        return isConfuse;
    }
    public int getIsStun() {
        return isStun;
    }
    public int getIsRevive() {
        return isRevive;
    }
    public int getIsInvincibility() {
        return isInvincibility;
    }
    public int getIsSilence() {
        return isSilence;
    }
    public int getIsInvisible() {
        return isInvisible;
    }
    public int getIsBuff() {
        return isBuff;
    }
    public int getDotDuration() {
        return dotDuration;
    }
    public int getIsFire() {
        return isFire;
    }
    public int getIsPoison() {
        return isPoison;
    }
    public int getIsFrostBurn() {
        return isFrostBurn;
    }
    public int getIsBleed() {
        return isBleed;
    }
    public int getIsHealDot() {
        return isHealDot;
    }
    public int getIsManaDot() {
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
        return durationLeft;
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
    public String getType() {
        return Inventory.TYPE_ABILITY;
    }
    @Override
    public String getName() {
        return abilityName;
    }
}
