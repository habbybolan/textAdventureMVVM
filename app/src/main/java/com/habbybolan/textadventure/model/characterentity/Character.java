package com.habbybolan.textadventure.model.characterentity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.model.inventory.Ability;
import com.habbybolan.textadventure.model.inventory.Item;
import com.habbybolan.textadventure.model.inventory.weapon.Weapon;
import com.habbybolan.textadventure.repository.database.DatabaseAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/*
character object that the user creates and plays
 */
public class Character implements CharacterEntity {
    public final static int HEALTH_CON_MULTIPLIER = 10;
    public final static int MANA_INT_MULTIPLIER = 10;
    public final static int BASE_HEALTH = 20;
    public final static int BASE_MANA = 20;


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
    private int strength;
    private String strDescription = "Increases the damage of attack based moves.";
    private int intelligence;
    private String intDescription = "Increases the damage of ability based moves and increases your mana pool.";
    private int constitution;
    private String conDescription = "Increases your health pool and ability to block attacks.";
    private int speed;
    private String spdDescription = "Increases your ability to attack quicker and to evade attacks and traps.";
    private int evasion;
    private int block;
    private int numStatPoints;
    private int level;
    private int expIncrease;
    private int gold;
    private int goldIncrease;
    private int health;
    private int maxHealth;
    private int mana;
    private int maxMana;

    private Weapon equippedWeapon;

    private int tempExtraHealth = 0;
    private int tempExtraMana = 0;

    private ArrayList<Ability> abilities = new ArrayList<>();

    // Inventory
    private ArrayList<Weapon> weapons = new ArrayList<>();
    private ArrayList<Item> items = new ArrayList<>();

    private int numAbilities;
    private int numWeapons;
    private int numItems;

    // DOTS
    private boolean isFire = false;
    private boolean isBleed = false;
    private boolean isPoison = false;
    private boolean isFrostBurn = false;
    private boolean isHealDot = false;
    private boolean isManaDot = false;

    // specials
    private boolean isStun = false;
    private boolean isConfuse = false;
    private boolean isInvincible = false;
    private boolean isSilence = false;
    private boolean isInvisible = false;

    // stats
    private int strBase = 0;
    private int intBase = 0;
    private int conBase = 0;
    private int spdBase = 0;
    private int evasionBase = 0;
    private int blockBase = 0;

    private int strIncrease = 0;
    private int intIncrease = 0;
    private int conIncrease = 0;
    private int spdIncrease = 0;
    private int evasionIncrease = 0;
    private int blockIncrease = 0;

    private int strDecrease = 0;
    private int intDecrease = 0;
    private int conDecrease = 0;
    private int spdDecrease = 0;
    private int evasionDecrease = 0;
    private int blockDecrease = 0;

    private Bitmap bitmapAlive;
    private Bitmap bitmapDead;

    private Context context;

    public Character(String characterData, DatabaseAdapter mDbHelper, Context context) {
        this.context = context;
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
            JSONArray dotMap = characterObject.getJSONArray("dotMap");
            for (int i = 0; i < dotMap.length(); i++) {
                JSONArray dot = (JSONArray) dotMap.get(i);
                this.dotMap.put(dot.getString(0), dot.getInt(1));
            }
            // SPECIAL
            JSONArray specialMap = characterObject.getJSONArray("specialMap");
            for (int i = 0; i < specialMap.length(); i++) {
                JSONArray special = (JSONArray) specialMap.get(i);
                this.specialMap.put(special.getString(0), special.getInt(1));
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


    // *** viewModel ***

    // remove a weapon
    public void removeWeapon(Weapon weapon) {
        numWeapons--;
        weapons.remove(weapon);
    }

    // add new weapon - check if weapon list is full before calling this
    public void addWeapon (Weapon weapon) {
        if (getWeapons().size() != MAX_WEAPONS) {
            numWeapons++;
            weapons.add(weapon);
        } else throw new IllegalStateException();
    }

    // remove an ability
    public void removeAbility(Ability ability) {
        numAbilities--;
        abilities.remove(ability);
    }
    // add new Ability - check if ability list is full before calling this
    public void addAbility(Ability ability) {
        if (getAbilities().size() != MAX_ABILITIES) {
            numAbilities++;
            abilities.add(ability);
        } else throw new IllegalStateException();
    }

    /*
    // remove an item - if not a consumable, remove the effects it had
    public void removeItem(Item item) {
        if (item.getIsConsumable() == 0) {
            resetItemChange(item);
        } else {
            items.remove(item);
        }
        numItems--;
    }

    // add new item - check if item list is full before calling this
    // add the effects it has to the character
    public void addItem(Item item) {
        if (getItems().size() != MAX_ITEMS) {
            if (item.getIsConsumable() == 0) {
                addItemChange(item);
            } else {
                items.add(item);
            }
            numItems++;
        } else throw new IllegalStateException();
    }
    // adds the newly added item effects
    private void addItemChange(Item item) {
        Damage damage = new Damage(model);
        items.add(item);
        // stat changes
        if (item.getStrChange() > 0) setStrIncrease(getStrIncrease() + item.getStrChange());
        if (item.getStrChange() < 0) setStrDecrease(getStrDecrease() + item.getStrChange());
        if (item.getIntChange() > 0) setIntIncrease(getIntIncrease() + item.getIntChange());
        if (item.getIntChange() < 0) setIntDecrease(getIntDecrease() + item.getIntChange());
        if (item.getConChange() > 0) setConIncrease(getConIncrease() + item.getConChange());
        if (item.getConChange() < 0) setConDecrease(getConDecrease() + item.getConChange());
        if (item.getSpdChange() > 0) setSpdIncrease(getSpdIncrease() + item.getSpdChange());
        if (item.getSpdChange() < 0) setSpdDecrease(getSpdDecrease() + item.getSpdChange());
        model.setStatChange("");
        // todo: add evasion and block stack separately to character JSON
        setMaxHealth(getMaxHealth() + item.getHealthChange());
        model.setHealthManaChange(Damage.HEALTH);
        setMaxMana(getMaxMana() + item.getManaChange());
        model.setHealthManaChange(Damage.MANA);
        // misc
        // TODO: add gold and exp change to character JSON
        // specials
        if (item.getIsConfuse() == 1)  damage.addNewSpecial(Damage.CONFUSE, -1, this);
        if (item.getIsStun() == 1) damage.addNewSpecial(Damage.STUN, -1, this);
        if (item.getIsInvisible() == 1) damage.addNewSpecial(Damage.INVISIBILITY, -1, this);
        if (item.getIsInvincible() == 1) damage.addNewSpecial(Damage.INVINCIBILITY, -1, this);
        if (item.getIsSilence() == 1) damage.addNewSpecial(Damage.SILENCE, -1, this);
        model.setSpecialChange("");
        // DOTS
        if (item.getIsFire() == 1) damage.addNewDot(Damage.FIRE, true, this);
        if (item.getIsBleed() == 1) damage.addNewDot(Damage.BLEED, true, this);
        if (item.getIsPoison() == 1) damage.addNewDot(Damage.POISON, true, this);
        if (item.getIsFrostBurn() == 1) damage.addNewDot(Damage.FROSTBURN, true, this);
        model.setDotChange("");
    }
    // removes the recently removed item effects
    private void resetItemChange(Item item) {
        items.remove(item);
        // stat changes
        if (item.getStrChange() > 0) setStrIncrease(getStrIncrease() - item.getStrChange());
        if (item.getStrChange() < 0) setStrDecrease(getStrDecrease() - item.getStrChange());
        if (item.getIntChange() > 0) setIntIncrease(getIntIncrease() - item.getIntChange());
        if (item.getIntChange() < 0) setIntDecrease(getIntDecrease() - item.getIntChange());
        if (item.getConChange() > 0) setConIncrease(getConIncrease() - item.getConChange());
        if (item.getConChange() < 0) setConDecrease(getConDecrease() - item.getConChange());
        if (item.getSpdChange() > 0) setSpdIncrease(getSpdIncrease() - item.getSpdChange());
        if (item.getSpdChange() < 0) setSpdDecrease(getSpdDecrease() - item.getSpdChange());
        model.setItemChange("");
        // todo: add evasion and block stack separately to character JSON
        if (item.getHealthChange() != 0) {
            setMaxHealth(getMaxHealth() - item.getHealthChange());
            if (getHealth() > getMaxHealth()) setHealth(getMaxHealth());
        }
        model.setHealthManaChange(Damage.HEALTH);
        if (item.getManaChange() != 0) {
            setMaxMana(getMaxMana() - item.getManaChange());
            if (getMana() > getMaxMana()) setMana(getMaxMana());
        }
        model.setHealthManaChange(Damage.MANA);
        // misc
        // TODO: add gold and exp change to character JSON
        // specials
        if (item.getIsConfuse() == 1) {
            // check if any other item has a confuse permanent effect
            boolean otherItemWithConfuse = false;
            for (int i = 0; i < items.size(); i++) {
                if (item.getIsConfuse() == 1) {
                    otherItemWithConfuse = true;
                    break;
                }
            }// if no other item has confuse, then remove from map and set character as not confused
            if (!otherItemWithConfuse) {
                specialMap.remove(Damage.CONFUSE);
                setIsConfuse(0);
            }
        }
        if (item.getIsStun() == 1) {
            // check if any other item has a Stun permanent effect
            boolean otherItemWithStun = false;
            for (int i = 0; i < items.size(); i++) {
                if (item.getIsStun() == 1) {
                    otherItemWithStun = true;
                    break;
                }
            }// if no other item has stun, then remove from map and set character as not stunned
            if (!otherItemWithStun) {
                specialMap.remove(Damage.STUN);
                setIsStun(0);
            }
        }
        if (item.getIsInvisible() == 1) {
            // check if any other item has a Invisible permanent effect
            boolean otherItemWithInvisible = false;
            for (int i = 0; i < items.size(); i++) {
                if (item.getIsInvisible() == 1) {
                    otherItemWithInvisible = true;
                    break;
                }
            }// if no other item has invisibility, then remove from map and set character as not invisible
            if (!otherItemWithInvisible) {
                specialMap.remove(Damage.INVISIBILITY);
                setIsInvisible(0);
            }
        }
        if (item.getIsInvincible() == 1) {
            // check if any other item has a Invincible permanent effect
            boolean otherItemWithInvincible = false;
            for (int i = 0; i < items.size(); i++) {
                if (item.getIsInvisible() == 1) {
                    otherItemWithInvincible = true;
                    break;
                }
            }// if no other item has invincibility, then remove from map and set character as not invincible
            if (!otherItemWithInvincible) {
                specialMap.remove(Damage.INVINCIBILITY);
                setIsInvincible(0);
            }
        }
        if (item.getIsSilence() == 1) {
            // check if any other item has a silence permanent effect
            boolean otherItemWithSilence = false;
            for (int i = 0; i < items.size(); i++) {
                if (item.getIsSilence() == 1) {
                    otherItemWithSilence = true;
                    break;
                }
            }// if no other item has silence, then remove from map and set character as not silenced
            if (!otherItemWithSilence) {
                specialMap.remove(Damage.SILENCE);
                setIsSilence(0);
            }
        }
        model.setSpecialChange("");
        // DOTS
        if (item.getIsFire() == 1) {
            // check if any other item has a dot permanent effect
            boolean otherItemWithDot = false;
            for (int i = 0; i < items.size(); i++) {
                if (item.getIsFire() == 1) {
                    otherItemWithDot = true;
                    break;
                }
            }
            if (!otherItemWithDot) {
                dotMap.remove(Damage.FIRE);
                setIsFire(0);
            }
        }
        if (item.getIsBleed() == 1) {
            // check if any other item has a dot permanent effect
            boolean otherItemWithDot = false;
            for (int i = 0; i < items.size(); i++) {
                if (item.getIsBleed() == 1) {
                    otherItemWithDot = true;
                    break;
                }
            }
            if (!otherItemWithDot) {
                dotMap.remove(Damage.BLEED);
                setIsBleed(0);
            }
        }
        if (item.getIsPoison() == 1) {
            // check if any other item has a dot permanent effect
            boolean otherItemWithDot = false;
            for (int i = 0; i < items.size(); i++) {
                if (item.getIsPoison() == 1) {
                    otherItemWithDot = true;
                    break;
                }
            }
            if (!otherItemWithDot) {
                dotMap.remove(Damage.POISON);
                setIsPoison(0);
            }
        }
        if (item.getIsFrostBurn() == 1) {
            // check if any other item has a dot permanent effect
            boolean otherItemWithDot = false;
            for (int i = 0; i < items.size(); i++) {
                if (item.getIsFrostBurn() == 1) {
                    otherItemWithDot = true;
                    break;
                }
            }
            if (!otherItemWithDot) {
                dotMap.remove(Damage.FROSTBURN);
                setIsFrostBurn(0);
            }
        }
        model.setDotChange("");
    }
    // consume an item
    public void consumeItem(Item item) {
        Damage damage = new Damage(model);
        int duration = item.getDuration();
        items.remove(item); // remove item
        numItems--;
        // stat change
        if (item.getStrChange() > 0) damage.addNewStatIncrease(Damage.STR, duration, item.getStrChange(), this);
        if (item.getStrChange() < 0) damage.addNewStatDecrease(Damage.STR, duration, item.getStrChange(), this);
        if (item.getIntChange() > 0) damage.addNewStatIncrease(Damage.INT, duration, item.getIntChange(), this);
        if (item.getIntChange() < 0) damage.addNewStatIncrease(Damage.INT, duration, item.getIntChange(), this);
        if (item.getConChange() > 0) damage.addNewStatIncrease(Damage.CON, duration, item.getConChange(), this);
        if (item.getConChange() < 0) damage.addNewStatIncrease(Damage.CON, duration, item.getConChange(), this);
        if (item.getSpdChange() > 0) damage.addNewStatIncrease(Damage.SPD, duration, item.getSpdChange(), this);
        if (item.getSpdChange() < 0) damage.addNewStatIncrease(Damage.SPD, duration, item.getSpdChange(), this);
        model.setStatChange("");
        // TODO: gold and exp change
        // TODO: evasion and block change
        // health/mana
        if (item.getHealthChange() > 0) damage.healTarget(this, item.getHealthChange());
        if (item.getHealthChange() < 0) damage.damageTarget(this, item.getHealthChange());
        model.setHealthManaChange(Damage.HEALTH);
        if (item.getManaChange() > 0) damage.manaTarget(this, item.getManaChange());
        if (item.getManaChange() < 0) damage.loseManaTarget(this, item.getManaChange());
        model.setHealthManaChange(Damage.MANA);
        // specials
        if (item.getIsConfuse() == 1) damage.addNewSpecial(Damage.CONFUSE, duration, this);
        if (item.getIsStun() == 1) damage.addNewSpecial(Damage.STUN, duration, this);
        if (item.getIsSilence() == 1) damage.addNewSpecial(Damage.SILENCE, duration, this);
        if (item.getIsInvisible() == 1) damage.addNewSpecial(Damage.INVISIBILITY, duration, this);
        if (item.getIsInvincible() == 1) damage.addNewSpecial(Damage.INVINCIBILITY, duration, this);
        model.setSpecialChange("");
        // DOTS
        if (item.getIsFire() == 1) damage.addNewDot(Damage.FIRE, false, this);
        if (item.getIsBleed() == 1) damage.addNewDot(Damage.BLEED, false, this);
        if (item.getIsPoison() == 1) damage.addNewDot(Damage.POISON, false, this);
        if (item.getIsFrostBurn() == 1) damage.addNewDot(Damage.FROSTBURN, false, this);
        model.setDotChange("");
        model.setItemChange("");
    }

    */



    /*
    public void setGold(int gold, CharacterChangeViewModel model) {
        this.gold = gold;
        model.setGoldChange("");
    }
    public void setLevel(int level, CharacterChangeViewModel model) {
        this.level = level;
    }
    */


    @Override
    public void removeInputDotMap(String key) {
        dotMap.remove(key);
    }
    @Override
    public void addInputDotMap(String key, int duration) {
        dotMap.put(key, duration);
    }
    @Override
    public void removeInputSpecialMap(String key) {
        specialMap.remove(key);
    }
    @Override
    public void addInputSpecialMap(String key, int duration) {
        specialMap.put(key, duration);
    }

    @Override
    public void removeZeroStatIncreaseList() {
        removeZeroStatList(statIncreaseList);
    }
    @Override
    public void removeZeroStatDecreaseList() {
        removeZeroStatList(statDecreaseList);
    }
    // helper for removeZeroStatDecreaseList and removeZeroStatIncreaseList
    private void removeZeroStatList(List<ArrayList<Object>> statList) {
        int index = 0;
        while (index < statList.size()) {
            if ((int)statList.get(index).get(1) == 0) statList.remove(index);
            else break;
        }
    }

    @Override
    public void addStatIncreaseList(ArrayList<Object> statIncrease) {
        addStatList(statIncrease, statIncreaseList);
    }
    @Override
    public void addStatDecreaseList(ArrayList<Object> statDecrease) {
        addStatList(statDecrease, statDecreaseList);
    }
    // helper for addStatIncreaseList and addStatDecreaseList
    private void addStatList(ArrayList<Object> statInput, List<ArrayList<Object>> statList) {
        if (!(statInput.get(0) instanceof String) || !(statInput.get(1) instanceof Integer) || !(statInput.get(2) instanceof Integer) || statInput.size() > 3) throw new IllegalArgumentException();
        for (int i = 0; i < statInput.size(); i++) {
            if ((int)statInput.get(1) < (int)statList.get(i).get(1)) {
                statList.add(i, statInput);
                break;
            }
        }
    }

    @Override
    public void removeZeroTempHealthList() {
        removeZeroTempList(tempHealthList);
    }
    @Override
    public void removeZeroTempManaList() {
        removeZeroTempList(tempManaList);
    }
    // helper for removeZeroTempManaList and removeZeroTempHealthList
    private void removeZeroTempList(List<ArrayList<Integer>> tempList) {
        int index = 0;
        while (index < tempList.size()) {
            if ((int)tempList.get(index).get(0) == 0) tempList.remove(index);
            else break;
        }
    }

    @Override
    public void addTempHealthList(ArrayList<Integer> tempExtraHealth) {
        addTempList(tempExtraHealth, tempHealthList);
    }
    @Override
    public void addTempManaList(ArrayList<Integer> tempExtraMana) {
        addTempList(tempExtraMana, tempManaList);
    }
    // helper for addTempManaList and addTempHealthList
    private void addTempList(ArrayList<Integer> tempExtra, List<ArrayList<Integer>> tempList) {
        if (tempExtra.size() > 2) throw new IllegalArgumentException();
        for (int i = 0; i < tempList.size(); i++) {
            if ((int)tempExtra.get(0) < (int)tempList.get(i).get(0)) {
                tempList.add(i, tempExtra);
                break;
            }
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
    public void setHealth(int health) {
        this.health = health;
    }
    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }
    public void setMana(int mana) {
        this.mana = mana;
    }
    public void setMaxMana(int maxMana) {
        this.maxMana = maxMana;
    }
    public void setIsAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }
    // temp extra health
    public void setTempExtraHealth(int tempExtraHealth) {
        this.tempExtraHealth = tempExtraHealth;
    }
    public void setTempExtraMana(int tempExtraMana) {
        this.tempExtraMana = tempExtraMana;
    }
    //specials
    public void setIsStun(boolean isStun) {
        this.isStun = isStun;
    }
    public void setIsConfuse(boolean isConfuse) {
        this.isConfuse = isConfuse;
    }
    public void setIsInvincible(boolean isInvincible) {
        this.isInvincible = isInvincible;
    }
    public void setIsSilence(boolean isSilence) {
        this.isSilence = isSilence;
    }
    public void setIsInvisible(boolean isInvisible) {
        this.isInvisible = isInvisible;
    }
    // stats
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
    @Override
    public void setEvasionIncrease(int evasionIncrease) {
        this.evasionIncrease = evasionIncrease;
    }
    @Override
    public void setBlockIncrease(int blockIncrease) {
        this.blockIncrease = blockIncrease;
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
    @Override
    public void setEvasionDecrease(int evasionDecrease) {
        this.evasionDecrease = evasionDecrease;
    }
    @Override
    public void setBlockDecrease(int blockDecrease) {
        this.blockDecrease = blockDecrease;
    }

    public void setStrBase(int strBase) {
        this.strBase = strBase;
    }
    public void setIntBase(int intBase) {
        this.intBase = intBase;
    }
    public void setConBase(int conBase) {
        this.conBase = conBase;
    }
    public void setSpdBase(int spdBase) {
        this.spdBase = spdBase;
    }
    @Override
    public void setEvasionBase(int evasionBase) {
        this.evasionBase = evasionBase;
    }
    @Override
    public void setBlockBase(int blockBase) {
        this.blockBase = blockBase;
    }

    @Override
    public boolean getIsFire() {
        return isFire;
    }
    @Override
    public boolean getIsBleed() {
        return isBleed;
    }
    @Override
    public boolean getIsPoison() {
        return isPoison;
    }
    @Override
    public boolean getIsFrostBurn() {
        return isFrostBurn;
    }
    @Override
    public boolean getIsHealDot() {
        return isHealDot;
    }
    @Override
    public boolean getIsManaDot() {
        return isManaDot;
    }
    @Override
    public void setIsFire(boolean isFire) {
        this.isFire = isFire;
    }
    @Override
    public void setIsBleed(boolean isBleed) {
        this.isBleed = isBleed;
    }
    @Override
    public void setIsPoison(boolean isPoison) {
        this.isPoison = isPoison;
    }
    @Override
    public void setIsFrostBurn(boolean isFrostBurn) {
        this.isFrostBurn = isFrostBurn;
    }
    @Override
    public void setIsHealDot(boolean isHealDot) {
        this.isHealDot = isHealDot;
    }
    @Override
    public void setIsManaDot(boolean isManaDot) {
        this.isManaDot = isManaDot;
    }


    // getter methods
    public String getClassType() {
        return classType;
    }
    public int getStrength() {
        return strength;
    }
    public String getStrDescription() {
        return strDescription;
    }
    public int getIntelligence() {
        return intelligence;
    }
    public String getIntDescription() {
        return intDescription;
    }
    public int getConstitution() {
        return constitution;
    }
    public String getConDescription() {
        return conDescription;
    }
    public int getSpeed() {
        return speed;
    }
    public int getEvasion() {
        return evasion;
    }
    public int getBlock() {
        return block;
    }

    @Override
    public void setStrength(int strength) {
        this.strength = strength;
    }
    @Override
    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
    }
    @Override
    public void setConstitution(int constitution) {
        this.constitution = constitution;
    }
    @Override
    public void setSpeed(int speed) {
        this.speed = speed;
    }
    public void setEvasion(int evasion) {
        this.evasion = evasion;
    }
    public void setBlock(int block) {
        this.block = block;
    }

    public String getSpdDescription() {
        return spdDescription;
    }
    public int getNumStatPoints() {
        return numStatPoints;
    }
    public int getLevel() {
        return level;
    }
    public int getExpIncrease() {
        return expIncrease;
    }
    public int getHealth() {
        return health;
    }
    public int getMaxHealth() {
        return maxHealth;
    }
    public int getMana() {
        return mana;
    }
    public int getMaxMana() {
        return maxMana;
    }
    public int getGold() {
        return gold;
    }
    public int getGoldIncrease() {
        return goldIncrease;
    }
    public int getNumItems() {
        return numItems;
    }
    public int getNumAbilities() {
        return numAbilities;
    }
    public int getNumWeapons() {
        return numWeapons;
    }
    public ArrayList<Ability> getAbilities() {
        return abilities;
    }
    public ArrayList<Weapon> getWeapons() {
        return weapons;
    }
    public Weapon getEquippedWeapon() {
        return equippedWeapon;
    }
    public ArrayList<Item> getItems() {
        return items;
    }
    public boolean isCharacter() {
        return true;
    }
    // extra health
    public int getTempExtraHealth() {
        return tempExtraHealth;
    }
    public int getTempExtraMana() {
        return tempExtraMana;
    }
    // specials
    public boolean getIsStun() {
        return isStun;
    }
    public boolean getIsConfuse() {
        return isConfuse;
    }
    public boolean getIsInvincible() {
        return isInvincible;
    }
    public boolean getIsSilence() {
        return isSilence;
    }
    public boolean getIsInvisible() {
        return isInvisible;
    }

    // stats
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
    public int getEvasionIncrease() {
        return evasionIncrease;
    }
    public int getBlockIncrease() {
        return blockIncrease;
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
    public int getEvasionDecrease() {
        return evasionDecrease;
    }
    public int getBlockDecrease() {
        return blockDecrease;
    }

    public int getStrBase() {
        return strBase;
    }
    public int getIntBase() {
        return intBase;
    }
    public int getConBase() {
        return conBase;
    }
    public int getSpdBase() {
        return spdBase;
    }
    public int getEvasionBase() {
        return evasionBase;
    }
    public int getBlockBase() {
        return blockBase;
    }

    public boolean getIsAlive() {
        return isAlive;
    }

    public Bitmap getAliveVector() {
        return bitmapAlive;
    }
    public Bitmap getDeadVector() {
        return bitmapDead;
    }

    public Map<String, Integer> getDotMap() {
        return dotMap;
    }
    public Map<String, Integer> getSpecialMap() {
        return specialMap;
    }
    public List<ArrayList<Integer>> getTempHealthList() {
        return tempHealthList;
    }
    public List<ArrayList<Integer>> getTempManaList() {
        return tempManaList;
    }
    public List<ArrayList<Object>> getStatIncreaseList() {
        return statIncreaseList;
    }
    public List<ArrayList<Object>> getStatDecreaseList() {
        return statDecreaseList;
    }
}
