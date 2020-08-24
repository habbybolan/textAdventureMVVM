package com.habbybolan.textadventure.model;
/*
deals with damage, stats changes, and ability effects (AOE, DOT, specials, damage)
 - all combat actions
 */


public class Damage {
    /*

    public Damage() {
    }

    // check any buffs or protections that characterEntity has
    // remove the damage amount from target health if no protection
    // or remove target's temporary extra health and direct health if any overflow of damage
    public void damageTarget(CharacterEntity target, int damage) {
        int overFlow = 0; // keeps track of damage if it gets rid of all tempExtraHealth
        if ((target.getTempExtraHealth() - damage) < 0) {
            overFlow = Math.abs(target.getTempExtraHealth() - damage);
        }
        // if the target has some temporary extra health, do damage to that first
        if (target.getTempExtraHealth() > 0) {
            depleteTempExtraHealth(target, damage);
        }
        // direct health takes damage if any overFlow from tempHealth
        target.setHealth(target.getHealth() - overFlow);
        // update the view model for change in health status
        if (target instanceof Character)
            model.setHealthManaChange(CharacterChangeViewModel.HEALTH);
    }

    // returns a random damage number between damageMin and damageMax
    // apply any additional strength the user has to the damage
    public int getRandomAmount(int min, int max, CharacterEntity user) {
        int damageDifference = max - min;
        Random rand  = new Random();
        int damageRoll = rand.nextInt(damageDifference+1);
        return damageRoll+min+user.getStrength();
    }

    // heals a target for a specific amount
    public void healTarget(CharacterEntity target, int amount) {
        target.setHealth(target.getHealth()+amount);
        if (target.getHealth() > target.getMaxHealth()) {
            target.setHealth(target.getMaxHealth());
        }
        // update the view model for change in health status
        if (target instanceof Character)
            model.setHealthManaChange(CharacterChangeViewModel.HEALTH);
    }

    // regenerates mana for a specific amount
    public void manaTarget(CharacterEntity target, int amount) {
        target.setMana(target.getMana()+amount);
        if (target.getMana() > target.getMaxMana()) {
            target.setMana(target.getMaxMana());
        }
        // update the view model for change in mana status
        if (target instanceof Character)
            model.setHealthManaChange(CharacterChangeViewModel.MANA);
    }

    // regenerates mana for a specific amount
    public void loseManaTarget(CharacterEntity target, int amount) {
        target.setMana(target.getMana()-amount);
        if (target.getMana() < 0) {
            target.setMana(0);
        }
        // update the view model for change in mana status
        if (target instanceof Character)
            model.setHealthManaChange(CharacterChangeViewModel.MANA);
    }



    // ***ABILITIES***

    // adds a new ability and applies its effects - stat increases, specials, and dots
    public void addNewAbilityEffects(Ability ability, CharacterEntity entity) {
        // todo: scale with Intelligence
        //if (!entity.appliedAbilities.contains(ability) ) {
        //    entity.appliedAbilities.add(ability);
        //} // todo: appliedAbilities unnecessary?
        if (ability.getMinDamage() != 0) damageTarget(entity, getRandomAmount(ability.getMinDamage(), ability.getMaxDamage(), entity));
        if (ability.getDamageAoe() != 0) doAoeStuff(); // todo: aoe
        // specials
        if (ability.getIsConfuse() == 1) addNewSpecial(CONFUSE, ability.getDuration(), entity);
        if (ability.getIsStun() == 1) addNewSpecial(STUN, ability.getDuration(), entity);
        if (ability.getIsInvincibility() == 1) addNewSpecial(INVINCIBILITY, ability.getDuration(), entity);
        if (ability.getIsSilence() == 1) addNewSpecial(SILENCE, ability.getDuration(), entity);
        if (ability.getIsInvisible() == 1) addNewSpecial(INVISIBILITY, ability.getDuration(), entity);
        // DOT
        if (ability.getIsFire() == 1) addNewDot(FIRE, false, entity);
        if (ability.getIsPoison() == 1) addNewDot(POISON, false, entity);
        if (ability.getIsBleed() == 1) addNewDot(BLEED, false, entity);
        if (ability.getIsFrostBurn() == 1) addNewDot(FROSTBURN, false, entity);
        if (ability.getIsHealDot() == 1) addNewDot(HEALTH, false, entity);
        if (ability.getIsManaDot() == 1) addNewDot(MANA, false, entity);
        // direct heal/mana
        if (ability.getHealMin() != 0) healTarget(entity, getRandomAmount(ability.getHealMin(), ability.getHealMax(), entity));
        if (ability.getManaMin() != 0) manaTarget(entity, getRandomAmount(ability.getManaMin(), ability.getManaMax(), entity));
        // stat increases
        if (ability.getStrIncrease() != 0) addNewStatIncrease(STR, ability.getDuration(), ability.getStrIncrease(), entity);
        if (ability.getIntIncrease() != 0) addNewStatIncrease(INT, ability.getDuration(), ability.getIntIncrease(), entity);
        if (ability.getConIncrease() != 0) addNewStatIncrease(CON, ability.getDuration(), ability.getConIncrease(), entity);
        if (ability.getSpdIncrease() != 0) addNewStatIncrease(SPD, ability.getDuration(), ability.getSpdIncrease(), entity);
        if (ability.getEvadeIncrease() != 0) addNewStatIncrease(EVASION, ability.getDuration(), ability.getEvadeIncrease(), entity);
        if (ability.getBlockIncrease() != 0) addNewStatIncrease(BLOCK, ability.getDuration(), ability.getBlockIncrease(), entity);
        // stat decreases
        if (ability.getStrDecrease() != 0) addNewStatDecrease(STR, ability.getDuration(), ability.getStrDecrease(), entity);
        if (ability.getIntDecrease() != 0) addNewStatDecrease(INT, ability.getDuration(), ability.getIntDecrease(), entity);
        if (ability.getConDecrease() != 0) addNewStatDecrease(CON, ability.getDuration(), ability.getConDecrease(), entity);
        if (ability.getSpdDecrease() != 0) addNewStatDecrease(SPD, ability.getDuration(), ability.getSpdDecrease(), entity);
        if (ability.getEvadeDecrease() != 0) addNewStatDecrease(EVASION, ability.getDuration(), ability.getEvadeDecrease(), entity);
        if (ability.getBlockDecrease() != 0) addNewStatDecrease(BLOCK, ability.getDuration(), ability.getBlockDecrease(), entity);
        // temp extra health- part of stat
        if (ability.getTempExtraHealth() != 0) addNewTempExtraHealthMana(TEMP_HEALTH, ability.getDuration(), ability.getTempExtraHealth(), entity);
    }


    // ***AOE***
    // todo: how to do AOE??
    public void doAoeStuff() {

    }


    // ***SPECIALS ***

    // adds a new special to specialMap if not already existing - alter isSpecial value if new
    // if exists, it reset the time for the special
    public void addNewSpecial(String special, int duration, CharacterEntity entity) {
        if (entity.specialMap.containsKey(special)) {
            // < 0  value indicates the special is permanently set from an item
            if (entity.specialMap.get(special) > 0 && entity.specialMap.get(special) < duration) {
                entity.specialMap.put(special, duration);
            }
        } else {
            alterIsSpecial(special, entity);
            entity.specialMap.put(special, duration);
        }
        // update the view model for change in special status
        if (entity instanceof Character) {
            model.setSpecialChange(special);
        }
    }

    // check if the special is active on the enemy
    // active returns 1, otherwise 0
    public boolean checkSpecials(String special, CharacterEntity entity) {
        if (special.equals(STUN)) {
            return entity.getIsStun() == 1;
        } else if (special.equals(CONFUSE)) {
            return entity.getIsConfuse() == 1;
        } else if (special.equals(INVINCIBILITY)) {
            return entity.getIsInvincible() == 1;
        } else if (special.equals(SILENCE)) {
            return entity.getIsSilence() == 1;
            // otherwise INVISIBILITY
        } else {
            return entity.getIsInvisible() == 1;
        }
    }

    // decrements the special duration left
    // if the duration is 0 after decrement, then set isSpecial value to 0
    // if the value < 0, do nothing, permanent special
    public void decrSpecialDuration(CharacterEntity entity) {
        for (String key : entity.specialMap.keySet()) {
            entity.specialMap.put(key, entity.specialMap.get(key)-1);
            if (entity.specialMap.get(key) == 0) {
                alterIsSpecial(key, entity); // changes
                entity.specialMap.remove(key);
                model.setSpecialChange(key);
            }
        }
    }

    // change the value of isSpecial value (0->1 or 1->0)
    public void alterIsSpecial(String special, CharacterEntity entity) {
        if (special.equals(STUN)) {
            entity.setIsStun(Math.abs(entity.getIsStun()-1));
        } else if (special.equals(CONFUSE)) {
            entity.setIsConfuse(Math.abs(entity.getIsConfuse()-1));
        } else if (special.equals(INVINCIBILITY)) {
            entity.setIsInvincible(Math.abs(entity.getIsInvincible()-1));
        } else if (special.equals(SILENCE)) {
            entity.setIsInvincible(Math.abs(entity.getIsSilence()-1));
            // otherwise INVISIBILITY
        } else {
            entity.setIsInvisible(Math.abs(entity.getIsInvisible()-1));
        }
    }

    // special effects - static
    public static final String STUN = "stun";
    public static final String CONFUSE = "confuse";
    public static final String INVINCIBILITY = "invincible";
    public static final String SILENCE = "silence";
    public static final String INVISIBILITY = "invisible";




    // ***STATS***

    // updates all the stat's main value, given its new baseStat, statIncrease, and statDecrease
    public void updateStats(CharacterEntity entity) {
        // update STR
        entity.setStrength(entity.getStrIncrease() + entity.getStrDecrease() + entity.getStrBase());
        if (entity.getStrength() < 0) entity.setStrength(0);
        // update INT
        entity.setIntelligence(entity.getIntIncrease() + entity.getIntDecrease() + entity.getIntBase());
        if (entity.getIntelligence() < 0) entity.setIntelligence(0);
        // update CON
        entity.setConstitution(entity.getConIncrease() + entity.getConDecrease() + entity.getConBase());
        if (entity.getConstitution() < 0) entity.setConstitution(0);
        // update SPD
        entity.setSpeed(entity.getSpdIncrease() + entity.getSpdDecrease() + entity.getSpdBase());
        if (entity.getSpeed() < 0) entity.setSpeed(0);
        // update Evasion
        entity.setEvasion(entity.getEvasionIncrease() + entity.getEvasionDecrease() + entity.getEvasionBase());
        if (entity.getEvasion() < 0) entity.setEvasion(0);
        // update Block
        entity.setBlock(entity.getBlockIncrease() + entity.getBlockDecrease() + entity.getBlockBase());
        if (entity.getBlock() < 0) entity.setBlock(0);
    }

    // add a new stat increase - can stack these, each stat increase represented as the list inside a list
    public void addNewStatIncrease(String stat, int duration, int amount, CharacterEntity entity) {
        ArrayList<Object> statValues = new ArrayList<>();
        statValues.add(stat);
        statValues.add(duration);
        statValues.add(amount);
        entity.statIncreaseList.add(statValues);
        findStatToIncrease((String)statValues.get(0), (int)statValues.get(2), entity);
        if (entity instanceof Character)
            model.setStatChange(stat);
        updateStats(entity);
    }

    // apply the stat increase to statIncrease and stat values
    private void findStatToIncrease(String stat, int amount, CharacterEntity entity) {
        switch (stat) {
            case STR:
                entity.setStrIncrease(entity.getStrIncrease() + amount);
                break;
            case INT:
                entity.setIntIncrease(entity.getIntIncrease() + amount);
                break;
            case CON:
                entity.setConIncrease(entity.getConIncrease() + amount);
                break;
            case SPD:
                entity.setSpdIncrease(entity.getSpdIncrease() + amount);
                break;
            case EVASION:
                entity.setEvasionIncrease(entity.getEvasionIncrease() + amount);
                break;
            case BLOCK:
                entity.setBlockIncrease(entity.getBlockIncrease() + amount);
                break;
        }
        findStatToAlter(stat, entity);
    }

    // undo a stat increase
    private void undoStatIncrease(String stat, int amount, CharacterEntity entity) {
        switch (stat) {
            case STR:
                entity.setStrIncrease(entity.getStrIncrease() - amount);
                break;
            case INT:
                entity.setIntIncrease(entity.getIntIncrease() - amount);
                break;
            case CON:
                entity.setConIncrease(entity.getConIncrease() - amount);
                break;
            case SPD:
                entity.setSpdIncrease(entity.getSpdIncrease() - amount);
                break;
            case EVASION:
                entity.setEvasionIncrease(entity.getEvasionIncrease() - amount);
                break;
            case BLOCK:
                entity.setBlockIncrease(entity.getBlockIncrease() - amount);
                break;
        }
        findStatToAlter(stat, entity);
        if (entity instanceof Character)
            model.setStatChange(stat);
    }

    // add a new stat decrease - can stack these, each stat decrease represented as the list inside a list
        // if the decrease goes less than 0 of a stat, then reduce the amount that the stat is decreased
    public void addNewStatDecrease(String stat, int duration, int amount, CharacterEntity entity) {
        ArrayList<Object> statValues = new ArrayList<>();
        statValues.add(stat);
        statValues.add(duration);
        statValues.add(amount);
        entity.statDecreaseList.add(statValues);
        findStatToDecrease((String)statValues.get(0), (int)statValues.get(2), entity);
        if (entity instanceof Character)
            model.setStatChange(stat);
        updateStats(entity);
    }

    // apply the stat decrease to statDecrease and stat values
    private void findStatToDecrease(String stat, int amount, CharacterEntity entity) {
        switch (stat) {
            case STR:
                entity.setStrDecrease(entity.getStrDecrease() + amount);
                break;
            case INT:
                entity.setIntDecrease(entity.getIntDecrease() + amount);
                break;
            case CON:
                entity.setConDecrease(entity.getConDecrease() + amount);
                break;
            case SPD:
                entity.setSpdDecrease(entity.getSpdDecrease() + amount);
                break;
            case EVASION:
                entity.setEvasionDecrease(entity.getEvasionDecrease() + amount);
                break;
            case BLOCK:
                entity.setBlockDecrease(entity.getBlockDecrease() + amount);
                break;
        }
        findStatToAlter(stat, entity);
    }

    // undo a stat decrease
    private void undoStatDecrease(String stat, int amount, CharacterEntity entity) {
        switch (stat) {
            case STR:
                entity.setStrDecrease(entity.getStrDecrease() - amount);
                break;
            case INT:
                entity.setIntDecrease(entity.getIntDecrease() - amount);
                break;
            case CON:
                entity.setConDecrease(entity.getConDecrease() - amount);
                break;
            case SPD:
                entity.setSpdDecrease(entity.getSpdDecrease() - amount);
                break;
            case EVASION:
                entity.setEvasionDecrease(entity.getEvasionDecrease() - amount);
                break;
            case BLOCK:
                entity.setBlockDecrease(entity.getBlockDecrease() - amount);
                break;
        }
        findStatToAlter(stat, entity);
        if (entity instanceof Character)
            model.setStatChange(stat);
    }

    // alter the stat amount, given it's new statIncrease or statDecrease
    private void findStatToAlter(String stat, CharacterEntity entity) {
        switch (stat) {
            case STR:
                entity.setStrength((entity.getStrBase() + entity.getStrIncrease()) - entity.getStrDecrease());
                if (entity.getStrength() < 0) entity.setStrength(0);
                break;
            case INT:
                entity.setIntelligence((entity.getIntBase() + entity.getIntIncrease()) - entity.getIntDecrease());
                if (entity.getIntelligence() < 0) entity.setIntelligence(0);
                break;
            case CON:
                entity.setConstitution((entity.getConBase() + entity.getConIncrease()) - entity.getConDecrease());
                if (entity.getConstitution() < 0) entity.setConstitution(0);
                break;
            case SPD:
                entity.setSpeed((entity.getSpdBase() + entity.getSpdIncrease()) - entity.getSpdDecrease());
                if (entity.getSpeed() < 0) entity.setSpeed(0);
                break;
            case EVASION:
                entity.setEvasion((entity.getEvasionBase() + entity.getEvasionIncrease()) - entity.getEvasionDecrease());
                if (entity.getEvasion() < 0) entity.setEvasion(0);
                break;
            case BLOCK:
                entity.setBlock((entity.getBlockBase() + entity.getBlockIncrease()) - entity.getBlockDecrease());
                if (entity.getBlock() < 0) entity.setBlock(0);
                break;
        }
    }

    // decrement the time remaining for the stat change
        // if the duration reaches 0 after decrement, reverse the change and remove from appropriate statChange list
    public void decrementStatChangeDuration(CharacterEntity entity) {
        List<ArrayList<Object>> statIncreaseList = entity.statIncreaseList;
        for (int i = 0; i < entity.statIncreaseList.size(); i++) {
            // get the duration of the stat change at index i and decrement
            int duration = (int)entity.statIncreaseList.get(i).get(1);
            entity.statIncreaseList.get(i).set(1, duration-1);
            // if duration is 0 after decrement, remove the stat change
            if (duration-1 == 0) {
                String stat = (String)entity.statIncreaseList.get(i).get(0);
                int amount = (int)entity.statIncreaseList.get(i).get(2);
                entity.statIncreaseList.remove(i);
                i--;
                undoStatIncrease(stat, amount, entity);
            }
        }
        for (int i = 0; i < entity.statDecreaseList.size(); i++) {
            // get the duration of the stat change at index i and decrement
            int duration = (int)entity.statDecreaseList.get(i).get(1);
            entity.statDecreaseList.get(i).set(1, duration-1);
            // if duration is 0 after decrement, remove the stat change
            if (duration-1 == 0) {
                String stat = (String)entity.statDecreaseList.get(i).get(0);
                int amount = (int)entity.statDecreaseList.get(i).get(2);
                entity.statDecreaseList.remove(i);
                i--;
                undoStatDecrease(stat, amount, entity);
            }
        }
        updateStats(entity);
    }


    public static final String STR = "STR";
    public static final String INT = "INT";
    public static final String CON = "CON";
    public static final String SPD = "SPD";
    public static final String EVASION = "Evasion";
    public static final String BLOCK = "Block";

    public static final String TEMP_HEALTH = "Temporary health";
    public static final String TEMP_MANA = "Temporary mana";


    // adds a new tempExtraHealth/tempExtraMana, in duration order, where smallest duration left at index 0 to array
        // update tempExtraHealth/Mana value
    public void addNewTempExtraHealthMana(String type, int duration, int tempExtra, CharacterEntity entity) {
        List<ArrayList<Integer>> list;
        if (type.equals(TEMP_HEALTH)) {
            list = entity.tempHealthList; // TODO; is this a deep or shallow copy?
            entity.setTempExtraHealth(entity.getTempExtraHealth() + tempExtra);
            updateTempListWithNewExtra(duration, tempExtra, entity.tempHealthList, entity);
        } else {
            list = entity.tempManaList;
            entity.setTempExtraMana(entity.getTempExtraMana() + tempExtra);
            updateTempListWithNewExtra(duration, tempExtra, entity.tempManaList, entity);
        }
    }

    // Helper for addNewTempExtraHealthMana for adding new tempExtraHealth/Mana to lists
    private void updateTempListWithNewExtra(int duration, int tempExtra, List<ArrayList<Integer>> list, CharacterEntity entity) {
        ArrayList<Integer> newTemp = new ArrayList<>();
        newTemp.add(duration);
        newTemp.add(tempExtra);
        int i = 0; // index of tempHealthList
        for (ArrayList listTemp : list) {
            if (newTemp.get(0) < (int) listTemp.get(0)) {
                insertTempAtPosition(newTemp, i, entity, list);
                break; // new element added, break from loop
            }
            i++;
        }
    }

    // deplete the tempExtraHealth buffs by amount, deleting any buffs that are completely depleted ( = 0)
    private void depleteTempExtraHealth(CharacterEntity entity, int amount) {
        depleteTempExtra(entity, amount, entity.tempHealthList);
    }

    // deplete the tempExtraMana buffs by amount, deleting any buffs that are completely depleted ( = 0)
    private void depleteTempExtraMana(CharacterEntity entity, int amount) {
        depleteTempExtra(entity, amount, entity.tempManaList);
    }

    // helper for depleteTempExtraHealth/Mana to deplete and/or remove extra amount from array indices
    private void depleteTempExtra(CharacterEntity entity, int amount, List<ArrayList<Integer>> list) {
        // loop through all indices of list and remove element if more amount to deplete than amount left over
            // start from index 0 as the list is sorted from from smallest duration left to largest - remove smallest first
        int index = 0;
        while (amount > 0 && index < list.size()) {
            ArrayList<Integer> element = list.get(index);
            int eleAmount = element.get(1);
            element.set(1, eleAmount-amount);
            // remove the element from list if completely depleted, and alter amount by amount applied
            if (element.get(1) < 0) {
                list.remove(index);
                amount -= eleAmount;
            }
            index++;
        }
    }

    // insert the newTempHealth buff at index i and push all indices i+ to the right\
        // helper for addNewTempExtraHealth
    private void insertTempAtPosition(ArrayList<Integer> newTemp, int i, CharacterEntity entity, List<ArrayList<Integer>> list) {
        ArrayList<Integer> prev = list.get(i);
        list.add(i, newTemp);

        for (int index = i+1; i < list.size(); index++) {
            list.add(index, prev);
            prev = list.get(index);
        }
        list.add(prev); // added to end to avoid outOfBounds exception
    }

    // decrements the duration of each tempExtraHealth in tempHealthList
        // if the duration reaches 0, then remove the extra health from the list
    public void decrementTempExtraDuration(CharacterEntity entity) {
        decrementTemp(entity.tempHealthList);
        decrementTemp(entity.tempManaList);
    }

    private void decrementTemp(List<ArrayList<Integer>> list) {
        int index = 0;
        // iterate over all tempHealthList elements, decrementing the duration value, removing if duration = 0;
        while (index < list.size()) {
            ArrayList<Integer> listTemp = list.get(index);
            int duration = listTemp.get(0);
            if (duration-1 > 0) {
                listTemp.set(index, duration - 1);
                index++;
            } else {
                list.remove(index);
            }
        }
    }



    // ***DOTS***

    // todo: alter isStatus values in Enemy and Character - add them first
    // adds a new Dot to to dotMap if not already existing
    // if exists, it reset the time for the dot
    public void addNewDot(String dot, boolean setInfinite, CharacterEntity entity) {
        int duration = 0;
        switch (dot) {
            case FIRE:
                duration = FIRE_DURATION;
                break;
            case POISON:
                duration = POISON_DURATION;
                break;
            case BLEED:
                duration = BLEED_DURATION;
                break;
            case FROSTBURN:
                duration = FROSTBURN_DURATION;
                break;
            case HEALTH:
                duration = HEAL_DURATION;
                break;
            case MANA:
                duration = MANA_DURATION;
                break;
            default: // shouldn't reach this
                break;
        }
        if (setInfinite) duration = -1; // negative duration if DOT set as infinite
        // set dot as infinite if setInfinite
        if (entity.specialMap.containsKey(dot)) {
            if (entity.specialMap.get(dot) > 0) entity.specialMap.put(dot, duration);
        } else {
            alterIsDot(dot, entity);
            entity.specialMap.put(dot, duration);
        }

        if (entity instanceof Character) model.setDotChange(dot);
    }

    private void alterIsDot(String dot, CharacterEntity entity) {
        if (dot.equals(FIRE)) {
            entity.setIsFire(Math.abs(entity.getIsFire()-1));
        } else if (dot.equals(BLEED)) {
            entity.setIsBleed(Math.abs(entity.getIsBleed()-1));
        } else if (dot.equals(POISON)) {
            entity.setIsPoison(Math.abs(entity.getIsPoison()-1));
        } else if (dot.equals(FROSTBURN)) {
            entity.setIsFrostBurn(Math.abs(entity.getIsFrostBurn()-1));
        }
    }

    // applies the effect of the DOT and decrements the time remaining in DotMap
    // if duration == 0, then remove from the dotMap
    public void applyDots(CharacterEntity entity) {
        Iterator<Map.Entry<String, Integer>> iter = entity.dotMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, Integer> entry = iter.next();
            String key = entry.getKey();
            int value = entity.dotMap.get(key);
            findDotToApply(key, entity);
            entity.dotMap.put(key, value-1);
            if (value-1 == 0) {
                alterIsDot(key, entity);
                iter.remove();
            }
        }
        if (entity instanceof Character) model.setDotChange("");
    }
    // helper for applyDots
    // find the proper method to call for using a specific dot
    private void findDotToApply(String dot, CharacterEntity entity) {
        if (dot.equals(FIRE)) {
            this.applyFire(entity);
        } else if (dot.equals(POISON)) {
            this.applyPoison(entity);
        } else if (dot.equals(BLEED)) {
            this.applyBleed(entity);
        } else if (dot.equals(FROSTBURN)) {
            this.applyFrostBurn(entity);
        } else if (dot.equals(HEALTH)) {
            this.applyHealthDot(entity);
            // otherwise MANA
        } else {
            this.applyManaDot(entity);
        }
    }


    // Dot effects - static
    public static final String FIRE = "fire";
    public final int FIRE_DURATION = 1;
    public final int FIRE_DAMAGE = 8;
    public static final String POISON = "poison";
    public final int POISON_DURATION = 4;
    public final int POISON_DAMAGE = 2;
    public static final String BLEED = "bleed";
    public final int BLEED_DURATION = 3;
    public final int BLEED_DAMAGE = 4;
    public static final String FROSTBURN = "frostBurn";
    public final int FROSTBURN_DURATION = 1;
    public final int FROSTBURN_DAMAGE = 1;
    public static final String HEALTH = "heal";
    public final int HEAL_DURATION = 3;
    public final int HEAL_AMOUNT = 5;
    public static final String MANA = "mana";
    public final int MANA_DURATION = 3;
    public final int MANA_AMOUNT = 5;

    // status application of damage over times
    public void applyFire(CharacterEntity entity) {
        damageTarget(entity, FIRE_DAMAGE);
    }
    public void applyPoison(CharacterEntity entity) {
        damageTarget(entity, POISON_DAMAGE);
    }
    public void applyBleed(CharacterEntity entity) {
        damageTarget(entity, BLEED_DAMAGE);
    }
    public void applyFrostBurn(CharacterEntity entity) {
        addNewSpecial(STUN, FROSTBURN_DURATION, entity);
        damageTarget(entity, FROSTBURN_DAMAGE);
    }
    public void applyHealthDot(CharacterEntity entity) {
        healTarget(entity, HEAL_AMOUNT);
    }
    public void applyManaDot(CharacterEntity entity) {
        manaTarget(entity, MANA_AMOUNT);
    }*/
}
