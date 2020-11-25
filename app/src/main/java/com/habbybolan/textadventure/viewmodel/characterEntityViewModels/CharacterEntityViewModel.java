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
import com.habbybolan.textadventure.model.effects.TempBarFactory;
import com.habbybolan.textadventure.model.effects.TempStat;
import com.habbybolan.textadventure.model.inventory.Ability;
import com.habbybolan.textadventure.model.inventory.weapon.Attack;
import com.habbybolan.textadventure.model.inventory.weapon.SpecialAttack;
import com.habbybolan.textadventure.model.inventory.weapon.Weapon;

import java.util.ArrayList;
import java.util.Random;

public abstract class CharacterEntityViewModel extends BaseObservable {

    // The player character or Enemy Character
    CharacterEntity characterEntity;

    // ** Abilities **

    // applies the ability from the attacker
    public void applyAbility(Ability ability, CharacterEntity attacker) {
        Random rand = new Random();

        if (ability.getMinDamage() != 0) {
            int damage =  rand.nextInt(ability.getMaxDamage() - ability.getMinDamage()) + ability.getMinDamage();
            if (ability.getIsStrScaled())
                damage += attacker.getStrength();
            if (ability.getIsIntScaled())
                damage += attacker.getIntelligence();
            characterEntity.damageTarget(damage);
        }
        if (ability.getDamageAoe() != 0) characterEntity.doAoeStuff(); // todo: aoe
        // specials
        SpecialEffect special;
        if (ability.getIsConfuse()) {
            special = new SpecialEffect(SpecialEffect.CONFUSE, ability.getDuration());
            characterEntity.addNewSpecial(special);
        }
        if (ability.getIsStun()) {
            special = new SpecialEffect(SpecialEffect.STUN, ability.getDuration());
            characterEntity.addNewSpecial(special);
        }
        if (ability.getIsInvincibility()) {
            special = new SpecialEffect(SpecialEffect.INVINCIBILITY, ability.getDuration());
            characterEntity.addNewSpecial(special);
        }
        if (ability.getIsSilence()) {
            special = new SpecialEffect(SpecialEffect.SILENCE, ability.getDuration());
            characterEntity.addNewSpecial(special);
        }
        if (ability.getIsInvisible()) {
            special = new SpecialEffect(SpecialEffect.INVISIBILITY, ability.getDuration());
            characterEntity.addNewSpecial(special);
        }
        // DOT
        Dot dot;
        if (ability.getIsFire()) {
            dot = new Dot(Dot.FIRE, false);
            characterEntity.addNewDot(dot);
        }
        if (ability.getIsPoison()) {
            dot = new Dot(Dot.POISON, false);
            characterEntity.addNewDot(dot);
        }
        if (ability.getIsBleed()) {
            dot = new Dot(Dot.BLEED, false);
            characterEntity.addNewDot(dot);
        }
        if (ability.getIsFrostBurn()) {
            dot = new Dot(Dot.FROSTBURN, false);
            characterEntity.addNewDot(dot);
        }
        if (ability.getIsHealDot()) {
            dot = new Dot(Dot.HEALTH_DOT, false);
            characterEntity.addNewDot(dot);
        }
        if (ability.getIsManaDot()) {
            dot = new Dot(Dot.MANA_DOT, false);
            characterEntity.addNewDot(dot);
        }
        // direct heal/mana
        if (ability.getHealMin() != 0) {
            int randHealthChange = rand.nextInt(ability.getHealMax() - ability.getHealMin()) + ability.getHealMin();
            characterEntity.changeHealthCurr(randHealthChange);
        }
        if (ability.getManaMin() != 0) {
            int randManaChange = rand.nextInt(ability.getManaMax() - ability.getManaMin()) + ability.getManaMin();
            characterEntity.changeManaCurr(randManaChange);
            notifyChangeMana();
        }
        // stat increases
        TempStat tempStat;
        if (ability.getStrIncrease() != 0) {
            tempStat = new TempStat(TempStat.STR, ability.getDuration(), ability.getStrIncrease());
            characterEntity.addNewStatIncrease(tempStat);
            notifyStatChange(tempStat);
        }
        if (ability.getIntIncrease() != 0) {
            tempStat = new TempStat(TempStat.INT, ability.getDuration(), ability.getIntIncrease());
            characterEntity.addNewStatIncrease(tempStat);
            notifyStatChange(tempStat);
        }
        if (ability.getConIncrease() != 0) {
            tempStat = new TempStat(TempStat.CON, ability.getDuration(), ability.getConIncrease());
            characterEntity.addNewStatIncrease(tempStat);
            notifyStatChange(tempStat);
        }
        if (ability.getSpdIncrease() != 0) {
            tempStat = new TempStat(TempStat.SPD, ability.getDuration(), ability.getSpdIncrease());
            characterEntity.addNewStatIncrease(tempStat);
            notifyStatChange(tempStat);
        }
        if (ability.getEvadeIncrease() != 0) {
            tempStat = new TempStat(TempStat.EVASION, ability.getDuration(), ability.getEvadeIncrease());
            characterEntity.addNewStatIncrease(tempStat);
            notifyStatChange(tempStat);
        }
        if (ability.getBlockIncrease() != 0) {
            tempStat = new TempStat(TempStat.BLOCK, ability.getDuration(), ability.getBlockIncrease());
            characterEntity.addNewStatIncrease(tempStat);
            notifyStatChange(tempStat);
        }
        // stat decreases
        if (ability.getStrDecrease() != 0) {
            tempStat = new TempStat(TempStat.STR, ability.getDuration(), ability.getStrDecrease());
            characterEntity.addNewStatDecrease(tempStat);
            notifyStatChange(tempStat);
        }
        if (ability.getIntDecrease() != 0) {
            tempStat = new TempStat(TempStat.INT, ability.getDuration(), ability.getIntDecrease());
            characterEntity.addNewStatDecrease(tempStat);
            notifyStatChange(tempStat);
        }
        if (ability.getConDecrease() != 0) {
            tempStat = new TempStat(TempStat.CON, ability.getDuration(), ability.getConDecrease());
            characterEntity.addNewStatDecrease(tempStat);
            notifyStatChange(tempStat);
        }
        if (ability.getSpdDecrease() != 0) {
            tempStat = new TempStat(TempStat.SPD, ability.getDuration(), ability.getSpdDecrease());
            characterEntity.addNewStatDecrease(tempStat);
            notifyStatChange(tempStat);
        }
        if (ability.getEvadeDecrease() != 0) {
            tempStat = new TempStat(TempStat.EVASION, ability.getDuration(), ability.getEvadeDecrease());
            characterEntity.addNewStatDecrease(tempStat);
            notifyStatChange(tempStat);
        }
        if (ability.getBlockDecrease() != 0) {
            tempStat = new TempStat(TempStat.BLOCK, ability.getDuration(), ability.getBlockDecrease());
            characterEntity.addNewStatDecrease(tempStat);
            notifyStatChange(tempStat);
        }
        // temp extra health
        TempBar tempBar;
        if (ability.getTempExtraHealth() != 0) {
            tempBar = TempBarFactory.createTempHealth(ability.getDuration(), ability.getTempExtraHealth());
            characterEntity.addTempHealthList(tempBar);
        }
        notifyChangeHealth();
        notifyChangeMana();
        ability.setActionUsed();
    }

    // ** Weapons **

    // apply the attack from the attacker

    /**
     * Apply the attack from the attacker.
     * @param attack        The CharacterEntity using the attack.
     * @param attacker      The CharacterEntity who the attack is being used on.
     */
    public void applyAttack(Attack attack, CharacterEntity attacker) {
        Random random = new Random();
        // get a random amount of damage given a range
        int damage = random.nextInt(attack.getDamageMax() - attack.getDamageMin()) + attack.getDamageMin();
        characterEntity.damageTarget(damage);
        notifyChangeHealth();
    }
    // apply the special attack from the attacker
    public void applySpecialAttack(SpecialAttack specialAttack, CharacterEntity attacker) {
        if (specialAttack.getAbility() != null) {
            applyAbility(specialAttack.getAbility(), attacker);
        }
        if (specialAttack.getAoe() > 0) {
            // todo: aoe
            characterEntity.doAoeStuff();
        }
        if (specialAttack.getDamageMin() != 0) {
            // get a random amount of damage given a range
            int damage = characterEntity.getRandomAmount(specialAttack.getDamageMin(), specialAttack.getDamageMax());
            characterEntity.damageTarget(damage);
            notifyChangeHealth();
        }
        specialAttack.setActionUsed();
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
    public int getEvasionBase() {
        return characterEntity.getEvasionBase();
    }
    public void setEvasionBase(int evasionBase) {
        characterEntity.setEvasionBase(evasionBase);
        notifyPropertyChanged(BR.evasionBase);
    }
    @Bindable
    public int getBlockBase() {
        return characterEntity.getBlockBase();
    }
    public void setBlockBase(int blockBase) {
        characterEntity.setBlockBase(blockBase);
        notifyPropertyChanged(BR.blockBase);
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
    public void setEvasion(int evasion) {
        updateAllStatChange.set(new TempStat(Effect.EVASION, evasion - characterEntity.getEvasion()));
        characterEntity.setEvasion(evasion);
        notifyPropertyChanged(BR.evasion);
    }
    @Bindable
    public int getBlock() {
        return characterEntity.getBlock();
    }
    public void setBlock(int block) {
        updateAllStatChange.set(new TempStat(Effect.BLOCK, block - characterEntity.getBlock()));
        characterEntity.setBlock(block);
        notifyPropertyChanged(BR.block);
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
    public int getDrawableIconResID() {
        return characterEntity.getDrawableIconResID();
    }
    public int getDrawableIconDeadResID() {
        return characterEntity.getDrawableIconDeadResID();
    }
}
