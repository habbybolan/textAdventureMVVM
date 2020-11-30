package com.habbybolan.textadventure.model.effects;

public abstract class Effect implements Comparable<Effect>{

    // Stats
    public static final String STR = "STR";
    public static final String INT = "INT";
    public static final String CON = "CON";
    public static final String SPD = "SPD";

    // bars
    public static final String TEMP_HEALTH = "Temporary health";
    public static final String TEMP_MANA = "Temporary mana";

    // Special effects
    public static final String STUN = "stun";
    public static final String CONFUSE = "confuse";
    public static final String INVINCIBILITY = "invincible";
    public static final String SILENCE = "silence";
    public static final String INVISIBILITY = "invisible";

    // Dots
    public static final String FIRE = "fire";
    public static final int FIRE_DURATION = 2;
    public static final int FIRE_DAMAGE = 8;
    public static final String POISON = "poison";
    public static final int POISON_DURATION = 4;
    public static final int POISON_DAMAGE = 2;
    public static final String BLEED = "bleed";
    public static final int BLEED_DURATION = 3;
    public static final int BLEED_DAMAGE = 4;
    public static final String FROSTBURN = "frostBurn";
    public static final int FROSTBURN_DURATION = 1;
    public static final int FROSTBURN_DAMAGE = 1;
    public static final String HEALTH_DOT = "healDot";
    public static final int HEAL_DOT_DURATION = 3;
    public static final int HEAL_DOT_AMOUNT = 5;
    public static final String MANA_DOT = "manaDOt";
    public static final int MANA_DOT_DURATION = 3;
    public static final int MANA_DOT_AMOUNT = 5;

    abstract String getType();
    abstract int getDuration();
    abstract void setDuration(int duration);
    abstract void decrementDuration();
    abstract boolean getIsIndefinite();
    abstract void checkValidType(String type);

    @Override
    public int compareTo(Effect e) {
        return Integer.compare(e.getDuration(), getDuration());
    }

    public boolean isTempStat() {
        return getType().equals(STR) || getType().equals(INT) || getType().equals(CON) || getType().equals(SPD);
    }

    // returns the drawable resource ID for the icon
    abstract int getIcon();
}
