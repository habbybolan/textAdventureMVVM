package com.habbybolan.textadventure.model.characterentity;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.model.effects.Dot;
import com.habbybolan.textadventure.model.effects.Effect;
import com.habbybolan.textadventure.model.effects.SpecialEffect;
import com.habbybolan.textadventure.model.effects.TempBar;
import com.habbybolan.textadventure.model.effects.TempBarFactory;
import com.habbybolan.textadventure.model.effects.TempStat;
import com.habbybolan.textadventure.model.inventory.Ability;
import com.habbybolan.textadventure.model.inventory.Inventory;
import com.habbybolan.textadventure.model.inventory.weapon.Weapon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

public class Enemy extends CharacterEntity {

    public static final int MAX_WEAPONS = 1;
    public static final int MAX_TIER = 3;

    public static final String OGRE = "ogre";
    public static final String SPIDER = "spider";
    public static final String DEMON = "demon";
    public static final String SLIME = "slime";
    public static final String SKELETON = "skeleton";

    // Tables
    public static final String TABLE_ENEMY = "enemy";
    public static final String TABLE_ENEMY_ABILITY = "enemy_ability";
    public static final String TABLE_ENEMY_ABILITY_BRIDGE = "enemy_ability_bridge";
    // column names enemy_table
    public static final String ENEMY_ID = "enemy_id";
    public static final String ENEMY_ABILITY_ID = "enemy_ability_id";
    public static final String TYPE = "type";
    public static final String IS_WEAPON = "is_weapon";
    public static final String STR_PERCENT = "str_percent";
    public static final String INT_PERCENT = "int_percent";
    public static final String CON_PERCENT = "con_percent";
    public static final String SPD_PERCENT = "spd_percent";
    public static final String TIER = "tier";


    private ArrayList<Ability> abilities = new ArrayList<>();
    private int difficulty;

    private String type;
    private int numStatPoints;
    private boolean isWeapon;

    /*
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
    }*/

    public Enemy(String type, int tier, boolean isWeapon, int strPercent, int intPercent, int conPercent, int spdPercent, ArrayList<Ability> abilities, int numStatPoints, Weapon weapon) {
        this.type = type;
        this.difficulty = tier;
        this.isWeapon = isWeapon;
        strength = numStatPoints * (strPercent / 100);
        intelligence = numStatPoints * (intPercent / 100);
        constitution = numStatPoints * (conPercent / 100);
        speed = numStatPoints * (spdPercent / 100);
        this.abilities = abilities;
        weapons.add(weapon);
        health = 1;
        maxHealth = 1;
        setDrawables(type);
    }

    /**
     * Sets the image resource icon id given the type of enemy
     * @param type  Enemy type
     */
    private void setDrawables(String type) {
        switch (type) {
            case SPIDER:
                drawableResID = R.drawable.spider_icon;
                drawableDeadResID = R.drawable.skeleton_dead;
                break;
            case OGRE:
                drawableResID = R.drawable.ogre_icon;
                drawableDeadResID = R.drawable.skeleton_dead;
                break;
            case DEMON:
                drawableResID = R.drawable.demon_icon;
                drawableDeadResID = R.drawable.skeleton_dead;
                break;
            case SKELETON:
                drawableResID = R.drawable.skeleton_icon;
                drawableDeadResID = R.drawable.skeleton_dead;
                break;
            case SLIME:
                drawableResID = R.drawable.slime_icon;
                drawableDeadResID = R.drawable.skeleton_dead;
                break;
            default:
                throw new IllegalArgumentException(type + "is an incorrect enemy type");
        }
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
            setDrawables(type);

            // stats
            if (enemyObject.has("str")) strength = enemyObject.getInt("str");
            if (enemyObject.has("strBase")) strBase = enemyObject.getInt("strBase");
            if (enemyObject.has("strIncrease")) strIncrease = enemyObject.getInt("strIncrease");
            if (enemyObject.has("strDecrease")) strDecrease = enemyObject.getInt("strDecrease");
            if (enemyObject.has("int")) intelligence = enemyObject.getInt("int");
            if (enemyObject.has("intBase")) intBase = enemyObject.getInt("intBase");
            if (enemyObject.has("intIncrease")) intIncrease = enemyObject.getInt("intIncrease");
            if (enemyObject.has("intDecrease")) intDecrease = enemyObject.getInt("intDecrease");
            if (enemyObject.has("con")) constitution = enemyObject.getInt("con");
            if (enemyObject.has("conBase")) conBase = enemyObject.getInt("conBase");
            if (enemyObject.has("conIncrease")) conIncrease = enemyObject.getInt("conIncrease");
            if (enemyObject.has("conDecrease")) conDecrease = enemyObject.getInt("conDecrease");
            if (enemyObject.has("spd")) speed = enemyObject.getInt("spd");
            if (enemyObject.has("spdBase")) spdBase = enemyObject.getInt("spdBase");
            if (enemyObject.has("spdIncrease")) spdIncrease = enemyObject.getInt("spdIncrease");
            if (enemyObject.has("spdDecrease")) spdDecrease = enemyObject.getInt("spdDecrease");
            numStatPoints = strBase + intBase + conBase + spdBase;
            if (enemyObject.has("block")) strength = enemyObject.getInt("block");
            if (enemyObject.has("blockIncrease")) strength = enemyObject.getInt("blockIncrease");
            if (enemyObject.has("blockDecrease")) strength = enemyObject.getInt("blockDecrease");
            if (enemyObject.has("evasion")) strength = enemyObject.getInt("evasion");
            if (enemyObject.has("evasionIncrease")) strength = enemyObject.getInt("evasionIncrease");
            if (enemyObject.has("evasionDecrease")) strength = enemyObject.getInt("evasionDecrease");
            // misc
            if (enemyObject.has("level")) level = enemyObject.getInt("level");
            // bars
            health = enemyObject.getInt("health");
            maxHealth = enemyObject.getInt("maxHealth");
            mana = enemyObject.getInt("mana");
            maxMana = enemyObject.getInt("maxMana");
            // abilities
            numAbilities = 0;
            JSONArray abilitiesArray = enemyObject.getJSONArray("abilities");
            for (int i= 0; i < abilitiesArray.length(); i++) {
                String abilityString = abilitiesArray.getString(i);
                abilities.add(new Ability(abilityString));
                numAbilities++;

            }
            // weapons
            JSONArray weaponsArray = enemyObject.getJSONArray("weapons");
            numWeapons = 1;
            for (int i = 0; i < weaponsArray.length(); i++) {
                String weaponString = weaponsArray.getString(i);
                weapons.add(new Weapon(weaponString));
                numWeapons++;
            }

            // DOTS
            if (enemyObject.has(Effect.FIRE)) isFire = enemyObject.getBoolean(Effect.FIRE);
            if (enemyObject.has(Effect.BLEED)) isBleed = enemyObject.getBoolean(Effect.BLEED);
            if (enemyObject.has(Effect.POISON)) isPoison = enemyObject.getBoolean(Effect.POISON);
            if (enemyObject.has(Effect.FROSTBURN)) isFrostBurn = enemyObject.getBoolean(Effect.FROSTBURN);
            if (enemyObject.has(Effect.HEALTH_DOT)) isHealDot = enemyObject.getBoolean(Effect.HEALTH_DOT);
            if (enemyObject.has(Effect.MANA_DOT)) isManaDot = enemyObject.getBoolean(Effect.MANA_DOT);
            if (enemyObject.has("dotList")) {
                JSONArray dotList = enemyObject.getJSONArray("dotList");
                for (int i = 0; i < dotList.length(); i++) {
                    JSONArray dot = (JSONArray) dotList.get(i);
                    this.dotList.add(new Dot(dot.getString(0), dot.getInt(1)));
                }
            }
            // SPECIAL
            if (enemyObject.has(Effect.STUN)) isStun = enemyObject.getBoolean(Effect.STUN);
            if (enemyObject.has(Effect.CONFUSE)) isConfuse = enemyObject.getBoolean(Effect.CONFUSE);
            if (enemyObject.has(Effect.SILENCE)) isSilence = enemyObject.getBoolean(Effect.SILENCE);
            if (enemyObject.has(Effect.INVINCIBILITY)) isInvincible = enemyObject.getBoolean(Effect.INVINCIBILITY);
            if (enemyObject.has(Effect.INVISIBILITY)) isInvisible = enemyObject.getBoolean(Effect.INVISIBILITY);
            if (enemyObject.has("specialList")) {
                JSONArray specialList = enemyObject.getJSONArray("specialList");
                for (int i = 0; i < specialList.length(); i++) {
                    JSONArray special = (JSONArray) specialList.get(i);
                    this.specialList.add(new SpecialEffect(special.getString(0), special.getInt(1)));
                }
            }
            // tempHealth
            if (enemyObject.has("tempHealthList")) {
                JSONArray tempHealthArray = enemyObject.getJSONArray("tempHealthList");
                for (int i = 0; i < tempHealthArray.length(); i++) {
                    JSONArray tempHealth = (JSONArray) tempHealthArray.get(i);
                    int duration = tempHealth.getInt(0);
                    int amount = tempHealth.getInt(1);
                    TempBar tempBar = TempBarFactory.createTempHealth(duration, amount);
                    tempHealthList.add(tempBar);
                }
            }
            // tempMana
            if (enemyObject.has("tempManaList")) {
                JSONArray tempManaArray = enemyObject.getJSONArray("tempManaList");
                for (int i = 0; i < tempManaArray.length(); i++) {
                    JSONArray tempMana = (JSONArray) tempManaArray.get(i);
                    int duration = tempMana.getInt(0);
                    int amount = tempMana.getInt(1);
                    TempBar tempBar = TempBarFactory.createTempMana(duration, amount);
                    tempManaList.add(tempBar);
                }
            }
            // stat Increase
            if (enemyObject.has("statIncreaseList")) {
                JSONArray statIncreaseArray = enemyObject.getJSONArray("statIncreaseList");
                for (int i = 0; i < statIncreaseArray.length(); i++) {
                    JSONArray statIncrease = (JSONArray) statIncreaseArray.get(i);
                    String type = statIncrease.getString(0);
                    int duration = statIncrease.getInt(1);
                    int amount = statIncrease.getInt(2);
                    TempStat tempStat = new TempStat(type, duration, amount);
                    statIncreaseList.add(tempStat);
                }
            }
            // stat Decrease
            if (enemyObject.has("statDecreaseList")) {
                JSONArray statDecreaseArray = enemyObject.getJSONArray("statDecreaseList");
                for (int i = 0; i < statDecreaseArray.length(); i++) {
                    JSONArray statDecrease = (JSONArray) statDecreaseArray.get(i);
                    String type = statDecrease.getString(0);
                    int duration = statDecrease.getInt(1);
                    int amount = statDecrease.getInt(2);
                    TempStat tempStat = new TempStat(type, duration, amount);
                    statDecreaseList.add(tempStat);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        drawableResID = R.drawable.skeleton_icon;
        drawableDeadResID = R.drawable.skeleton_dead;
    }

    // returns a random action for the enemy to perform
    public Inventory getRandomAction() {
        // action can be either attack, special attack, ability 1, or ability 2
        Random random = new Random();
        // todo: change to be able to account for multiple abilities
        int actionNum = random.nextInt(3);
        switch (actionNum) {
            case 0:
                // weapon attack
                return weapons.get(0).getAttack();
            case 1:
                // weapon special attack
                return weapons.get(0).getSpecialAttack();
            case 2:
                // ability 1
                return abilities.get(0);
            default:
                throw new IllegalStateException();

        }
    }

    // decrement the cooldown on all abilities w/ cooldown >0
    public void decrCooldowns() {
        // check the ability in special attack if it exists
        weapons.get(0).getSpecialAttack().decrementCooldownCurr();
        for (int i = 0; i < abilities.size(); i++) {
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

        JSONEnemy.put("block", block);
        JSONEnemy.put("blockIncrease", blockIncrease);
        JSONEnemy.put("blockDecrease", blockDecrease);
        JSONEnemy.put("evasion", evasion);
        JSONEnemy.put("evasionIncrease", evasionIncrease);
        JSONEnemy.put("evasionDecrease", evasionDecrease);
        // abilities
        JSONArray abilitiesArray = new JSONArray();
        for (int i = 0; i < abilities.size(); i++) {
            abilitiesArray.put(abilities.get(i).serializeToJSON());
        }
        JSONEnemy.put("abilities", abilitiesArray);
        // weapons
        JSONArray weaponsArray = new JSONArray();
        for (int i = 0; i < weapons.size(); i++) {
            weaponsArray.put(weapons.get(i).serializeToJSON());
        }
        JSONEnemy.put("weapons", weaponsArray);
        // bars
        JSONEnemy.put("health", health);
        JSONEnemy.put("maxHealth", maxHealth);
        JSONEnemy.put("mana", mana);
        JSONEnemy.put("maxMana", maxMana);
        // misc
        JSONEnemy.put("level", level);
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
        JSONArray tempHealthArray = new JSONArray(); // <key, value>
        for (int i = 0; i < tempHealthList.size(); i++) {
            JSONArray tempHealth = new JSONArray(); // <duration, amount>
            tempHealth.put(tempHealthList.get(i).getDuration());
            tempHealth.put(tempHealthList.get(i).getAmount());
            tempHealthArray.put(tempHealth);
        }
        JSONEnemy.put("tempHealthList", tempHealthArray);
        // temp mana
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
    public int getDifficulty() {
        return difficulty;
    }
    public void setID(int ID) {
        this.ID = ID;
    }
}
