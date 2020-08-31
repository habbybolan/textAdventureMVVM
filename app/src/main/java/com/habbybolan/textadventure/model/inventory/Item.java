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
    private boolean isConfuse = false;
    private boolean isStun = false;
    private boolean isSilence = false;
    private boolean isInvincible = false;
    private boolean isInvisible = false;
    // consumable
    private boolean isConsumable = false;
    // tier
    private int tier = 0;
    // ability
    private Ability ability;
    // trap disablers
    private boolean escapeTrap = false;
    // DOTS
    private boolean isFire = false;
    private boolean isBleed = false;
    private boolean isPoison = false;
    private boolean isFrostBurn = false;
    private boolean isHealthDot = false;
    private boolean isManaDot = false;
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

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        else if (o.getClass() != getClass()) return false;
        else if (o == this) return true;
        else return (checkItemEquality((Item) o));
    }

    // helper for checking if 2 ability scrolls are equal
    private boolean checkItemEquality(Item item) {
        return item.getName().equals(getName());
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
        setIsConfuse(cursor.getInt(confuse)==1);
        int stun = cursor.getColumnIndex("stun");
        setIsStun(cursor.getInt(stun)==1);
        int silence = cursor.getColumnIndex("silence");
        setIsSilence(cursor.getInt(silence)==1);
        int invincible = cursor.getColumnIndex("invincible");
        setIsInvincible(cursor.getInt(invincible)==1);
        int invisible = cursor.getColumnIndex("invisible");
        setIsInvisible(cursor.getInt(invisible)==1);
        // description
        int description = cursor.getColumnIndex("description");
        setDescription(cursor.getString(description));
        int consumable = cursor.getColumnIndex("consumable");
        setIsConsumable(cursor.getInt(consumable)==1);
        int tier = cursor.getColumnIndex("tier");
        setTier(cursor.getInt(tier));
        int escapeTrapColIndex = cursor.getColumnIndex("escape_trap");
        setEscapeTrap(cursor.getInt(escapeTrapColIndex)==1);
        // DOTS
        int fireColIndex = cursor.getColumnIndex("fire");
        setIsFire(cursor.getInt(fireColIndex)==1);
        int bleedColIndex = cursor.getColumnIndex("bleed");
        setIsBleed(cursor.getInt(bleedColIndex)==1);
        int poisonColIndex = cursor.getColumnIndex("poison");
        setIsPoison(cursor.getInt(poisonColIndex)==1);
        int frostBurnColIndex = cursor.getColumnIndex("frostBurn");
        setIsFrostBurn(cursor.getInt(frostBurnColIndex)==1);
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
    public void setIsConfuse(boolean isConfuse) {
        this.isConfuse = isConfuse;
    }
    public void setIsStun(boolean isStun) {
        this.isStun = isStun;
    }
    public void setIsSilence(boolean isSilence) {
        this.isSilence = isSilence;
    }
    public void setIsInvincible(boolean isInvincible) {
        this.isInvincible = isInvincible;
    }
    public void setIsInvisible(boolean isInvisible) {
        this.isInvisible = isInvisible;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setIsConsumable(boolean isConsumable) {
        this.isConsumable = isConsumable;
    }
    public void setTier(int tier) {
        this.tier = tier;
    }
    public void setEscapeTrap(boolean escapeTrap) {
        this.escapeTrap = escapeTrap;
    }
    public void setIsFire(boolean isFire) {
        this.isFire = isFire;
    }
    public void setIsBleed(boolean isBleed) {
        this.isBleed = isBleed;
    }
    public void setIsPoison(boolean isPoison) {
        this.isPoison = isPoison;
    }
    public void setIsFrostBurn(boolean isFrostBurn) {
        this.isFrostBurn = isFrostBurn;
    }
    public void setIsHealthDot(boolean isHealthDot) {
        this.isHealthDot = isHealthDot;
    }
    public void setIsManaDot(boolean isManaDot) {
        this.isManaDot = isManaDot;
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
    public boolean getIsConfuse() {
        return isConfuse;
    }
    public boolean getIsStun() {
        return isStun;
    }
    public boolean getIsSilence() {
        return isSilence;
    }
    public boolean getIsInvincible() {
        return isInvincible;
    }
    public boolean getIsInvisible() {
        return isInvisible;
    }
    public boolean getIsConsumable() {
        return isConsumable;
    }
    public int getTier() {
        return tier;
    }
    public boolean getEscapeTrap() {
        return escapeTrap;
    }
    public boolean getIsFire() {
        return isFire;
    }
    public boolean getIsBleed() {
        return isBleed;
    }
    public boolean getIsPoison() {
        return isPoison;
    }
    public boolean getIsFrostBurn() {
        return isFrostBurn;
    }
    public boolean getIsHealthDot() {
        return isHealthDot;
    }
    public boolean getIsManaDot() {
        return isManaDot;
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
