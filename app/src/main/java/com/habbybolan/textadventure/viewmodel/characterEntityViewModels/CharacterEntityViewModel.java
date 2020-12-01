package com.habbybolan.textadventure.viewmodel.characterEntityViewModels;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;

import com.habbybolan.textadventure.BR;
import com.habbybolan.textadventure.model.characterentity.CharacterEntity;
import com.habbybolan.textadventure.model.effects.Dot;
import com.habbybolan.textadventure.model.effects.Effect;
import com.habbybolan.textadventure.model.effects.SpecialEffect;
import com.habbybolan.textadventure.model.effects.TempBar;
import com.habbybolan.textadventure.model.effects.TempStat;
import com.habbybolan.textadventure.model.inventory.Ability;
import com.habbybolan.textadventure.model.inventory.weapon.Attack;
import com.habbybolan.textadventure.model.inventory.weapon.SpecialAttack;
import com.habbybolan.textadventure.model.inventory.weapon.Weapon;

import java.util.ArrayList;

public abstract class CharacterEntityViewModel extends BaseObservable {

    // The player character or Enemy Character
    CharacterEntity characterEntity;

    // ** Abilities **

    /**
     * Apply the ability from the attacker to this characterEntity
     * @param ability       The Ability to attack this characterEntity with
     * @param attacker      The CharacterEntity using the ability
     */
    public void applyAbility(Ability ability, CharacterEntity attacker, CharacterEntityViewModel enemyLeft, CharacterEntityViewModel enemyRight) {
        characterEntity.applyAbility(ability, attacker);
        notifyStatChange();
        notifyChangeHealth();
        notifyChangeMana();
        ability.setActionUsed();
        if (enemyLeft != null) enemyLeft.applyAbilitySplash(ability, attacker);
        if (enemyRight != null) enemyRight.applyAbilitySplash(ability, attacker);
    }

    /**
     * Applies the splash damage and effects to this CharacterEntity.
     * @param ability   The ability to apply the splash damage and effects from
     * @param attacker  The CharacterEntity using the Ability
     */
    void applyAbilitySplash(Ability ability, CharacterEntity attacker) {
        characterEntity.applyAbilitySplash(ability, attacker);
        notifyStatChange();
        notifyChangeHealth();
        notifyChangeMana();
    }

    // ** Weapons **

    // apply the attack from the attacker

    /**
     * Apply the attack from the attacker to this characterEntity
     * @param attack        The attack to attack this characterEntity with
     * @param attacker      The CharacterEntity using the attack
     */
    public void applyAttack(Attack attack, CharacterEntity attacker) {
        characterEntity.applyAttack(attack, attacker);
        notifyChangeHealth();
        notifyChangeMana();
    }

    /**
     * Apply the special Attack from the attacker to this characterEntity
     * @param specialAttack     The special attack to attack this characterEntity with
     * @param attacker          The attacker using the special attack
     */
    public void applySpecialAttack(SpecialAttack specialAttack, CharacterEntity attacker, CharacterEntityViewModel enemyLeft, CharacterEntityViewModel enemyRight) {
        characterEntity.applySpecialAttack(specialAttack, attacker);
        notifyChangeHealth();
        notifyChangeMana();
        specialAttack.setActionUsed();
        if (enemyLeft != null) enemyLeft.applySpecialAttackSplash(specialAttack, attacker);
        if (enemyRight != null) enemyRight.applySpecialAttackSplash(specialAttack, attacker);
    }

    /**
     * Applies the splash damage
     */
    private void applySpecialAttackSplash(SpecialAttack specialAttack, CharacterEntity attacker) {
        characterEntity.applySpecialAttackSplash(specialAttack, attacker);
        notifyChangeHealth();
        notifyChangeMana();
    }

    // ** Dot Effects **

    public ObservableArrayList<Dot> getDotList() {
        return characterEntity.getDotList();
    }

    public void addInputDot(Dot dot) {
        characterEntity.addNewDot(dot);
    }

    /**
     * Apply dots effects to character and observe all removed if duration reaches 0.
     */
    public void applyDots() {
        characterEntity.applyDots();
        notifyChangeHealth();
    }

    // ** Special Effects **

    public ObservableArrayList<SpecialEffect> getSpecialList() {
        return characterEntity.getSpecialList();
    }
    public void addInputSpecial(SpecialEffect special) {
        characterEntity.addNewSpecial(special);
    }

    /**
     * Decrement the duration of the special effect applied. If it reaches 0, remove the
     * effects and observe the removed effect.
     */
    public void decrSpecialDuration() {
        characterEntity.decrSpecialDuration();
    }

    /**
     * Removes all special effects on character.
     */
    public void removeAllSpecialEffects() {
        characterEntity.removeAllSpecialEffects();
    }

    // ** Stat Changes **

    // STATS

    public ObservableArrayList<TempStat> getTempStatIncrObserver() {
        return characterEntity.getStatIncreaseList();
    }

    public ObservableArrayList<TempStat> getTempStatDecrObserver() {
        return characterEntity.getStatDecreaseList();
    }

    // Observer for when the stat value changes
    public ObservableField<TempStat>  getUpdateAllStatChange() {
        return updateAllStatChange;
    }
    private ObservableField<TempStat> updateAllStatChange = new ObservableField<>();

    // add a temporary stat to the stat incr/decr list
    public void addInputStat(TempStat tempStat) {
        if (tempStat.getAmount() > 0) {
            characterEntity.addNewStatIncrease(tempStat);
        } else {
            characterEntity.addNewStatDecrease(tempStat);
        }
        notifyStatChange();
    }

    private void notifyStatChange() {
        notifyPropertyChanged(BR.strength);
        notifyPropertyChanged(BR.strBase);
        notifyPropertyChanged(BR.intelligence);
        notifyPropertyChanged(BR.intBase);
        notifyPropertyChanged(BR.constitution);
        notifyPropertyChanged(BR.conBase);
        notifyPropertyChanged(BR.speed);
        notifyPropertyChanged(BR.spdBase);
        notifyPropertyChanged(BR.evasion);
        notifyPropertyChanged(BR.block);
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
        }
    }

    // decrement the temp incr and decr stat durations
    public void decrementTempStatDuration() {
        characterEntity.decrementTempStatIncrDuration();
        characterEntity.decrementTempStatDecrDuration();
    }

    // BARS

    // direct damage applied to player character
    public void damageCharacterEntity(int damageAmount) {
        characterEntity.damageTarget(damageAmount);
        notifyChangeHealth();
    }

    // decrement both the health and mana temp extra bars and observe any changes
    public void decrementTempExtraDuration() {
        characterEntity.decrementTempExtraDuration();
    }

    // given tempBar, add it to either TempExtraHealth or TempExtraMana list depending on type
    public void addNewTempBar(TempBar tempBar) {
        characterEntity.addNewTempExtraHealthMana(tempBar);
        if (tempBar.getType().equals(TempBar.TEMP_HEALTH)) {
            notifyPropertyChanged(BR.health);
            notifyPropertyChanged(BR.maxHealth);
        } else {
            notifyPropertyChanged(BR.mana);
            notifyPropertyChanged(BR.maxMana);
        }
    }

    public ObservableArrayList<TempBar> getHealthList() {
        return characterEntity.getTempHealthList();
    }

    public ObservableArrayList<TempBar> getManaList() {
        return characterEntity.getTempManaList();
    }


    // ** CHANGES**

    // Health mana changes

    public ObservableField<Integer> getHealthObserve() {
        return characterEntity.healthObserve;
    }
    @Bindable
    public int getHealth() {
        return characterEntity.getHealth();
    }
    // set the new health
    public void setHealth(int health) {
        // apply the change
        characterEntity.setHealth(health);
        notifyPropertyChanged(BR.health);
    }
    // notify healthChange to changeHealth
    public void notifyChangeHealth() {
        notifyPropertyChanged(BR.health);
        notifyPropertyChanged(BR.maxHealth);
    }
    @Bindable
    public int getMaxHealth() {
        return characterEntity.getMaxHealth();
    }
    public void setMaxHealth(int maxHealth) {
        characterEntity.setMaxHealth(maxHealth);
    }

    public ObservableField<Integer> getManaObserve() {
        return characterEntity.manaObserve;
    }
    @Bindable
    public int getMana() {
        return characterEntity.getMana();
    }
    public void setMana(int mana) {
        // apply the change
        characterEntity.setMana(mana);
        notifyPropertyChanged(BR.mana);
    }
    // notify manaChange to changeMana
    public void notifyChangeMana() {
        notifyPropertyChanged(BR.mana);
        notifyPropertyChanged(BR.maxMana);
    }
    @Bindable
    public int getMaxMana() {
        return characterEntity.getMaxMana();
    }
    public void setMaxMana(int maxMana) {
        characterEntity.setMaxMana(maxMana);
    }

    // Dots
    @Bindable
    public boolean getIsFire() {
        return characterEntity.getIsFire();
    }
    public void setIsFire(boolean isFire) {
        characterEntity.setIsFire(isFire);
    }
    @Bindable
    public boolean getIsBleed() {
        return characterEntity.getIsBleed();
    }
    public void setIsBleed(boolean isBleed) {
        characterEntity.setIsBleed(isBleed);
    }
    @Bindable
    public boolean getIsPoison() {
        return characterEntity.getIsPoison();
    }
    public void setIsPoison(boolean isPoison) {
        characterEntity.setIsPoison(isPoison);
    }
    @Bindable
    public boolean getIsFrostBurn() {
        return characterEntity.getIsFrostBurn();
    }
    public void setIsFrostBurn(boolean isFrostBurn) {
        characterEntity.setIsFrostBurn(isFrostBurn);
    }
    @Bindable
    public boolean getIsHealDot() {
        return characterEntity.getIsHealDot();
    }
    public void setIsHealDot(boolean isHealDot) {
        characterEntity.setIsHealDot(isHealDot);
    }
    @Bindable
    public boolean getIsManaDot() {
        return characterEntity.getIsManaDot();
    }
    public void setIsManaDot(boolean isManaDot) {
        characterEntity.setIsManaDot(isManaDot);
    }

    // special changes

    @Bindable
    public boolean getIsStun() {
        return characterEntity.getIsStun();
    }
    public void setIsStun(boolean isStun) {
        characterEntity.setIsStun(isStun);
        notifyPropertyChanged(BR.isStun);
    }
    @Bindable
    public boolean getIsConfuse() {
        return characterEntity.getIsConfuse();
    }
    public void setIsConfuse(boolean isConfuse) {
        characterEntity.setIsConfuse(isConfuse);
        notifyPropertyChanged(BR.isConfuse);
    }
    @Bindable
    public boolean getIsInvincible() {
        return characterEntity.getIsInvincible();
    }
    public void setIsInvincible(boolean isInvincible) {
        characterEntity.setIsInvincible(isInvincible);
        notifyPropertyChanged(BR.isInvincible);
    }
    @Bindable
    public boolean getIsInvisible() {
        return characterEntity.getIsInvisible();
    }
    public void setIsInvisible(boolean isInvisible) {
        characterEntity.setIsInvisible(isInvisible);
        notifyPropertyChanged(BR.isInvisible);
    }
    @Bindable
    public boolean getIsSilence() {
        return characterEntity.getIsSilence();
    }
    public void setIsSilence(boolean isSilence) {
        characterEntity.setIsSilence(isSilence);
        notifyPropertyChanged(BR.isSilence);
    }

    // stat changes

    @Bindable
    public int getStrBase() {
        return characterEntity.getStrBase();
    }
    public void setStrBase(int strBase) {
        characterEntity.setStrBase(strBase);
        notifyPropertyChanged(BR.strBase);
    }
    @Bindable
    public int getIntBase() {
        return characterEntity.getIntBase();
    }
    public void setIntBase(int intBase) {
        characterEntity.setIntBase(intBase);
        notifyPropertyChanged(BR.intBase);
    }
    @Bindable
    public int getConBase() {
        return characterEntity.getConBase();
    }
    public void setConBase(int conBase) {
        characterEntity.setConBase(conBase);
        notifyPropertyChanged(BR.conBase);
    }
    @Bindable
    public int getSpdBase() {
        return characterEntity.getSpdBase();
    }
    public void setSpdBase(int spdBase) {
        characterEntity.setSpdBase(spdBase);
        notifyPropertyChanged(BR.spdBase);
    }

    @Bindable
    public int getStrength() {
        return characterEntity.getStrength();
    }
    public void setStrength(int strength) {
        updateAllStatChange.set(new TempStat(Effect.STR, strength - characterEntity.getStrength()));
        characterEntity.setStrength(strength);
        notifyPropertyChanged(BR.strength);
    }
    @Bindable
    public int getIntelligence() {
        return characterEntity.getIntelligence();
    }
    public void setIntelligence(int intelligence) {
        updateAllStatChange.set(new TempStat(Effect.INT, intelligence - characterEntity.getIntelligence()));
        characterEntity.setIntelligence(intelligence);
        notifyPropertyChanged(BR.intelligence);
    }
    @Bindable
    public int getConstitution() {
        return characterEntity.getConstitution();
    }
    public void setConstitution(int constitution) {
        updateAllStatChange.set(new TempStat(Effect.CON, constitution - characterEntity.getConstitution()));
        characterEntity.setConstitution(constitution);
        notifyPropertyChanged(BR.constitution);
    }
    @Bindable
    public int getSpeed() {
        return characterEntity.getSpeed();
    }
    public void setSpeed(int speed) {
        updateAllStatChange.set(new TempStat(Effect.SPD, speed - characterEntity.getSpeed()));
        characterEntity.setSpeed(speed);
        notifyPropertyChanged(BR.speed);
    }
    @Bindable
    public int getEvasion() {
        return characterEntity.getEvasion();
    }
    @Bindable
    public int getBlock() {
        return characterEntity.getBlock();
    }

    public ObservableArrayList<Ability> getAbilities() {
        return characterEntity.getAbilities();
    }
    public ArrayList<Weapon> getWeapons() {
        return characterEntity.getWeapons();
    }

    public boolean isEnoughMana(int amount) {
        return characterEntity.isEnoughMana(amount);
    }

    public CharacterEntity getCharacterEntity() {
        return characterEntity;
    }
    public boolean getIsCharacter() {
        return characterEntity.getIsCharacter();
    }
    public boolean getIsAlive() {
        return characterEntity.getIsAlive();
    }
    public void decrCooldowns() {
        characterEntity.decrCooldowns();
    }
    public int getDrawableResID() {
        return characterEntity.getDrawableResID();
    }
    public int getDrawableDeadResID() {
        return characterEntity.getDrawableDeadResID();
    }
}
