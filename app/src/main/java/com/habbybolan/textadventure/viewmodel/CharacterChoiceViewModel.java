package com.habbybolan.textadventure.viewmodel;

import android.content.Context;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.habbybolan.textadventure.BR;
import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.model.characterentity.Character;
import com.habbybolan.textadventure.model.inventory.Ability;
import com.habbybolan.textadventure.model.inventory.Item;
import com.habbybolan.textadventure.model.inventory.weapon.Weapon;
import com.habbybolan.textadventure.repository.LocallySavedFiles;
import com.habbybolan.textadventure.repository.database.LootInventory;

/*
viewModel that deals with the CharacterChoiceActivity UI data
 */
public class CharacterChoiceViewModel extends BaseObservable {

    public static final String wizard = "Wizard";
    public static final String paladin = "Paladin";
    public static final String archer = "Archer";
    public static final String warrior = "Warrior";

    private String className;

    private int strValue;
    private int intValue;
    private int conValue;
    private int spdValue;

    private String healthValue;
    private String manaValue;

    private String abilityName;
    private String weaponName;
    private String attackName;
    private String sAttackName;

    private int characterPortrait;

    // Wizard starting inventory
    private Ability wizardAbility;
    private Weapon wizardWeapon;
    private Item wizardItem;

    // Warrior starting inventory
    private Ability warriorAbility;
    private Weapon warriorWeapon;
    private Item warriorItem;

    // Paladin starting inventory
    private Ability paladinAbility;
    private Weapon paladinWeapon;
    private Item paladinItem;

    // Archer starting inventory
    private Ability archerAbility;
    private Weapon archerWeapon;
    private Item archerItem;


    // ApplicationContext
    private Context context;

    public CharacterChoiceViewModel(Context context) {
        this.context = context;
        setUpStartingInventory();
        selectCharacter(wizard);
    }

    /**
     * Sets up all starting Abilities, weapons, and items, stored in fields.
     */
    private void setUpStartingInventory() {
        LootInventory lootInventory = new LootInventory(context);
        wizardAbility = lootInventory.getAbilityFromID(context.getResources().getInteger(R.integer.Wizard_Start_Ability));
        wizardWeapon = lootInventory.getWeaponFromID(context.getResources().getInteger(R.integer.Wizard_Start_Weapon));
        wizardItem = lootInventory.getItemFromID(context.getResources().getInteger(R.integer.Wizard_Start_Item));

        warriorAbility = lootInventory.getAbilityFromID(context.getResources().getInteger(R.integer.Warrior_Start_Ability));
        warriorWeapon = lootInventory.getWeaponFromID(context.getResources().getInteger(R.integer.Warrior_Start_Weapon));
        warriorItem = lootInventory.getItemFromID(context.getResources().getInteger(R.integer.Warrior_Start_Item));

        paladinAbility = lootInventory.getAbilityFromID(context.getResources().getInteger(R.integer.Paladin_Start_Ability));
        paladinWeapon = lootInventory.getWeaponFromID(context.getResources().getInteger(R.integer.Paladin_Start_Weapon));
        paladinItem = lootInventory.getItemFromID(context.getResources().getInteger(R.integer.Paladin_Start_Item));

        archerAbility = lootInventory.getAbilityFromID(context.getResources().getInteger(R.integer.Archer_Start_Ability));
        archerWeapon = lootInventory.getWeaponFromID(context.getResources().getInteger(R.integer.Archer_Start_Weapon));
        archerItem = lootInventory.getItemFromID(context.getResources().getInteger(R.integer.Archer_Start_Item));
    }

    // create and saves a new character given the class name
    public void saveNewCharacter() {
        LocallySavedFiles save = new LocallySavedFiles(context);
        save.saveNewCharacterLocally(className);
    }

    @Bindable
    public int getStrValue() {
        return strValue;
    }
    private void setStrValue(int strValue) {
        this.strValue = strValue;
        notifyPropertyChanged(BR.strValue);
    }

    @Bindable
    public int getIntValue() {
        return intValue;
    }
    private void setIntValue(int intValue) {
        this.intValue = intValue;
        notifyPropertyChanged(BR.intValue);
    }

    @Bindable
    public int getConValue() {
        return conValue;
    }
    public void setConValue(int conValue) {
        this.conValue = conValue;
        notifyPropertyChanged(BR.conValue);
    }

    @Bindable
    public int getSpdValue() {
        return spdValue;
    }
    public void setSpdValue(int spdValue) {
        this.spdValue = spdValue;
        notifyPropertyChanged(BR.spdValue);
    }

    @Bindable
    public int getCharacterPortrait() {
        return characterPortrait;
    }
    private void setCharacterPortrait(int characterPortrait) {
        this.characterPortrait = characterPortrait;
        notifyPropertyChanged(BR.characterPortrait);
    }

    @Bindable
    public String getHealthValue() {
        return healthValue;
    }
    private void setHealthValue(String healthValue) {
        this.healthValue = healthValue;
        notifyPropertyChanged(BR.healthValue);
    }

    @Bindable
    public String getManaValue() {
        return manaValue;
    }
    private void setManaValue(String manaValue) {
        this.manaValue = manaValue;
        notifyPropertyChanged(BR.manaValue);
    }

    @Bindable
    public String getAbilityName() {
        return abilityName;
    }
    private void setAbilityName(String abilityName) {
        this.abilityName = abilityName;
        notifyPropertyChanged(BR.abilityName);
    }

    @Bindable
    public String getWeaponName() {
        return weaponName;
    }
    private void setWeaponName(String weaponName) {
        this.weaponName = weaponName;
        notifyPropertyChanged(BR.weaponName);
    }

    @Bindable
    public String getAttackName() {
        return attackName;
    }
    private void setAttackName(String attackName) {
        this.attackName = attackName;
        notifyPropertyChanged(BR.attackName);
    }

    @Bindable
    public String getSAttackName() {
        return sAttackName;
    }
    private void setSAttackName(String sAttackName) {
        this.sAttackName = sAttackName;
        notifyPropertyChanged(BR.sAttackName);
    }

    public String getClassName() {
        return className;
    }

    public void selectCharacter(String character) {
        switch (character) {
            case wizard:
                changeToWizard();
                break;
            case paladin:
                changeToPaladin();
                break;
            case archer:
                changeToArcher();
                break;
            case warrior:
                changeToWarrior();
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    // helper to set up the views for the wizard
    private void changeToWizard()  {
        className = wizard;
        setStrValue(R.string.Wizard_Start_Str);
        setIntValue(R.string.Wizard_Start_Int);
        setConValue(R.string.Wizard_Start_Con);
        setSpdValue(R.string.Wizard_Start_Spd);
        setCharacterPortrait(R.drawable.wizard);
        int conValue = Integer.parseInt(context.getResources().getString(R.string.Wizard_Start_Con));
        setHealthValue(String.valueOf(conValue * Character.HEALTH_CON_MULTIPLIER + Character.BASE_HEALTH));
        int intValue = Integer.parseInt(context.getResources().getString(R.string.Wizard_Start_Int));
        setManaValue(String.valueOf(intValue * Character.MANA_INT_MULTIPLIER + Character.BASE_MANA));

        setAbilityName(wizardAbility.getName());
        setWeaponName(wizardWeapon.getName());
        setAttackName(wizardWeapon.getAttack().getAttackName());
        setSAttackName(wizardWeapon.getSpecialAttack().getSpecialAttackName());
    }

    // helper to set up the views for the Paladin
    private void changeToPaladin() {
        className = paladin;
        setStrValue(R.string.Paladin_Start_Str);
        setIntValue(R.string.Paladin_Start_Int);
        setConValue(R.string.Paladin_Start_Con);
        setSpdValue(R.string.Paladin_Start_Spd);
        setCharacterPortrait(R.drawable.paladin);
        int conValue = Integer.parseInt(context.getResources().getString(R.string.Paladin_Start_Con));
        setHealthValue(String.valueOf(conValue * Character.HEALTH_CON_MULTIPLIER + Character.BASE_HEALTH));
        int intValue = Integer.parseInt(context.getResources().getString(R.string.Paladin_Start_Int));
        setManaValue(String.valueOf(intValue * Character.MANA_INT_MULTIPLIER + Character.BASE_MANA));

        setAbilityName(paladinAbility.getName());
        setWeaponName(paladinWeapon.getName());
        setAttackName(paladinWeapon.getAttack().getAttackName());
        setSAttackName(paladinWeapon.getSpecialAttack().getSpecialAttackName());
    }

    // helper to set up the views for the Archer
    private void changeToArcher() {
        className = archer;
        setStrValue(R.string.Archer_Start_Str);
        setIntValue(R.string.Archer_Start_Int);
        setConValue(R.string.Archer_Start_Con);
        setSpdValue(R.string.Archer_Start_Spd);
        setCharacterPortrait(R.drawable.archer);
        int conValue = Integer.parseInt(context.getResources().getString(R.string.Archer_Start_Con));
        setHealthValue(String.valueOf(conValue * Character.HEALTH_CON_MULTIPLIER + Character.BASE_HEALTH));
        int intValue = Integer.parseInt(context.getResources().getString(R.string.Archer_Start_Int));
        setManaValue(String.valueOf(intValue * Character.MANA_INT_MULTIPLIER + Character.BASE_MANA));

        setAbilityName(archerAbility.getName());
        setWeaponName(archerWeapon.getName());
        setAttackName(archerWeapon.getAttack().getAttackName());
        setSAttackName(archerWeapon.getSpecialAttack().getSpecialAttackName());
    }

    // helper to set up the views for the Warrior
    private void changeToWarrior() {
        className = warrior;
        setStrValue(R.string.Warrior_Start_Str);
        setIntValue(R.string.Warrior_Start_Int);
        setConValue(R.string.Warrior_Start_Con);
        setSpdValue(R.string.Warrior_Start_Spd);
        setCharacterPortrait(R.drawable.warrior);
        int conValue = Integer.parseInt(context.getResources().getString(R.string.Warrior_Start_Con));
        setHealthValue(String.valueOf(conValue * Character.HEALTH_CON_MULTIPLIER + Character.BASE_HEALTH));
        int intValue = Integer.parseInt(context.getResources().getString(R.string.Warrior_Start_Int));
        setManaValue(String.valueOf(intValue * Character.MANA_INT_MULTIPLIER + Character.BASE_MANA));

        setAbilityName(warriorAbility.getName());
        setWeaponName(warriorWeapon.getName());
        setAttackName(warriorWeapon.getAttack().getAttackName());
        setSAttackName(warriorWeapon.getSpecialAttack().getSpecialAttackName());
    }
}
