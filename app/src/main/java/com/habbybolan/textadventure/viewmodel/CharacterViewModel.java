package com.habbybolan.textadventure.viewmodel;

import android.content.Context;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.habbybolan.textadventure.BR;
import com.habbybolan.textadventure.model.characterentity.Character;
import com.habbybolan.textadventure.model.characterentity.CharacterEntity;
import com.habbybolan.textadventure.model.inventory.Ability;
import com.habbybolan.textadventure.model.inventory.Item;
import com.habbybolan.textadventure.model.inventory.weapon.Weapon;
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

    // retrieve a character from memory
    // todo: this dependency may be confusing? - inject dependency from somewhere else preferably
    public CharacterViewModel(Context context) {
        SaveDataLocally save = new SaveDataLocally(context);
        String characterData = save.readCharacterData(context);
        DatabaseAdapter mDbHelper = new DatabaseAdapter(context);
        character = new Character(characterData, mDbHelper, context);
    }

    // ** Inventory **

        // Abilities
    // Observable for adding and deleting abilities and updating RecyclerView in CharacterFragment
    private ObservableField<Integer> abilityObserverAdd = new ObservableField<>();
    @Bindable
    public ObservableField<Integer> getAbilityObserverAdd() {
        return abilityObserverAdd;
    }
    private ObservableField<Integer> abilityObserverRemove = new ObservableField<>();
    @Bindable
    public ObservableField<Integer> getAbilityObserverRemove() {
        return abilityObserverRemove;
    }
    public void removeAbility(Ability ability) {
        int index = character.removeAbility(ability);
        if (index >= 0) abilityObserverRemove.set(index);
    }
    public void addAbility(Ability ability) {
        int index = character.addAbility(ability);
        abilityObserverAdd.set(index);
    }
    public void removeAbilityAtIndex(int index) {
        character.removeAbilityAtIndex(index);
        abilityObserverRemove.set(index);
    }
    public ArrayList<Ability> getAbilities() {
        return character.getAbilities();
    }

        // Weapons
    // Observable for adding and deleting weapons and updating RecyclerView in CharacterFragment
    private ObservableField<Integer> weaponObserverAdd = new ObservableField<>();
    @Bindable
    public ObservableField<Integer> getWeaponObserverAdd() {
        return weaponObserverAdd;
    }
    private ObservableField<Integer> weaponObserverRemove = new ObservableField<>();
    @Bindable
    public ObservableField<Integer> getWeaponObserverRemove() {
        return weaponObserverRemove;
    }
    public ArrayList<Weapon> getWeapons() {
        return character.getWeapons();
    }
    public void removeWeapon(Weapon weapon) {
        int index = character.removeWeapon(weapon);
        if (index >= 0) weaponObserverRemove.set(index);
    }
    public void addWeapon(Weapon weapon) {
        int index = character.addWeapon(weapon);
        weaponObserverAdd.set(index);
    }
    public void removeWeaponAtIndex(int index) {
        character.removeWeaponAtIndex(index);
        weaponObserverRemove.set(index);
    }

        // Items
    // Observable for adding and deleting items and updating RecyclerView in CharacterFragment
    private ObservableField<Integer> itemObserverAdd = new ObservableField<>();
    @Bindable
    public ObservableField<Integer> getItemObserverAdd() {
        return itemObserverAdd;
    }
    private ObservableField<Integer> itemObserverRemove = new ObservableField<>();
    @Bindable
    public ObservableField<Integer> getItemObserverRemove() {
        return itemObserverRemove;
    }
    public ArrayList<Item> getItems() {
        return character.getItems();
    }
    public void removeItem(Item item) {
        int index = character.removeItem(item);
        removeItemAtIndex(index);
    }
    public void removeItemAtIndex(int index) {
        Item item = character.removeItemAtIndex(index);
        if (!item.getIsConsumable()) {
            // stat changes
            if (item.getStrChange() != 0) setStrength(character.getStrength() - item.getStrChange());
            if (item.getIntChange() != 0) setIntelligence(character.getIntelligence() - item.getIntChange());
            if (item.getConChange() != 0) setConstitution(character.getConstitution() - item.getConChange());
            if (item.getSpdChange() != 0) setSpeed(character.getSpeed() - item.getSpdChange());
            if (item.getEvasionChange() != 0) setEvasion(character.getEvasion() - item.getEvasionChange());
            if (item.getBlockChange() != 0) setBlock(character.getBlock() - item.getBlockChange());
            // bar changes
            if (item.getHealthChange() != 0) {
                setMaxHealth(character.getMaxHealth() - item.getHealthChange());
                setHealth(character.getHealthAfterItemRemoval(item.getHealthChange()));
            }
            if (item.getManaChange() != 0) {
                setMaxMana(character.getMaxMana() - item.getManaChange());
                setMana(character.getManaAfterItemRemoval(item.getManaChange()));
            }
            // special changes
            if (item.getIsConfuse()) if (!character.isStillSpecialEffect(CharacterEntity.CONFUSE)) setIsConfuse(false);
            if (item.getIsStun()) if (!character.isStillSpecialEffect(CharacterEntity.STUN)) setIsStun(false);
            if (item.getIsSilence()) if (!character.isStillSpecialEffect(CharacterEntity.SILENCE)) setIsSilence(false);
            if (item.getIsInvisible()) if (!character.isStillSpecialEffect(CharacterEntity.INVISIBILITY)) setIsInvisible(false);
            if (item.getIsInvincible()) if (!character.isStillSpecialEffect(CharacterEntity.INVINCIBILITY)) setIsInvincible(false);
            // DOT changes
            if (item.getIsFire()) if (!character.removeInputDotMap(CharacterEntity.FIRE, true)) dotObserverRemove.set(CharacterEntity.FIRE);
            if (item.getIsBleed()) if (!character.removeInputDotMap(CharacterEntity.BLEED, true)) dotObserverRemove.set(CharacterEntity.BLEED);
            if (item.getIsPoison()) if (!character.removeInputDotMap(CharacterEntity.POISON, true)) dotObserverRemove.set(CharacterEntity.POISON);
            if (item.getIsFrostBurn()) if (!character.removeInputDotMap(CharacterEntity.FROSTBURN, true)) dotObserverRemove.set(CharacterEntity.FROSTBURN);
            if (item.getIsHealthDot()) if (!character.removeInputDotMap(CharacterEntity.HEALTH_DOT, true)) dotObserverRemove.set(CharacterEntity.HEALTH_DOT);
            if (item.getIsManaDot()) if (!character.removeInputDotMap(CharacterEntity.MANA_DOT, true)) dotObserverRemove.set(CharacterEntity.MANA_DOT);
        }
        itemObserverRemove.set(index);
    }
    public void addItem(Item item) {
        int index = character.addItem(item);
        if (!item.getIsConsumable()) {
            // stat changes
            if (item.getStrChange() != 0) setStrength(character.getStrength() + item.getStrChange());
            if (item.getIntChange() != 0) setIntelligence(character.getIntelligence() + item.getIntChange());
            if (item.getConChange() != 0) setConstitution(character.getConstitution() + item.getConChange());
            if (item.getSpdChange() != 0) setSpeed(character.getSpeed() + item.getSpdChange());
            if (item.getEvasionChange() != 0) setEvasion(character.getEvasion() + item.getEvasionChange());
            if (item.getBlockChange() != 0) setBlock(character.getBlock() + item.getBlockChange());
            // bar changes
            if (item.getHealthChange() != 0) {
                setMaxHealth(character.getMaxHealth() + item.getHealthChange());
                setHealth(character.getHealth() + item.getHealthChange());
            }
            if (item.getManaChange() != 0) {
                setMaxMana(character.getMaxMana() + item.getManaChange());
                setMana(character.getMana() + item.getManaChange());
            }
            // special changes
            if (item.getIsConfuse()) if (character.addNewSpecial(CharacterEntity.CONFUSE, 0, true)) specialObserverAdd.set(CharacterEntity.CONFUSE);
            if (item.getIsStun()) if (character.addNewSpecial(CharacterEntity.STUN, 0, true)) specialObserverAdd.set(CharacterEntity.STUN);
            if (item.getIsSilence()) if (character.addNewSpecial(CharacterEntity.SILENCE, 0, true)) specialObserverAdd.set(CharacterEntity.SILENCE);
            if (item.getIsInvisible()) if (character.addNewSpecial(CharacterEntity.INVISIBILITY, 0, true)) specialObserverAdd.set(CharacterEntity.INVISIBILITY);
            if (item.getIsInvincible()) if (character.addNewSpecial(CharacterEntity.INVINCIBILITY, 0, true)) specialObserverAdd.set(CharacterEntity.INVINCIBILITY);
            // DOT changes
            if (item.getIsFire()) if (character.addNewDot(CharacterEntity.FIRE, true)) dotObserverAdd.set(CharacterEntity.FIRE);
            if (item.getIsBleed()) if (character.addNewDot(CharacterEntity.BLEED, true)) dotObserverAdd.set(CharacterEntity.BLEED);
            if (item.getIsPoison()) if (character.addNewDot(CharacterEntity.POISON, true)) dotObserverAdd.set(CharacterEntity.POISON);
            if (item.getIsFrostBurn()) if (character.addNewDot(CharacterEntity.FROSTBURN, true)) dotObserverAdd.set(CharacterEntity.FROSTBURN);
            if (item.getIsHealthDot()) if (character.addNewDot(CharacterEntity.HEALTH_DOT, true)) dotObserverAdd.set(CharacterEntity.HEALTH_DOT);
            if (item.getIsManaDot()) if (character.addNewDot(CharacterEntity.MANA_DOT, true)) dotObserverAdd.set(CharacterEntity.MANA_DOT);
        }
        itemObserverAdd.set(index);
    }
    // consume an item
    public void consumeItem(Item item) {
        if (!item.getIsConsumable()) throw new IllegalArgumentException();
        int index = character.removeItem(item);
        // stat change
        if (item.getStrChange() > 0) {
            character.addNewStatIncrease(CharacterEntity.STR, item.getDuration(), item.getStrChange());
            notifyPropertyChanged(BR.strIncrease);
            notifyPropertyChanged(BR.strength);
        }
        if (item.getStrChange() < 0) {
            character.addNewStatDecrease(CharacterEntity.STR, item.getDuration(), item.getStrChange());
            notifyPropertyChanged(BR.strDecrease);
            notifyPropertyChanged(BR.strength);
        }
        if (item.getIntChange() > 0) {
            character.addNewStatIncrease(CharacterEntity.INT, item.getDuration(), item.getIntChange());
            notifyPropertyChanged(BR.intIncrease);
            notifyPropertyChanged(BR.intelligence);
        }
        if (item.getIntChange() < 0) {
            character.addNewStatDecrease(CharacterEntity.INT, item.getDuration(), item.getIntChange());
            notifyPropertyChanged(BR.intDecrease);
            notifyPropertyChanged(BR.intelligence);
        }
        if (item.getConChange() > 0) {
            character.addNewStatIncrease(CharacterEntity.CON, item.getDuration(), item.getConChange());
            notifyPropertyChanged(BR.conIncrease);
            notifyPropertyChanged(BR.constitution);
        }
        if (item.getConChange() < 0) {
            character.addNewStatDecrease(CharacterEntity.CON, item.getDuration(), item.getConChange());
            notifyPropertyChanged(BR.conDecrease);
            notifyPropertyChanged(BR.constitution);
        }
        if (item.getSpdChange() > 0) {
            character.addNewStatIncrease(CharacterEntity.SPD, item.getDuration(), item.getSpdChange());
            notifyPropertyChanged(BR.spdIncrease);
            notifyPropertyChanged(BR.speed);
        }
        if (item.getSpdChange() < 0) {
            character.addNewStatDecrease(CharacterEntity.SPD, item.getDuration(), item.getSpdChange());
            notifyPropertyChanged(BR.spdDecrease);
            notifyPropertyChanged(BR.speed);
        }
        if (item.getBlockChange() > 0) {
            character.addNewStatIncrease(CharacterEntity.BLOCK, item.getDuration(), item.getBlockChange());
            notifyPropertyChanged(BR.blockIncrease);
            notifyPropertyChanged(BR.block);
        }
        if (item.getBlockChange() < 0) {
            character.addNewStatDecrease(CharacterEntity.BLOCK, item.getDuration(), item.getBlockChange());
            notifyPropertyChanged(BR.blockDecrease);
            notifyPropertyChanged(BR.block);
        }
        if (item.getEvasionChange() > 0) {
            character.addNewStatIncrease(CharacterEntity.EVASION, item.getDuration(), item.getEvasionChange());
            notifyPropertyChanged(BR.evasionIncrease);
            notifyPropertyChanged(BR.evasion);
        }
        if (item.getEvasionChange() < 0) {
            character.addNewStatDecrease(CharacterEntity.EVASION, item.getDuration(), item.getEvasionChange());
            notifyPropertyChanged(BR.evasionDecrease);
            notifyPropertyChanged(BR.evasion);
        }
        // health/mana
        if (item.getHealthChange() > 0) character.healTarget(item.getHealthChange());
        if (item.getHealthChange() < 0) character.damageTarget(item.getHealthChange());
        if (item.getManaChange() > 0) character.manaTarget(item.getManaChange());
        if (item.getManaChange() < 0) character.loseManaTarget(item.getManaChange());
        // specials
        if (item.getIsConfuse()) character.addNewSpecial(CharacterEntity.CONFUSE, item.getDuration(), false);
        if (item.getIsStun()) character.addNewSpecial(CharacterEntity.STUN, item.getDuration(), false);
        if (item.getIsSilence()) character.addNewSpecial(CharacterEntity.SILENCE, item.getDuration(), false);
        if (item.getIsInvisible()) character.addNewSpecial(CharacterEntity.INVISIBILITY, item.getDuration(), false);
        if (item.getIsInvincible()) character.addNewSpecial(CharacterEntity.INVINCIBILITY, item.getDuration(), false);
        // DOTS
        if (item.getIsFire()) character.addNewDot(CharacterEntity.FIRE, false);
        if (item.getIsBleed()) character.addNewDot(CharacterEntity.BLEED, false);
        if (item.getIsPoison()) character.addNewDot(CharacterEntity.POISON, false);
        if (item.getIsFrostBurn()) character.addNewDot(CharacterEntity.FROSTBURN, false);
        itemObserverRemove.set(index);
    }


    // direct damage applied to player character
    public void damageCharacter(int damageAmount) {
        character.damageTarget(damageAmount);
        notifyPropertyChanged(BR.health);
    }


    // character arrays
    public ArrayList<String> mapIntoList(Map<String, Integer> map) {
        ArrayList<String> list = new ArrayList<>();
        for (Map.Entry<String, Integer> pair : map.entrySet()) {
            list.add(pair.getKey());
        }
        return list;
    }

    // ** Dot Effects **

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
        character.removeInputDotMap(key, true);
        notifyPropertyChanged(BR.dotMap);
        // observed by CharacterViewModel
        dotObserverRemove.set(key);
    }
    public void addInputDotMap(String key, boolean isInfinite) {
        // observed by XML
        boolean isChanged = character.addNewDot(key, isInfinite);
        notifyPropertyChanged(BR.dotMap);
        // observed by CharacterViewModel
        if (isChanged) dotObserverAdd.set(key);
    }

    // ** Special Effects **

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
        removeInputSpecialMap(key);
        notifyPropertyChanged(BR.specialMap);
        // observed by CharacterViewModel
        specialObserverRemove.set(key);
        notifyPropertyChanged(BR.specialObserverRemove);
    }
    public void addInputSpecialMap(String key, int duration, boolean isInfinite) {
        // observed by XML
        character.addNewSpecial(key, duration, isInfinite);
        notifyPropertyChanged(BR.specialMap);
        // observed by CharacterViewModel
        specialObserverAdd.set(key);
        notifyPropertyChanged(BR.specialObserverAdd);
    }

    // ** Stat Changes **

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
    public int getMaxHealth() {
        return character.getMaxHealth();
    }
    public void setMaxHealth(int maxHealth) {
        character.setMaxHealth(maxHealth);
        notifyPropertyChanged(BR.maxHealth);
    }
    @Bindable
    public String getMana() {
        return String.valueOf(character.getMana());
    }
    public void setMana(int mana) {
        character.setMana(mana);
        notifyPropertyChanged(BR.mana);
    }
    @Bindable
    public int getMaxMana() {
        return character.getMaxMana();
    }
    public void setMaxMana(int maxMana) {
        character.setMaxMana(maxMana);
        notifyPropertyChanged(BR.maxMana);
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
