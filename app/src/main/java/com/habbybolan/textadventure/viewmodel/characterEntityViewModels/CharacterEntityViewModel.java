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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class CharacterEntityViewModel extends BaseObservable {

    CharacterEntity characterEntity;

    // ** Abilities **

    // Observable for adding and deleting abilities and updating RecyclerView in CharacterFragment
    private ObservableField<Ability> abilityObserverAdd = new ObservableField<>();
    public ObservableField<Ability> getAbilityObserverAdd() {
        return abilityObserverAdd;
    }
    private ObservableField<Ability> abilityObserverRemove = new ObservableField<>();
    public ObservableField<Ability> getAbilityObserverRemove() {
        return abilityObserverRemove;
    }
    // applies the ability from the attacker
    public void applyAbility(Ability ability, CharacterEntity attacker) {
        // todo: use attacker stats to alter ability effectiveness
        Random rand = new Random();
        if (ability.getMinDamage() != 0) {
            notifyChangeHealth(characterEntity.damageTarget(characterEntity.getRandomAmount(ability.getMinDamage(), ability.getMaxDamage())));
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
            int healthChanged = characterEntity.increaseHealth(randHealthChange);
            notifyChangeHealth(healthChanged);
        }
        if (ability.getManaMin() != 0) {
            int randManaChange = rand.nextInt(ability.getManaMax() - ability.getManaMin()) + ability.getManaMin();
            int manaChanged = characterEntity.changeMana(randManaChange);
            notifyChangeMana(manaChanged);
        }
        // stat increases
        TempStat tempStat;
        if (ability.getStrIncrease() != 0) {
            tempStat = new TempStat(TempStat.STR, ability.getDuration(), ability.getStrIncrease());
            characterEntity.addNewStatIncrease(tempStat);
            notifyStatChange(tempStat);
            tempStatIncrObserver.add(tempStat);
        }
        if (ability.getIntIncrease() != 0) {
            tempStat = new TempStat(TempStat.INT, ability.getDuration(), ability.getIntIncrease());
            characterEntity.addNewStatIncrease(tempStat);
            notifyStatChange(tempStat);
            tempStatIncrObserver.add(tempStat);
        }
        if (ability.getConIncrease() != 0) {
            tempStat = new TempStat(TempStat.CON, ability.getDuration(), ability.getConIncrease());
            characterEntity.addNewStatIncrease(tempStat);
            notifyStatChange(tempStat);
            tempStatIncrObserver.add(tempStat);
        }
        if (ability.getSpdIncrease() != 0) {
            tempStat = new TempStat(TempStat.SPD, ability.getDuration(), ability.getSpdIncrease());
            characterEntity.addNewStatIncrease(tempStat);
            notifyStatChange(tempStat);
            tempStatIncrObserver.add(tempStat);
        }
        if (ability.getEvadeIncrease() != 0) {
            tempStat = new TempStat(TempStat.EVASION, ability.getDuration(), ability.getEvadeIncrease());
            characterEntity.addNewStatIncrease(tempStat);
            notifyStatChange(tempStat);
            tempStatIncrObserver.add(tempStat);
        }
        if (ability.getBlockIncrease() != 0) {
            tempStat = new TempStat(TempStat.BLOCK, ability.getDuration(), ability.getBlockIncrease());
            characterEntity.addNewStatIncrease(tempStat);
            notifyStatChange(tempStat);
            tempStatIncrObserver.add(tempStat);
        }
        // stat decreases
        if (ability.getStrDecrease() != 0) {
            tempStat = new TempStat(TempStat.STR, ability.getDuration(), ability.getStrDecrease());
            characterEntity.addNewStatDecrease(tempStat);
            notifyStatChange(tempStat);
            tempStatDecrObserver.add(tempStat);
        }
        if (ability.getIntDecrease() != 0) {
            tempStat = new TempStat(TempStat.INT, ability.getDuration(), ability.getIntDecrease());
            characterEntity.addNewStatDecrease(tempStat);
            notifyStatChange(tempStat);
            tempStatDecrObserver.add(tempStat);
        }
        if (ability.getConDecrease() != 0) {
            tempStat = new TempStat(TempStat.CON, ability.getDuration(), ability.getConDecrease());
            characterEntity.addNewStatDecrease(tempStat);
            notifyStatChange(tempStat);
            tempStatDecrObserver.add(tempStat);
        }
        if (ability.getSpdDecrease() != 0) {
            tempStat = new TempStat(TempStat.SPD, ability.getDuration(), ability.getSpdDecrease());
            characterEntity.addNewStatDecrease(tempStat);
            notifyStatChange(tempStat);
            tempStatDecrObserver.add(tempStat);
        }
        if (ability.getEvadeDecrease() != 0) {
            tempStat = new TempStat(TempStat.EVASION, ability.getDuration(), ability.getEvadeDecrease());
            characterEntity.addNewStatDecrease(tempStat);
            notifyStatChange(tempStat);
            tempStatDecrObserver.add(tempStat);
        }
        if (ability.getBlockDecrease() != 0) {
            tempStat = new TempStat(TempStat.BLOCK, ability.getDuration(), ability.getBlockDecrease());
            characterEntity.addNewStatDecrease(tempStat);
            notifyStatChange(tempStat);
            tempStatDecrObserver.add(tempStat);
        }
        // temp extra health
        TempBar tempBar;
        if (ability.getTempExtraHealth() != 0) {
            tempBar = new TempBar(TempBar.TEMP_HEALTH, ability.getDuration(), ability.getTempExtraHealth());
            barObserver.add(tempBar);
        }
        ability.setActionUsed();
    }

    // ** Weapons **

    // apply the attack from the attacker
    public void applyAttack(Attack attack, CharacterEntity attacker) {
        Random random = new Random();
        // get a random amount of damage given a range
        int damage = random.nextInt(attack.getDamageMax() - attack.getDamageMin()) + attack.getDamageMin();
        notifyChangeHealth(characterEntity.damageTarget(damage));
    }
    // apply the special attack from the attacker
    public void applySpecialAttack(SpecialAttack specialAttack, CharacterEntity attacker) {
        if (specialAttack.getAbility() != null) {
            applyAbility(specialAttack.getAbility(), attacker);
        }
        if (specialAttack.getAoe() > 0) {
            characterEntity.doAoeStuff(); // todo: aoe
        }
        if (specialAttack.getDamageMin() != 0) {
            // get a random amount of damage given a range
            int damage = characterEntity.getRandomAmount(specialAttack.getDamageMin(), specialAttack.getDamageMax());
            notifyChangeHealth(characterEntity.damageTarget(damage));
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

    // ** Stat Changes **

    // STATS

    public ObservableArrayList<TempStat> getTempStatIncrObserver() {
        return tempStatIncrObserver;
    }
    private ObservableArrayList<TempStat> tempStatIncrObserver = new ObservableArrayList<>();

    public ObservableArrayList<TempStat> getTempStatDecrObserver() {
        return tempStatDecrObserver;
    }
    private ObservableArrayList<TempStat> tempStatDecrObserver = new ObservableArrayList<>();

    // Observer for when the stat value changes
    public ObservableField<TempStat>  getUpdateAllStatChange() {
        return updateAllStatChange;
    }
    private ObservableField<TempStat> updateAllStatChange = new ObservableField<>();

    // add a temporary stat to the stat incr/decr list
    public void addInputStat(TempStat tempStat) {
        if (tempStat.getAmount() > 0) {
            characterEntity.addNewStatIncrease(tempStat);
            tempStatIncrObserver.add(tempStat);
        } else {
            characterEntity.addNewStatDecrease(tempStat);
            tempStatDecrObserver.add(tempStat);
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
        ArrayList<TempStat> statIncrRemoved = characterEntity.decrementTempStatIncrDuration();
        for (TempStat tempStat : statIncrRemoved) {
            tempStatIncrObserver.remove(tempStat);
            notifyStatChange(tempStat);
        }
        ArrayList<TempStat> statDecrRemoved = characterEntity.decrementTempStatDecrDuration();
        for (TempStat tempStat : statDecrRemoved) {
            tempStatDecrObserver.remove(tempStat);
            notifyStatChange(tempStat);
        }
    }

    // BARS

    // direct damage applied to player character
    public void damageCharacterEntity(int damageAmount) {
        notifyChangeHealth(characterEntity.damageTarget(damageAmount));
    }

    private ObservableArrayList<TempBar> barObserver = new ObservableArrayList<>();
    public ObservableArrayList<TempBar> getBarObserver() {
        return barObserver;
    }

    // decrement both the health and mana temp extra bars and observe any changes
    public void decrementTempExtraDuration() {
        decrementTempExtraHealthDuration();
        decrementTempExtraManaDuration();
    }
    // decrement the temp extra health duration and observe any changes
    private void decrementTempExtraHealthDuration() {
        int health = characterEntity.getHealth();
        ArrayList<TempBar> tempBarsRemoved = characterEntity.decrementTempExtraHealthDuration();
        int healthChange = characterEntity.getHealth() - health;
        for (TempBar tempBar : tempBarsRemoved) {
            barObserver.remove(tempBar);
        }
        if (healthChange != 0) notifyChangeHealth(healthChange);
    }
    // decrement the temp extra mana duration and observe any changes
    private void decrementTempExtraManaDuration() {
        int mana = characterEntity.getMana();
        ArrayList<TempBar> tempBarsRemoved = characterEntity.decrementTempExtraManaDuration();
        int manaChange = characterEntity.getMana() - mana;
        for (TempBar tempBar : tempBarsRemoved) {
            barObserver.remove(tempBar);
        }
        if (manaChange != 0) notifyChangeMana(manaChange);
    }

    // given tempBar, add it to either TempExtraHealth or TempExtraMana list depending on type
    public void addTempHealthMana(TempBar tempBar) {
        if (tempBar.getType().equals(TempBar.TEMP_HEALTH)) {
            addTempHealthList(tempBar);
        } else {
            addTempManaList(tempBar);
        }
        barObserver.add(tempBar);
    }

    public List<TempBar> getTempHealthList() {
        return characterEntity.getTempHealthList();
    }
    public void removeZeroTempHealthList() {
        ArrayList<TempBar> tempBarsRemoved = characterEntity.removeZeroTempHealthList();
        for (TempBar tempBar : tempBarsRemoved) {
            barObserver.remove(tempBar);
        }
    }
    public void addTempHealthList(TempBar tempExtraHealth) {
        characterEntity.addTempHealthList(tempExtraHealth);
        notifyPropertyChanged(BR.health);
        notifyPropertyChanged(BR.maxHealth);
    }
    public int getTempExtraHealth() {
        return characterEntity.getTempExtraHealth();
    }
    public void setTempExtraHealth(int tempExtraHealth) {
        characterEntity.setTempExtraHealth(tempExtraHealth);
    }

    public List<TempBar> getTempManaList() {
        return characterEntity.getTempManaList();
    }
    public void removeZeroTempManaList() {
        characterEntity.removeZeroTempManaList();
    }
    public void addTempManaList(TempBar tempExtraMana) {
        characterEntity.addTempManaList(tempExtraMana);
        notifyPropertyChanged(BR.mana);
        notifyPropertyChanged(BR.maxMana);
    }
    public int getTempExtraMana() {
        return characterEntity.getTempExtraMana();
    }
    public void setTempExtraMana(int tempExtraMana) {
        characterEntity.setTempExtraMana(tempExtraMana);
    }

    // ** CHANGES**

    // Health mana changes

    @Bindable
    private
    ObservableField<Integer> healthObserve = new ObservableField<>();
    public ObservableField<Integer> getHealthObserve() {
        return healthObserve;
    }
    @Bindable
    public String getHealth() {
        return String.valueOf(characterEntity.getHealth());
    }
    // set the new health
    public void setHealth(int health) {
        // observe the health change
        healthObserve.set(new Integer(health - characterEntity.getHealth()));
        // apply the change
        characterEntity.setHealth(health);
        notifyPropertyChanged(BR.health);
    }
    // notify healthChange to changeHealth
    public void notifyChangeHealth(int changeHealth) {
        healthObserve.set(new Integer(changeHealth));
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

    private ObservableField<Integer> manaObserve = new ObservableField<>();
    public ObservableField<Integer> getManaObserve() {
        return manaObserve;
    }
    @Bindable
    public String getMana() {
        return String.valueOf(characterEntity.getMana());
    }
    public void setMana(int mana) {
        // observe mana changes
        manaObserve.set(new Integer(mana - characterEntity.getMana()));
        // apply the change
        characterEntity.setMana(mana);
        notifyPropertyChanged(BR.mana);
    }
    // notify manaChange to changeMana
    public void notifyChangeMana(int changeMana) {
        manaObserve.set(new Integer(changeMana));
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
}
