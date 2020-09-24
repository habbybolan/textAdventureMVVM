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

/*
character object that the user creates and plays
 */
public class Character extends CharacterEntity {
    public final static int HEALTH_CON_MULTIPLIER = 10;
    public final static int MANA_INT_MULTIPLIER = 10;
    public final static int BASE_HEALTH = 20;
    public final static int BASE_MANA = 20;

    final static int evasionMultiplier = 1;
    final static int blockMultiplier = 1;

    public static final String PALADIN_CLASS_TYPE = "Paladin";
    public static final String WARRIOR_CLASS_TYPE = "Warrior";
    public static final String WIZARD_CLASS_TYPE = "Wizard";
    public static final String ARCHER_CLASS_TYPE = "Archer";



    // all MAX values for a character
    public static final int MAX_ABILITIES = 4;
    public static final int MAX_WEAPONS = 2;
    public static final int MAX_ITEMS = 5;
    public boolean isAlive = true;
    public final int NUM_ACTIONS = 2;

    // holds all the buff and debuff abilities applied to the player character
    private ArrayList<Ability> appliedAbilities = new ArrayList<>();

    // todo: turn stat variables from String to int
    // Character info
    private String classType;
    private int expIncrease;
    private int gold;
    private int goldIncrease;

    private Weapon equippedWeapon;


    public Character(String characterData, DatabaseAdapter mDbHelper, Context context) {
        JSONObject characterObject;
        isCharacter = true;
        classType = "";
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
            // abilities
            numAbilities = 0;
            JSONArray abilitiesArray = characterObject.getJSONArray("abilities");
            for (int i= 0; i < MAX_ABILITIES; i++) {
                int indAbility = Integer.parseInt(abilitiesArray.getString(i));
                if (indAbility != 0) {
                    abilities.add(new Ability(indAbility, mDbHelper));
                    numAbilities++;
                }
            }
            // weapons
            JSONArray weaponsArray = characterObject.getJSONArray("weapons");
            numWeapons = 0;
            for (int i = 0; i < MAX_WEAPONS; i++) {
                int indWeapon = Integer.parseInt(weaponsArray.getString(i));
                if (indWeapon != 0) {
                    weapons.add(new Weapon(indWeapon, mDbHelper));
                    numWeapons++;
                }
            }
            // todo: allow change of equipped weapon
            //equippedWeapon = weapons.get(0);
            // items
            numItems = 0;
            JSONArray itemsArray = characterObject.getJSONArray("items");
            for (int i = 0; i < MAX_ITEMS; i++) {
                int indItem = Integer.parseInt(itemsArray.getString(i));
                if (indItem != 0) {
                    items.add(new Item(itemsArray.getInt(i), mDbHelper));
                    numItems++;
                }
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

    // decrement the cooldown on all abilities w/ cooldown > 0
    public void decrCooldowns() {
        // decrement special ability cooldown if >0 of all weapons
        for (int i = 0; i < weapons.size(); i++) {
            if (weapons.get(i).getSpecialAttack().getCooldownCurr() > 0) {
                weapons.get(i).getSpecialAttack().setCooldownCurr(weapons.get(i).getSpecialAttack().getCooldownCurr() - 1);
            }
        }
        // decrement ability cooldowns if >0
        for (int i = 0; i < abilities.size(); i++) {
            if (abilities.get(i).getCooldownLeft() > 0) {
                abilities.get(i).setCooldownCurr(abilities.get(i).getCooldownLeft()-1);
            }
        }
        // decrement item ability cooldowns if >0
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getAbility() != null && items.get(i).getAbility().getCooldownLeft() > 0) {
                items.get(i).getAbility().setCooldownCurr(items.get(i).getAbility().getCooldownLeft()-1);
            }
        }
    }

    // ** Inventory **

        // Weapons
    // remove a weapon
    public void removeWeapon(Weapon weapon) {
        if (!weapons.remove(weapon)) throw new IllegalArgumentException();
        else numWeapons--;
    }
    // add new weapon - check if weapon list is full before calling this
    public void addWeapon (Weapon weapon) {
        if (weapons.size() != MAX_WEAPONS) {
            numWeapons++;
            weapons.add(weapon);
        }
    }
    // remove a weapon at index
    public Weapon removeWeaponAtIndex(int index) {
        numWeapons--;
        Weapon weapon = weapons.get(index);
        weapons.remove(index);
        return weapon;
    }

        // Abilities
    // remove an ability, returning the index removed
    public void removeAbility(Ability ability) {
        if (!abilities.remove(ability)) throw new IllegalArgumentException();
        else numAbilities--;
    }
    // remove an ability at index
    public Ability removeAbilityAtIndex(int index) {
        numAbilities--;
        Ability ability = abilities.get(index);
        abilities.remove(index);
        return ability;
    }
    // add new Ability - check if ability list is full before calling this
    public void addAbility(Ability ability) {
        if (abilities.size() != MAX_ABILITIES) {
            numAbilities++;
            abilities.add(ability);
        }
    }

        // Items

    // remove an item - if not a consumable, remove the effects it had
    public void removeItem(Item item) {
        if (!items.remove(item)) throw new IllegalArgumentException();
        else numItems--;
    }
    // finds index of an item
    public int getItemIndex(Item item) {
        for (int i = 0; i < items.size(); i++) {
            if (item.equals(items.get(i))) {
                return i;
            }
        }
        throw new IllegalArgumentException();
    }
    // find item at index
    public Item getItemAtIndex(int index) {
        return items.get(index);
    }
    // remove an item at index and return it
    public Item removeItemAtIndex(int index) {
        numItems--;
        Item item = items.get(index);
        items.remove(index);
        return item;
    }
    // add item, returning index of newest
    public void addItem(Item item) {
        if (items.size() != MAX_ITEMS) {
            items.add(item);
            numItems++;
        }
    }

    // ** bars **

    // get the new health after removing item
    public int getHealthAfterItemRemoval(int healthChange) {
        setMaxHealth(getMaxHealth() - healthChange);
        if (getHealth() > getMaxHealth()) {
            return maxHealth;
        } else {
            return health;
        }
    }

    // get the new mana after removing item
    public int getManaAfterItemRemoval(int manaChange) {
        setMaxMana(getMaxHealth() - manaChange);
        if (getMana() > getMaxMana()) {
            return maxMana;
        } else {
            return mana;
        }
    }


    // setter methods
    public void setGoldIncrease(int goldIncrease) {
        this.goldIncrease = goldIncrease;
    }
    public void setGold(int gold) {
        this.gold = gold;
    }
    public void setExpIncrease(int expIncrease) {
        this.expIncrease = expIncrease;
    }


    // getter methods
    public Weapon getEquippedWeapon() {
        return equippedWeapon;
    }
    public String getClassType() {
        return classType;
    }
    public int getLevel() {
        return level;
    }
    public int getExpIncrease() {
        return expIncrease;
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
