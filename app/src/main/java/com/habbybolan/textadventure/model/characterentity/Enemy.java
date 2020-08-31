package com.habbybolan.textadventure.model.characterentity;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.model.inventory.Ability;
import com.habbybolan.textadventure.model.inventory.weapon.Weapon;
import com.habbybolan.textadventure.repository.database.DatabaseAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class Enemy extends CharacterEntity {

    public static final int MAX_ABILITIES = 2;
    public static final int MAX_TIER = 3;

    final static int evasionMultiplier = 1;
    final static int blockMultiplier = 1;

    // Character info
    private int strength;
    private int intelligence;
    private int constitution;
    private int speed;
    private int evasion;
    private int block;
    private int level;
    private int health;
    private int maxHealth;
    private int mana;
    private int maxMana;
    private Weapon weapon;
    // temp extra health
    private int tempExtraHealth = 0;
    private int tempExtraMana = 0;

    private boolean isAlive;


    private ArrayList<Ability> abilities = new ArrayList<>();
    private String[] arrayOfAbilities = new String[MAX_ABILITIES];

    private String enemyString;
    private String type;

    private int numStatPoints;
    // percentage of stat points for enemy compared to the main character
    final private double percentOfStats = 0.75;

    // DOTS
    private boolean isFire = false;
    private boolean isBleed = false;
    private boolean isPoison = false;
    private boolean isFrostBurn = false;

    // specials
    private boolean isStun = false;
    private boolean isConfuse = false;
    private boolean isInvincible = false;
    private boolean isSilence = false;
    private boolean isInvisible = false;
    private boolean isHealDot = false;
    private boolean isManaDot = false;

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
    private int aliveVector;
    private int deadVector;


    public Enemy(int numStatPoints, Context context, DatabaseAdapter mDbHelper, String enemyString, String type) throws ExecutionException, InterruptedException {
        this.numStatPoints = (int) Math.rint(numStatPoints * percentOfStats);
        this.enemyString = enemyString;
        this.type = type;

        // choose 2 random stats for the enemy
        Random rand = new Random();
        // str = 0, int = 1, con = 2, spd = 3
        int firstStat = rand.nextInt(3);
        // offset of first stat to get second stat
        int secondStat = rand.nextInt(2) + 1;
        // randomize the amount to increase the first stat by
        int amountToIncreaseFirstStat = rand.nextInt(this.numStatPoints) + 1;
        // set the amount for the 2 variables
        selectStats(firstStat, secondStat, amountToIncreaseFirstStat);

        // set enemy health
        // todo: how to decide health amount
        setHealth(20);
        maxHealth = 20;

        // choose a random weapon
        // todo: how to decide tier?
        int weaponID = findRandWeaponWithTier("1", context);
        setWeapon(new Weapon(weaponID, mDbHelper));

        // choose MAX_ABILITIES random abilities
        abilities = mDbHelper.getRandomAbilities(MAX_ABILITIES);
        /*for (int i = 0; i < MAX_ABILITIES; i++) {
            int abilityColID = cursor.getColumnIndex("ability_id");
            abilities.add(new Ability(cursor.getInt(abilityColID), context));
            cursor.moveToNext();
        }*/
        mDbHelper.close();


        bitmapAlive = BitmapFactory.decodeResource(context.getResources(), R.drawable.skeleton);
        bitmapDead = BitmapFactory.decodeResource(context.getResources(), R.drawable.skeleton_dead);

    }

    // find a random weapon of a specific tier and return it
    static public int findRandWeaponWithTier(String tier, Context context) throws ExecutionException, InterruptedException {
        DatabaseAdapter mDbHelper = new DatabaseAdapter(context);
        mDbHelper.createDatabase();
        mDbHelper.open();

        Cursor cursor = mDbHelper.getRandomWeaponOfTier(Integer.parseInt(tier));
        int weaponColID = cursor.getColumnIndex("weapon_id");
        return cursor.getInt(weaponColID);
    }


    // helper for constructor to set up enemy stats
    private void selectStats(int firstStat, int secondStat, int amountToIncreaseFirstStat) {
        switch (firstStat) {
            case 0:
                setStrength(amountToIncreaseFirstStat);
                setStrBase(amountToIncreaseFirstStat);
                switch (secondStat) {
                    case 1:
                        setIntelligence(this.numStatPoints - amountToIncreaseFirstStat);
                        setIntBase(this.numStatPoints - amountToIncreaseFirstStat);
                        break;
                    case 2:
                        setConstitution(this.numStatPoints - amountToIncreaseFirstStat);
                        setConBase(this.numStatPoints - amountToIncreaseFirstStat);
                        break;
                    case 3:
                        setSpeed(this.numStatPoints - amountToIncreaseFirstStat);
                        setSpdBase(this.numStatPoints - amountToIncreaseFirstStat);
                        break;
                    default:
                        break;
                }
                break;
            case 1:
                setIntelligence(amountToIncreaseFirstStat);
                setIntBase(amountToIncreaseFirstStat);
                switch (secondStat) {
                    case 1:
                        setConstitution(this.numStatPoints - amountToIncreaseFirstStat);
                        setConBase(this.numStatPoints - amountToIncreaseFirstStat);
                        break;
                    case 2:
                        setSpeed(this.numStatPoints - amountToIncreaseFirstStat);
                        setSpdBase(this.numStatPoints - amountToIncreaseFirstStat);
                        break;
                    case 3:
                        setStrength(this.numStatPoints - amountToIncreaseFirstStat);
                        setStrBase(this.numStatPoints - amountToIncreaseFirstStat);
                        break;
                    default:
                        break;
                }
                break;
            case 2:
                setConstitution(amountToIncreaseFirstStat);
                setConBase(amountToIncreaseFirstStat);
                switch (secondStat) {
                    case 1:
                        setSpeed(this.numStatPoints - amountToIncreaseFirstStat);
                        setSpdBase(this.numStatPoints - amountToIncreaseFirstStat);
                        break;
                    case 2:
                        setStrength(this.numStatPoints - amountToIncreaseFirstStat);
                        setStrBase(this.numStatPoints - amountToIncreaseFirstStat);
                        break;
                    case 3:
                        setIntelligence(this.numStatPoints - amountToIncreaseFirstStat);
                        setIntBase(this.numStatPoints - amountToIncreaseFirstStat);
                        break;
                    default:
                        break;
                }
                break;
            case 3:
                setSpeed(amountToIncreaseFirstStat);
                setSpdBase(amountToIncreaseFirstStat);
                switch (secondStat) {
                    case 1:
                        setStrength(this.numStatPoints - amountToIncreaseFirstStat);
                        setStrBase(this.numStatPoints - amountToIncreaseFirstStat);
                        break;
                    case 2:
                        setIntelligence(this.numStatPoints - amountToIncreaseFirstStat);
                        setIntBase(this.numStatPoints - amountToIncreaseFirstStat);
                        break;
                    case 3:
                        setConstitution(this.numStatPoints - amountToIncreaseFirstStat);
                        setConBase(this.numStatPoints - amountToIncreaseFirstStat);
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }


    // ***ABILITIES**

    // decrement the cooldown on all abilities w/ cooldown >0
    public void decrCooldowns() {
        // check the ability in special attack if it exists
        if (weapon.getSpecialAttack().getAbility() != null && weapon.getSpecialAttack().getAbility().getCooldownLeft() > 0) {
            weapon.getSpecialAttack().getAbility().setCooldownCurr(weapon.getSpecialAttack().getAbility().getCooldownLeft()-1);
        }
        for (int i = 0; i < MAX_ABILITIES; i++) {
            if (abilities.get(i).getCooldownLeft() > 0) {
                abilities.get(i).setCooldownCurr(abilities.get(i).getCooldownLeft()-1);
            }
        }
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




    // setters
    public void setHealth(int health) {
        this.health = health;
    }
    public void setMana(int mana) {
        this.mana = mana;
    }
    // temp extra health
    public void setTempExtraHealth(int tempExtraHealth) {
        this.tempExtraHealth = tempExtraHealth;
    }

    @Override
    public int getTempExtraMana() {
        return tempExtraMana;
    }

    @Override
    public void setTempExtraMana(int tempExtraMana) {
        this.tempExtraMana = tempExtraMana;
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
    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }
    public void setAbilities(ArrayList<Ability> abilities) {
        this.abilities = abilities;
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


    public void setIsAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }







    // getters
    @Override
    public int getStrength() {
        return strength;
    }
    @Override
    public int getIntelligence() {
        return intelligence;
    }
    @Override
    public int getConstitution() {
        return constitution;
    }
    @Override
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
    public int getLevel() {
        return level;
    }
    @Override
    public int getHealth() {
        return health;
    }
    @Override
    public int getMaxHealth() {
        return maxHealth;
    }
    @Override
    public int getMana() {
        return mana;
    }
    @Override
    public int getMaxMana() {
        return maxMana;
    }
    @Override
    // extra health
    public int getTempExtraHealth() {
        return tempExtraHealth;
    }
    public ArrayList<Ability> getAbilities() {
        return abilities;
    }
    public String getEnemyString() {
        return enemyString;
    }
    public String getType() {
        return type;
    }
    public boolean isCharacter() {
        return false;
    }
    public Weapon getWeapon() {
        return weapon;
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
    @Override
    public boolean getIsHealDot() {
        return isHealDot;
    }
    @Override
    public boolean getIsManaDot() {
        return isManaDot;
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

    public List<ArrayList<Integer>> getTempHealthList() {
        return tempHealthList;
    }
    public List<ArrayList<Integer>> getTempManaList() {
        return tempManaList;
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
}
