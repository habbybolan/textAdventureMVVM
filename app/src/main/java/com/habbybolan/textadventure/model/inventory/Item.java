package com.habbybolan.textadventure.model.inventory;

import android.database.Cursor;

import com.habbybolan.textadventure.repository.database.DatabaseAdapter;

import java.util.concurrent.ExecutionException;

// create an item object that is copies from db.sqlite

public class Item implements Inventory{


    // todo: add negative effects to item database. [ex) adds +100 health but permanent confusion]

    // stat changes
    private int intChange = 0; // how much the item increases Int stat
    private int strChange = 0; // how much the item increases Str stat
    private int conChange = 0; // how much the item increases Con stat
    private int spdChange = 0; // how much the item increases Spd stat
    // gold
    private int goldChange = 0; // item increase (%)
    // exp
    private int expChange = 0; // exp increase (%)

    private int evasionChange = 0; // evasion increase (%)
    private int blockChange = 0; // block increase (%)
    // health/mana
    private int healthChange = 0;
    private int manaChange = 0;
    // specials
    private int isConfuse = 0;
    private int isStun = 0;
    private int isSilence = 0;
    private int isInvincible = 0;
    private int isInvisible = 0;
    // consumable
    private int isConsumable = 0;
    // tier
    private int tier = 0;
    // ability
    private Ability ability;
    // trap disablers
    private int escapeTrap = 0;
    // DOTS
    private int isFire = 0;
    private int isBleed = 0;
    private int isPoison = 0;
    private int isFrostBurn = 0;
    // duration
    private int duration = 0;
    // value for quest deliver encounter
    private boolean isGeneric = false;
    private int turnToDeliver = -1;


    // description and name
    private String description = "";
    private String itemName = "";

    static public final String table = "items";

    private int itemID;


    // constructor where database opened and closed elsewhere
    public Item(String itemIDString, DatabaseAdapter mDbHelper) throws ExecutionException, InterruptedException {
        if (isInteger(itemIDString)) {
            // check if the ID is an integer
            int itemID = Integer.parseInt(itemIDString);
            this.itemID = itemID;
            setVariables(mDbHelper, itemID);
        } else {
            // otherwise, itemID is a name and it's a generic item (no database, used for deliver QuestEncounter)
            isGeneric = true;
            itemName = itemIDString;
        }
    }

    private static boolean isInteger(String s) {
        boolean isValidInteger = false;
        try {
            Integer.parseInt(s);
            // s is a valid integer
            isValidInteger = true;
        }
        catch (NumberFormatException ex) {
            // s is not an integer - do nothing
        }
        return isValidInteger;
    }


    // creates a generic item, given name and description, for deliver quest
    public Item(String name, String description, int turnToDeliver) {
        setItemName(name);
        setDescription(description);
        isGeneric = true;
        this.turnToDeliver = turnToDeliver;
    }

    // sets all the variables that describe the weapon
    private void setVariables(DatabaseAdapter mDbHelper , int itemID) throws ExecutionException, InterruptedException {
        Cursor cursor = mDbHelper.getData(table);
        cursor.moveToPosition(itemID-1);
        // set up the item name
        int nameOfItemColIndex = cursor.getColumnIndex("item_name");
        setItemName(cursor.getString(nameOfItemColIndex));
        // set ability
        int abilityColID = cursor.getColumnIndex("ability_id");
        if (cursor.getInt(abilityColID) != 0) {
            Ability ability = new Ability(cursor.getInt(abilityColID), mDbHelper);
            setAbility(ability);
        }

        // set stat increases
        int strChangeColIndex = cursor.getColumnIndex("str_change");
        setStrChange(cursor.getInt(strChangeColIndex));
        int IntChange = cursor.getColumnIndex("int_change");
        setIntChange(cursor.getInt(IntChange));
        int conChange = cursor.getColumnIndex("con_change");
        setConChange(cursor.getInt(conChange));
        int spdChange = cursor.getColumnIndex("spd_change");
        setSpdChange(cursor.getInt(spdChange));
        int blockChange = cursor.getColumnIndex("block_change");
        setBlockChange(cursor.getInt(blockChange));
        int evasionChange = cursor.getColumnIndex("evade_change");
        setEvasionChange(cursor.getInt(evasionChange));
        // set bars
        int healthChange = cursor.getColumnIndex("health_change");
        setHealthChange(cursor.getInt(healthChange));
        int manaChange = cursor.getColumnIndex("mana_change");
        setManaChange(cursor.getInt(manaChange));
        // set gold
        int goldChange = cursor.getColumnIndex("gold_change");
        setGoldChange(cursor.getInt(goldChange));
        // set exp
        int expChange = cursor.getColumnIndex("exp_change");
        setExpChange(cursor.getInt(expChange));
        // specials
        int confuse = cursor.getColumnIndex("confuse");
        setIsConfuse(cursor.getInt(confuse));
        int stun = cursor.getColumnIndex("stun");
        setIsStun(stun);
        int silence = cursor.getColumnIndex("silence");
        setIsSilence(cursor.getInt(silence));
        int invincible = cursor.getColumnIndex("invincible");
        setIsInvincible(cursor.getInt(invincible));
        int invisible = cursor.getColumnIndex("invisible");
        setIsInvisible(cursor.getInt(invisible));
        // description
        int description = cursor.getColumnIndex("description");
        setDescription(cursor.getString(description));
        int consumable = cursor.getColumnIndex("consumable");
        setIsConsumable(cursor.getInt(consumable));
        int tier = cursor.getColumnIndex("tier");
        setTier(cursor.getInt(tier));
        int escapeTrapColIndex = cursor.getColumnIndex("escape_trap");
        setEscapeTrap(cursor.getInt(escapeTrapColIndex));
        // DOTS
        int fireColIndex = cursor.getColumnIndex("fire");
        setIsFire(cursor.getInt(fireColIndex));
        int bleedColIndex = cursor.getColumnIndex("bleed");
        setIsBleed(cursor.getInt(bleedColIndex));
        int poisonColIndex = cursor.getColumnIndex("poison");
        setIsPoison(cursor.getInt(poisonColIndex));
        int frostBurnColIndex = cursor.getColumnIndex("frostBurn");
        setIsFrostBurn(cursor.getInt(frostBurnColIndex));
        // duration
        int durationColIndex = cursor.getColumnIndex("duration");
        setDuration(cursor.getInt(durationColIndex));
        cursor.close();
    }

    // setters
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    public void setAbility(Ability ability) {
        this.ability = ability;
    }
    public void setStrChange(int strChange) {
        this.strChange = strChange;
    }
    public void setIntChange(int intChange) {
        this.intChange = intChange;
    }
    public void setConChange(int conChange) {
        this.conChange = conChange;
    }
    public void setSpdChange(int spdChange) {
        this.spdChange = spdChange;
    }
    public void setEvasionChange(int evasionChange) {
        this.evasionChange = evasionChange;
    }
    public void setBlockChange(int blockChange) {
        this.blockChange = blockChange;
    }
    public void setHealthChange(int healthChange) {
        this.healthChange = healthChange;
    }
    public void setManaChange(int manaChange) {
        this.manaChange = manaChange;
    }
    public void setGoldChange(int goldChange) {
        this.goldChange = goldChange;
    }
    public void setExpChange(int expChange) {
        this.expChange = expChange;
    }
    public void setIsConfuse(int isConfuse) {
        this.isConfuse = isConfuse;
    }
    public void setIsStun(int isStun) {
        this.isStun = isStun;
    }
    public void setIsSilence(int isSilence) {
        this.isSilence = isSilence;
    }
    public void setIsInvincible(int isInvincible) {
        this.isInvincible = isInvincible;
    }
    public void setIsInvisible(int isInvisible) {
        this.isInvisible = isInvisible;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setIsConsumable(int isConsumable) {
        this.isConsumable = isConsumable;
    }
    public void setTier(int tier) {
        this.tier = tier;
    }
    public void setEscapeTrap(int escapeTrap) {
        this.escapeTrap = escapeTrap;
    }
    public void setIsFire(int isFire) {
        this.isFire = isFire;
    }
    public void setIsBleed(int isBleed) {
        this.isBleed = isBleed;
    }
    public void setIsPoison(int isPoison) {
        this.isPoison = isPoison;
    }
    public void setIsFrostBurn(int isFrostBurn) {
        this.isFrostBurn = isFrostBurn;
    }
    private void setDuration(int duration) {
        this.duration = duration;
    }


    // getters
    public int getIntChange() {
        return intChange;
    }
    public int getStrChange() {
        return strChange;
    }
    public int getConChange() {
        return conChange;
    }
    public int getSpdChange() {
        return spdChange;
    }
    public int getHealthChange() {
        return healthChange;
    }
    public int getManaChange() {
        return manaChange;
    }
    public int getEvasionChange() {
        return evasionChange;
    }
    public int getBlockChange() {
        return blockChange;
    }
    public String getDescription() {
        return description;
    }
    public int getGoldChange() {
        return goldChange;
    }
    public int getExpChange() {
        return expChange;
    }
    public int getItemID() {
        return itemID;
    }
    public Ability getAbility() {
        return ability;
    }
    public int getIsConfuse() {
        return isConfuse;
    }
    public int getIsStun() {
        return isStun;
    }
    public int getIsSilence() {
        return isSilence;
    }
    public int getIsInvincible() {
        return isInvincible;
    }
    public int getIsInvisible() {
        return isInvisible;
    }
    public int getIsConsumable() {
        return isConsumable;
    }
    public int getTier() {
        return tier;
    }
    public int getEscapeTrap() {
        return escapeTrap;
    }
    public int getIsFire() {
        return isFire;
    }
    public int getIsBleed() {
        return isBleed;
    }
    public int getIsPoison() {
        return isPoison;
    }
    public int getIsFrostBurn() {
        return isFrostBurn;
    }
    public int getDuration() {
        return duration;
    }
    public boolean getIsGeneric() {
        return isGeneric;
    }
    public int getTurnToDeliver() {
        return turnToDeliver;
    }

    @Override
    public String getType() {
        return Inventory.TYPE_ITEM;
    }
    @Override
    public String getName() {
        return itemName;
    }
}
