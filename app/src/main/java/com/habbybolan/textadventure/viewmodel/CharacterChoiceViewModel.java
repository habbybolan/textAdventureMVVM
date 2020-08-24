package com.habbybolan.textadventure.viewmodel;

import android.content.Context;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.habbybolan.textadventure.BR;
import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.model.characterentity.Character;
import com.habbybolan.textadventure.model.inventory.Ability;
import com.habbybolan.textadventure.model.inventory.weapon.Weapon;
import com.habbybolan.textadventure.repository.database.DatabaseAdapter;

import java.util.concurrent.ExecutionException;

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

    // ApplicationContext
    private Context context;

    public CharacterChoiceViewModel(Context context) {
        this.context = context;
        selectCharacter(wizard);
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
        try {
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
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // helper to set up the views for the wizard
    private void changeToWizard() throws ExecutionException, InterruptedException {
        className = wizard;
        setStrValue(R.string.Wizard_Start_Str);
        setIntValue(R.string.Wizard_Start_Int);
        setConValue(R.string.Wizard_Start_Con);
        setSpdValue(R.string.Wizard_Start_Spd);
        setCharacterPortrait(R.drawable.wizard_background_select);
        int conValue = Integer.parseInt(context.getResources().getString(R.string.Wizard_Start_Con));
        setHealthValue(String.valueOf(conValue * Character.HEALTH_CON_MULTIPLIER + Character.BASE_HEALTH));
        int intValue = Integer.parseInt(context.getResources().getString(R.string.Wizard_Start_Int));
        setManaValue(String.valueOf(intValue * Character.MANA_INT_MULTIPLIER + Character.BASE_MANA));

        // database accesses
        DatabaseAdapter mDbHelper = new DatabaseAdapter(context);

        Ability ability = new Ability(Integer.parseInt(context.getResources().getString(R.string.Wizard_Start_Ability)), mDbHelper);
        setAbilityName(ability.getName());
        Weapon weapon = new Weapon(Integer.parseInt(context.getResources().getString(R.string.Wizard_Start_Weapon)), mDbHelper);
        setWeaponName(weapon.getName());
        setAttackName(weapon.getAttack().getAttackName());
        setSAttackName(weapon.getSpecialAttack().getSpecialAttackName());
        mDbHelper.close();
    }

    // helper to set up the views for the Paladin
    private void changeToPaladin() throws ExecutionException, InterruptedException {
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

        // database accesses
        DatabaseAdapter mDbHelper = new DatabaseAdapter(context);

        Ability ability = new Ability(Integer.parseInt(context.getResources().getString(R.string.Paladin_Start_Ability)), mDbHelper);
        setAbilityName(ability.getName());
        Weapon weapon = new Weapon(Integer.parseInt(context.getResources().getString(R.string.Paladin_Start_Weapon)), mDbHelper);
        setWeaponName(weapon.getName());
        setAttackName(weapon.getAttack().getAttackName());
        setSAttackName(weapon.getSpecialAttack().getSpecialAttackName());
        mDbHelper.close();
    }

    // helper to set up the views for the Archer
    private void changeToArcher() throws ExecutionException, InterruptedException {
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

        // database accesses
        DatabaseAdapter mDbHelper = new DatabaseAdapter(context);

        Ability ability = new Ability(Integer.parseInt(context.getResources().getString(R.string.Archer_Start_Ability)), mDbHelper);
        setAbilityName(ability.getName());
        Weapon weapon = new Weapon(Integer.parseInt(context.getResources().getString(R.string.Archer_Start_Weapon)), mDbHelper);
        setWeaponName(weapon.getName());
        setAttackName(weapon.getAttack().getAttackName());
        setSAttackName(weapon.getSpecialAttack().getSpecialAttackName());
        mDbHelper.close();
    }

    // helper to set up the views for the Warrior
    private void changeToWarrior() throws ExecutionException, InterruptedException {
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

        // database accesses
        DatabaseAdapter mDbHelper = new DatabaseAdapter(context);

        Ability ability = new Ability(Integer.parseInt(context.getResources().getString(R.string.Warrior_Start_Ability)), mDbHelper);
        setAbilityName(ability.getName());
        Weapon weapon = new Weapon(Integer.parseInt(context.getResources().getString(R.string.Warrior_Start_Weapon)), mDbHelper);
        setWeaponName(weapon.getName());
        setAttackName(weapon.getAttack().getAttackName());
        setSAttackName(weapon.getSpecialAttack().getSpecialAttackName());
        mDbHelper.close();
    }
}
