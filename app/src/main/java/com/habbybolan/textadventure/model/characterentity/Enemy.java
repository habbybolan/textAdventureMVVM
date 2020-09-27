package com.habbybolan.textadventure.model.characterentity;

import android.content.Context;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.model.effects.Dot;
import com.habbybolan.textadventure.model.effects.SpecialEffect;
import com.habbybolan.textadventure.model.effects.TempBar;
import com.habbybolan.textadventure.model.effects.TempStat;
import com.habbybolan.textadventure.model.inventory.Ability;
import com.habbybolan.textadventure.model.inventory.Inventory;
import com.habbybolan.textadventure.model.inventory.Item;
import com.habbybolan.textadventure.model.inventory.weapon.Attack;
import com.habbybolan.textadventure.model.inventory.weapon.SpecialAttack;
import com.habbybolan.textadventure.model.inventory.weapon.Weapon;
import com.habbybolan.textadventure.repository.database.DatabaseAdapter;
import com.habbybolan.textadventure.viewmodel.characterEntityViewModels.CharacterViewModel;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class Enemy extends CharacterEntity {

    public static final int MAX_WEAPONS = 1;
    public static final int MAX_ABILITIES = 2;
    public static final int MAX_TIER = 3;


    // Character info
    private Weapon weapon;
    // temp extra health


    private ArrayList<Ability> abilities = new ArrayList<>();
    private String[] arrayOfAbilities = new String[MAX_ABILITIES];

    private String type;

    private int numStatPoints;

    public Enemy(int numStatPoints, String type, int difficulty, Context context) throws ExecutionException, InterruptedException {
        isCharacter = false;
        // percentage of stat points for enemy compared to the main character
        double PERCENT_OF_STATS = 0.75;
        this.numStatPoints = (int) Math.rint(numStatPoints * PERCENT_OF_STATS);
        this.type = type;

        // choose 2 random stats for the enemy
        Random rand = new Random();
        // str = 0, int = 1, con = 2, spd = 3
        int firstStat = rand.nextInt(4);
        // second stat
        int secondStat = rand.nextInt(4);
        // randomize the amount to increase the first stat by
        int amountToIncreaseFirstStat = rand.nextInt(this.numStatPoints) + 1;
        // set the amount for the 2 variables
        selectStats(firstStat, amountToIncreaseFirstStat);
        selectStats(secondStat, numStatPoints - amountToIncreaseFirstStat);

        // set enemy health
        // todo: how to decide health amount
        health = 10;
        maxHealth = 10;

        // choose a random weapon
        // todo: how to decide tier?
        DatabaseAdapter db = new DatabaseAdapter(context);
        this.weapon = db.getRandomWeaponOfTier(1);
        // choose MAX_ABILITIES random abilities
        abilities = db.getRandomAbilities(MAX_ABILITIES);
        db.close();

        drawableResID = R.drawable.skeleton_icon;
        drawableDeadResID = R.drawable.skeleton_dead;
    }


    // helper for constructor to set up enemy stats
    private void selectStats(int stat, int amount) {
        switch (stat) {
            case 0:
                setStrength(amount);
                setStrBase(amount);
                break;
            case 1:
                setIntelligence(amount);
                setIntBase(amount);
                break;
            case 2:
                setConstitution(amount);
                setConBase(amount);
                break;
            case 3:
                setSpeed(amount);
                setSpdBase(amount);
                break;
            default:
                break;
        }
    }

    // ** ITEMS **

    // use the ability attached to the item
    public void applyItem(Item item, CharacterEntity attacker) {
        if (item.getAbility() == null) throw new IllegalArgumentException();
        applyAbility(item.getAbility(), attacker);
    }

    // ** WEAPONS **

    // apply the attack from the attacker
    public void applyAttack(Attack attack, CharacterEntity attacker) {
        Random random = new Random();
        int damage = random.nextInt(attack.getDamageMax() - attack.getDamageMin()) + attack.getDamageMin();
        changeHealth(damage);
    }
    // apply the special attack from the attacker
    public void applySpecialAttack(SpecialAttack specialAttack, CharacterEntity attacker) {
        if (specialAttack.getAbility() != null) {
            applyAbility(specialAttack.getAbility(), attacker);
        }
        if (specialAttack.getAoe() > 0) {
            doAoeStuff(); // todo: aoe
        }
        if (specialAttack.getDamageMin() != 0) {
            damageTarget(getRandomAmount(specialAttack.getDamageMin(), specialAttack.getDamageMax()));
        }
    }

    // returns a random action for the enemy to perform
    public Inventory getRandomAction() {
        // action can be either attack, special attack, ability 1, or ability 2
        Random random = new Random();
        int actionNum = random.nextInt(4);
        switch (actionNum) {
            case 0:
                // weapon attack
                return weapon.getAttack();
            case 1:
                // weapon special attack
                return weapon.getSpecialAttack();
            case 2:
                // ability 1
                return abilities.get(0);
            case 3:
                // ability 2
                return abilities.get(1);
            default:
                throw new IllegalStateException();

        }
    }

    // ***ABILITIES**

    // apply ability to enemy from attacker
    public void applyAbility(Ability ability, CharacterEntity attacker) {
        CharacterViewModel characterVM = CharacterViewModel.getInstance();
        // todo: scale with Intelligence
        if (ability.getMinDamage() != 0) damageTarget(getRandomAmount(ability.getMinDamage(), ability.getMaxDamage()));
        if (ability.getDamageAoe() != 0) doAoeStuff(); // todo: aoe
        // specials
        if (ability.getIsConfuse()) addNewSpecial(new SpecialEffect(SpecialEffect.CONFUSE, ability.getDuration()));
        if (ability.getIsStun()) addNewSpecial(new SpecialEffect(SpecialEffect.STUN, ability.getDuration()));
        if (ability.getIsInvincibility()) addNewSpecial(new SpecialEffect(SpecialEffect.INVINCIBILITY, ability.getDuration()));
        if (ability.getIsSilence()) addNewSpecial(new SpecialEffect(SpecialEffect.SILENCE, ability.getDuration()));
        if (ability.getIsInvisible()) addNewSpecial(new SpecialEffect(SpecialEffect.INVISIBILITY, ability.getDuration()));
        // DOT
        if (ability.getIsFire()) addNewDot(new Dot(Dot.FIRE, false));
        if (ability.getIsPoison()) addNewDot(new Dot(Dot.POISON, false));
        if (ability.getIsBleed()) addNewDot(new Dot(Dot.BLEED, false));
        if (ability.getIsFrostBurn()) addNewDot(new Dot(Dot.FROSTBURN, false));
        if (ability.getIsHealDot()) addNewDot(new Dot(Dot.HEALTH_DOT, false));
        if (ability.getIsManaDot()) addNewDot(new Dot(Dot.MANA_DOT, false));
        // direct heal/mana
        if (ability.getHealMin() != 0) changeHealth(getRandomAmount(ability.getHealMin(), ability.getHealMax()));
        if (ability.getManaMin() != 0) changeMana(getRandomAmount(ability.getManaMin(), ability.getManaMax()));
        // stat increases
        if (ability.getStrIncrease() != 0) addNewStatIncrease(new TempStat(STR, ability.getDuration(), ability.getStrIncrease()));
        if (ability.getIntIncrease() != 0) addNewStatIncrease(new TempStat(INT, ability.getDuration(), ability.getIntIncrease()));
        if (ability.getConIncrease() != 0) addNewStatIncrease(new TempStat(CON, ability.getDuration(), ability.getConIncrease()));
        if (ability.getSpdIncrease() != 0) addNewStatIncrease(new TempStat(SPD, ability.getDuration(), ability.getSpdIncrease()));
        if (ability.getEvadeIncrease() != 0) addNewStatIncrease(new TempStat(EVASION, ability.getDuration(), ability.getEvadeIncrease()));
        if (ability.getBlockIncrease() != 0) addNewStatIncrease(new TempStat(BLOCK, ability.getDuration(), ability.getBlockIncrease()));
        // stat decreases
        if (ability.getStrDecrease() != 0) addNewStatDecrease(new TempStat(STR, ability.getDuration(), ability.getStrDecrease()));
        if (ability.getIntDecrease() != 0) addNewStatDecrease(new TempStat(INT, ability.getDuration(), ability.getIntDecrease()));
        if (ability.getConDecrease() != 0) addNewStatDecrease(new TempStat(CON, ability.getDuration(), ability.getConDecrease()));
        if (ability.getSpdDecrease() != 0) addNewStatDecrease(new TempStat(SPD, ability.getDuration(), ability.getSpdDecrease()));
        if (ability.getEvadeDecrease() != 0) addNewStatDecrease(new TempStat(EVASION, ability.getDuration(), ability.getEvadeDecrease()));
        if (ability.getBlockDecrease() != 0) addNewStatDecrease(new TempStat(BLOCK, ability.getDuration(), ability.getBlockDecrease()));
        // temp extra health
        if (ability.getTempExtraHealth() != 0) addNewTempExtraHealthMana(new TempBar(TEMP_HEALTH, ability.getDuration(), ability.getTempExtraHealth()));
    }

    // decrement the cooldown on all abilities w/ cooldown >0
    public void decrCooldowns() {
        // check the ability in special attack if it exists
        if (weapon.getSpecialAttack().getAbility() != null && weapon.getSpecialAttack().getAbility().getCooldownLeft() > 0) {
            weapon.getSpecialAttack().getAbility().setCooldownCurr(weapon.getSpecialAttack().getAbility().getCooldownLeft()-1);
        }
        for (int i = 0; i < MAX_ABILITIES; i++) {
            if (abilities.get(i).getCooldownLeft() > 0) {
                abilities.get(i).setCooldownCurr(abilities.get(i).getCooldownLeft()-1);
            }
        }
    }

    public String getType() {
        return type;
    }
}
