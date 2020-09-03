package com.habbybolan.textadventure.model.characterentity;

import android.content.Context;
import android.graphics.BitmapFactory;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.model.effects.Dot;
import com.habbybolan.textadventure.model.effects.SpecialEffect;
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
        classType = "";
        try {
            characterObject = new JSONObject(characterData);
            classType = characterObject.getString("class");
            // stats
            strength = characterObject.getInt("str");
            strBase = characterObject.getInt("strBase");
            intelligence = characterObject.getInt("int");
            intBase = characterObject.getInt("intBase");
            constitution = characterObject.getInt("con");
            conBase = characterObject.getInt("conBase");
            speed = characterObject.getInt("spd");
            spdBase = characterObject.getInt("spdBase");
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
            equippedWeapon = weapons.get(0); // todo: allow change of equipped weapon
            // items
            numItems = 0;
            JSONArray itemsArray = characterObject.getJSONArray("items");
            for (int i = 0; i < MAX_ITEMS; i++) {
                int indItem = Integer.parseInt(itemsArray.getString(i));
                if (indItem != 0) {
                    items.add(new Item(itemsArray.getString(i), mDbHelper));
                    numItems++;
                }
            }

            // DOTS
            JSONArray dotList = characterObject.getJSONArray("dotList");
            for (int i = 0; i < dotList.length(); i++) {
                JSONArray dot = (JSONArray) dotList.get(i);
                this.dotList.add(new Dot(dot.getString(0), dot.getInt(1)));
            }
            // SPECIAL
            JSONArray specialMap = characterObject.getJSONArray("specialMap");
            for (int i = 0; i < specialMap.length(); i++) {
                JSONArray special = (JSONArray) specialMap.get(i);
                this.specialList.add(new SpecialEffect(special.getString(0), special.getInt(1)));
            }
            // tempHealth
            JSONArray tempHealthArray = characterObject.getJSONArray("tempHealthList");
            for (int i = 0; i < tempHealthArray.length(); i++) {
                JSONArray tempHealth = (JSONArray) tempHealthArray.get(i);
                ArrayList<Integer> tempHealthInput = new ArrayList<>();
                tempHealthInput.add(tempHealth.getInt(0)); // duration
                tempHealthInput.add(tempHealth.getInt(1)); // amount
                this.tempHealthList.add(tempHealthInput);
            }
            // tempMana
            JSONArray tempManaArray = characterObject.getJSONArray("tempManaList");
            for (int i = 0; i < tempManaArray.length(); i++) {
                JSONArray tempMana = (JSONArray) tempManaArray.get(i);
                ArrayList<Integer> tempManaInput = new ArrayList<>();
                tempManaInput.add(tempMana.getInt(0)); // duration
                tempManaInput.add(tempMana.getInt(1)); // amount
                this.tempManaList.add(tempManaInput);
            }
            // stat Increase
            JSONArray statIncreaseArray = characterObject.getJSONArray("statIncreaseList");
            for (int i = 0; i < statIncreaseArray.length(); i++) {
                JSONArray statIncrease = (JSONArray) statIncreaseArray.get(i);
                ArrayList<Object> statIncreaseInput = new ArrayList<>();
                statIncreaseInput.add(statIncrease.getString(0)); // stat type
                statIncreaseInput.add(statIncrease.getInt(1)); // duration
                statIncreaseInput.add(statIncrease.getInt(2)); // amount
                this.statIncreaseList.add(statIncreaseInput);
            }
            // stat Decrease
            JSONArray statDecreaseArray = characterObject.getJSONArray("statDecreaseList");
            for (int i = 0; i < statDecreaseArray.length(); i++) {
                JSONArray statDecrease = (JSONArray) statDecreaseArray.get(i);
                ArrayList<Object> statDecreaseInput = new ArrayList<>();
                statDecreaseInput.add(statDecrease.getString(0)); // stat type
                statDecreaseInput.add(statDecrease.getInt(1)); // duration
                statDecreaseInput.add(statDecrease.getInt(2)); // amount
                this.statIncreaseList.add(statDecreaseInput);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        bitmapAlive = BitmapFactory.decodeResource(context.getResources(), R.drawable.paladin);
        bitmapDead = BitmapFactory.decodeResource(context.getResources(), R.drawable.paladin);
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
    public int removeWeapon(Weapon weapon) {
        for (int i = 0; i < weapons.size(); i++) {
            if (weapons.get(i).equals(weapon)) {
                weapons.remove(i);
                numWeapons--;
                return i;
            }
        }
        throw new IllegalArgumentException();
    }
    // add new weapon - check if weapon list is full before calling this
    public int addWeapon (Weapon weapon) {
        if (getWeapons().size() != MAX_WEAPONS) {
            numWeapons++;
            weapons.add(weapon);
            return weapons.size()-1;
        } else throw new IllegalStateException();
    }
    // remove a weapon at index
    public void removeWeaponAtIndex(int index) {
        numWeapons--;
        weapons.remove(index);
    }

        // Abilities
    // remove an ability, returning the index removed
    public int removeAbility(Ability ability) {
        for (int i = 0; i < abilities.size(); i++) {
            if (abilities.get(i).equals(ability)) {
                abilities.remove(i);
                numAbilities--;
                return i;
            }
        }
        throw new IllegalArgumentException();
    }
    // remove an ability at index
    public void removeAbilityAtIndex(int index) {
        numAbilities--;
        abilities.remove(index);
    }
    // add new Ability - check if ability list is full before calling this
    public int addAbility(Ability ability) {
        if (getAbilities().size() != MAX_ABILITIES) {
            numAbilities++;
            abilities.add(ability);
            return abilities.size()-1;
        } else throw new IllegalStateException();
    }

        // Items
    // remove an item - if not a consumable, remove the effects it had
    public int removeItem(Item item) {
        for (int i = 0; i < items.size(); i++) {
            if (item.equals(items.get(i))) {
                items.remove(i);
                numItems--;
                return i;
            }
        }
        throw new IllegalArgumentException();
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
    public int addItem(Item item) {
        if (item == null) throw new IllegalArgumentException();
        items.add(item);
        return numItems++;
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
}
