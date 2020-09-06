package com.habbybolan.textadventure.model.characterentity;

import android.graphics.Bitmap;

import com.habbybolan.textadventure.model.effects.Dot;
import com.habbybolan.textadventure.model.effects.SpecialEffect;
import com.habbybolan.textadventure.model.inventory.Ability;
import com.habbybolan.textadventure.model.inventory.Item;
import com.habbybolan.textadventure.model.inventory.weapon.Weapon;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class CharacterEntity {

    protected int strIncrease;
    protected int intIncrease;
    protected int conIncrease;
    protected int spdIncrease;
    protected int evasionIncrease;
    protected int blockIncrease;

    protected int strDecrease;
    protected int intDecrease;
    protected int conDecrease;
    protected int spdDecrease;
    protected int evasionDecrease;
    protected int blockDecrease;

    protected int strBase;
    protected int intBase;
    protected int conBase;
    protected int spdBase;
    protected int evasionBase;
    protected int blockBase;
    
    protected int strength;
    protected int intelligence;
    protected int constitution;
    protected int speed;
    protected int evasion;
    protected int block;

    protected int numStatPoints;

    private String strDescription = "Increases the damage of attack based moves.";
    private String intDescription = "Increases the damage of ability based moves and increases your mana pool.";
    private String conDescription = "Increases your health pool and ability to block attacks.";
    private String spdDescription = "Increases your ability to attack quicker and to evade attacks and traps.";

    // DOTS
    protected boolean isFire = false;
    protected boolean isBleed = false;
    protected boolean isPoison = false;
    protected boolean isFrostBurn = false;
    protected boolean isHealDot = false;
    protected boolean isManaDot = false;

    // specials
    protected boolean isStun = false;
    protected boolean isConfuse = false;
    protected boolean isInvincible = false;
    protected boolean isSilence = false;
    protected boolean isInvisible = false;

    // bars
    protected int health;
    protected int maxHealth;
    protected int mana;
    protected int maxMana;

    protected int tempExtraHealth = 0;
    protected int tempExtraMana = 0;

    protected int numAbilities;
    protected int numWeapons;
    protected int numItems;

    protected int level;

    protected boolean isAlive;
    protected Bitmap bitmapAlive;
    protected Bitmap bitmapDead;

    // Inventory
    protected ArrayList<Ability> abilities = new ArrayList<>();
    protected ArrayList<Weapon> weapons = new ArrayList<>();
    protected ArrayList<Item> items = new ArrayList<>();


    // decrement all ability cooldowns
    public abstract void decrCooldowns();

    // *** Damage ***

    // check any buffs or protections that characterEntity has
    // remove the damage amount from target health if no protection
    // or remove target's temporary extra health and direct health if any overflow of damage
    public int damageTarget(int damage) {
        int overFlow = 0; // keeps track of damage if it gets rid of all tempExtraHealth
        if ((getTempExtraHealth() - damage) < 0) {
            overFlow = Math.abs(getTempExtraHealth() - damage);
        }
        // if the target has some temporary extra health, do damage to that first
        if (getTempExtraHealth() > 0) {
            depleteTempExtraHealth(damage);
        }
        // direct health takes damage if any overFlow from tempHealth
        return getHealth() - overFlow;
    }

    // returns a random damage number between damageMin and damageMax
    // apply any additional strength the user has to the damage
    public int getRandomAmount(int min, int max) {
        int damageDifference = max - min;
        Random rand  = new Random();
        int damageRoll = rand.nextInt(damageDifference+1);
        return damageRoll + min + getStrength();
    }

    // heals a target for a specific amount
    public int changeHealth(int amount) {
        setHealth(getHealth()+amount);
        if (getHealth() > getMaxHealth()) setHealth(getMaxHealth());
        if (getHealth() < 0) setHealth(0);
        return getHealth();
    }

    // regenerates mana for a specific amount
    public int changeMana(int amount) {
        setMana(getMana()+amount);
        if (getMana() > getMaxMana()) setMana(getMaxMana());

        if (getMana() < 0) setMana(0);
        return getMana();
    }


    // *** STATS ***

    // keep track of stat increases - list<ArrayList<stat, duration, amount>> sorted by duration
    protected List<ArrayList<Object>> statIncreaseList = new ArrayList<>();
    public void removeZeroStatIncreaseList() {
        removeZeroStatList(statIncreaseList);
    }
    public void addStatIncreaseList(ArrayList<Object> statIncrease) {
        addStatList(statIncrease, statIncreaseList);
    }

    // keep track of stat decreases - list<ArrayList<stat, duration, amount>> sorted by duration
    List<ArrayList<Object>> statDecreaseList = new ArrayList<>();
    public void removeZeroStatDecreaseList() {
        removeZeroStatList(statDecreaseList);
    }
    public void addStatDecreaseList(ArrayList<Object> statDecrease) {
        addStatList(statDecrease, statDecreaseList);
    }
    // helper for removeZeroStatDecreaseList and removeZeroStatIncreaseList
    private void removeZeroStatList(List<ArrayList<Object>> statList) {
        int index = 0;
        while (index < statList.size()) {
            if ((int)statList.get(index).get(1) == 0) statList.remove(index);
            else break;
        }
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

    // keep track of temp health increase - list<ArrayList<duration, amount>> sorted by duration
    List<ArrayList<Integer>> tempHealthList = new ArrayList<>();
    public void removeZeroTempHealthList() {
        removeZeroTempList(tempHealthList);
    }
    public void addTempHealthList(ArrayList<Integer> tempExtraHealth) {
        addTempList(tempExtraHealth, tempHealthList);
    }

    // keep track of temp mana increase - list<ArrayList<duration, amount>> sorted by duration
    List<ArrayList<Integer>> tempManaList = new ArrayList<>();
    public void removeZeroTempManaList() {
        removeZeroTempList(tempManaList);
    }
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
    // helper for removeZeroTempManaList and removeZeroTempHealthList
    private void removeZeroTempList(List<ArrayList<Integer>> tempList) {
        int index = 0;
        while (index < tempList.size()) {
            if ((int)tempList.get(index).get(0) == 0) tempList.remove(index);
            else break;
        }
    }


    // updates all the stat's main value, given its new baseStat, statIncrease, and statDecrease
    public void updateStats() {
        // update STR
        setStrength(getStrIncrease() + getStrDecrease() + getStrBase());
        if (getStrength() < 0) setStrength(0);
        // update INT
        setIntelligence(getIntIncrease() + getIntDecrease() + getIntBase());
        if (getIntelligence() < 0) setIntelligence(0);
        // update CON
        setConstitution(getConIncrease() + getConDecrease() + getConBase());
        if (getConstitution() < 0) setConstitution(0);
        // update SPD
        setSpeed(getSpdIncrease() + getSpdDecrease() + getSpdBase());
        if (getSpeed() < 0) setSpeed(0);
        // update Evasion
        setEvasion(getEvasionIncrease() + getEvasionDecrease() + getEvasionBase());
        if (getEvasion() < 0) setEvasion(0);
        // update Block
        setBlock(getBlockIncrease() + getBlockDecrease() + getBlockBase());
        if (getBlock() < 0) setBlock(0);
    }

    // add a new stat increase - can stack these, each stat increase represented as the list inside a list
    public void addNewStatIncrease(String stat, int duration, int amount) {
        ArrayList<Object> statValues = new ArrayList<>();
        statValues.add(stat);
        statValues.add(duration);
        statValues.add(amount);
        statIncreaseList.add(statValues);
        findStatToIncrease((String)statValues.get(0), (int)statValues.get(2));
        updateStats();
    }

    // apply the stat increase to statIncrease and stat values
    private void findStatToIncrease(String stat, int amount) {
        switch (stat) {
            case STR:
                setStrIncrease(getStrIncrease() + amount);
                break;
            case INT:
                setIntIncrease(getIntIncrease() + amount);
                break;
            case CON:
                setConIncrease(getConIncrease() + amount);
                break;
            case SPD:
                setSpdIncrease(getSpdIncrease() + amount);
                break;
            case EVASION:
                setEvasionIncrease(getEvasionIncrease() + amount);
                break;
            case BLOCK:
                setBlockIncrease(getBlockIncrease() + amount);
                break;
        }
        findStatToAlter(stat);
    }

    // undo a stat increase
    private void undoStatIncrease(String stat, int amount) {
        switch (stat) {
            case STR:
                setStrIncrease(getStrIncrease() - amount);
                break;
            case INT:
                setIntIncrease(getIntIncrease() - amount);
                break;
            case CON:
                setConIncrease(getConIncrease() - amount);
                break;
            case SPD:
                setSpdIncrease(getSpdIncrease() - amount);
                break;
            case EVASION:
                setEvasionIncrease(getEvasionIncrease() - amount);
                break;
            case BLOCK:
                setBlockIncrease(getBlockIncrease() - amount);
                break;
        }
        findStatToAlter(stat);
    }

    // add a new stat decrease - can stack these, each stat decrease represented as the list inside a list
    // if the decrease goes less than 0 of a stat, then reduce the amount that the stat is decreased
    public void addNewStatDecrease(String stat, int duration, int amount) {
        ArrayList<Object> statValues = new ArrayList<>();
        statValues.add(stat);
        statValues.add(duration);
        statValues.add(amount);
        statDecreaseList.add(statValues);
        findStatToDecrease((String)statValues.get(0), (int)statValues.get(2));
        updateStats();
    }

    // apply the stat decrease to statDecrease and stat values
    private void findStatToDecrease(String stat, int amount) {
        switch (stat) {
            case STR:
                setStrDecrease(getStrDecrease() + amount);
                break;
            case INT:
                setIntDecrease(getIntDecrease() + amount);
                break;
            case CON:
                setConDecrease(getConDecrease() + amount);
                break;
            case SPD:
                setSpdDecrease(getSpdDecrease() + amount);
                break;
            case EVASION:
                setEvasionDecrease(getEvasionDecrease() + amount);
                break;
            case BLOCK:
                setBlockDecrease(getBlockDecrease() + amount);
                break;
        }
        findStatToAlter(stat);
    }

    // undo a stat decrease
    private void undoStatDecrease(String stat, int amount) {
        switch (stat) {
            case STR:
                setStrDecrease(getStrDecrease() - amount);
                break;
            case INT:
                setIntDecrease(getIntDecrease() - amount);
                break;
            case CON:
                setConDecrease(getConDecrease() - amount);
                break;
            case SPD:
                setSpdDecrease(getSpdDecrease() - amount);
                break;
            case EVASION:
                setEvasionDecrease(getEvasionDecrease() - amount);
                break;
            case BLOCK:
                setBlockDecrease(getBlockDecrease() - amount);
                break;
        }
        findStatToAlter(stat);
    }

    // alter the stat amount, given it's new statIncrease or statDecrease
    private void findStatToAlter(String stat) {
        switch (stat) {
            case STR:
                setStrength((getStrBase() + getStrIncrease()) - getStrDecrease());
                if (getStrength() < 0) setStrength(0);
                break;
            case INT:
                setIntelligence((getIntBase() + getIntIncrease()) - getIntDecrease());
                if (getIntelligence() < 0) setIntelligence(0);
                break;
            case CON:
                setConstitution((getConBase() + getConIncrease()) - getConDecrease());
                if (getConstitution() < 0) setConstitution(0);
                break;
            case SPD:
                setSpeed((getSpdBase() + getSpdIncrease()) - getSpdDecrease());
                if (getSpeed() < 0) setSpeed(0);
                break;
            case EVASION:
                setEvasion((getEvasionBase() + getEvasionIncrease()) - getEvasionDecrease());
                if (getEvasion() < 0) setEvasion(0);
                break;
            case BLOCK:
                setBlock((getBlockBase() + getBlockIncrease()) - getBlockDecrease());
                if (getBlock() < 0) setBlock(0);
                break;
        }
    }

    // decrement the time remaining for the stat change
        // if the duration reaches 0 after decrement, reverse the change and remove from appropriate statChange list
    public void decrementStatChangeDuration() {
        List<ArrayList<Object>> statIncreaseList = getStatIncreaseList();
        for (int i = 0; i < statIncreaseList.size(); i++) {
            // get the duration of the stat change at index i and decrement
            int duration = (int)statIncreaseList.get(i).get(1);
            statIncreaseList.get(i).set(1, duration-1);
            // if duration is 0 after decrement, remove the stat change
            if (duration-1 == 0) {
                String stat = (String)statIncreaseList.get(i).get(0);
                int amount = (int)statIncreaseList.get(i).get(2);
                statIncreaseList.remove(i);
                i--;
                undoStatIncrease(stat, amount);
            }
        }
        for (int i = 0; i < statDecreaseList.size(); i++) {
            // get the duration of the stat change at index i and decrement
            int duration = (int)statDecreaseList.get(i).get(1);
            statDecreaseList.get(i).set(1, duration-1);
            // if duration is 0 after decrement, remove the stat change
            if (duration-1 == 0) {
                String stat = (String)statDecreaseList.get(i).get(0);
                int amount = (int)statDecreaseList.get(i).get(2);
                statDecreaseList.remove(i);
                i--;
                undoStatDecrease(stat, amount);
            }
        }
        updateStats();
    }

    public static final String STR = "STR";
    public static final String INT = "INT";
    public static final String CON = "CON";
    public static final String SPD = "SPD";
    public static final String EVASION = "Evasion";
    public static final String BLOCK = "Block";
    public static final String TEMP_HEALTH = "Temporary health";
    public static final String TEMP_MANA = "Temporary mana";


    // adds a new tempExtraHealth/tempExtraMana, in duration order, where smallest duration left at index 0 to array
    // update tempExtraHealth/Mana value
    public void addNewTempExtraHealthMana(String type, int duration, int tempExtra) {
        List<ArrayList<Integer>> list;
        if (type.equals(TEMP_HEALTH)) {
            list = tempHealthList; // TODO; is this a deep or shallow copy?
            setTempExtraHealth(getTempExtraHealth() + tempExtra);
            updateTempListWithNewExtra(duration, tempExtra, tempHealthList);
        } else {
            list = tempManaList;
            setTempExtraMana(getTempExtraMana() + tempExtra);
            updateTempListWithNewExtra(duration, tempExtra, tempManaList);
        }
    }
    // Helper for addNewTempExtraHealthMana for adding new tempExtraHealth/Mana to lists

    private void updateTempListWithNewExtra(int duration, int tempExtra, List<ArrayList<Integer>> list) {
        ArrayList<Integer> newTemp = new ArrayList<>();
        newTemp.add(duration);
        newTemp.add(tempExtra);
        int i = 0; // index of tempHealthList
        for (ArrayList listTemp : list) {
            if (newTemp.get(0) < (int) listTemp.get(0)) {
                insertTempAtPosition(newTemp, i, list);
                break; // new element added, break from loop
            }
            i++;
        }
    }

    // deplete the tempExtraHealth buffs by amount, deleting any buffs that are completely depleted ( = 0)
    private void depleteTempExtraHealth(int amount) {
        depleteTempExtra(amount, tempHealthList);
    }

    // deplete the tempExtraMana buffs by amount, deleting any buffs that are completely depleted ( = 0)
    private void depleteTempExtraMana(int amount) {
        depleteTempExtra(amount, tempManaList);
    }

    // helper for depleteTempExtraHealth/Mana to deplete and/or remove extra amount from array indices
    private void depleteTempExtra(int amount, List<ArrayList<Integer>> list) {
        // loop through all indices of list and remove element if more amount to deplete than amount left over
        // start from index 0 as the list is sorted from from smallest duration left to largest - remove smallest first
        int index = 0;
        while (amount > 0 && index < list.size()) {
            ArrayList<Integer> element = list.get(index);
            int eleAmount = element.get(1);
            element.set(1, eleAmount-amount);
            // remove the element from list if completely depleted, and alter amount by amount applied
            if (element.get(1) < 0) {
                list.remove(index);
                amount -= eleAmount;
            }
            index++;
        }
    }

    // insert the newTempHealth buff at index i and push all indices i+ to the right\
    // helper for addNewTempExtraHealth
    private void insertTempAtPosition(ArrayList<Integer> newTemp, int i, List<ArrayList<Integer>> list) {
        ArrayList<Integer> prev = list.get(i);
        list.add(i, newTemp);

        for (int index = i+1; i < list.size(); index++) {
            list.add(index, prev);
            prev = list.get(index);
        }
        list.add(prev); // added to end to avoid outOfBounds exception
    }

    // decrements the duration of each tempExtraHealth in tempHealthList
    // if the duration reaches 0, then remove the extra health from the list
    public void decrementTempExtraDuration() {
        decrementTemp(tempHealthList);
        decrementTemp(tempManaList);
    }

    // decrement the duration of an arrayList, removing it if it reaches duration 0
    private void decrementTemp(List<ArrayList<Integer>> list) {
        int index = 0;
        // iterate over all tempHealthList elements, decrementing the duration value, removing if duration = 0;
        while (index < list.size()) {
            ArrayList<Integer> listTemp = list.get(index);
            int duration = listTemp.get(0);
            if (duration-1 > 0) {
                listTemp.set(index, duration - 1);
                index++;
            } else {
                list.remove(index);
            }
        }
    }

    // ***AOE***

    // todo: how to do AOE??
    public void doAoeStuff() {

    }


    // ***ABILITIES***

    // adds a new ability and applies its effects - stat increases, specials, and dots
    // todo: damage not observed as damageTarget returns value, doesn't set it
    public void addNewAbilityEffects(Ability ability) {
        // todo: scale with Intelligence
        if (ability.getMinDamage() != 0) damageTarget(getRandomAmount(ability.getMinDamage(), ability.getMaxDamage()));
        if (ability.getDamageAoe() != 0) doAoeStuff(); // todo: aoe
        // specials
        if (ability.getIsConfuse()) addNewSpecial(new SpecialEffect(SpecialEffect.CONFUSE, ability.getDuration()));
        if (ability.getIsStun()) addNewSpecial(new SpecialEffect(SpecialEffect.STUN, ability.getDuration()));
        if (ability.getIsInvincibility()) addNewSpecial(new SpecialEffect(SpecialEffect.INVINCIBILITY, ability.getDuration()));
        if (ability.getIsSilence()) addNewSpecial(new SpecialEffect(SpecialEffect.SILENCE, ability.getDuration()));
        if (ability.getIsInvisible()) addNewSpecial(new SpecialEffect(SpecialEffect.INVISIBILITY, ability.getDuration()));
        // DOT
        if (ability.getIsFire()) addNewDot(new Dot(Dot.FIRE, false));
        if (ability.getIsPoison()) addNewDot(new Dot(Dot.POISON, false));
        if (ability.getIsBleed()) addNewDot(new Dot(Dot.BLEED, false));
        if (ability.getIsFrostBurn()) addNewDot(new Dot(Dot.FROSTBURN, false));
        if (ability.getIsHealDot()) addNewDot(new Dot(Dot.HEALTH_DOT, false));
        if (ability.getIsManaDot()) addNewDot(new Dot(Dot.MANA_DOT, false));
        // direct heal/mana
        if (ability.getHealMin() != 0) changeHealth(getRandomAmount(ability.getHealMin(), ability.getHealMax()));
        if (ability.getManaMin() != 0) changeMana(getRandomAmount(ability.getManaMin(), ability.getManaMax()));
        // stat increases
        if (ability.getStrIncrease() != 0) addNewStatIncrease(STR, ability.getDuration(), ability.getStrIncrease());
        if (ability.getIntIncrease() != 0) addNewStatIncrease(INT, ability.getDuration(), ability.getIntIncrease());
        if (ability.getConIncrease() != 0) addNewStatIncrease(CON, ability.getDuration(), ability.getConIncrease());
        if (ability.getSpdIncrease() != 0) addNewStatIncrease(SPD, ability.getDuration(), ability.getSpdIncrease());
        if (ability.getEvadeIncrease() != 0) addNewStatIncrease(EVASION, ability.getDuration(), ability.getEvadeIncrease());
        if (ability.getBlockIncrease() != 0) addNewStatIncrease(BLOCK, ability.getDuration(), ability.getBlockIncrease());
        // stat decreases
        if (ability.getStrDecrease() != 0) addNewStatDecrease(STR, ability.getDuration(), ability.getStrDecrease());
        if (ability.getIntDecrease() != 0) addNewStatDecrease(INT, ability.getDuration(), ability.getIntDecrease());
        if (ability.getConDecrease() != 0) addNewStatDecrease(CON, ability.getDuration(), ability.getConDecrease());
        if (ability.getSpdDecrease() != 0) addNewStatDecrease(SPD, ability.getDuration(), ability.getSpdDecrease());
        if (ability.getEvadeDecrease() != 0) addNewStatDecrease(EVASION, ability.getDuration(), ability.getEvadeDecrease());
        if (ability.getBlockDecrease() != 0) addNewStatDecrease(BLOCK, ability.getDuration(), ability.getBlockDecrease());
        // temp extra health- part of stat
        if (ability.getTempExtraHealth() != 0) addNewTempExtraHealthMana(TEMP_HEALTH, ability.getDuration(), ability.getTempExtraHealth());
    }

    // ** Special Effects **

    // keep track of special duration left <name, duration> sorted by duration
    ArrayList<SpecialEffect> specialList = new ArrayList<>();

    // returns true if still effected by status effect from other items or applied special effect
    public boolean isStillSpecialEffect(SpecialEffect special) {
        switch (special.getType()) {
            case SpecialEffect.CONFUSE:
                return isStillConfuse();
            case SpecialEffect.STUN:
                return isStillStun();
            case SpecialEffect.INVINCIBILITY:
                return isStillInvincible();
            case SpecialEffect.INVISIBILITY:
                return isStillInvisible();
            case SpecialEffect.SILENCE:
                return isStillSilence();
            default:
                throw new IllegalArgumentException();
        }
    }
    // helper for isStillSpecialEffect to check if confuse is still applied after item removal
    private boolean isStillConfuse() {
        boolean stillConfused = false;
        for (Item item : items) {
            if (item.getIsConfuse()) {
                stillConfused = true;
                break;
            }
        }
        if (specialList.contains(new SpecialEffect(SpecialEffect.CONFUSE, false))) stillConfused = true;
        setIsConfuse(stillConfused);
        return stillConfused;
    }
    // helper for isStillSpecialEffect to check if stun is still applied after item removal
    private boolean isStillStun() {
        boolean stillStunned = false;
        for (Item item : items) {
            if (item.getIsStun()) {
                stillStunned = true;
                break;
            }
        }
        if (specialList.contains(new SpecialEffect(SpecialEffect.STUN, false))) stillStunned = true;
        setIsStun(stillStunned);
        return stillStunned;
    }
    // helper for isStillSpecialEffect to check if Silence is still applied after item removal
    private boolean isStillSilence() {
        boolean stillSilenced = false;
        for (Item item : items) {
            if (item.getIsSilence()) {
                stillSilenced = true;
                break;
            }
        }
        if (specialList.contains(new SpecialEffect(SpecialEffect.SILENCE, false))) stillSilenced = true;
        setIsSilence(stillSilenced);
        return stillSilenced;
    }
    // helper for isStillSpecialEffect to check if Invisibility is still applied after item removal
    private boolean isStillInvisible() {
        boolean stillInvisible = false;
        for (Item item : items) {
            if (item.getIsInvisible()) {
                stillInvisible = true;
                break;
            }
        }
        if (specialList.contains(new SpecialEffect(SpecialEffect.INVISIBILITY, false)))stillInvisible = true;
        setIsInvisible(stillInvisible);
        return stillInvisible;
    }
    // helper for isStillSpecialEffect to check if Invincibility is still applied after item removal
    private boolean isStillInvincible() {
        boolean stillInvincible = false;
        for (Item item : items) {
            if (item.getIsInvincible()) {
                stillInvincible = true;
                break;
            }
        }
        if (specialList.contains(new SpecialEffect(SpecialEffect.INVINCIBILITY, false))) stillInvincible = true;
        setIsInvincible(stillInvincible);
        return stillInvincible;
    }

    public boolean removeInputSpecial(SpecialEffect special) {
        if (special.getIsInfinite()) {
            // key may not be in specialList if isInfinite
            return isStillSpecialEffect(special);
        }
        specialList.remove(special);
        // check if there is an item applying special to character
        return isStillSpecialEffect(special);
    }

    // adds a new special to specialMap if not already existing - alter isSpecial value if new
        // if exists, it reset the time for the special
        // returns true if the special effect went from false->true
    public boolean addNewSpecial(SpecialEffect special) {
        boolean isChanged = false;
        switch (special.getType()) {
            case SpecialEffect.STUN:
                if (!getIsStun()) isChanged = true;
                setIsStun(true);
                break;
            case SpecialEffect.SILENCE:
                if (!getIsSilence()) isChanged = true;
                setIsSilence(true);
                break;
            case SpecialEffect.CONFUSE:
                if (!getIsConfuse()) isChanged = true;
                setIsConfuse(true);
                break;
            case SpecialEffect.INVISIBILITY:
                if (!getIsInvisible()) isChanged = true;
                setIsInvisible(true);
                break;
            case SpecialEffect.INVINCIBILITY:
                if (!getIsInvincible()) isChanged = true;
                setIsInvincible(true);
                break;
        }
        if (!special.getIsInfinite()) {
            // if the duration is not infinite, add to the specialList /
            if (specialList.contains(special)) {
                for (SpecialEffect appliedSpecial : specialList) {
                    if (appliedSpecial.equals(special)) {
                        if (special.getDuration() > appliedSpecial.getDuration()) appliedSpecial.setDuration(special.getDuration());
                    }
                }
            } else {
                alterIsSpecial(special.getType());
                specialList.add(special);
            }
        }
        return isChanged;
    }

    // check if the special is active on the enemy
    // active returns 1, otherwise 0
    public boolean checkSpecials(String special) {
        switch (special) {
            case SpecialEffect.STUN:
                return getIsStun();
            case SpecialEffect.CONFUSE:
                return getIsConfuse();
            case SpecialEffect.INVINCIBILITY:
                return getIsInvincible();
            case SpecialEffect.SILENCE:
                return getIsSilence();
            case SpecialEffect.INVISIBILITY:
                return getIsInvisible();
        }
        throw new IllegalArgumentException();
    }

    // decrements the special duration left
    // if the duration is 0 after decrement, then set isSpecial value to 0
    // if the value < 0, do nothing, permanent special
    public void decrSpecialDuration() {
        for (SpecialEffect special : specialList) {
            special.decrementDuration();
            if (special.getDuration() == 0) {
                removeInputSpecial(special);
            }
        }
    }

    // change the value of isSpecial boolean value
    public void alterIsSpecial(String special) {
        switch (special) {
            case SpecialEffect.STUN:
                setIsStun(!getIsStun());
                break;
            case SpecialEffect.CONFUSE:
                setIsConfuse(!getIsConfuse());
                break;
            case SpecialEffect.INVINCIBILITY:
                setIsInvincible(!getIsInvincible());
                break;
            case SpecialEffect.SILENCE:
                setIsInvincible(!getIsSilence());
                break;
            case SpecialEffect.INVISIBILITY:
                setIsInvisible(!getIsInvisible());
                break;
        }
    }


    // ** Dot effects **

    // list of Dots applied to character
    ArrayList<Dot> dotList = new ArrayList<>();

    /// returns true if still effected by dot effect from other items or applied dots
    public boolean isStillDotEffect(Dot dot) {
        switch (dot.getType()) {
            case Dot.FIRE:
                return isStillFire();
            case Dot.BLEED:
                return isStillBleed();
            case Dot.POISON:
                return isStillPoison();
            case Dot.FROSTBURN:
                return isStillFrostBurn();
            case Dot.HEALTH_DOT:
                return isStillHealthDot();
            case Dot.MANA_DOT:
                return isStillManaDot();
            default:
                throw new IllegalArgumentException();
        }
    }
    // helper for isStillDotEffect to check if Fire is still applied after item removal
    private boolean isStillFire() {
        boolean stillFire = false;
        for (Item item : items) {
            if (item.getIsFire()) {
                stillFire = true;
                break;
            }
        }
        if (dotList.contains(new Dot(Dot.FIRE, 0))) stillFire = true;
        setIsFire(stillFire);
        return stillFire;
    }
    // helper for isStillDotEffect to check if Bleed is still applied after item removal
    private boolean isStillBleed() {
        boolean stillBleed = false;
        for (Item item : items) {
            if (item.getIsBleed()) {
                stillBleed = true;
                break;
            }
        }
        if (dotList.contains(new Dot(Dot.BLEED, 0))) stillBleed = true;
        setIsBleed(stillBleed);
        return stillBleed;
    }
    // helper for isStillDotEffect to check if Poison is still applied after item removal
    private boolean isStillPoison() {
        boolean stillPoison = false;
        for (Item item : items) {
            if (item.getIsPoison()) {
                stillPoison = true;
                break;
            }
        }
        if (dotList.contains(new Dot(Dot.POISON, 0))) stillPoison = true;
        setIsPoison(stillPoison);
        return stillPoison;
    }
    // helper for isStillDotEffect to check if FrostBurn is still applied after item removal
    private boolean isStillFrostBurn() {
        boolean stillFrostBurn = false;
        for (Item item : items) {
            if (item.getIsFrostBurn()) {
                stillFrostBurn = true;
                break;
            }
        }
        if (dotList.contains(new Dot(Dot.FROSTBURN, 0))) stillFrostBurn = true;
        setIsFrostBurn(stillFrostBurn);
        return stillFrostBurn;
    }
    // helper for isStillDotEffect to check if Health dot is still applied after item removal
    private boolean isStillHealthDot() {
        boolean stillHealthDot = false;
        for (Item item : items) {
            if (item.getIsHealthDot()) {
                stillHealthDot = true;
                break;
            }
        }
        if (dotList.contains(new Dot(Dot.HEALTH_DOT, 0))) stillHealthDot = true;
        setIsHealDot(stillHealthDot);
        return stillHealthDot;
    }
    // helper for isStillDotEffect to check if Mana dot is still applied after item removal
    private boolean isStillManaDot() {
        boolean stillManaDot = false;
        for (Item item : items) {
            if (item.getIsManaDot()) {
                stillManaDot = true;
                break;
            }
        }
        if (dotList.contains(new Dot(Dot.MANA_DOT, 0))) stillManaDot = true;
        setIsManaDot(stillManaDot);
        return stillManaDot;
    }

    // remove a dot from the dotList and return true if isDot is still true
    public boolean removeInputDot(Dot dot) {
        if (dot.getIsInfinite()) {
            // key may not be in dotList if isInfinite
            return (isStillDotEffect(dot));
        }
        dotList.remove(dot);
        // check if there is an item applying dot to character
        return isStillDotEffect(dot);
    }

    // adds a new Dot to to dotList if not already existing
        // if exists, it reset the time for the dot
        // if isInfinite is true, then only make isDot true, not adding to dotMap
        // return true if the isDot is set from false to true
    public boolean addNewDot(Dot dot) {
        boolean isChanged = false;
        switch (dot.getType()) {
            case Dot.FIRE:
                if (!getIsFire()) isChanged = true;
                setIsFire(true);
                break;
            case Dot.POISON:
                if (!getIsPoison()) isChanged = true;
                setIsPoison(true);
                break;
            case Dot.BLEED:
                if (!getIsBleed()) isChanged = true;
                setIsBleed(true);
                break;
            case Dot.FROSTBURN:
                if (!getIsFrostBurn()) isChanged = true;
                setIsFrostBurn(true);
                break;
            case Dot.HEALTH_DOT:
                if (!getIsHealDot()) isChanged = true;
                setIsHealDot(true);
                break;
            case Dot.MANA_DOT:
                if (!getIsManaDot()) isChanged = true;
                setIsManaDot(true);
                break;
            default: // shouldn't reach this
                break;
        }
        if (!dot.getIsInfinite()) {
            // if the duration is not infinite, add to the dotList /
            if (dotList.contains(dot)) {
                for (Dot appliedDot : dotList) {
                    if (appliedDot.equals(dot)) {
                        if (dot.getDuration() > appliedDot.getDuration()) appliedDot.setDuration(dot.getDuration());
                    }
                }
            } else {
                dotList.add(dot);
            }
        }
        return isChanged;
    }

    // swap the isDot on entity
    private void alterIsDot(String dotType) {
        switch (dotType) {
            case Dot.FIRE:
                setIsFire(!getIsFire());
                break;
            case Dot.BLEED:
                setIsBleed(!getIsBleed());
                break;
            case Dot.POISON:
                setIsPoison(!getIsPoison());
                break;
            case Dot.FROSTBURN:
                setIsFrostBurn(!getIsFrostBurn());
                break;
            case Dot.HEALTH_DOT:
                setIsHealDot(!getIsHealDot());
                break;
            case Dot.MANA_DOT:
                setIsManaDot(!getIsManaDot());
            default:
                throw new IllegalArgumentException();
        }
    }

    // applies the effect of the DOT and decrements the time remaining in DotMap
        // if duration == 0, then remove from the dotMap
        // returns true if any dots applied are removed
    public void applyDots() {
        for (Dot dot : dotList) {
            findDotToApply(dot.getType());
            dot.decrementDuration();
            if (dot.getDuration() == 0) {
                removeInputDot(dot);
            }
        }
    }

    // helper for applyDots
        // find the proper method to call for using a specific dot
    private void findDotToApply(String dot) {
        switch (dot) {
            case Dot.FIRE:
                applyFire();
                break;
            case Dot.POISON:
                applyPoison();
                break;
            case Dot.BLEED:
                applyBleed();
                break;
            case Dot.FROSTBURN:
                applyFrostBurn();
                break;
            case Dot.HEALTH_DOT:
                applyHealthDot();
                break;
            case Dot.MANA_DOT:
                applyManaDot();
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    // status application of damage over times
    public void applyFire() {
        damageTarget(Dot.FIRE_DAMAGE);
    }
    public void applyPoison() {
        damageTarget(Dot.POISON_DAMAGE);
    }
    public void applyBleed() {
        damageTarget(Dot.BLEED_DAMAGE);
    }
    public void applyFrostBurn() {
        addNewSpecial(new SpecialEffect(SpecialEffect.STUN, Dot.FROSTBURN_DURATION));
        damageTarget(Dot.FROSTBURN_DAMAGE);
    }
    public void applyHealthDot() {
        changeHealth(Dot.HEAL_DOT_AMOUNT);
    }
    public void applyManaDot() {
        changeMana(Dot.MANA_DOT_AMOUNT);
    }



    // GETTERS AND SETTERS *****

    // ** Dot Effects **
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
    public boolean getIsHealDot() {
        return isHealDot;
    }
    public boolean getIsManaDot() {
        return isManaDot;
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
    public void setIsHealDot(boolean isHealDot) {
        this.isHealDot = isHealDot;
    }
    public void setIsManaDot(boolean isManaDot) {
        this.isManaDot = isManaDot;
    }

    // ** inventory **
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
    public ArrayList<Item> getItems() {
        return items;
    }
    public boolean isCharacter() {
        return true;
    }
    // ** bars **
    public int getTempExtraHealth() {
        return tempExtraHealth;
    }
    public int getTempExtraMana() {
        return tempExtraMana;
    }
    public void setTempExtraHealth(int tempExtraHealth) {
        this.tempExtraHealth = tempExtraHealth;
    }
    public void setTempExtraMana(int tempExtraMana) {
        this.tempExtraMana = tempExtraMana;
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

    // ** specials **
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

    // ** stats **
    public int getNumStatPoints() {
        return numStatPoints;
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
    public void setEvasionIncrease(int evasionIncrease) {
        this.evasionIncrease = evasionIncrease;
    }
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
    public void setEvasionDecrease(int evasionDecrease) {
        this.evasionDecrease = evasionDecrease;
    }
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
    public void setEvasionBase(int evasionBase) {
        this.evasionBase = evasionBase;
    }
    public void setBlockBase(int blockBase) {
        this.blockBase = blockBase;
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
    public String getSpdDescription() {
        return spdDescription;
    }
    public int getEvasion() {
        return evasion;
    }
    public int getBlock() {
        return block;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }
    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
    }
    public void setConstitution(int constitution) {
        this.constitution = constitution;
    }
    public void setSpeed(int speed) {
        this.speed = speed;
    }
    public void setEvasion(int evasion) {
        this.evasion = evasion;
    }
    public void setBlock(int block) {
        this.block = block;
    }

    public boolean getIsAlive() {
        return isAlive;
    }
    public void setIsAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    public Bitmap getAliveVector() {
        return bitmapAlive;
    }
    public Bitmap getDeadVector() {
        return bitmapDead;
    }

    public ArrayList<Dot> getDotList() {
        return dotList;
    }
    public ArrayList<SpecialEffect> getSpecialList() {
        return specialList;
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

    public int getLevel() {
        return level;
    }
    public void setLevel(int level) {
        this.level = level;
    }
}
