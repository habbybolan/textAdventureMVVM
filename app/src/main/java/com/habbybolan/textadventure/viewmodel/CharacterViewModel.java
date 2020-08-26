package com.habbybolan.textadventure.viewmodel;

import android.content.Context;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.habbybolan.textadventure.BR;
import com.habbybolan.textadventure.model.characterentity.Character;
import com.habbybolan.textadventure.model.characterentity.Damage;
import com.habbybolan.textadventure.model.inventory.Ability;
import com.habbybolan.textadventure.repository.SaveDataLocally;
import com.habbybolan.textadventure.repository.database.DatabaseAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
holds current character data used to tie the data to necessary views
 */
public class CharacterViewModel extends BaseObservable {

    private Character character;
    private Damage damage;

    // retrieve a character from memory
    // todo: this dependency may be confusing? - inject dependency from somewhere else preferably
    public CharacterViewModel(Context context) {
        SaveDataLocally save = new SaveDataLocally(context);
        String characterData = save.readCharacterData(context);
        DatabaseAdapter mDbHelper = new DatabaseAdapter(context);
        character = new Character(characterData, mDbHelper, context);
        damage = new Damage();

        // todo: test
        character.addInputDotMap("1", 10);
        character.addInputSpecialMap("1", 10);
        character.addInputDotMap("2", 5);
        character.addInputSpecialMap("2", 5);
    }

    // Observable for adding and deleting abilities from the RecyclerView in CharacterFragment
    private ObservableField<Ability> abilityObserverAdd = new ObservableField<>();
    @Bindable
    public ObservableField<Ability> getAbilityObserverAdd() {
        return abilityObserverAdd;
    }
    private ObservableField<Ability> abilityObserverRemove = new ObservableField<>();
    @Bindable
    public ObservableField<Ability> getAbilityObserverRemove() {
        return abilityObserverRemove;
    }
    public void removeAbility(Ability ability) {
        character.removeAbility(ability);
        abilityObserverRemove.set(ability);
    }
    public void addAbility(Ability ability) {
        character.addAbility(ability);
        abilityObserverAdd.set(ability);
    }


    public ArrayList<String> mapIntoList(Map<String, Integer> map) {
        ArrayList<String> list = new ArrayList<>();
        for (Map.Entry<String, Integer> pair : map.entrySet()) {
            list.add(pair.getKey());
        }
        return list;
    }

    // direct damage applied to player character
    public void damageCharacter(int damageAmount) {
        damage.damageTarget(character, damageAmount);
        notifyPropertyChanged(BR.health);
    }

    // ** Dot **
    // dot applied to player character
    public void addDot(String dot, boolean isInfinite) {
        damage.addNewDot(dot, isInfinite, character);
        notifyPropertyChanged(BR.dotMap);
        dotObserverAdd.set(dot);
    }
    // dot removed from player character
    public void removeDot(String dot) {
        dotObserverRemove.set(dot);
    }
    // apply the effects of any dots and decrement their duration
        // removes any dots with duration = 0
    public void applyDots() {
        ArrayList<String> isRemoved = damage.applyDots(character);
        for (String dotRemoved : isRemoved) {
            dotObserverRemove.set(dotRemoved);
        }
    }

    // ** special **
    // special applied to player character
    public void addSpecial(String special, int duration) {
        damage.addNewSpecial(special, duration, character);
        notifyPropertyChanged(BR.specialMap);
        specialObserverAdd.set(special);
    }
    // special removed from player character
    public void removeSpecial(String special) {
        specialObserverRemove.set(special);
    }

    // heal player character
    public void addHealth(int heal) {
        setHealth(character.getHealth() + heal);
    }
    // mana regen player character
    public void changeMana(int mana) {
        setMana(character.getMana() + mana);
    }


    // *** character arrays ***

        // DOT EFFECTS
    private ObservableField<String> dotObserverAdd = new ObservableField<>();
    public ObservableField<String> getDotObserverAdd() {
        return dotObserverAdd;
    }
    private ObservableField<String> dotObserverRemove = new ObservableField<>();
    public ObservableField<String> getDotObserverRemove() {
        return dotObserverRemove;
    }
    @Bindable
    public Map<String, Integer> getDotMap() {
        return character.getDotMap();
    }
    public void removeInputDotMap(String key) {
        // observed by XML
        character.removeInputDotMap(key);
        notifyPropertyChanged(BR.dotMap);
        // observed by CharacterViewModel
        dotObserverRemove.set(key);
    }
    public void addInputDotMap(String key, int duration) {
        // observed by XML
        character.addInputDotMap(key, duration);
        notifyPropertyChanged(BR.dotMap);
        // observed by CharacterViewModel
        dotObserverAdd.set(key);
    }

        // SPECIAL EFFECTS
    private ObservableField<String> specialObserverAdd = new ObservableField<>();
    @Bindable
    public ObservableField<String> getSpecialObserverAdd() {
        return specialObserverAdd;
    }
    private ObservableField<String> specialObserverRemove = new ObservableField<>();
    @Bindable
    public ObservableField<String> getSpecialObserverRemove() {
        return specialObserverRemove;
    }
    @Bindable
    public Map<String, Integer> getSpecialMap() {
        return character.getSpecialMap();
    }
    public void removeInputSpecialMap(String key) {
        // observed by XML
        character.removeInputSpecialMap(key);
        notifyPropertyChanged(BR.specialMap);
        // observed by CharacterViewModel
        specialObserverRemove.set(key);
        notifyPropertyChanged(BR.specialObserverRemove);
    }
    public void addInputSpecialMap(String key, int duration) {
        // observed by XML
        character.addInputSpecialMap(key, duration);
        notifyPropertyChanged(BR.specialMap);
        // observed by CharacterViewModel
        specialObserverAdd.set(key);
        notifyPropertyChanged(BR.specialObserverAdd);
    }

    @Bindable
    public List<ArrayList<Object>> getStatIncreaseList() {
        return character.getStatIncreaseList();
    }
    public void removeZeroStatIncreaseList() {
        character.removeZeroStatIncreaseList();
        notifyPropertyChanged(BR.statIncreaseList);
    }
    public void addStatIncreaseList(ArrayList<Object> statIncrease) {
        character.addStatIncreaseList(statIncrease);
        notifyPropertyChanged(BR.statIncreaseList);
    }

    @Bindable
    public List<ArrayList<Object>> getStatDecreaseList() {
        return character.getStatDecreaseList();
    }
    public void removeZeroStatDecreaseList() {
        character.removeZeroStatDecreaseList();
        notifyPropertyChanged(BR.statDecreaseList);
    }
    public void addStatDecreaseList(ArrayList<Object> statDecrease) {
        character.addStatDecreaseList(statDecrease);
        notifyPropertyChanged(BR.statDecreaseList);
    }

    @Bindable
    public List<ArrayList<Integer>> getTempHealthList() {
        return character.getTempHealthList();
    }
    public void removeZeroTempHealthList() {
        character.removeZeroTempHealthList();
        notifyPropertyChanged(BR.tempHealthList);
    }
    public void addTempHealthList(ArrayList<Integer> tempExtraHealth) {
        character.addTempHealthList(tempExtraHealth);
        notifyPropertyChanged(BR.tempHealthList);
    }
    @Bindable
    public int getTempExtraHealth() {
        return character.getTempExtraHealth();
    }
    public void setTempExtraHealth(int tempExtraHealth) {
        character.setTempExtraHealth(tempExtraHealth);
        notifyPropertyChanged(BR.tempExtraHealth);
    }

    @Bindable
    public List<ArrayList<Integer>> getTempManaList() {
        return character.getTempManaList();
    }
    public void removeZeroTempManaList() {
        character.removeZeroTempManaList();
        notifyPropertyChanged(BR.tempManaList);
    }
    public void addTempManaList(ArrayList<Integer> tempExtraMana) {
        character.addTempManaList(tempExtraMana);
        notifyPropertyChanged(BR.tempManaList);
    }
    @Bindable
    public int getTempExtraMana() {
        return character.getTempExtraMana();
    }
    public void setTempExtraMana(int tempExtraMana) {
        character.setTempExtraMana(tempExtraMana);
        notifyPropertyChanged(BR.tempExtraMana);
    }

    // *** character stat ***

        // bars
    @Bindable
    public String getHealth() {
        return String.valueOf(character.getHealth());
    }
    public void setHealth(int health) {
        character.setHealth(health);
        notifyPropertyChanged(BR.health);
    }
    @Bindable
    public String getMana() {
        return String.valueOf(character.getMana());
    }
    public void setMana(int mana) {
        character.setMana(mana);
        notifyPropertyChanged(BR.mana);
    }
        // misc
    @Bindable
    public String getGold() {
        return String.valueOf(character.getGold());
    }
    public void setGold(int gold) {
        character.setGold(gold);
        notifyPropertyChanged(BR.gold);
    }
        // DOTS
    @Bindable
    public boolean getIsFire() {
        return character.getIsFire();
    }
    public void setIsFire(boolean isFire) {
        character.setIsFire(isFire);
        notifyPropertyChanged(BR.isFire);
    }
    @Bindable
    public boolean getIsBleed() {
        return character.getIsBleed();
    }
    public void setIsBleed(boolean isBleed) {
        character.setIsBleed(isBleed);
        notifyPropertyChanged(BR.isBleed);
    }
    @Bindable
    public boolean getIsPoison() {
        return character.getIsPoison();
    }
    public void setIsPoison(boolean isPoison) {
        character.setIsPoison(isPoison);
        notifyPropertyChanged(BR.isPoison);
    }
    @Bindable
    public boolean getIsFrostBurn() {
        return character.getIsFrostBurn();
    }
    public void setIsFrostBurn(boolean isFrostBurn) {
        character.setIsFrostBurn(isFrostBurn);
        notifyPropertyChanged(BR.isFrostBurn);
    }
        // specials
    @Bindable
    public boolean getIsStun() {
        return character.getIsStun();
    }
    public void setIsStun(boolean isStun) {
        character.setIsStun(isStun);
        notifyPropertyChanged(BR.isStun);
    }
    @Bindable
    public boolean getIsConfuse() {
        return character.getIsConfuse();
    }
    public void setIsConfuse(boolean isConfuse) {
        character.setIsConfuse(isConfuse);
        notifyPropertyChanged(BR.isConfuse);
    }
    @Bindable
    public boolean getIsInvincible() {
        return character.getIsInvincible();
    }
    public void setIsInvincible(boolean isInvincible) {
        character.setIsInvincible(isInvincible);
        notifyPropertyChanged(BR.isInvincible);
    }
    @Bindable
    public boolean getIsInvisible() {
        return character.getIsInvisible();
    }
    public void setIsInvisible(boolean isInvisible) {
        character.setIsInvisible(isInvisible);
        notifyPropertyChanged(BR.isInvisible);
    }
    @Bindable
    public boolean getIsSilence() {
        return character.getIsSilence();
    }
    public void setIsSilence(boolean isSilence) {
        character.setIsSilence(isSilence);
        notifyPropertyChanged(BR.isSilence);
    }
        // STATS
    @Bindable
    public int getStrIncrease() {
        return character.getStrIncrease();
    }
    public void setStrIncrease(int strIncrease) {
        character.setStrIncrease(strIncrease);
        notifyPropertyChanged(BR.strIncrease);
    }
    @Bindable
    public int getIntIncrease() {
        return character.getIntIncrease();
    }
    public void setIntIncrease(int intIncrease) {
        character.setIntIncrease(intIncrease);
        notifyPropertyChanged(BR.intIncrease);
    }
    @Bindable
    public int getConIncrease() {
        return character.getConIncrease();
    }
    public void setConIncrease(int conIncrease) {
        character.setConIncrease(conIncrease);
        notifyPropertyChanged(BR.conIncrease);
    }
    @Bindable
    public int getSpdIncrease() {
        return character.getSpdIncrease();
    }
    public void setSpdIncrease(int spdIncrease) {
        character.setSpdIncrease(spdIncrease);
        notifyPropertyChanged(BR.spdIncrease);
    }
    @Bindable
    public int getEvasionIncrease() {
        return character.getEvasionIncrease();
    }
    public void setEvasionIncrease(int evasionIncrease) {
        character.setEvasionIncrease(evasionIncrease);
        notifyPropertyChanged(BR.evasionIncrease);
    }
    @Bindable
    public int getBlockIncrease() {
        return character.getBlockIncrease();
    }
    public void setBlockIncrease(int blockIncrease) {
        character.setBlockIncrease(blockIncrease);
        notifyPropertyChanged(BR.blockIncrease);
    }

    @Bindable
    public int getStrDecrease() {
        return character.getStrDecrease();
    }
    public void setStrDecrease(int strDecrease) {
        character.setStrDecrease(strDecrease);
        notifyPropertyChanged(BR.strDecrease);
    }
    @Bindable
    public int getIntDecrease() {
        return character.getIntDecrease();
    }
    public void setIntDecrease(int intDecrease) {
        character.setIntDecrease(intDecrease);
        notifyPropertyChanged(BR.intDecrease);
    }
    @Bindable
    public int getConDecrease() {
        return character.getConDecrease();
    }
    public void setConDecrease(int conDecrease) {
        character.setConDecrease(conDecrease);
        notifyPropertyChanged(BR.conDecrease);
    }
    @Bindable
    public int getSpdDecrease() {
        return character.getSpdDecrease();
    }
    public void setSpdDecrease(int spdDecrease) {
        character.setSpdDecrease(spdDecrease);
        notifyPropertyChanged(BR.spdDecrease);
    }
    @Bindable
    public int getEvasionDecrease() {
        return character.getEvasionDecrease();
    }
    public void setEvasionDecrease(int evasionDecrease) {
        character.setEvasionDecrease(evasionDecrease);
        notifyPropertyChanged(BR.evasionDecrease);
    }
    @Bindable
    public int getBlockDecrease() {
        return character.getBlockDecrease();
    }
    public void setBlockDecrease(int blockDecrease) {
        character.setBlockDecrease(blockDecrease);
        notifyPropertyChanged(BR.blockDecrease);
    }

    @Bindable
    public int getStrBase() {
        return character.getStrBase();
    }
    public void setStrBase(int strBase) {
        character.setStrBase(strBase);
        notifyPropertyChanged(BR.strBase);
    }
    @Bindable
    public int getIntBase() {
        return character.getIntBase();
    }
    public void setIntBase(int intBase) {
        character.setIntBase(intBase);
        notifyPropertyChanged(BR.intBase);
    }
    @Bindable
    public int getConBase() {
        return character.getConBase();
    }
    public void setConBase(int conBase) {
        character.setConBase(conBase);
        notifyPropertyChanged(BR.conBase);
    }
    @Bindable
    public int getSpdBase() {
        return character.getSpdBase();
    }
    public void setSpdBase(int spdBase) {
        character.setSpdBase(spdBase);
        notifyPropertyChanged(BR.spdBase);
    }
    @Bindable
    public int getEvasionBase() {
        return character.getEvasionBase();
    }
    public void setEvasionBase(int evasionBase) {
        character.setEvasionBase(evasionBase);
        notifyPropertyChanged(BR.evasionBase);
    }
    @Bindable
    public int getBlockBase() {
        return character.getBlockBase();
    }
    public void setBlockBase(int blockBase) {
        character.setBlockBase(blockBase);
        notifyPropertyChanged(BR.blockBase);
    }

    @Bindable
    public int getStrength() {
        return character.getStrength();
    }
    public void setStrength(int strength) {
        character.setStrength(strength);
        notifyPropertyChanged(BR.strength);
    }
    @Bindable
    public int getIntelligence() {
        return character.getIntelligence();
    }
    public void setIntelligence(int intelligence) {
        character.setIntelligence(intelligence);
        notifyPropertyChanged(BR.intelligence);
    }
    @Bindable
    public int getConstitution() {
        return character.getConstitution();
    }
    public void setConstitution(int constitution) {
        character.setConstitution(constitution);
        notifyPropertyChanged(BR.constitution);
    }
    @Bindable
    public int getSpeed() {
        return character.getSpeed();
    }
    public void setSpeed(int speed) {
        character.setSpeed(speed);
        notifyPropertyChanged(BR.speed);
    }
    @Bindable
    public int getEvasion() {
        return character.getEvasion();
    }
    public void setEvasion(int evasion) {
        character.setEvasion(evasion);
        notifyPropertyChanged(BR.evasion);
    }
    @Bindable
    public int getBlock() {
        return character.getBlock();
    }
    public void setBlock(int block) {
        character.setBlock(block);
        notifyPropertyChanged(BR.block);
    }

    public Character getCharacter() {
        return character;
    }
}
