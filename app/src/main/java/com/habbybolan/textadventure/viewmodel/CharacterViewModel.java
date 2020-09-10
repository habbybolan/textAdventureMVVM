package com.habbybolan.textadventure.viewmodel;

import android.content.Context;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.habbybolan.textadventure.BR;
import com.habbybolan.textadventure.model.characterentity.Character;
import com.habbybolan.textadventure.model.characterentity.CharacterEntity;
import com.habbybolan.textadventure.model.dialogue.HealthDialogue;
import com.habbybolan.textadventure.model.dialogue.ManaDialogue;
import com.habbybolan.textadventure.model.effects.Dot;
import com.habbybolan.textadventure.model.effects.Effect;
import com.habbybolan.textadventure.model.effects.SpecialEffect;
import com.habbybolan.textadventure.model.effects.TempBar;
import com.habbybolan.textadventure.model.effects.TempStat;
import com.habbybolan.textadventure.model.inventory.Ability;
import com.habbybolan.textadventure.model.inventory.Inventory;
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

    // Inventory object to be added to inventory if user decides to
    private Inventory inventoryToRetrieve;
    private Inventory getInventoryToRetrieve() {
        return inventoryToRetrieve;
    }
    public void setInventoryToRetrieve(Inventory inventory) {
        this.inventoryToRetrieve = inventory;
    }
    // set field to null to notify it's already retrieved
    public void removeInventoryToRetrieve() {
        inventoryToRetrieve = null;
    }

        // Abilities

    // Observable for adding and deleting abilities and updating RecyclerView in CharacterFragment
    private ObservableField<Ability> abilityObserverAdd = new ObservableField<>();
    public ObservableField<Ability> getAbilityObserverAdd() {
        return abilityObserverAdd;
    }
    private ObservableField<Ability> abilityObserverRemove = new ObservableField<>();
    public ObservableField<Ability> getAbilityObserverRemove() {
        return abilityObserverRemove;
    }
    public void removeAbility(Ability ability) {
        character.removeAbility(ability);
        abilityObserverRemove.set(ability);
    }
    public boolean addAbility(Ability ability) {
        if (character.getNumAbilities() >= Character.MAX_ABILITIES) return false;
        character.addAbility(ability);
        abilityObserverAdd.set(ability);
        return true;
    }
    public void removeAbilityAtIndex(int index) {
        abilityObserverRemove.set(character.removeAbilityAtIndex(index));
    }
    public ArrayList<Ability> getAbilities() {
        return character.getAbilities();
    }

        // Weapons

    // Observable for adding and deleting weapons and updating RecyclerView in CharacterFragment
    private ObservableField<Weapon> weaponObserverAdd = new ObservableField<>();
    public ObservableField<Weapon> getWeaponObserverAdd() {
        return weaponObserverAdd;
    }
    private ObservableField<Weapon> weaponObserverRemove = new ObservableField<>();
    public ObservableField<Weapon> getWeaponObserverRemove() {
        return weaponObserverRemove;
    }
    public ArrayList<Weapon> getWeapons() {
        return character.getWeapons();
    }
    public void removeWeapon(Weapon weapon) {
        character.removeWeapon(weapon);
        weaponObserverRemove.set(weapon);
    }
    public boolean addWeapon(Weapon weapon) {
        if (character.getNumWeapons() >= Character.MAX_WEAPONS) return false;
        character.addWeapon(weapon);
        weaponObserverAdd.set(weapon);
        return true;
    }
    public void removeWeaponAtIndex(int index) {
        weaponObserverRemove.set(character.removeWeaponAtIndex(index));
    }

        // Items

    // Observable for adding and deleting items and updating RecyclerView in CharacterFragment
    private ObservableField<Item> itemObserverAdd = new ObservableField<>();
    public ObservableField<Item> getItemObserverAdd() {
        return itemObserverAdd;
    }
    private ObservableField<Item> itemObserverRemove = new ObservableField<>();
    public ObservableField<Item> getItemObserverRemove() {
        return itemObserverRemove;
    }
    public ArrayList<Item> getItems() {
        return character.getItems();
    }
    public void removeItem(Item item) {
        removeItemAtIndex(character.getItemIndex(item));
    }
    public void removeItemAtIndex(int index) {
        Item item = character.removeItemAtIndex(index);
        itemObserverRemove.set(item);
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
                setHealth(character.changeHealth(-item.getHealthChange()));
            }
            if (item.getManaChange() != 0) {
                setMaxMana(character.getMaxMana() - item.getManaChange());
                setMana(character.changeMana(-item.getManaChange()));
            }
            // special changes
            SpecialEffect special;
            if (item.getIsConfuse()) {
                special = new SpecialEffect(SpecialEffect.CONFUSE);
                if (!character.removeInputSpecial(special)) updateAllSpecialAdd.set(special);


            }
            if (item.getIsStun()) {
                special = new SpecialEffect(SpecialEffect.STUN);
                if (!character.removeInputSpecial(special)) updateAllSpecialAdd.set(special);


            }
            if (item.getIsSilence()) {
                special = new SpecialEffect(SpecialEffect.SILENCE);
                if (!character.removeInputSpecial(special)) updateAllSpecialAdd.set(special);


            }
            if (item.getIsInvisible()) {
                special = new SpecialEffect(SpecialEffect.INVISIBILITY);
                if (!character.removeInputSpecial(special)) updateAllSpecialAdd.set(special);


            }
            if (item.getIsInvincible()) {
                special = new SpecialEffect(SpecialEffect.INVINCIBILITY);
                if (!character.removeInputSpecial(special)) updateAllSpecialAdd.set(special);

            }
            // DOT changes
            Dot dot;
            if (item.getIsFire()) {
                dot = new Dot(Dot.FIRE, true);
                if (!character.removeInputDot(dot)) updateAllDotAdd.set(dot);


            }
            if (item.getIsBleed()) {
                dot = new Dot(Dot.BLEED, true);
                if (!character.removeInputDot(dot)) updateAllDotAdd.set(dot);

            }
            if (item.getIsPoison()) {
                dot = new Dot(Dot.FIRE, true);
                if (!character.removeInputDot(dot)) updateAllDotAdd.set(dot);

            }
            if (item.getIsFrostBurn()) {
                dot = new Dot(Dot.FROSTBURN, true);
                if (!character.removeInputDot(dot)) updateAllDotAdd.set(dot);

            }
            if (item.getIsHealthDot()) {
                dot = new Dot(Dot.HEALTH_DOT, true);
                if (!character.removeInputDot(dot)) updateAllDotAdd.set(dot);

            }
            if (item.getIsManaDot()) {
                dot = new Dot(Dot.MANA_DOT, true);
                if (!character.removeInputDot(dot)) updateAllDotAdd.set(dot);

            }
        }
    }
    public boolean addItem(Item item) {
        if (character.getNumItems() >= Character.MAX_ITEMS) return false;
        character.addItem(item);
        itemObserverAdd.set(item);
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
                setHealth(character.changeHealth(item.getHealthChange()));
            }
            if (item.getManaChange() != 0) {
                setMaxMana(character.getMaxMana() + item.getManaChange());
                setMana(character.changeMana(item.getManaChange()));
            }
            // special changes
            SpecialEffect special;
            if (item.getIsConfuse()) {
                special = new SpecialEffect(SpecialEffect.CONFUSE);
                if (character.addNewSpecial(special)) updateAllSpecialAdd.set(special);
            }
            if (item.getIsStun()) {
                special = new SpecialEffect(SpecialEffect.STUN);
                if (character.addNewSpecial(special)) updateAllSpecialAdd.set(special);
            }
            if (item.getIsSilence()) {
                special = new SpecialEffect(SpecialEffect.SILENCE);
                if (character.addNewSpecial(special)) updateAllSpecialAdd.set(special);
            }
            if (item.getIsInvisible()) {
                special = new SpecialEffect(SpecialEffect.INVISIBILITY);
                if (character.addNewSpecial(special)) updateAllSpecialAdd.set(special);
            }
            if (item.getIsInvincible()) {
                special = new SpecialEffect(SpecialEffect.INVINCIBILITY);
                if (character.addNewSpecial(special)) updateAllSpecialAdd.set(special);
            }
            // DOT changes
            Dot dot;
            if (item.getIsFire()) {
                dot = new Dot(Dot.FIRE, true);
                if (character.addNewDot(dot)) updateAllDotAdd.set(dot);
            }
            if (item.getIsBleed()) {
                dot = new Dot(Dot.BLEED, true);
                if (character.addNewDot(dot)) updateAllDotAdd.set(dot);
            }
            if (item.getIsPoison()) {
                dot = new Dot(Dot.POISON, true);
                if (character.addNewDot(dot)) updateAllDotAdd.set(dot);
            }
            if (item.getIsFrostBurn()) {
                dot = new Dot(Dot.FROSTBURN, true);
                if (character.addNewDot(dot)) updateAllDotAdd.set(dot);
            }
            if (item.getIsHealthDot()) {
                dot = new Dot(Dot.HEALTH_DOT, true);
                if (character.addNewDot(dot)) updateAllDotAdd.set(null);
            }
            if (item.getIsManaDot()) {
                dot = new Dot(Dot.MANA_DOT, true);
                if (character.addNewDot(dot)) updateAllDotAdd.set(null);
            }
        }
        return true;
    }
    // consume an item
    public void consumeItem(Item item) {
        if (!item.getIsConsumable()) throw new IllegalArgumentException();
        removeItem(item);
        // stat change
        if (item.getStrChange() > 0) {
            character.addNewStatIncrease(new TempStat(CharacterEntity.STR, item.getDuration(), item.getStrChange()));
            notifyPropertyChanged(BR.strength);
        }
        if (item.getStrChange() < 0) {
            character.addNewStatDecrease(new TempStat(CharacterEntity.STR, item.getDuration(), item.getStrChange()));
            notifyPropertyChanged(BR.strength);
        }
        if (item.getIntChange() > 0) {
            character.addNewStatIncrease(new TempStat(CharacterEntity.INT, item.getDuration(), item.getIntChange()));
            notifyPropertyChanged(BR.intelligence);
        }
        if (item.getIntChange() < 0) {
            character.addNewStatDecrease(new TempStat(CharacterEntity.INT, item.getDuration(), item.getIntChange()));
            notifyPropertyChanged(BR.intelligence);
        }
        if (item.getConChange() > 0) {
            character.addNewStatIncrease(new TempStat(CharacterEntity.CON, item.getDuration(), item.getConChange()));
            notifyPropertyChanged(BR.constitution);
        }
        if (item.getConChange() < 0) {
            character.addNewStatDecrease(new TempStat(CharacterEntity.CON, item.getDuration(), item.getConChange()));
            notifyPropertyChanged(BR.constitution);
        }
        if (item.getSpdChange() > 0) {
            character.addNewStatIncrease(new TempStat(CharacterEntity.SPD, item.getDuration(), item.getSpdChange()));
            notifyPropertyChanged(BR.speed);
        }
        if (item.getSpdChange() < 0) {
            character.addNewStatDecrease(new TempStat(CharacterEntity.SPD, item.getDuration(), item.getSpdChange()));
            notifyPropertyChanged(BR.speed);
        }
        if (item.getBlockChange() > 0) {
            character.addNewStatIncrease(new TempStat(CharacterEntity.BLOCK, item.getDuration(), item.getBlockChange()));
            notifyPropertyChanged(BR.block);
        }
        if (item.getBlockChange() < 0) {
            character.addNewStatDecrease(new TempStat(CharacterEntity.BLOCK, item.getDuration(), item.getBlockChange()));
            notifyPropertyChanged(BR.block);
        }
        if (item.getEvasionChange() > 0) {
            character.addNewStatIncrease(new TempStat(CharacterEntity.EVASION, item.getDuration(), item.getEvasionChange()));
            notifyPropertyChanged(BR.evasion);
        }
        if (item.getEvasionChange() < 0) {
            character.addNewStatDecrease(new TempStat(CharacterEntity.EVASION, item.getDuration(), item.getEvasionChange()));
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
    }
    // consume item at index
    public void consumeItemAtIndex(int index) {
        consumeItem(character.getItemAtIndex(index));
    }

    // returns true if a disarm trap exists, and removes the trap from inventory
    public boolean checkInventoryForTrapItem() {
        ArrayList<Item> items = character.getItems();
        for (Item trapItem : items) {
            if (trapItem.getEscapeTrap()) {
                items.remove(trapItem);
                itemObserverRemove.set(trapItem);
                return true;
            }
        }
        return false;
    }

    // direct damage applied to player character
    public void damageCharacter(int damageAmount) {
        setHealth(character.damageTarget(damageAmount));
    }

    // ** Dot Effects **

    public ObservableField<Dot>  getUpdateAllDotAdd() {
        return updateAllDotAdd;
    }
    private ObservableField<Dot> updateAllDotAdd = new ObservableField<>();

    public ObservableField<Void>  getUpdateAllDotRemove() {
        return updateAllDotRemove;
    }
    private ObservableField<Void> updateAllDotRemove = new ObservableField<>();

    @Bindable
    public ArrayList<Dot> getDotList() {
        return character.getDotList();
    }
    public void removeInputDot(Dot dot) {
        // observed by XML
        character.removeInputDot(dot);
        notifyPropertyChanged(BR.dotList);
        // observed by CharacterViewModel
        updateAllDotRemove.set(null);
    }
    public void addInputDot(Dot dot) {
        // observed by XML
        boolean isChanged = character.addNewDot(dot);
        notifyPropertyChanged(BR.dotList);
        // observed by CharacterViewModel
        updateAllDotAdd.set(dot);
    }
    public void applyDots() {
        character.applyDots();
        /*for (Dot dot : removedDots) {
            dotObserverRemove.set(dot);
        }*/
        // compare with dummy Dot
        updateAllDotRemove.set(null);
    }

    // ** Special Effects **

    private ObservableField<SpecialEffect> updateAllSpecialAdd = new ObservableField<>();
    public ObservableField<SpecialEffect>  getUpdateAllSpecialAdd() {
        return updateAllSpecialAdd;
    }
    private ObservableField<Void> updateAllSpecialRemove = new ObservableField<>();
    public ObservableField<Void>  getUpdateAllSpecialRemove() {
        return updateAllSpecialRemove;
    }

    @Bindable
    public ArrayList<SpecialEffect> getSpecialList() {
        return character.getSpecialList();
    }
    public void removeInputSpecial(SpecialEffect special) {
        // observed by XML
        character.removeInputSpecial(special);
        notifyPropertyChanged(BR.specialList);
        // observed by CharacterViewModel
        updateAllSpecialRemove.set(null);
    }
    public void addInputSpecial(SpecialEffect special) {
        // observed by XML
        character.addNewSpecial(special);
        notifyPropertyChanged(BR.specialList);
        // observed by CharacterViewModel
        updateAllSpecialAdd.set(special);
    }
    public void decrSpecialDuration() {
        character.decrSpecialDuration();
        /*for (SpecialEffect special : removedSpecials) {
            specialObserverRemove.set(special);
        }*/
        // compare with dummy Dot
        updateAllSpecialRemove.set(null);
    }

    // ** Stat Changes **

        // STATS

    public ObservableField<TempStat>  getUpdateAllStatIncrAdd() {
        return updateAllStatIncrAdd;
    }
    private ObservableField<TempStat> updateAllStatIncrAdd = new ObservableField<>();
    public ObservableField<TempStat>  getUpdateAllStatIncrRemove() {
        return updateAllStatIncrRemove;
    }
    private ObservableField<TempStat> updateAllStatIncrRemove = new ObservableField<>();

    public ObservableField<TempStat>  getUpdateAllStatDecrAdd() {
        return updateAllStatDecrAdd;
    }
    private ObservableField<TempStat> updateAllStatDecrAdd = new ObservableField<>();
    public ObservableField<TempStat>  getUpdateAllStatDecrRemove() {
        return updateAllStatDecrRemove;
    }
    private ObservableField<TempStat> updateAllStatDecrRemove = new ObservableField<>();

    // Observer for when the stat value changes
    public ObservableField<TempStat>  getUpdateAllStatChange() {
        return updateAllStatChange;
    }
    private ObservableField<TempStat> updateAllStatChange = new ObservableField<>();

    // add a temporary stat to the stat incr/decr list
    public void addInputStat(TempStat tempStat) {
        if (tempStat.getAmount() > 0) {
            character.addNewStatIncrease(tempStat);
            updateAllStatIncrAdd.set(tempStat);
        } else {
            character.addNewStatDecrease(tempStat);
            updateAllStatDecrRemove.set(tempStat);
        }
        notifyStatChange(tempStat);
    }

    private void notifyStatChange(TempStat tempStat) {
        switch (tempStat.getType()) {
            case TempStat.STR:
                notifyPropertyChanged(BR.strength);
                notifyPropertyChanged(BR.strBase);
                break;
            case TempStat.INT:
                notifyPropertyChanged(BR.intelligence);
                notifyPropertyChanged(BR.intBase);
                break;
            case TempStat.CON:
                notifyPropertyChanged(BR.constitution);
                notifyPropertyChanged(BR.conBase);
                break;
            case TempStat.SPD:
                notifyPropertyChanged(BR.speed);
                notifyPropertyChanged(BR.spdBase);
                break;
            case TempStat.EVASION:
                notifyPropertyChanged(BR.evasion);
                notifyPropertyChanged(BR.evasionBase);
                break;
            case TempStat.BLOCK:
                notifyPropertyChanged(BR.block);
                notifyPropertyChanged(BR.blockBase);
                break;
        }
    }

    // given a Stat object, find the correct base value to change
    public void permIncreaseStat(TempStat stat) {
        int amount = stat.getAmount();
        switch (stat.getType()) {
            case Effect.STR:
                setStrBase(getStrBase() + amount);
                setStrength(getStrength() + amount);
                break;
            case Effect.INT:
                setIntBase(getIntBase() + amount);
                setIntelligence(getIntelligence() + amount);
                break;
            case Effect.CON:
                setConBase(getConBase() + amount);
                setConstitution(getConstitution() + amount);
                break;
            case Effect.SPD:
                setSpdBase(getSpdBase() + amount);
                setSpeed(getSpeed() + amount);
                break;
            case Effect.EVASION:
                setEvasionBase(getEvasionBase() + amount);
                setEvasion(getEvasion() + amount);
                break;
            case Effect.BLOCK:
                setBlockBase(getBlockBase() + amount);
                setBlock(getBlock() + amount);
                break;
        }
    }

        // BARS

    public void decrementStatChangeDuration() {
        character.decrementStatChangeDuration();
        // todo: update binded values
    }
    public void decrementTempExtraDuration() {
        character.decrementTempExtraDuration();
        // todo: update binded values
    }

    private ObservableField<TempBar> updateAllBarAdd = new ObservableField<>();
    public ObservableField<TempBar> getUpdateAllBarAdd() {
        return updateAllBarAdd;
    }
    private ObservableField<Void> updateAllBarRemove = new ObservableField<>();
    public ObservableField<Void> getUpdateAllBarRemove() {
        return updateAllBarRemove;
    }

    // given tempBar, add it to either TempExtraHealth or TempExtraMana list depending on type
    public void addTempHealthMana(TempBar tempBar) {
        if (tempBar.getType().equals(TempBar.TEMP_HEALTH)) {
            addTempHealthList(tempBar);
        } else {
            addTempManaList(tempBar);
        }
        updateAllBarAdd.set(tempBar);
    }

    @Bindable
    public List<TempBar> getTempHealthList() {
        return character.getTempHealthList();
    }
    public void removeZeroTempHealthList() {
        character.removeZeroTempHealthList();
        updateAllBarRemove.set(null);
        notifyPropertyChanged(BR.tempHealthList);
    }
    public void addTempHealthList(TempBar tempExtraHealth) {
        character.addTempHealthList(tempExtraHealth);
        notifyPropertyChanged(BR.health);
        notifyPropertyChanged(BR.maxHealth);
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
    public List<TempBar> getTempManaList() {
        return character.getTempManaList();
    }
    public void removeZeroTempManaList() {
        character.removeZeroTempManaList();
        notifyPropertyChanged(BR.tempManaList);
    }
    public void addTempManaList(TempBar tempExtraMana) {
        character.addTempManaList(tempExtraMana);
        notifyPropertyChanged(BR.mana);
        notifyPropertyChanged(BR.maxMana);
    }
    @Bindable
    public int getTempExtraMana() {
        return character.getTempExtraMana();
    }
    public void setTempExtraMana(int tempExtraMana) {
        character.setTempExtraMana(tempExtraMana);
        notifyPropertyChanged(BR.tempExtraMana);
    }

    public void setPermBarIncr(TempBar tempBar) {
        if (tempBar.getType().equals(TempBar.TEMP_HEALTH)) {
            setPermHealthIncr(tempBar);
        } else {
            setPermManaIncr(tempBar);
        }
    }

    // set a permanent health increase
    private void setPermHealthIncr(TempBar tempBar) {
        setMaxHealth(getMaxHealth() + tempBar.getAmount());
        setHealth(Integer.parseInt(getHealth()) + tempBar.getAmount());
    }

    // set a permanent mana increase
    private void setPermManaIncr(TempBar tempBar) {
        setMaxMana(getMaxMana() + tempBar.getAmount());
        setMana(Integer.parseInt(getMana()) + tempBar.getAmount());
    }



    @Bindable
    private ObservableField<HealthDialogue> healthObserve = new ObservableField<>();
    public ObservableField<HealthDialogue> getHealthObserve() {
        return healthObserve;
    }
    @Bindable
    public String getHealth() {
        return String.valueOf(character.getHealth());
    }
    // change the health by healthChange amount
    public void setHealth(int health) {
        healthObserve.set(new HealthDialogue(health-character.getHealth()));
        character.setHealth(health);
        notifyPropertyChanged(BR.health);
    }
    @Bindable
    public int getMaxHealth() {
        return character.getMaxHealth();
    }
    public void setMaxHealth(int maxHealth) {
        character.setMaxHealth(character.getHealth() + maxHealth);
        notifyPropertyChanged(BR.maxHealth);
    }

    private ObservableField<ManaDialogue> manaObserve = new ObservableField<>();
    public ObservableField<ManaDialogue> getManaObserve() {
        return manaObserve;
    }
    @Bindable
    public String getMana() {
        return String.valueOf(character.getMana());
    }
    public void setMana(int mana) {
        manaObserve.set(new ManaDialogue(mana));
        character.setMana(mana - character.getMana());
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
        updateAllStatChange.set(new TempStat(Effect.STR, strength - character.getStrength()));
        character.setStrength(strength);
        notifyPropertyChanged(BR.strength);
    }
    @Bindable
    public int getIntelligence() {
        return character.getIntelligence();
    }
    public void setIntelligence(int intelligence) {
        updateAllStatChange.set(new TempStat(Effect.INT, intelligence - character.getIntelligence()));
        character.setIntelligence(intelligence);
        notifyPropertyChanged(BR.intelligence);
    }
    @Bindable
    public int getConstitution() {
        return character.getConstitution();
    }
    public void setConstitution(int constitution) {
        updateAllStatChange.set(new TempStat(Effect.CON, constitution - character.getConstitution()));
        character.setConstitution(constitution);
        notifyPropertyChanged(BR.constitution);
    }
    @Bindable
    public int getSpeed() {
        return character.getSpeed();
    }
    public void setSpeed(int speed) {
        updateAllStatChange.set(new TempStat(Effect.SPD, speed - character.getSpeed()));
        character.setSpeed(speed);
        notifyPropertyChanged(BR.speed);
    }
    @Bindable
    public int getEvasion() {
        return character.getEvasion();
    }
    public void setEvasion(int evasion) {
        updateAllStatChange.set(new TempStat(Effect.EVASION, evasion - character.getEvasion()));
        character.setEvasion(evasion);
        notifyPropertyChanged(BR.evasion);
    }
    @Bindable
    public int getBlock() {
        return character.getBlock();
    }
    public void setBlock(int block) {
        updateAllStatChange.set(new TempStat(Effect.BLOCK, block - character.getBlock()));
        character.setBlock(block);
        notifyPropertyChanged(BR.block);
    }

    public Character getCharacter() {
        return character;
    }
}
