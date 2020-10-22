package com.habbybolan.textadventure.model.inventory;

import android.database.Cursor;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.repository.database.DatabaseAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

// create an item object that is copies from db.sqlite

public class Item extends Action {


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

    private int pictureResource;


    // constructor where database opened and closed elsewhere
    public Item(int itemID, DatabaseAdapter mDbHelper) throws ExecutionException, InterruptedException {
        this.itemID = itemID;
        Cursor cursor = mDbHelper.getItemCursorFromID(itemID);
        setVariables(mDbHelper, cursor);
        setPictureResource();
    }

    // constructor where database opened and closed elsewhere
    public Item(Cursor cursor, DatabaseAdapter mDbHelper, int itemID) throws ExecutionException, InterruptedException {
        this.itemID = itemID;
        setVariables(mDbHelper, cursor);
        setPictureResource();
    }

    public Item(String stringItem) {
        try {
            JSONObject JSONItem = new JSONObject(stringItem);
            if (!JSONItem.getBoolean(IS_GENERIC)) {
                isGeneric = false;
                itemName = JSONItem.getString(ITEM_NAME);
                itemID = JSONItem.getInt(ITEM_ID);
                if (JSONItem.has(ABILITY))
                    ability = new Ability(JSONItem.getJSONObject(ABILITY).toString());
                // stat
                strChange = JSONItem.getInt(STR_CHANGE);
                intChange = JSONItem.getInt(INT_CHANGE);
                conChange = JSONItem.getInt(CON_CHANGE);
                spdChange = JSONItem.getInt(SPD_CHANGE);
                evasionChange = JSONItem.getInt(EVASION_CHANGE);
                blockChange = JSONItem.getInt(BLOCK_CHANGE);
                // bars
                healthChange = JSONItem.getInt(HEALTH_CHANGE);
                manaChange = JSONItem.getInt(MANA_CHANGE);
                // gold
                goldChange = JSONItem.getInt(GOLD_CHANGE);
                // exp
                expChange = JSONItem.getInt(EXP_CHANGE);
                // misc
                description = JSONItem.getString(DESCRIPTION);
                isConsumable = JSONItem.getBoolean(IS_CONSUMABLE);
                tier = JSONItem.getInt(TIER);
                escapeTrap = JSONItem.getBoolean(ESCAPE_TRAP);
                // dots
                isFire = JSONItem.getBoolean(IS_FIRE);
                isBleed = JSONItem.getBoolean(IS_BLEED);
                isFrostBurn = JSONItem.getBoolean(IS_FROST_BURN);
                isPoison = JSONItem.getBoolean(IS_POISON);
                // duration
                duration = JSONItem.getInt(DURATION);
                pictureResource = JSONItem.getInt(IMAGE_RESOURCE);
            } else {
                isGeneric = true;
                itemName = JSONItem.getString(ITEM_NAME);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // sets the picture resource
    @Override
    public void setPictureResource() {
        // todo: get pic based on type and tier
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
    private void setVariables(DatabaseAdapter mDbHelper , Cursor cursor) throws ExecutionException, InterruptedException {
        // set up the item name
        int nameOfItemColIndex = cursor.getColumnIndex(ITEM_NAME);
        setItemName(cursor.getString(nameOfItemColIndex));
        // set ability
        int abilityColID = cursor.getColumnIndex(ABILITY_ID);
        int abilityID = cursor.getInt(abilityColID);
        if (cursor.getInt(abilityColID) != 0) {
            Ability ability = new Ability(abilityID, mDbHelper);
            setAbility(ability);
        }

        // set stat increases
        int strChangeColIndex = cursor.getColumnIndex(STR_CHANGE);
        setStrChange(cursor.getInt(strChangeColIndex));
        int IntChange = cursor.getColumnIndex(INT_CHANGE);
        setIntChange(cursor.getInt(IntChange));
        int conChange = cursor.getColumnIndex(CON_CHANGE);
        setConChange(cursor.getInt(conChange));
        int spdChange = cursor.getColumnIndex(SPD_CHANGE);
        setSpdChange(cursor.getInt(spdChange));
        int blockChange = cursor.getColumnIndex(BLOCK_CHANGE);
        setBlockChange(cursor.getInt(blockChange));
        int evasionChange = cursor.getColumnIndex(EVASION_CHANGE);
        setEvasionChange(cursor.getInt(evasionChange));
        // set bars
        int healthChange = cursor.getColumnIndex(HEALTH_CHANGE);
        setHealthChange(cursor.getInt(healthChange));
        int manaChange = cursor.getColumnIndex(MANA_CHANGE);
        setManaChange(cursor.getInt(manaChange));
        // set gold
        int goldChange = cursor.getColumnIndex(GOLD_CHANGE);
        setGoldChange(cursor.getInt(goldChange));
        // set exp
        int expChange = cursor.getColumnIndex(EXP_CHANGE);
        setExpChange(cursor.getInt(expChange));
        // specials
        int confuse = cursor.getColumnIndex(IS_CONFUSE);
        setIsConfuse(cursor.getInt(confuse)==1);
        int stun = cursor.getColumnIndex(IS_SILENCE);
        setIsStun(cursor.getInt(stun)==1);
        int silence = cursor.getColumnIndex(IS_SILENCE);
        setIsSilence(cursor.getInt(silence)==1);
        int invincible = cursor.getColumnIndex(IS_INVINCIBILITY);
        setIsInvincible(cursor.getInt(invincible)==1);
        int invisible = cursor.getColumnIndex(IS_INVISIBLE);
        setIsInvisible(cursor.getInt(invisible)==1);
        // description
        int description = cursor.getColumnIndex(DESCRIPTION);
        setDescription(cursor.getString(description));
        int consumable = cursor.getColumnIndex(IS_CONSUMABLE);
        setIsConsumable(cursor.getInt(consumable)==1);
        int tier = cursor.getColumnIndex(TIER);
        setTier(cursor.getInt(tier));
        int escapeTrapColIndex = cursor.getColumnIndex(ESCAPE_TRAP);
        setEscapeTrap(cursor.getInt(escapeTrapColIndex)==1);
        // DOTS
        int fireColIndex = cursor.getColumnIndex(IS_FIRE);
        setIsFire(cursor.getInt(fireColIndex)==1);
        int bleedColIndex = cursor.getColumnIndex(IS_BLEED);
        setIsBleed(cursor.getInt(bleedColIndex)==1);
        int poisonColIndex = cursor.getColumnIndex(IS_POISON);
        setIsPoison(cursor.getInt(poisonColIndex)==1);
        int frostBurnColIndex = cursor.getColumnIndex(IS_FROST_BURN);
        setIsFrostBurn(cursor.getInt(frostBurnColIndex)==1);
        // duration
        int durationColIndex = cursor.getColumnIndex(DURATION);
        setDuration(cursor.getInt(durationColIndex));
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
    public int getID() {
        return itemID;
    }
    @Override
    public String getType() {
        return Inventory.TYPE_ITEM;
    }
    @Override
    public String getName() {
        return itemName;
    }

    @Override
    public JSONObject serializeToJSON() throws JSONException {
        JSONObject JSONInventory = new JSONObject();
        if (!isGeneric) {
            JSONInventory.put(ITEM_NAME, itemName);
            JSONInventory.put(ITEM_ID, itemID);
            JSONInventory.put(IS_GENERIC, isGeneric);
            JSONInventory.put(INVENTORY_TYPE, TYPE_ITEM);
            if (ability != null) {
                JSONObject JSONAbility = ability.serializeToJSON();
                JSONInventory.put(ABILITY, JSONAbility);
            }
            // stats
            JSONInventory.put(STR_CHANGE, strChange);
            JSONInventory.put(INT_CHANGE, intChange);
            JSONInventory.put(CON_CHANGE, conChange);
            JSONInventory.put(SPD_CHANGE, spdChange);
            JSONInventory.put(EVASION_CHANGE, evasionChange);
            JSONInventory.put(BLOCK_CHANGE, blockChange);
            // bars
            JSONInventory.put(HEALTH_CHANGE, healthChange);
            JSONInventory.put(MANA_CHANGE, manaChange);
            // gold
            JSONInventory.put(GOLD_CHANGE, goldChange);
            // exp
            JSONInventory.put(EXP_CHANGE, expChange);
            // misc
            JSONInventory.put(DESCRIPTION, description);
            JSONInventory.put(IS_CONSUMABLE, isConsumable);
            JSONInventory.put(TIER, tier);
            JSONInventory.put(ESCAPE_TRAP, escapeTrap);
            // dots
            JSONInventory.put(IS_FIRE, isFire);
            JSONInventory.put(IS_BLEED, isBleed);
            JSONInventory.put(IS_POISON, isPoison);
            JSONInventory.put(IS_FROST_BURN, isFrostBurn);
            // duration
            JSONInventory.put(DURATION, duration);
            JSONInventory.put(IMAGE_RESOURCE, pictureResource);
        } else {
            JSONInventory.put(ITEM_NAME, itemName);
            JSONInventory.put(IS_GENERIC, isGeneric);
        }
        return JSONInventory;
    }


    @Override
    public void setActionUsed() {
        if (ability != null) ability.setActionUsed();
    }

    public static final String ITEM_NAME = "item_name";
    public static final String ITEM_ID = "item_id";
    // ability
    public static final String ABILITY = "ability";
    public static final String ABILITY_ID = "ability_id";
    // stats
    public static final String STR_CHANGE = "str_change";
    public static final String INT_CHANGE = "int_change";
    public static final String CON_CHANGE = "con_change";
    public static final String SPD_CHANGE = "spd_change";
    public static final String BLOCK_CHANGE = "block_change";
    public static final String EVASION_CHANGE = "evade_change";
    // bars
    public static final String HEALTH_CHANGE = "health_change";
    public static final String MANA_CHANGE = "mana_change";
    // gold
    public static final String GOLD_CHANGE = "gold_change";
    // exp
    public static final String EXP_CHANGE = "exp_change";
    // specials
    public static final String IS_CONFUSE = "confuse";
    public static final String IS_STUN = "stun";
    public static final String IS_SILENCE = "silence";
    public static final String IS_INVINCIBILITY = "invincible";
    public static final String IS_INVISIBLE = "invisible";
    // misc
    public static final String DESCRIPTION = "description";
    public static final String IS_CONSUMABLE = "consumable";
    public static final String TIER = "tier";
    public static final String ESCAPE_TRAP = "escape_trap";
    // dots
    public static final String IS_FIRE = "fire";
    public static final String IS_BLEED = "bleed";
    public static final String IS_POISON = "poison";
    public static final String IS_FROST_BURN = "frostBurn";
    // duration
    public static final String DURATION = "duration";
    // image resource
    public static final String IMAGE_RESOURCE = "image_resource";
    public static final String IS_GENERIC = "is_generic";

}
