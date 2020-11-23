package com.habbybolan.textadventure.model;

import com.habbybolan.textadventure.model.effects.Effect;
import com.habbybolan.textadventure.model.inventory.Ability;
import com.habbybolan.textadventure.model.inventory.Item;
import com.habbybolan.textadventure.model.inventory.weapon.Attack;
import com.habbybolan.textadventure.model.inventory.weapon.SpecialAttack;
import com.habbybolan.textadventure.model.inventory.weapon.Weapon;

/**
 * Creates the info string of an Inventory Object to display
 */

public class InventoryInfoModel {

    public InventoryInfoModel() {}

    public static String getAbilityInfo(Ability ability) {
        StringBuilder sb = new StringBuilder();
        sb.append(formatLine("tier", ability.getTier()));
        sb.append(formatLine("cooldown", ability.getCooldownMax()));
        sb.append(formatLine("cost", ability.getCost()));
        if (ability.getTempExtraHealth() > 0)
            sb.append(formatLine("shields", ability.getTempExtraHealth()));
        if (ability.getMinDamage() > 0)
            sb.append(formatLineInterval("damage", ability.getMinDamage(), ability.getMaxDamage()));
        if (ability.getDamageAoe() > 0) {
            sb.append(formatLine("soe size", ability.getDamageAoe()));
            sb.append(formatLineInterval("Splash damage", ability.getSplashMin(), ability.getSplashMax()));
        }
        // specials
        if (ability.getSpecialAoe() > 0)
            sb.append(formatLine("special aoe size", ability.getSpecialAoe()));
        if (ability.getIsConfuse()) sb.append(formatLine(Effect.CONFUSE, ability.getDuration()));
        if (ability.getIsStun()) sb.append(formatLine(Effect.STUN, ability.getDuration()));
        if (ability.getIsInvisible()) sb.append(formatLine(Effect.INVISIBILITY, ability.getDuration()));
        if (ability.getIsInvincibility()) sb.append(formatLine(Effect.INVINCIBILITY, ability.getDuration()));
        if (ability.getIsSilence()) sb.append(formatLine(Effect.SILENCE, ability.getDuration()));
        if (ability.getIsRevive()) sb.append(formatLine("revives"));
        // dots
        if (ability.getIsFire()) sb.append(formatLine(Effect.FIRE, Effect.FIRE_DURATION));
        if (ability.getIsFrostBurn()) sb.append(formatLine(Effect.FROSTBURN, Effect.FROSTBURN_DURATION));
        if (ability.getIsBleed()) sb.append(formatLine(Effect.BLEED, Effect.BLEED_DURATION));
        if (ability.getIsPoison()) sb.append(formatLine(Effect.POISON, Effect.POISON_DURATION));
        if (ability.getIsHealDot()) sb.append(formatLine(Effect.HEALTH_DOT, Effect.HEAL_DOT_DURATION));
        if (ability.getIsManaDot()) sb.append(formatLine(Effect.MANA_DOT, Effect.MANA_DOT_DURATION));
        // bars
        if (ability.getHealMin() > 0)
            sb.append(formatLineInterval("Heal", ability.getHealMin(), ability.getHealMax()));
        if (ability.getManaMin() > 0)
            sb.append(formatLineInterval("Mana", ability.getManaMin(), ability.getManaMax()));
        // stats
        if (ability.getStrIncrease() > 0) sb.append(formatLine("strength increase", ability.getStrIncrease()));
        if (ability.getStrDecrease() > 0) sb.append(formatLine("Strength decrease", ability.getStrDecrease()));
        if (ability.getIntIncrease() > 0) sb.append(formatLine("intelligence increase", ability.getIntIncrease()));
        if (ability.getIntDecrease() > 0) sb.append(formatLine("intelligence decrease", ability.getIntDecrease()));
        if (ability.getConIncrease() > 0) sb.append(formatLine("constitution increase", ability.getConIncrease()));
        if (ability.getConDecrease() > 0) sb.append(formatLine("constitution decrease", ability.getConDecrease()));
        if (ability.getSpdIncrease() > 0) sb.append(formatLine("speed increase", ability.getSpdIncrease()));
        if (ability.getSpdDecrease() > 0) sb.append(formatLine("speed decrease", ability.getSpdDecrease()));
        if (ability.getBlockIncrease() > 0) sb.append(formatLine("block increase", ability.getBlockIncrease()));
        if (ability.getBlockDecrease() > 0) sb.append(formatLine("block decrease", ability.getBlockDecrease()));
        if (ability.getEvadeIncrease() > 0) sb.append(formatLine("evasion increase", ability.getEvadeIncrease()));
        if (ability.getEvadeDecrease() > 0) sb.append(formatLine("evasion decrease", ability.getEvadeDecrease()));

        return sb.toString();
    }

    public static String getItemInfo(Item item) {
        StringBuilder sb = new StringBuilder();
        sb.append(formatLine(item.getDescription()));
        sb.append(formatLine("tier", item.getTier()));
        sb.append(formatLine("consumable", item.getIsConsumable()));
        if (item.getDuration() > 0) sb.append(formatLine("duration", item.getDuration()));
        if (item.getEscapeTrap()) sb.append(formatLine("escape trap"));
        if (item.getStrChange() != 0) sb.append(formatLine("str", item.getStrChange()));
        if (item.getIntChange() != 0) sb.append(formatLine("int", item.getIntChange()));
        if (item.getConChange() != 0) sb.append(formatLine("con", item.getConChange()));
        if (item.getSpdChange() != 0) sb.append(formatLine("spd", item.getSpdChange()));
        if (item.getBlockChange() != 0) sb.append(formatLine("block", item.getBlockChange()));
        if (item.getEvasionChange() != 0) sb.append(formatLine("evasion", item.getEvasionChange()));
        if (item.getHealthChange() != 0) sb.append(formatLine("health", item.getHealthChange()));
        if (item.getManaChange() != 0) sb.append(formatLine("mana", item.getManaChange()));
        if (item.getIsConfuse()) sb.append(formatLine("confuse"));
        if (item.getIsStun()) sb.append(formatLine("stun"));
        if (item.getIsSilence()) sb.append(formatLine("silence"));
        if (item.getIsInvisible()) sb.append(formatLine("invisible"));
        if (item.getIsInvincible()) sb.append(formatLine("invincible"));
        if (item.getIsFire()) sb.append(formatLine("fire"));
        if (item.getIsBleed()) sb.append(formatLine("bleed"));
        if (item.getIsPoison()) sb.append(formatLine("poison"));
        if (item.getIsFrostBurn()) sb.append(formatLine("frost burn"));
        if (item.getGoldChange() != 0) sb.append(formatLine("gold", item.getGoldChange()));
        if (item.getExpChange() != 0) sb.append(formatLine("exp", item.getExpChange()));
        return sb.toString();
    }

    public static String getWeaponInfo(Weapon weapon) {
        StringBuilder sb = new StringBuilder();
        sb.append(formatLine(weapon.getDescription()));
        sb.append(formatLine("tier", weapon.getTier()));
        return sb.toString();
    }

    public static String getAttackInfo(Attack attack) {
        StringBuilder sb = new StringBuilder();
        if (attack.getIsRanged()) sb.append(formatLine("ranged"));
        sb.append(formatLineInterval("damage", attack.getDamageMin(), attack.getDamageMax()));
        return sb.toString();
    }

    public static String getSpecialAttackInfo(SpecialAttack specialAttack) {
        StringBuilder sb = new StringBuilder();
        if (specialAttack.getIsRanged()) sb.append(formatLine("ranged"));
        sb.append(formatLineInterval("damage", specialAttack.getDamageMin(), specialAttack.getDamageMax()));
        if (specialAttack.getAoe() > 0) {
            sb.append(formatLine("number of targets", specialAttack.getAoe()));
            sb.append(formatLineInterval("splash damage", specialAttack.getSplashDamageMin(), specialAttack.getSplashDamageMax()));
            sb.append(formatLine("cooldown", specialAttack.getCooldownMax()));
        }
        return sb.toString();
    }

    private static String formatLine(String title) {
        return "\n" + title;
    }

    private static String formatLine(String title, boolean value) {
        return "\n" + title + ": " + value;
    }

    private static String formatLine(String title, int value) {
        return "\n" + title + ": " + value;
    }

    private static String formatLineInterval(String title, int valueMin, int valueMax) {
        return "\n" + title + ": " + valueMin + " - " + valueMax;
    }


}
