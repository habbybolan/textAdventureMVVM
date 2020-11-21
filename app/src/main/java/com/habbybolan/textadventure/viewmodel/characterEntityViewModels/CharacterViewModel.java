package com.habbybolan.textadventure.viewmodel.characterEntityViewModels;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.habbybolan.textadventure.BR;
import com.habbybolan.textadventure.model.characterentity.Character;
import com.habbybolan.textadventure.model.characterentity.CharacterEntity;
import com.habbybolan.textadventure.model.effects.Dot;
import com.habbybolan.textadventure.model.effects.SpecialEffect;
import com.habbybolan.textadventure.model.effects.TempBar;
import com.habbybolan.textadventure.model.effects.TempStat;
import com.habbybolan.textadventure.model.inventory.Ability;
import com.habbybolan.textadventure.model.inventory.Inventory;
import com.habbybolan.textadventure.model.inventory.Item;
import com.habbybolan.textadventure.model.inventory.weapon.Weapon;
import com.habbybolan.textadventure.repository.SaveDataLocally;

import java.util.ArrayList;

/*
holds current character data used to tie the data to necessary views
 */
public class CharacterViewModel extends CharacterEntityViewModel {

    private Character character;
    private Context context;

    private static CharacterViewModel instance = null;

    // returns true if viewModel is initiated
    public static boolean isInitiated() {
        return instance != null;
    }

    // uses singleton pattern to access in all encounter fragments
    public static CharacterViewModel getInstance() {
        if (instance == null) throw new AssertionError("Have to call init first");
        return instance;
    }

    // must be initialized before getting the instance
    public static CharacterViewModel init(Context context) {
        if (instance != null) throw new AssertionError("Already Initialized");
        instance = new CharacterViewModel(context);
        return instance;
    }

    // private constructor only accessed from initiation
    private CharacterViewModel(Context context) {
        this.context = context;
        SaveDataLocally save = new SaveDataLocally(context);
        String characterData = save.readCharacterData();
        // todo: set up ObservableArrayLists?
        character = new Character(characterData, context);
        this.characterEntity = character;
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

    // save character
    public void saveCharacter() {
        SaveDataLocally save = new SaveDataLocally(context);
        save.saveCharacterLocally(character);
    }


    /**
     * Add a new inventory, calling the proper method to add the Ability, Item, or Weapon
     * @param inventory The Inventory object to be added.
     */
    public void addNewInventory(Inventory inventory) {
        String type = inventory.getType();
        switch (type) {
            case Inventory.TYPE_ABILITY:
                addAbility((Ability) inventory);
                break;
            case Inventory.TYPE_ITEM:
                addItem((Item) inventory);
                break;
            case Inventory.TYPE_WEAPON:
                addWeapon((Weapon) inventory);
                break;
            default:
                throw new IllegalArgumentException("Inventory object not a Weapon, Ability, or Item");
        }
    }

    /**
     * Remove an Inventory object from player character inventory
     * @param inventory Inventory object to remove.
     */
    public void removeInventory(Inventory inventory) {
        String type = inventory.getType();
        switch (type) {
            case Inventory.TYPE_ABILITY:
                removeAbility((Ability) inventory);
                break;
            case Inventory.TYPE_ITEM:
                removeItem((Item) inventory);
                break;
            case Inventory.TYPE_WEAPON:
                removeWeapon((Weapon) inventory);
                break;
            default:
                throw new IllegalArgumentException("Inventory object not a Weapon, Ability, or Item");
        }
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
                // health and max health altered
                notifyChangeHealth(character.changeHealthMax(-item.getHealthChange()));
            }
            if (item.getManaChange() != 0) {
                setMaxMana(character.getMaxMana() - item.getManaChange());
                character.changeManaMax(-item.getManaChange());
                // max mana altered
                notifyChangeMana(-item.getManaChange());
            }
            // special changes
            SpecialEffect special;
            if (item.getIsConfuse()) {
                special = new SpecialEffect(SpecialEffect.CONFUSE);
                character.removeInputSpecial(special);


            }
            if (item.getIsStun()) {
                special = new SpecialEffect(SpecialEffect.STUN);
                character.removeInputSpecial(special);


            }
            if (item.getIsSilence()) {
                special = new SpecialEffect(SpecialEffect.SILENCE);
                character.removeInputSpecial(special);


            }
            if (item.getIsInvisible()) {
                special = new SpecialEffect(SpecialEffect.INVISIBILITY);
                character.removeInputSpecial(special);


            }
            if (item.getIsInvincible()) {
                special = new SpecialEffect(SpecialEffect.INVINCIBILITY);
                character.removeInputSpecial(special);

            }
            // DOT changes
            Dot dot;
            if (item.getIsFire()) {
                dot = new Dot(Dot.FIRE, true);
                character.removeInputDot(dot);


            }
            if (item.getIsBleed()) {
                dot = new Dot(Dot.BLEED, true);
                character.removeInputDot(dot);

            }
            if (item.getIsPoison()) {
                dot = new Dot(Dot.FIRE, true);
                character.removeInputDot(dot);

            }
            if (item.getIsFrostBurn()) {
                dot = new Dot(Dot.FROSTBURN, true);
                character.removeInputDot(dot);

            }
            if (item.getIsHealthDot()) {
                dot = new Dot(Dot.HEALTH_DOT, true);
                character.removeInputDot(dot);

            }
            if (item.getIsManaDot()) {
                dot = new Dot(Dot.MANA_DOT, true);
                character.removeInputDot(dot);

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
                // health and max health altered
                notifyChangeHealth(character.changeHealthMax(item.getHealthChange()));
            }
            if (item.getManaChange() != 0) {
                setMaxMana(character.getMaxMana() + item.getManaChange());
                character.changeManaMax(item.getManaChange());
                // max mana altered
                notifyChangeMana(item.getManaChange());
            }
            // special changes
            SpecialEffect special;
            if (item.getIsConfuse()) {
                special = new SpecialEffect(SpecialEffect.CONFUSE);
                character.addNewSpecial(special);
            }
            if (item.getIsStun()) {
                special = new SpecialEffect(SpecialEffect.STUN);
                character.addNewSpecial(special);
            }
            if (item.getIsSilence()) {
                special = new SpecialEffect(SpecialEffect.SILENCE);
                character.addNewSpecial(special);
            }
            if (item.getIsInvisible()) {
                special = new SpecialEffect(SpecialEffect.INVISIBILITY);
                character.addNewSpecial(special);
            }
            if (item.getIsInvincible()) {
                special = new SpecialEffect(SpecialEffect.INVINCIBILITY);
                character.addNewSpecial(special);
            }
            // DOT changes
            Dot dot;
            if (item.getIsFire()) {
                dot = new Dot(Dot.FIRE, true);
                character.addNewDot(dot);
            }
            if (item.getIsBleed()) {
                dot = new Dot(Dot.BLEED, true);
                character.addNewDot(dot);
            }
            if (item.getIsPoison()) {
                dot = new Dot(Dot.POISON, true);
                character.addNewDot(dot);
            }
            if (item.getIsFrostBurn()) {
                dot = new Dot(Dot.FROSTBURN, true);
                character.addNewDot(dot);
            }
            if (item.getIsHealthDot()) {
                dot = new Dot(Dot.HEALTH_DOT, true);
                character.addNewDot(dot);
            }
            if (item.getIsManaDot()) {
                dot = new Dot(Dot.MANA_DOT, true);
                character.addNewDot(dot);
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
        if (item.getHealthChange() != 0) {
            // health and max health altered
            notifyChangeHealth(character.changeHealthCurr(item.getHealthChange()));
        }
        if (item.getManaChange() != 0) {
            int manaChange = character.changeManaCurr(item.getManaChange());
            // only mana altered
            notifyChangeMana(manaChange);
        }
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


    /**
     * Set an indefinite increase to health or mana
     * @param tempBar   TempBar object to apply to health or mana change
     */
    public void setIndefiniteBarIncr(TempBar tempBar) {
        if (tempBar.getType().equals(TempBar.TEMP_HEALTH)) {
            setIndefiniteHealthIncr(tempBar);
        } else {
            setIndefiniteManaIncr(tempBar);
        }
    }
    // set an indefinite health increase (max and curr health change)
    private void setIndefiniteHealthIncr(TempBar tempBar) {
        setMaxHealth(getMaxHealth() + tempBar.getAmount());
        character.changeHealthMax(tempBar.getAmount());
        // max health changed
        notifyChangeHealth(tempBar.getAmount());
    }
    // set an indefinite mana increase (max and curr mana change)
    private void setIndefiniteManaIncr(TempBar tempBar) {
        setMaxMana(getMaxMana() + tempBar.getAmount());
        character.changeManaMax(tempBar.getAmount());
        // max mana altered
        notifyChangeMana(tempBar.getAmount());
    }


        // misc
    private ObservableField<Integer> goldObserve = new ObservableField<>();
    public ObservableField<Integer> getGoldObserve() {
        return goldObserve;
    }
    @Bindable
    public String getGold() {
        return String.valueOf(character.getGold());
    }
    public void setGold(int gold) {
        character.setGold(gold);
        notifyPropertyChanged(BR.gold);
    }
    /**
     * change the gold by amount and update it with observable fields and data binding
     * @param amount    amount to change gold by
     */
    @SuppressLint("UseValueOf")
    public void goldChange(int amount) {
        Integer goldChange  = character.goldChange(amount);
        notifyPropertyChanged(BR.gold);
        goldObserve.set(new Integer(goldChange));
    }

    private ObservableField<Integer> expObserve = new ObservableField<>();
    public ObservableField<Integer> getExpObserve() {
        return expObserve;
    }
    @Bindable
    public String getExp() {
        return String.valueOf(character.getExp());
    }
    public void addExp(int amount) {
        character.addExp(amount);
        notifyPropertyChanged(BR.exp);
        expObserve.set(new Integer(amount));
    }


    @Bindable
    public int getDrawableResID() {
        return character.getDrawableResID();
    }
    @Bindable
    public int getDrawableDeadResID() {
        return character.getDrawableDeadResID();
    }


    public Character getCharacter() {
        return character;
    }
    public int getEncounterState() {
        return character.getEncounterState();
    }
    /**
     * Sets the encounter state back to outdoor encounters.
     */
    public void setStateToOutdoor() {
        character.setEncounterState(Character.OUTDOOR_STATE);
    }

    /**
     * Sets the encounter state back to outdoor encounters.
     */
    public void setStateToMultiDungeon() {
        character.setEncounterState(Character.MULTI_DUNGEON_STATE);
    }

    /**
     * Sets the encounter state back to outdoor encounters.
     */
    public void setStateToCombatDungeon() {
        character.setEncounterState(Character.COMBAT_DUNGEON_STATE);
    }
    @Bindable
    public int getDistance() {
        return character.getDistance();
    }
    public void incrementDistance() {
        character.incrementDistance();
        notifyPropertyChanged(BR.distance);
    }
    public void setDungeonCounter(int counter) {
        character.setDungeonCounter(counter);
    }
    public int getDungeonCounter() {
        return character.getDungeonCounter();
    }
    public void decrementDungeonCounter() {
        character.decrementDungeonCounter();
    }
    public int getDungeonLength() {
        return character.getDungeonLength();
    }
}
