package com.habbybolan.textadventure.model.characterentity;

import android.content.Context;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.model.effects.Dot;
import com.habbybolan.textadventure.model.effects.Effect;
import com.habbybolan.textadventure.model.effects.SpecialEffect;
import com.habbybolan.textadventure.model.effects.TempBar;
import com.habbybolan.textadventure.model.effects.TempStat;
import com.habbybolan.textadventure.model.inventory.Ability;
import com.habbybolan.textadventure.model.inventory.Item;
import com.habbybolan.textadventure.model.inventory.weapon.Weapon;
import com.habbybolan.textadventure.repository.database.DatabaseAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Character object for the user's character they play. Only one created at any given time.
 */
public class Character extends CharacterEntity {
    public final static int HEALTH_CON_MULTIPLIER = 10;
    public final static int MANA_INT_MULTIPLIER = 10;
    public final static int BASE_HEALTH = 20;
    public final static int BASE_MANA = 20;

    public static final String PALADIN_CLASS_TYPE = "Paladin";
    public static final String WARRIOR_CLASS_TYPE = "Warrior";
    public static final String WIZARD_CLASS_TYPE = "Wizard";
    public static final String ARCHER_CLASS_TYPE = "Archer";

    // all MAX values for a character
    public static final int MAX_ABILITIES = 4;
    public static final int MAX_WEAPONS = 2;
    public static final int MAX_ITEMS = 5;
    public final int NUM_ACTIONS = 2;

    // holds all the buff and debuff abilities applied to the player character
    private ArrayList<Ability> appliedAbilities = new ArrayList<>();

    // Character info
    private String classType;
    private int exp;
    private int gold;
    private int goldIncrease;


    public Character(String characterData, Context context) {
        JSONObject characterObject;
        ID = 1;
        isCharacter = true;
        try {
            characterObject = new JSONObject(characterData);
            classType = characterObject.getString("class");
            // stats
            strength = characterObject.getInt("str");
            strBase = characterObject.getInt("strBase");
            strIncrease = characterObject.getInt("strIncrease");
            strDecrease = characterObject.getInt("strDecrease");
            intelligence = characterObject.getInt("int");
            intBase = characterObject.getInt("intBase");
            intIncrease = characterObject.getInt("intIncrease");
            intDecrease = characterObject.getInt("intDecrease");
            constitution = characterObject.getInt("con");
            conBase = characterObject.getInt("conBase");
            conIncrease = characterObject.getInt("conIncrease");
            conDecrease = characterObject.getInt("conDecrease");
            speed = characterObject.getInt("spd");
            spdBase = characterObject.getInt("spdBase");
            spdIncrease = characterObject.getInt("spdIncrease");
            spdDecrease = characterObject.getInt("spdDecrease");
            numStatPoints = strBase + intBase + conBase + spdBase;
            // misc
            level = characterObject.getInt("level");
            gold = characterObject.getInt("gold");
            // bars
            health = characterObject.getInt("health");
            maxHealth = characterObject.getInt("maxHealth");
            mana = characterObject.getInt("mana");
            maxMana = characterObject.getInt("maxMana");
            DatabaseAdapter db = new DatabaseAdapter(context);
            // abilities
            numAbilities = 0;
            JSONArray abilitiesArray = characterObject.getJSONArray("abilities");
            for (int i= 0; i < abilitiesArray.length(); i++) {
                String abilityString = abilitiesArray.getString(i);
                if (isInventoryID(abilityString))
                    abilities.add(new Ability(abilitiesArray.getInt(i), db));
                 else
                    abilities.add(new Ability(abilityString));
                 numAbilities++;

            }
            // weapons
            JSONArray weaponsArray = characterObject.getJSONArray("weapons");
            numWeapons = 0;
            for (int i = 0; i < weaponsArray.length(); i++) {
                String weaponString = weaponsArray.getString(i);
                if (isInventoryID(weaponString))
                    weapons.add(new Weapon(weaponsArray.getInt(i), db));
                else
                    weapons.add(new Weapon(weaponString));
                numWeapons++;

            }
            // items
            numItems = 0;
            JSONArray itemsArray = characterObject.getJSONArray("items");
            for (int i = 0; i < itemsArray.length(); i++) {
                String itemString = itemsArray.getString(i);
                if (isInventoryID(itemString))
                    items.add(new Item(itemsArray.getInt(i), db));
                else
                    items.add(new Item(itemsArray.getJSONObject(i).toString()));
                numItems++;
            }

            // DOTS
            isFire = characterObject.getBoolean(Effect.FIRE);
            isBleed = characterObject.getBoolean(Effect.BLEED);
            isPoison = characterObject.getBoolean(Effect.POISON);
            isFrostBurn = characterObject.getBoolean(Effect.FROSTBURN);
            isHealDot = characterObject.getBoolean(Effect.HEALTH_DOT);
            isManaDot = characterObject.getBoolean(Effect.MANA_DOT);
            JSONArray dotList = characterObject.getJSONArray("dotList");
            for (int i = 0; i < dotList.length(); i++) {
                JSONArray dot = (JSONArray) dotList.get(i);
                this.dotList.add(new Dot(dot.getString(0), dot.getInt(1)));
            }
            // SPECIAL
            isStun = characterObject.getBoolean(Effect.STUN);
            isConfuse = characterObject.getBoolean(Effect.CONFUSE);
            isSilence = characterObject.getBoolean(Effect.SILENCE);
            isInvincible = characterObject.getBoolean(Effect.INVINCIBILITY);
            isInvisible = characterObject.getBoolean(Effect.INVISIBILITY);
            JSONArray specialList = characterObject.getJSONArray("specialList");
            for (int i = 0; i < specialList.length(); i++) {
                JSONArray special = (JSONArray) specialList.get(i);
                this.specialList.add(new SpecialEffect(special.getString(0), special.getInt(1)));
            }
            // tempHealth
            JSONArray tempHealthArray = characterObject.getJSONArray("tempHealthList");
            for (int i = 0; i < tempHealthArray.length(); i++) {
                JSONArray tempHealth = (JSONArray) tempHealthArray.get(i);
                int duration = tempHealth.getInt(0);
                int amount = tempHealth.getInt(1);
                TempBar tempBar = new TempBar(CharacterEntity.TEMP_HEALTH, duration, amount);
                tempHealthList.add(tempBar);
            }
            // tempMana
            JSONArray tempManaArray = characterObject.getJSONArray("tempManaList");
            for (int i = 0; i < tempManaArray.length(); i++) {
                JSONArray tempMana = (JSONArray) tempManaArray.get(i);
                int duration = tempMana.getInt(0);
                int amount = tempMana.getInt(1);
                TempBar tempBar = new TempBar(CharacterEntity.TEMP_MANA, duration, amount);
                tempManaList.add(tempBar);
            }
            // stat Increase
            JSONArray statIncreaseArray = characterObject.getJSONArray("statIncreaseList");
            for (int i = 0; i < statIncreaseArray.length(); i++) {
                JSONArray statIncrease = (JSONArray) statIncreaseArray.get(i);
                String type = statIncrease.getString(0);
                int duration = statIncrease.getInt(1);
                int amount = statIncrease.getInt(2);
                TempStat tempStat = new TempStat(type, duration, amount);
                statIncreaseList.add(tempStat);
            }
            // stat Decrease
            JSONArray statDecreaseArray = characterObject.getJSONArray("statDecreaseList");
            for (int i = 0; i < statDecreaseArray.length(); i++) {
                JSONArray statDecrease = (JSONArray) statDecreaseArray.get(i);
                String type = statDecrease.getString(0);
                int duration = statDecrease.getInt(1);
                int amount = statDecrease.getInt(2);
                TempStat tempStat = new TempStat(type, duration, amount);
                statDecreaseList.add(tempStat);
            }

        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        switch(classType) {
            case PALADIN_CLASS_TYPE:
                drawableResID = R.drawable.paladin_icon;
                drawableDeadResID = R.drawable.paladin_icon;
            case WIZARD_CLASS_TYPE:
                drawableResID = R.drawable.wizard_icon;
                drawableDeadResID = R.drawable.wizard_icon;
            case WARRIOR_CLASS_TYPE:
                drawableResID = R.drawable.warrior_icon;
                drawableDeadResID = R.drawable.warrior_icon;
            case ARCHER_CLASS_TYPE:
                drawableResID = R.drawable.archer_icon;
                drawableDeadResID = R.drawable.archer_icon;
        }
    }

    /**
     * returns true if the string is an ID for an inventory object
     * @param s     The String of the inventory object stored
     * @return      true if s is an inventory ID
     */
    private boolean isInventoryID(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (!isInteger(s.charAt(i))) return false;
        }
        return true;
    }

    /**
     *
     * @param c     single character from the ID String
     * @return      true if the character is an integer
     */
    private boolean isInteger(char c) {
        return c=='0'|| c=='1'||c=='2'||c=='3'||c=='4'||c=='5'||c=='6'||c=='7'||c=='8'||c=='9';
    }

    // decrement the cooldown on all abilities w/ cooldown > 0
    public void decrCooldowns() {
        // decrement special ability cooldown if >0 of all weapons
        for (int i = 0; i < weapons.size(); i++) {
            weapons.get(i).getSpecialAttack().decrementCooldownCurr();
        }
        // decrement ability cooldowns if >0
        for (int i = 0; i < abilities.size(); i++) {
            abilities.get(i).decrementCooldownCurr();
        }
        // decrement item ability cooldowns if >0
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getAbility() != null) {
                items.get(i).getAbility().decrementCooldownCurr();
            }
        }
    }

    @Override
    public JSONObject serializeToJSON() throws JSONException {
        // JSON object holding all character information
        JSONObject JSONCharacter = new JSONObject();

        JSONCharacter.put("class", classType);

        JSONCharacter.put("str", strength); // str
        JSONCharacter.put("strBase", strBase); // base str
        JSONCharacter.put("strIncrease", strIncrease);
        JSONCharacter.put("strDecrease", strDecrease);
        JSONCharacter.put("int", intelligence); // int
        JSONCharacter.put("intBase", intBase); // base int
        JSONCharacter.put("intIncrease", intIncrease);
        JSONCharacter.put("intDecrease", intDecrease);
        JSONCharacter.put("con", constitution); // con
        JSONCharacter.put("conBase", conBase); // base con
        JSONCharacter.put("conIncrease", conIncrease);
        JSONCharacter.put("conDecrease", conDecrease);
        JSONCharacter.put("spd", speed); // spd
        JSONCharacter.put("spdBase", spdBase); // base spd
        JSONCharacter.put("spdIncrease", spdIncrease);
        JSONCharacter.put("spdDecrease", spdDecrease);
        // abilities
        JSONArray abilitiesArray = new JSONArray();
        for (int i = 0; i < abilities.size(); i++) {
            abilitiesArray.put(abilities.get(i).serializeToJSON());
        }
        JSONCharacter.put("abilities", abilitiesArray);
        // weapons
        JSONArray weaponsArray = new JSONArray();
        for (int i = 0; i < weapons.size(); i++) {
            weaponsArray.put(weapons.get(i).serializeToJSON());
        }
        JSONCharacter.put("weapons", weaponsArray);
        // bars
        JSONCharacter.put("health", health);
        JSONCharacter.put("maxHealth", maxHealth);
        JSONCharacter.put("mana", mana);
        JSONCharacter.put("maxMana", maxMana);
        // misc
        JSONCharacter.put("level", level);
        JSONCharacter.put("gold", gold);
        JSONCharacter.put("exp", exp);
        // Items
        JSONArray itemsArray = new JSONArray();
        for (int i = 0; i < items.size(); i++) {
            itemsArray.put(items.get(i).serializeToJSON());
        }
        JSONCharacter.put("items", itemsArray);
        // specials
        JSONCharacter.put(Effect.STUN, isStun);
        JSONCharacter.put(Effect.CONFUSE, isConfuse);
        JSONCharacter.put(Effect.INVINCIBILITY, isInvincible);
        JSONCharacter.put(Effect.SILENCE, isSilence);
        JSONCharacter.put(Effect.INVISIBILITY, isInvisible);
        JSONArray specialArray = new JSONArray();
        for (SpecialEffect appliedSpecial: specialList) {
            JSONArray special = new JSONArray();
            special.put(appliedSpecial.getType());
            special.put(appliedSpecial.getDuration());
            specialArray.put(special);
        }
        JSONCharacter.put("specialList", specialArray);
        // temp health
        JSONCharacter.put("tempExtraHealth", tempExtraHealth);
        JSONArray tempHealthArray = new JSONArray(); // <key, value>
        for (int i = 0; i < tempHealthList.size(); i++) {
            JSONArray tempHealth = new JSONArray(); // <duration, amount>
            tempHealth.put(tempHealthList.get(i).getDuration());
            tempHealth.put(tempHealthList.get(i).getAmount());
            tempHealthArray.put(tempHealth);
        }
        JSONCharacter.put("tempHealthList", tempHealthArray);
        // temp mana
        JSONCharacter.put("tempExtraMana", tempExtraMana);
        JSONArray tempManaArray = new JSONArray(); // <key, value>
        for (int i = 0; i < tempManaList.size(); i++) {
            JSONArray tempMana = new JSONArray(); // <duration, amount>
            tempMana.put(tempManaList.get(i).getDuration());
            tempMana.put(tempManaList.get(i).getAmount());
            tempManaArray.put(tempMana);
        }
        JSONCharacter.put("tempManaList", tempManaArray);
        // stat increase
        JSONArray statIncreaseArray = new JSONArray(); // <stat, duration, amount>
        for (int i = 0; i < statIncreaseList.size(); i++) {
            JSONArray statIncrease = new JSONArray();
            statIncrease.put(statIncreaseList.get(i).getType());
            statIncrease.put(statIncreaseList.get(i).getDuration());
            statIncrease.put(statIncreaseList.get(i).getAmount());
            statIncreaseArray.put(statIncrease);
        }
        JSONCharacter.put("statIncreaseList", statIncreaseArray);
        // stat decrease
        JSONArray statDecreaseArray = new JSONArray(); // <stat, duration, amount>
        for (int i = 0; i < statDecreaseList.size(); i++) {
            JSONArray statDecrease = new JSONArray();
            statDecrease.put(statDecreaseList.get(i).getType());
            statDecrease.put(statDecreaseList.get(i).getDuration());
            statDecrease.put(statDecreaseList.get(i).getAmount());
            statDecreaseArray.put(statDecrease);
        }
        JSONCharacter.put("statDecreaseList", statDecreaseArray);
        // DOT
        JSONCharacter.put(Effect.BLEED, isBleed);
        JSONCharacter.put(Effect.POISON, isPoison);
        JSONCharacter.put(Effect.FIRE, isFire);
        JSONCharacter.put(Effect.FROSTBURN, isFrostBurn);
        JSONCharacter.put(Effect.HEALTH_DOT, isHealDot);
        JSONCharacter.put(Effect.MANA_DOT, isManaDot);
        JSONArray dotArray = new JSONArray(); // <key, value>
        for (Dot appliedDot: dotList) {
            JSONArray dot = new JSONArray();
            dot.put(appliedDot.getType());
            dot.put(appliedDot.getDuration());
            dotArray.put(dot);
        }
        JSONCharacter.put("dotList", dotArray);

        return JSONCharacter;
    }

    // Weapons

    /**
     * remove a specific weapon from weapons array
     * @param weapon    the weapon object
     */
    public void removeWeapon(Weapon weapon) {
        if (!weapons.remove(weapon)) throw new IllegalArgumentException();
        else numWeapons--;
    }
    /**
     * add a new weapon to weapons array
     * @param weapon    the weapon object
     */
    public void addWeapon (Weapon weapon) {
        if (weapons.size() != MAX_WEAPONS) {
            numWeapons++;
            weapons.add(weapon);
        }
    }
    /**
     * remove a weapon from a specific index in weapons
     * @param index     the index of weapons array to remove
     * @return          the weapon removed from weapons
     */
    public Weapon removeWeaponAtIndex(int index) {
        numWeapons--;
        Weapon weapon = weapons.get(index);
        weapons.remove(index);
        return weapon;
    }

    // Abilities

    /**
     * remove an ability from abilities array
     * @param ability   the Ability object
     */
    public void removeAbility(Ability ability) {
        if (!abilities.remove(ability)) throw new IllegalArgumentException();
        else numAbilities--;
    }
    /**
     * remove an ability at index from abilities array
     * @param index     the index in abilities array
     * @return          the Ability object removed from abilities array
     */
    public Ability removeAbilityAtIndex(int index) {
        numAbilities--;
        Ability ability = abilities.get(index);
        abilities.remove(index);
        return ability;
    }
    /**
     *  add new ability to abilities array
     * @param ability   the Ability object
     */
    public void addAbility(Ability ability) {
        if (abilities.size() != MAX_ABILITIES) {
            numAbilities++;
            abilities.add(ability);
        }
    }

    // Items

    /**
     * remove item from items array and removing any of its effects
     * @param item      the Item object
     */
    public void removeItem(Item item) {
        if (!items.remove(item)) throw new IllegalArgumentException();
        else numItems--;
    }
    /**
     * get the index of an item from items array
     * @param item      the item object
     * @return          the index of the item inside items
     */
    public int getItemIndex(Item item) {
        for (int i = 0; i < items.size(); i++) {
            if (item.equals(items.get(i))) {
                return i;
            }
        }
        throw new IllegalArgumentException();
    }
    /**
     * get the item at specific index of items array
     * @param index     the index in items array
     * @return          the item at index 'index' from items
     */
    public Item getItemAtIndex(int index) {
        return items.get(index);
    }
    /**
     * remove item at index from items array
     * @param index     the index of items array
     * @return          the item removed from index 'index'
     */
    public Item removeItemAtIndex(int index) {
        numItems--;
        Item item = items.get(index);
        items.remove(index);
        return item;
    }
    /**
     * add an item to items and add any indefinite effects that it causes
     * @param item      Item object
     */
    public void addItem(Item item) {
        if (items.size() != MAX_ITEMS) {
            items.add(item);
            numItems++;
        }
    }


    // setter methods
    public void setGoldIncrease(int goldIncrease) {
        this.goldIncrease = goldIncrease;
    }
    public void setGold(int gold) {
        this.gold = gold;
    }

    /**
     * change the gold by amount and return the changed amount
     * @param amount    the amount for gold change
     * @return          the changed amount of gold
     */
    public int goldChange(int amount) {
        int goldTemp = gold;
        gold += amount;
        if (gold < 0) gold = 0;
        return gold - goldTemp;
    }
    public void setExp(int expIncrease) {
        this.exp = expIncrease;
    }

    /**
     * add xp to player character
     * @param amount    amount to add to character xp
     */
    public void addExp(int amount) {
        exp += amount;
        // todo: level up from xp
    }

    public String getClassType() {
        return classType;
    }
    public int getLevel() {
        return level;
    }
    public int getExp() {
        return exp;
    }
    public int getGold() {
        return gold;
    }
    public int getGoldIncrease() {
        return goldIncrease;
    }

    public int getNumStatPoints() {
        return strBase + intBase + conBase + spdBase;
    }
}
