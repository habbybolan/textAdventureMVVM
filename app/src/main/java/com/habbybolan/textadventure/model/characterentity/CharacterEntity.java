package com.habbybolan.textadventure.model.characterentity;

import android.graphics.Bitmap;

import com.habbybolan.textadventure.model.inventory.Ability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface CharacterEntity {

    final static int evasionMultiplier = 1;
    final static int blockMultiplier = 1;

    // setter methods
    void setHealth(int health);
    void setMana(int mana);

    // decrement all ability cooldowns
    public void decrCooldowns();


    public int getTempExtraHealth();
    public void setTempExtraHealth(int tempExtraHealth);
    public int getTempExtraMana();
    public void setTempExtraMana(int tempExtraMana);
    // specials
    void setIsStun(boolean isStun);
    void setIsConfuse(boolean isConfuse);
    void setIsInvincible(boolean isInvincible);
    void setIsSilence(boolean isInvincible);
    void setIsInvisible(boolean isInvisible);

    boolean getIsStun();
    boolean getIsConfuse();
    boolean getIsInvincible();
    boolean getIsSilence();
    boolean getIsInvisible();

    // ArrayLists

    // key holds the effect name, value holds the duration left on the effect
    // keep track of DOT duration left <name, duration> sorted by duration
    Map<String, Integer> dotMap = new HashMap<>();
    void removeInputDotMap(String key);
    void addInputDotMap(String key, int duration);

    // keep track of special duration left <name, duration> sorted by duration
    Map<String, Integer> specialMap = new HashMap<>();
    void removeInputSpecialMap(String key);
    void addInputSpecialMap(String key, int duration);

    // keep track of stat increases - list<ArrayList<stat, duration, amount>> sorted by duration
    List<ArrayList<Object>> statIncreaseList = new ArrayList<>();
    void removeZeroStatIncreaseList();
    void addStatIncreaseList(ArrayList<Object> statIncrease);

    // keep track of stat decreases - list<ArrayList<stat, duration, amount>> sorted by duration
    List<ArrayList<Object>> statDecreaseList = new ArrayList<>();
    void removeZeroStatDecreaseList();
    void addStatDecreaseList(ArrayList<Object> statDecrease);

    // keep track of temp health increase - list<ArrayList<duration, amount>> sorted by duration
    List<ArrayList<Integer>> tempHealthList = new ArrayList<>();
    void removeZeroTempHealthList();
    void addTempHealthList(ArrayList<Integer> tempExtraHealth);

    // keep track of temp mana increase - list<ArrayList<duration, amount>> sorted by duration
    List<ArrayList<Integer>> tempManaList = new ArrayList<>();
    void removeZeroTempManaList();
    void addTempManaList(ArrayList<Integer> tempExtraHealth);


    // holds all the buff and debuff abilities applied to this enemy
    ArrayList<Ability> appliedAbilities = new ArrayList<>();


    // STATS
    int getStrIncrease();
    int getIntIncrease();
    int getConIncrease();
    int getSpdIncrease();
    int getEvasionIncrease();
    int getBlockIncrease();
    void setStrIncrease(int strength);
    void setIntIncrease(int intelligence);
    void setConIncrease(int constitution);
    void setSpdIncrease(int speed);
    void setEvasionIncrease(int evasionIncrease);
    void setBlockIncrease(int blockIncrease);

    int getStrDecrease();
    int getIntDecrease();
    int getConDecrease();
    int getSpdDecrease();
    int getEvasionDecrease();
    int getBlockDecrease();
    void setStrDecrease(int strength);
    void setIntDecrease(int intelligence);
    void setConDecrease(int constitution);
    void setSpdDecrease(int speed);
    void setEvasionDecrease(int evasionDecrease);
    void setBlockDecrease(int blockDecrease);

    int getStrBase();
    int getIntBase();
    int getConBase();
    int getSpdBase();
    int getEvasionBase();
    int getBlockBase();

    void setStrBase(int strength);
    void setIntBase(int intelligence);
    void setConBase(int constitution);
    void setSpdBase(int speed);
    void setEvasionBase(int evasionBase);
    void setBlockBase(int blockBase);

    boolean getIsFire();
    boolean getIsBleed();
    boolean getIsPoison();
    boolean getIsFrostBurn();
    void setIsFire(boolean isFire);
    void setIsBleed(boolean isBleed);
    void setIsPoison(boolean isPoison);
    void setIsFrostBurn(boolean isFrostBurn);

   // public void setIsAlive();

    // getter methods
    int getStrength();
    int getIntelligence();
    int getConstitution();
    int getSpeed();
    int getEvasion();
    int getBlock();

    void setStrength(int strength);
    void setIntelligence(int intelligence);
    void setConstitution(int constitution);
    void setSpeed(int speed);
    void setEvasion(int evasion);
    void setBlock(int block);


    int getLevel();
    int getHealth();
    int getMaxHealth();
    int getMana();
    int getMaxMana();
    ArrayList<Ability> getAbilities();

    Bitmap getAliveVector();
    Bitmap getDeadVector();

    boolean getIsAlive();

}
