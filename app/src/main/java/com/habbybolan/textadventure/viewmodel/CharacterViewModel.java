package com.habbybolan.textadventure.viewmodel;

import android.content.Context;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.habbybolan.textadventure.BR;
import com.habbybolan.textadventure.model.characterentity.Character;
import com.habbybolan.textadventure.model.characterentity.CharacterEntity;
import com.habbybolan.textadventure.model.effects.Dot;
import com.habbybolan.textadventure.model.effects.SpecialEffect;
import com.habbybolan.textadventure.model.inventory.Ability;
import com.habbybolan.textadventure.model.inventory.Item;
import com.habbybolan.textadventure.model.inventory.weapon.Weapon;
import com.habbybolan.textadventure.repository.SaveDataLocally;
import com.habbybolan.textadventure.repository.database.DatabaseAdapter;

import java.util.ArrayList;
import java.util.List;

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

    // ** CharacterFragment drop/consume state **

    // keeps track of the state that gives the ability for Inventory Object to be consumed/dropped inside CharacterFragment
    private ObservableField<Boolean> stateInventoryObserver = new ObservableField<>(true);
    public ObservableField<Boolean> getStateInventoryObserver() {
        return stateInventoryObserver;
    }
    public void setStateInventoryObserver(boolean isDropConsume) {
        stateInventoryObserver.set(isDropConsume);
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
        int index = character.getItemIndex(item);
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
            SpecialEffect special;
            if (item.getIsConfuse()) {
                special = new SpecialEffect(SpecialEffect.CONFUSE, true);
                if (!character.removeInputSpecial(special)) updateAllSpecial.set(null);


            }
            if (item.getIsStun()) {
                special = new SpecialEffect(SpecialEffect.STUN, true);
                if (!character.removeInputSpecial(special)) updateAllSpecial.set(null);


            }
            if (item.getIsSilence()) {
                special = new SpecialEffect(SpecialEffect.SILENCE, true);
                if (!character.removeInputSpecial(special)) updateAllSpecial.set(null);


            }
            if (item.getIsInvisible()) {
                special = new SpecialEffect(SpecialEffect.INVISIBILITY, true);
                if (!character.removeInputSpecial(special)) updateAllSpecial.set(null);


            }
            if (item.getIsInvincible()) {
                special = new SpecialEffect(SpecialEffect.INVINCIBILITY, true);
                if (!character.removeInputSpecial(special)) updateAllSpecial.set(null);

            }
            // DOT changes
            Dot dot;
            if (item.getIsFire()) {
                dot = new Dot(Dot.FIRE, true);
                if (!character.removeInputDot(dot)) updateAllDot.set(null);


            }
            if (item.getIsBleed()) {
                dot = new Dot(Dot.BLEED, true);
                if (!character.removeInputDot(dot)) updateAllDot.set(null);

            }
            if (item.getIsPoison()) {
                dot = new Dot(Dot.FIRE, true);
                if (!character.removeInputDot(dot)) updateAllDot.set(null);

            }
            if (item.getIsFrostBurn()) {
                dot = new Dot(Dot.FROSTBURN, true);
                if (!character.removeInputDot(dot)) updateAllDot.set(null);

            }
            if (item.getIsHealthDot()) {
                dot = new Dot(Dot.HEALTH_DOT, true);
                if (!character.removeInputDot(dot)) updateAllDot.set(null);

            }
            if (item.getIsManaDot()) {
                dot = new Dot(Dot.MANA_DOT, true);
                if (!character.removeInputDot(dot)) updateAllDot.set(null);

            }
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
            SpecialEffect special;
            if (item.getIsConfuse()) {
                special = new SpecialEffect(SpecialEffect.CONFUSE, true);
                if (character.addNewSpecial(special)) updateAllSpecial.set(null);
            }
            if (item.getIsStun()) {
                special = new SpecialEffect(SpecialEffect.STUN, true);
                if (character.addNewSpecial(special)) updateAllSpecial.set(null);
            }
            if (item.getIsSilence()) {
                special = new SpecialEffect(SpecialEffect.SILENCE, true);
                if (character.addNewSpecial(special)) updateAllSpecial.set(null);
            }
            if (item.getIsInvisible()) {
                special = new SpecialEffect(SpecialEffect.INVISIBILITY, true);
                if (character.addNewSpecial(special)) updateAllSpecial.set(null);
            }
            if (item.getIsInvincible()) {
                special = new SpecialEffect(SpecialEffect.INVINCIBILITY, true);
                if (character.addNewSpecial(special)) updateAllSpecial.set(null);
            }
            // DOT changes
            Dot dot;
            if (item.getIsFire()) {
                dot = new Dot(Dot.FIRE, true);
                if (character.addNewDot(dot)) updateAllDot.set(null);
            }
            if (item.getIsBleed()) {
                dot = new Dot(Dot.BLEED, true);
                if (character.addNewDot(dot)) updateAllDot.set(null);
            }
            if (item.getIsPoison()) {
                dot = new Dot(Dot.POISON, true);
                if (character.addNewDot(dot)) updateAllDot.set(null);
            }
            if (item.getIsFrostBurn()) {
                dot = new Dot(Dot.FROSTBURN, true);
                if (character.addNewDot(dot)) updateAllDot.set(null);
            }
            if (item.getIsHealthDot()) {
                dot = new Dot(Dot.HEALTH_DOT, true);
                if (character.addNewDot(dot)) updateAllDot.set(null);
            }
            if (item.getIsManaDot()) {
                dot = new Dot(Dot.MANA_DOT, true);
                if (character.addNewDot(dot)) updateAllDot.set(null);
            }
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
        if (item.getHealthChange() != 0) setHealth(character.changeHealth(item.getHealthChange()));
        if (item.getManaChange() != 0) setMana(character.changeMana(item.getManaChange()));
        // specials
        if (item.getIsConfuse()) character.addNewSpecial(new SpecialEffect(SpecialEffect.CONFUSE, item.getDuration()));
        if (item.getIsStun()) character.addNewSpecial(new SpecialEffect(SpecialEffect.STUN, item.getDuration()));
        if (item.getIsSilence()) character.addNewSpecial(new SpecialEffect(SpecialEffect.SILENCE, item.getDuration()));
        if (item.getIsInvisible()) character.addNewSpecial(new SpecialEffect(SpecialEffect.INVISIBILITY, item.getDuration()));
        if (item.getIsInvincible()) character.addNewSpecial(new SpecialEffect(SpecialEffect.INVINCIBILITY, item.getDuration()));
        // DOTS
        if (item.getIsFire()) character.addNewDot(new Dot(Dot.FIRE, false));
        if (item.getIsBleed()) character.addNewDot(new Dot(Dot.BLEED, false));
        if (item.getIsPoison()) character.addNewDot(new Dot(Dot.POISON, false));
        if (item.getIsFrostBurn()) character.addNewDot(new Dot(Dot.FROSTBURN, false));
        if (item.getIsHealthDot()) character.addNewDot(new Dot(Dot.HEALTH_DOT, false));
        if (item.getIsManaDot()) character.addNewDot(new Dot(Dot.MANA_DOT, false));
        itemObserverRemove.set(index);
    }
    // consume item at index
    public void consumeItemAtIndex(int index) {
        consumeItem(character.getItemAtIndex(index));
    }

    // returns true if a disarm trap exists, and removes the trap from inventory
    public boolean checkInventoryForTrapItem() {
        ArrayList<Item> items = character.getItems();
        int index = 0;
        for (Item trapItem : items) {
            if (trapItem.getEscapeTrap()) {
                items.remove(trapItem);
                itemObserverRemove.set(index);
                return true;
            }
            index++;
        }
        return false;
    }

    // direct damage applied to player character
    public void damageCharacter(int damageAmount) {
        character.damageTarget(damageAmount);
        notifyPropertyChanged(BR.health);
    }

    // ** Dot Effects **

    public ObservableField<Dot>  getUpdateAllDot() {
        return updateAllDot;
    }
    private ObservableField<Dot> updateAllDot = new ObservableField<>();
    /*private ObservableField<Dot> dotObserverAdd = new ObservableField<>();
    public ObservableField<Dot> getDotObserverAdd() {
        return dotObserverAdd;
    }
    private ObservableField<Dot> dotObserverRemove = new ObservableField<>();
    public ObservableField<Dot> getDotObserverRemove() {
        return dotObserverRemove;
    }*/
    @Bindable
    public ArrayList<Dot> getDotList() {
        return character.getDotList();
    }
    public void removeInputDot(Dot dot) {
        // observed by XML
        character.removeInputDot(dot);
        notifyPropertyChanged(BR.dotList);
        // observed by CharacterViewModel
        updateAllDot.set(dot);
    }
    public void addInputDot(Dot dot) {
        // observed by XML
        boolean isChanged = character.addNewDot(dot);
        notifyPropertyChanged(BR.dotList);
        // observed by CharacterViewModel
        updateAllDot.set(dot);
    }
    public void applyDots() {
        character.applyDots();
        /*for (Dot dot : removedDots) {
            dotObserverRemove.set(dot);
        }*/
        updateAllDot.set(new Dot(Dot.FIRE, false));
    }

    // ** Special Effects **

    private ObservableField<SpecialEffect> updateAllSpecial = new ObservableField<>();
    public ObservableField<SpecialEffect>  getUpdateAllSpecial() {
        return updateAllSpecial;
    }
    /*
    private ObservableField<SpecialEffect> specialObserverAdd = new ObservableField<>();
    public ObservableField<SpecialEffect> getSpecialObserverAdd() {
        return specialObserverAdd;
    }
    private ObservableField<SpecialEffect> specialObserverRemove = new ObservableField<>();
    public ObservableField<SpecialEffect> getSpecialObserverRemove() {
        return specialObserverRemove;
    }*/

    @Bindable
    public ArrayList<SpecialEffect> getSpecialList() {
        return character.getSpecialList();
    }
    public void removeInputSpecial(SpecialEffect special) {
        // observed by XML
        character.removeInputSpecial(special);
        notifyPropertyChanged(BR.specialList);
        // observed by CharacterViewModel
        updateAllSpecial.set(special);
    }
    public void addInputSpecial(SpecialEffect special) {
        // observed by XML
        character.addNewSpecial(special);
        notifyPropertyChanged(BR.specialList);
        // observed by CharacterViewModel
        updateAllSpecial.set(special);
    }
    public void decrSpecialDuration() {
        character.decrSpecialDuration();
        /*for (SpecialEffect special : removedSpecials) {
            specialObserverRemove.set(special);
        }*/
        updateAllSpecial.set(new SpecialEffect(SpecialEffect.STUN, false));
    }

    // ** Stat Changes **

    public void decrementStatChangeDuration() {
        character.decrementStatChangeDuration();
        // todo: update binded values
    }
    public void decrementTempExtraDuration() {
        character.decrementTempExtraDuration();
        // todo: update binded values
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

        // Dots
    @Bindable
    public boolean getIsFire() {
        return character.getIsFire();
    }
    public void setIsFire(boolean isFire) {
        character.setIsFire(isFire);
    }
    @Bindable
    public boolean getIsBleed() {
        return character.getIsBleed();
    }
    public void setIsBleed(boolean isBleed) {
        character.setIsBleed(isBleed);
    }
    @Bindable
    public boolean getIsPoison() {
        return character.getIsPoison();
    }
    public void setIsPoison(boolean isPoison) {
        character.setIsPoison(isPoison);
    }
    @Bindable
    public boolean getIsFrostBurn() {
        return character.getIsFrostBurn();
    }
    public void setIsFrostBurn(boolean isFrostBurn) {
        character.setIsFrostBurn(isFrostBurn);
    }
    @Bindable
    public boolean getIsHealDot() {
        return character.getIsHealDot();
    }
    public void setIsHealDot(boolean isHealDot) {
        character.setIsHealDot(isHealDot);
    }
    @Bindable
    public boolean getIsManaDot() {
        return character.getIsManaDot();
    }
    public void setIsManaDot(boolean isManaDot) {
        character.setIsManaDot(isManaDot);
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
