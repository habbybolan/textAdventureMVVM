package com.habbybolan.textadventure.model.characterentity;

import androidx.databinding.ObservableArrayList;

import com.habbybolan.textadventure.model.effects.Dot;
import com.habbybolan.textadventure.model.effects.Effect;
import com.habbybolan.textadventure.model.effects.SpecialEffect;
import com.habbybolan.textadventure.model.effects.TempBar;
import com.habbybolan.textadventure.model.effects.TempStat;
import com.habbybolan.textadventure.model.inventory.Ability;
import com.habbybolan.textadventure.model.inventory.Item;
import com.habbybolan.textadventure.model.inventory.weapon.Weapon;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class CharacterEntity implements Comparable<CharacterEntity> {

    protected int strIncrease = 0;
    protected int intIncrease = 0;
    protected int conIncrease = 0;
    protected int spdIncrease = 0;
    protected int evasionIncrease = 0;
    protected int blockIncrease = 0;

    protected int strDecrease = 0;
    protected int intDecrease = 0;
    protected int conDecrease = 0;
    protected int spdDecrease = 0;
    protected int evasionDecrease = 0;
    protected int blockDecrease = 0;

    protected int strBase = 0;
    protected int intBase = 0;
    protected int conBase = 0;
    protected int spdBase = 0;
    protected int evasionBase = 0;
    protected int blockBase = 0;
    
    protected int strength = 0;
    protected int intelligence = 0;
    protected int constitution = 0;
    protected int speed = 0;
    protected int evasion = 0;
    protected int block = 0;

    int numStatPoints = 0;
    boolean isCharacter;

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
    int ID;

    protected boolean isAlive = true;
    protected int drawableResID;
    protected int drawableDeadResID;
    protected int drawableIconResID;
    protected int drawableIconDeadResID;

    // Inventory
    protected ArrayList<Ability> abilities = new ArrayList<>();
    protected ArrayList<Weapon> weapons = new ArrayList<>();
    protected ArrayList<Item> items = new ArrayList<>();

    // keep track of special duration left <name, duration> sorted by duration
    ObservableArrayList<SpecialEffect> specialList = new ObservableArrayList<>();

    // list of Dots applied to character
    ObservableArrayList<Dot> dotList = new ObservableArrayList<>();

    // keep track of stat increases - list<ArrayList<stat, duration, amount>> sorted by duration
    ObservableArrayList<TempStat> statIncreaseList = new ObservableArrayList<>();

    // keep track of stat decreases - list<ArrayList<stat, duration, amount>> sorted by duration
    ObservableArrayList<TempStat> statDecreaseList = new ObservableArrayList<>();

    // decrement all ability cooldowns
    public abstract void decrCooldowns();


    // ** comparing CharacterEntities **

    @Override
    public int compareTo(CharacterEntity entity) {
        return Integer.compare(entity.getSpeed(), speed);
    }

    abstract JSONObject serializeToJSON() throws JSONException;

    // *** Damage ***

    // check any buffs or protections that characterEntity has
    // remove the damage amount from target health if no protection
    // or remove target's temporary extra health and direct health if any overflow of damage
    public int damageTarget(int damage) {
        if (damage < 0) throw new IllegalArgumentException();
        int prevHealth = health;
        int overFlow = 0; // keeps track of damage if it gets rid of all tempExtraHealth
        if ((getTempExtraHealth() - damage) < 0) {
            overFlow = Math.abs(getTempExtraHealth() - damage);
        }
        // if the target has some temporary extra health, do damage to that first
        if (getTempExtraHealth() > 0) {
            depleteTempExtraHealth(damage);
        }
        // direct health takes damage if any overFlow from tempHealth
        health -= overFlow;
        if (health < 0) {
            health = 0;
            isAlive = false;
        }
        return health - prevHealth;
    }

    // returns a random damage number between damageMin and damageMax
    // apply any additional strength the user has to the damage
    public int getRandomAmount(int min, int max) {
        int damageDifference = max - min;
        Random rand  = new Random();
        int damageRoll = rand.nextInt(damageDifference+1);
        return damageRoll + min + getStrength();
    }

    // returns amount to change heal entity by and change
    public int increaseHealth(int amount) {
        if (amount < 0) throw new IllegalArgumentException();
        int prevHealth = health;
        if (amount + health >= maxHealth) health = maxHealth;
        else health = health+amount;
        return health-prevHealth;
    }

    // changes the direct health and max health by a certain amount
        // not used for taking damage or heals
        // used for indefinite/permanent health changes
        // returns health change if any
    public int changeHealth(int amount) {
        int tempHealth = health;
        maxHealth += amount;
        if (amount > 0)
            health += amount;
        else {
            if (health > maxHealth)
                health = maxHealth;
        }
        return health - tempHealth;
    }

    // returns amount to recover mana of entity for
    public int changeMana(int amount) {
        int prevMana = mana;
        if (amount + mana > maxMana) mana = maxMana;
        else if (amount + mana < 0) mana = 0;
        else mana = mana+amount;
        return mana - prevMana;
    }


    // *** STATS ***

    public static final String STR = "STR";
    public static final String INT = "INT";
    public static final String CON = "CON";
    public static final String SPD = "SPD";
    public static final String EVASION = "Evasion";
    public static final String BLOCK = "Block";

    // updates all the stat's main value, given its new baseStat, statIncrease, and statDecrease
        // helper for when statIncrease/statDecrease changes
    private void updateStats() {
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
    public void addNewStatIncrease(TempStat tempStat) {
        statIncreaseList.add(tempStat);
        findStatToIncrease(tempStat);
        updateStats();
    }

    // apply the stat increase to statIncrease and stat values
        // helper for addNewStatIncrease
    private void findStatToIncrease(TempStat tempStat) {
        final int amount = tempStat.getAmount();
        switch (tempStat.getType()) {
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
        findStatToAlter(tempStat);
    }

    // add a new stat decrease - can stack these, each stat decrease represented as the list inside a list
    // if the decrease goes less than 0 of a stat, then reduce the amount that the stat is decreased
    public void addNewStatDecrease(TempStat tempStat) {
        statDecreaseList.add(tempStat);
        findStatToDecrease(tempStat);
        updateStats();
    }

    // apply the stat decrease to statDecrease and stat values
    private void findStatToDecrease(TempStat tempStat) {
        final int amount = tempStat.getAmount();
        switch (tempStat.getType()) {
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
        findStatToAlter(tempStat);
    }

    // alter the stat amount, given it's new statIncrease or statDecrease
        // helper for changes stat field value
    private void findStatToAlter(TempStat tempStat) {
        switch (tempStat.getType()) {
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
    public void decrementTempStatDuration() {
        decrementTempStatIncrDuration();
        decrementTempStatDecrDuration();
        updateStats();
    }

    // decrement the duration of all temp incr stat durations
    public void decrementTempStatIncrDuration() {
        for (int i = 0; i < statIncreaseList.size(); i++) {
            // get the duration of the stat change at index i and decrement
            int duration = statIncreaseList.get(i).getDuration();
            statIncreaseList.get(i).decrementDuration();
            // if duration is 0 after decrement, remove the stat change
            if (duration-1 == 0) {
                TempStat tempStat = statIncreaseList.get(i);
                statIncreaseList.remove(i);
                i--;
                undoStatIncrease(tempStat);
            }
        }
    }
    // decrement the duration of all temp decr stat durations
    public void decrementTempStatDecrDuration() {
        for (int i = 0; i < statDecreaseList.size(); i++) {
            // get the duration of the stat change at index i and decrement
            int duration = statDecreaseList.get(i).getDuration();
            statDecreaseList.get(i).decrementDuration();
            // if duration is 0 after decrement, remove the stat change
            if (duration-1 == 0) {
                TempStat tempStat = statDecreaseList.get(i);
                statDecreaseList.remove(i);
                i--;
                undoStatDecrease(tempStat);
            }
        }
    }

    // undo a stat increase
        // helper for decrementStatChangeDuration()
    private void undoStatIncrease(TempStat tempStat) {
        final int amount = tempStat.getAmount();
        switch (tempStat.getType()) {
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
        findStatToAlter(tempStat);
    }

    // undo a stat decrease
        // helper for decrementStatChangeDuration()
    private void undoStatDecrease(TempStat tempStat) {
        final int amount = tempStat.getAmount();
        switch (tempStat.getType()) {
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
        findStatToAlter(tempStat);
    }

    // BARS

    public static final String TEMP_HEALTH = "Temporary health";
    public static final String TEMP_MANA = "Temporary mana";

    // keep track of temp health increase - list<ArrayList<duration, amount>> sorted by duration
    List<TempBar> tempHealthList = new ArrayList<>();
    public ArrayList<TempBar> removeZeroTempHealthList() {
        return removeZeroTempBarList(tempHealthList);
    }
    public void addTempHealthList(TempBar tempExtraHealth) {
        tempHealthList.add(tempExtraHealth);
        // update the changes to tempHealthList
        health = health + tempExtraHealth.getAmount();
        maxHealth = maxHealth + tempExtraHealth.getAmount();
    }


    // keep track of temp mana increase - list<ArrayList<duration, amount>> sorted by duration
    List<TempBar> tempManaList = new ArrayList<>();
    public void removeZeroTempManaList() {
        removeZeroTempBarList(tempManaList);
    }
    public void addTempManaList(TempBar tempExtraMana) {
        tempManaList.add(tempExtraMana);
        // apply changes to tempManaList
        mana = mana + tempExtraMana.getAmount();
        maxMana = maxMana + tempExtraMana.getAmount();
    }

    // helper for removeZeroTempManaList and removeZeroTempHealthList
    private ArrayList<TempBar> removeZeroTempBarList(List<TempBar> tempList) {
        int index = 0;
        ArrayList<TempBar> tempBars = new ArrayList<>();
        while (index < tempList.size()) {
            if (tempList.get(index).getDuration() == 0) {
                tempBars.add(tempList.get(index));
                tempList.remove(index);
                resetTempBar(tempList.get(index));
            }
            else break;
        }
        return tempBars;
    }

    // resets the temporary increases of tempHealth and tempMana
    private void resetTempBar(TempBar tempBar) {
        if (tempBar.getType().equals(Effect.TEMP_HEALTH)) {
            // reset the temp health
            maxHealth = maxHealth - tempBar.getAmount();
            // only change health if maxHealth drops below health value
            if (maxHealth < health) health = maxHealth;
        } else {
            // otherwise reset the temp mana
            maxMana = maxMana - tempBar.getAmount();
            // only change mana if maxMana drops below mana value
            if (maxMana < mana) mana = maxMana;
        }
    }

    // adds a new tempExtraHealth/tempExtraMana, in duration order, where smallest duration left at index 0 to array
    // update tempExtraHealth/Mana value
    public void addNewTempExtraHealthMana(TempBar tempBar) {
        if (tempBar.getType().equals(TEMP_HEALTH)) {
            setTempExtraHealth(getTempExtraHealth() + tempBar.getAmount());
            tempHealthList.add(tempBar);
        } else {
            setTempExtraMana(getTempExtraMana() + tempBar.getAmount());
            tempManaList.add(tempBar);
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
    private void depleteTempExtra(int amount, List<TempBar> list) {
        // loop through all indices of list and remove element if more amount to deplete than amount left over
        // looking at index 0 as the list is sorted from from smallest duration left to largest
        final int index = 0;
        while (amount > 0 && index < list.size()) {
            int eleAmount = list.get(index).getAmount();
            // remove the element from list if completely depleted, and alter amount by amount applied
            if (eleAmount <= amount) {
                list.remove(index);
                amount -= eleAmount;
            } else {
                list.get(index).decrementAmount(amount);
                amount = 0;
            }
        }
    }

    // decrements the duration of each tempExtraHealth in tempHealthList
    // if the duration reaches 0, then remove the extra health from the list
    public void decrementTempExtraDuration() {
        decrementTempExtraHealthDuration();
        decrementTempExtraManaDuration();
    }

    public ArrayList<TempBar> decrementTempExtraHealthDuration() {
        return decrementTemp(tempHealthList);
    }
    public ArrayList<TempBar> decrementTempExtraManaDuration() {
        return decrementTemp(tempManaList);
    }

    // decrement the duration of an arrayList, removing it if it reaches duration 0
    private ArrayList<TempBar> decrementTemp(List<TempBar> list) {
        ArrayList<TempBar> tempBarsRemoved = new ArrayList<>();
        int index = 0;
        // iterate over all tempHealthList elements, decrementing the duration value, removing if duration = 0;
        while (index < list.size()) {
            list.get(index).decrementDuration();
            if (list.get(index).getDuration() > 0) {
                index++;
            } else {
                tempBarsRemoved.add(list.get(index));
                list.remove(index);
            }
        }
        return tempBarsRemoved;
    }

    // ***AOE***

    // todo: how to do AOE??
    public void doAoeStuff() {

    }


    // ***ABILITIES***

    // ** Special Effects **

    // returns true if still effected by status effect from other items or applied special effect
    private boolean isStillSpecialEffect(SpecialEffect special) {
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
        if (specialList.contains(new SpecialEffect(SpecialEffect.CONFUSE))) stillConfused = true;
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
        if (specialList.contains(new SpecialEffect(SpecialEffect.STUN))) stillStunned = true;
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
        if (specialList.contains(new SpecialEffect(SpecialEffect.SILENCE))) stillSilenced = true;
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
        if (specialList.contains(new SpecialEffect(SpecialEffect.INVISIBILITY)))stillInvisible = true;
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
        if (specialList.contains(new SpecialEffect(SpecialEffect.INVINCIBILITY))) stillInvincible = true;
        setIsInvincible(stillInvincible);
        return stillInvincible;
    }

    public boolean removeInputSpecial(SpecialEffect special) {
        if (special.getIsIndefinite()) {
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
        if (!special.getIsIndefinite()) {
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
        int i = 0;
        while (i < specialList.size()) {
            SpecialEffect special = specialList.get(i);
            special.decrementDuration();
            if (special.getDuration() == 0) {
                removeInputSpecial(special);
            } else {
                i++;
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

    /**
     * Finds the correct method to call to check if the dot effect is still applied to character.
     * @param dot   The dot effect to check.
     * @return      True if the dot effect should still be applied.
     */
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

    /**
     * Helper for isStillDotEffect to check if the health dot is still applied.
     * @return  True if isFire should still be true by indefinite means.
     */
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

    /**
     * Helper for isStillDotEffect to check if the health dot is still applied.
     * @return  True if isBleed should still be true by indefinite means.
     */
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

    /**
     * Helper for isStillDotEffect to check if the health dot is still applied.
     * @return  True if isPoison should still be true by indefinite means.
     */
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

    /**
     * Helper for isStillDotEffect to check if the health dot is still applied.
     * @return  True if isFrostBurn should still be true by indefinite means.
     */
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

    /**
     * Helper for isStillDotEffect to check if the health dot is still applied.
     * @return  True if isHealthDot should still be true by indefinite means.
     */
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

    /**
     * Helper for isStillDotEffect for checking if the mana dot is still applied.
     * @return  True is isManaDot should still be true by indefinite means.
     */
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


    /**
     * Remove a specific dot from dotList. Find if the effect of the dot is still applied to the character
     * through an indefinite means.
     * @param dot   The dot to remove from dotList.
     * @return      True is the dot effect is still applied after removal from dotList through indefinite means.
     */
    public boolean removeInputDot(Dot dot) {
        if (dot.getIsIndefinite()) {
            // key may not be in dotList if isInfinite
            return (isStillDotEffect(dot));
        }
        dotList.remove(dot);
        // check if there is an item applying dot to character
        return isStillDotEffect(dot);
    }

    /**
     * Adds a new Dot to the dotList if not already existing.
     * If it already exists in the list, then reset the time for the dot.
     * If isInfinite is true, then only make isDot true, not adding to the dotList.
     * @param dot   The dot to add the dotList
     * @return      True if character is now affected by the effect after the dot is added.
     */
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
        if (!dot.getIsIndefinite()) {
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

    /**
     * Swaps the boolean on isDot field applied to character.
     * @param dotType   The dot type to flip.
     */
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

    /**
     * Applies the effect of the Dot and decrements the remaining duration of the dot. If the duration == 0, then
     * remove from the dotList.
     */
    public void applyDots() {
        int i = 0;
        // remove any dot if duration == 0
        while (i < dotList.size()) {
            Dot dot = dotList.get(i);
            findDotToApply(dot.getType());
            dot.decrementDuration();
            if (dot.getDuration() == 0) {
                dotList.remove(i);
            } else {
                i++;
            }
        }
    }

    /**
     * Helper for applyDots. Find the proper method to call corresponding the the correct dot being applied.
     * @param dot   The dot to apply to character (damage)
     */
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
        increaseHealth(Dot.HEAL_DOT_AMOUNT);
    }
    public void applyManaDot() {
        changeMana(Dot.MANA_DOT_AMOUNT);
    }



    // GETTERS AND SETTERS *****

    public int getID() {
        return ID;
    }

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
        if (health == 0) isAlive = false;
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

    public int getDrawableResID() {
        return drawableResID;
    }
    public int getDrawableDeadResID() {
        return drawableDeadResID;
    }
    public int getDrawableIconResID() {
        return drawableIconResID;
    }
    public int getDrawableIconDeadResID() {
        return drawableIconDeadResID;
    }

    public ObservableArrayList<Dot> getDotList() {
        return dotList;
    }
    public ObservableArrayList<SpecialEffect> getSpecialList() {
        return specialList;
    }
    public List<TempBar> getTempHealthList() {
        return tempHealthList;
    }
    public List<TempBar> getTempManaList() {
        return tempManaList;
    }
    public ObservableArrayList<TempStat> getStatIncreaseList() {
        return statIncreaseList;
    }
    public ObservableArrayList<TempStat> getStatDecreaseList() {
        return statDecreaseList;
    }

    public int getLevel() {
        return level;
    }
    public void setLevel(int level) {
        this.level = level;
    }

    public boolean getIsCharacter() {
        return isCharacter;
    }
}
