package com.habbybolan.textadventure.model.characterentity;

import android.content.Context;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.model.effects.Dot;
import com.habbybolan.textadventure.model.effects.Effect;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private int difficulty;

    private String type;

    private int numStatPoints;

    public Enemy(int numStatPoints, String type, int difficulty, int ID, Context context) throws ExecutionException, InterruptedException {
        isCharacter = false;
        this.difficulty = difficulty;
        // percentage of stat points for enemy compared to the main character
        double PERCENT_OF_STATS = 0.75;
        this.numStatPoints = (int) Math.rint(numStatPoints * PERCENT_OF_STATS);
        this.type = type;
        this.ID = ID;

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
        health = 1;
        maxHealth = 1;

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

    /**
     * Constructor for reading a saved JSON String of an Enemy
     * @param JSONEnemy     The JSON String of the saved Enemy data
     */
    public Enemy(String JSONEnemy) {
        JSONObject enemyObject;
        isCharacter = false;
        try {
            enemyObject = new JSONObject(JSONEnemy);
            // enemy specific
            difficulty = enemyObject.getInt("difficulty");
            ID = enemyObject.getInt("id");
            isAlive = enemyObject.getBoolean("isAlive");
            type = enemyObject.getString("type");
            // stats
            strength = enemyObject.getInt("str");
            strBase = enemyObject.getInt("strBase");
            strIncrease = enemyObject.getInt("strIncrease");
            strDecrease = enemyObject.getInt("strDecrease");
            intelligence = enemyObject.getInt("int");
            intBase = enemyObject.getInt("intBase");
            intIncrease = enemyObject.getInt("intIncrease");
            intDecrease = enemyObject.getInt("intDecrease");
            constitution = enemyObject.getInt("con");
            conBase = enemyObject.getInt("conBase");
            conIncrease = enemyObject.getInt("conIncrease");
            conDecrease = enemyObject.getInt("conDecrease");
            speed = enemyObject.getInt("spd");
            spdBase = enemyObject.getInt("spdBase");
            spdIncrease = enemyObject.getInt("spdIncrease");
            spdDecrease = enemyObject.getInt("spdDecrease");
            numStatPoints = strBase + intBase + conBase + spdBase;
            // misc
            level = enemyObject.getInt("level");
            // bars
            health = enemyObject.getInt("health");
            maxHealth = enemyObject.getInt("maxHealth");
            mana = enemyObject.getInt("mana");
            maxMana = enemyObject.getInt("maxMana");
            // abilities
            numAbilities = 0;
            JSONArray abilitiesArray = enemyObject.getJSONArray("abilities");
            for (int i= 0; i < abilitiesArray.length(); i++) {
                abilities.add(new Ability(abilitiesArray.getString(i)));
                numAbilities++;

            }
            // weapons
            String weaponsString = enemyObject.getString("weapon");
            numWeapons = 1;
            weapon = new Weapon(weaponsString);

            // items
            numItems = 0;
            JSONArray itemsArray = enemyObject.getJSONArray("items");
            for (int i = 0; i < itemsArray.length(); i++) {
                items.add(new Item(itemsArray.getString(i)));
                numItems++;
            }

            // DOTS
            isFire = enemyObject.getBoolean(Effect.FIRE);
            isBleed = enemyObject.getBoolean(Effect.BLEED);
            isPoison = enemyObject.getBoolean(Effect.POISON);
            isFrostBurn = enemyObject.getBoolean(Effect.FROSTBURN);
            isHealDot = enemyObject.getBoolean(Effect.HEALTH_DOT);
            isManaDot = enemyObject.getBoolean(Effect.MANA_DOT);
            JSONArray dotList = enemyObject.getJSONArray("dotList");
            for (int i = 0; i < dotList.length(); i++) {
                JSONArray dot = (JSONArray) dotList.get(i);
                this.dotList.add(new Dot(dot.getString(0), dot.getInt(1)));
            }
            // SPECIAL
            isStun = enemyObject.getBoolean(Effect.STUN);
            isConfuse = enemyObject.getBoolean(Effect.CONFUSE);
            isSilence = enemyObject.getBoolean(Effect.SILENCE);
            isInvincible = enemyObject.getBoolean(Effect.INVINCIBILITY);
            isInvisible = enemyObject.getBoolean(Effect.INVISIBILITY);
            JSONArray specialList = enemyObject.getJSONArray("specialList");
            for (int i = 0; i < specialList.length(); i++) {
                JSONArray special = (JSONArray) specialList.get(i);
                this.specialList.add(new SpecialEffect(special.getString(0), special.getInt(1)));
            }
            // tempHealth
            JSONArray tempHealthArray = enemyObject.getJSONArray("tempHealthList");
            for (int i = 0; i < tempHealthArray.length(); i++) {
                JSONArray tempHealth = (JSONArray) tempHealthArray.get(i);
                int duration = tempHealth.getInt(0);
                int amount = tempHealth.getInt(1);
                TempBar tempBar = new TempBar(CharacterEntity.TEMP_HEALTH, duration, amount);
                tempHealthList.add(tempBar);
            }
            // tempMana
            JSONArray tempManaArray = enemyObject.getJSONArray("tempManaList");
            for (int i = 0; i < tempManaArray.length(); i++) {
                JSONArray tempMana = (JSONArray) tempManaArray.get(i);
                int duration = tempMana.getInt(0);
                int amount = tempMana.getInt(1);
                TempBar tempBar = new TempBar(CharacterEntity.TEMP_MANA, duration, amount);
                tempManaList.add(tempBar);
            }
            // stat Increase
            JSONArray statIncreaseArray = enemyObject.getJSONArray("statIncreaseList");
            for (int i = 0; i < statIncreaseArray.length(); i++) {
                JSONArray statIncrease = (JSONArray) statIncreaseArray.get(i);
                String type = statIncrease.getString(0);
                int duration = statIncrease.getInt(1);
                int amount = statIncrease.getInt(2);
                TempStat tempStat = new TempStat(type, duration, amount);
                statIncreaseList.add(tempStat);
            }
            // stat Decrease
            JSONArray statDecreaseArray = enemyObject.getJSONArray("statDecreaseList");
            for (int i = 0; i < statDecreaseArray.length(); i++) {
                JSONArray statDecrease = (JSONArray) statDecreaseArray.get(i);
                String type = statDecrease.getString(0);
                int duration = statDecrease.getInt(1);
                int amount = statDecrease.getInt(2);
                TempStat tempStat = new TempStat(type, duration, amount);
                statDecreaseList.add(tempStat);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

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
        damageTarget(damage);
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
        if (ability.getHealMin() != 0) increaseHealth(getRandomAmount(ability.getHealMin(), ability.getHealMax()));
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
        weapon.getSpecialAttack().decrementCooldownCurr();
        for (int i = 0; i < MAX_ABILITIES; i++) {
            abilities.get(i).decrementCooldownCurr();
        }
    }

    @Override
    public JSONObject serializeToJSON() throws JSONException {
        JSONObject JSONEnemy = new JSONObject();
        JSONEnemy.put("isCharacter", false);
        JSONEnemy.put("difficulty", difficulty);
        JSONEnemy.put("id", ID);
        JSONEnemy.put("isAlive", isAlive);
        JSONEnemy.put("type", type);

        JSONEnemy.put("str", strength); // str
        JSONEnemy.put("strBase", strBase); // base str
        JSONEnemy.put("strIncrease", strIncrease);
        JSONEnemy.put("strDecrease", strDecrease);
        JSONEnemy.put("int", intelligence); // int
        JSONEnemy.put("intBase", intBase); // base int
        JSONEnemy.put("intIncrease", intIncrease);
        JSONEnemy.put("intDecrease", intDecrease);
        JSONEnemy.put("con", constitution); // con
        JSONEnemy.put("conBase", conBase); // base con
        JSONEnemy.put("conIncrease", conIncrease);
        JSONEnemy.put("conDecrease", conDecrease);
        JSONEnemy.put("spd", speed); // spd
        JSONEnemy.put("spdBase", spdBase); // base spd
        JSONEnemy.put("spdIncrease", spdIncrease);
        JSONEnemy.put("spdDecrease", spdDecrease);
        // abilities
        JSONArray abilitiesArray = new JSONArray();
        for (int i = 0; i < MAX_ABILITIES; i++) {
            if (abilities.size() > i)
                abilitiesArray.put(abilities.get(i).serializeToJSON());
        }
        JSONEnemy.put("abilities", abilitiesArray);
        // weapon
        JSONEnemy.put("weapon", weapon.serializeToJSON());
        // bars
        JSONEnemy.put("health", health);
        JSONEnemy.put("maxHealth", maxHealth);
        JSONEnemy.put("mana", mana);
        JSONEnemy.put("maxMana", maxMana);
        // misc
        JSONEnemy.put("level",level);
        // Items
        JSONArray itemsArray = new JSONArray();
        for (int i = 0; i < Character.MAX_ITEMS; i++) {
            if (items.size() > i)
                itemsArray.put(items.get(i).serializeToJSON());
        }
        JSONEnemy.put("items", itemsArray);
        // specials
        JSONEnemy.put(Effect.STUN, isStun);
        JSONEnemy.put(Effect.CONFUSE, isConfuse);
        JSONEnemy.put(Effect.INVINCIBILITY, isInvincible);
        JSONEnemy.put(Effect.SILENCE, isSilence);
        JSONEnemy.put(Effect.INVISIBILITY, isInvisible);
        JSONArray specialArray = new JSONArray();
        for (SpecialEffect appliedSpecial: specialList) {
            JSONArray special = new JSONArray();
            special.put(appliedSpecial.getType());
            special.put(appliedSpecial.getDuration());
            specialArray.put(special);
        }
        JSONEnemy.put("specialList", specialArray);
        // temp health
        JSONEnemy.put("tempExtraHealth", tempExtraHealth);
        JSONArray tempHealthArray = new JSONArray(); // <key, value>
        for (int i = 0; i < tempHealthList.size(); i++) {
            JSONArray tempHealth = new JSONArray(); // <duration, amount>
            tempHealth.put(tempHealthList.get(i).getDuration());
            tempHealth.put(tempHealthList.get(i).getAmount());
            tempHealthArray.put(tempHealth);
        }
        JSONEnemy.put("tempHealthList", tempHealthArray);
        // temp mana
        JSONEnemy.put("tempExtraMana", tempExtraMana);
        JSONArray tempManaArray = new JSONArray(); // <key, value>
        for (int i = 0; i < tempManaList.size(); i++) {
            JSONArray tempMana = new JSONArray(); // <duration, amount>
            tempMana.put(tempManaList.get(i).getDuration());
            tempMana.put(tempManaList.get(i).getAmount());
            tempManaArray.put(tempMana);
        }
        JSONEnemy.put("tempManaList", tempManaArray);
        // stat increase
        JSONArray statIncreaseArray = new JSONArray(); // <stat, duration, amount>
        for (int i = 0; i < statIncreaseList.size(); i++) {
            JSONArray statIncrease = new JSONArray();
            statIncrease.put(statIncreaseList.get(i).getType());
            statIncrease.put(statIncreaseList.get(i).getDuration());
            statIncrease.put(statIncreaseList.get(i).getAmount());
            statIncreaseArray.put(statIncrease);
        }
        JSONEnemy.put("statIncreaseList", statIncreaseArray);
        // stat decrease
        JSONArray statDecreaseArray = new JSONArray(); // <stat, duration, amount>
        for (int i = 0; i < statDecreaseList.size(); i++) {
            JSONArray statDecrease = new JSONArray();
            statDecrease.put(statDecreaseList.get(i).getType());
            statDecrease.put(statDecreaseList.get(i).getDuration());
            statDecrease.put(statDecreaseList.get(i).getAmount());
            statDecreaseArray.put(statDecrease);
        }
        JSONEnemy.put("statDecreaseList", statDecreaseArray);
        // DOT
        JSONEnemy.put(Effect.BLEED, isBleed);
        JSONEnemy.put(Effect.POISON, isPoison);
        JSONEnemy.put(Effect.FIRE, isFire);
        JSONEnemy.put(Effect.FROSTBURN, isFrostBurn);
        JSONEnemy.put(Effect.HEALTH_DOT, isHealDot);
        JSONEnemy.put(Effect.MANA_DOT, isManaDot);
        JSONArray dotArray = new JSONArray(); // <key, value>
        for (Dot appliedDot: dotList) {
            JSONArray dot = new JSONArray();
            dot.put(appliedDot.getType());
            dot.put(appliedDot.getDuration());
            dotArray.put(dot);
        }
        JSONEnemy.put("dotList", dotArray);

        return JSONEnemy;
    }


    public String getType() {
        return type;
    }
}
