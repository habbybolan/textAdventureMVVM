package com.habbybolan.textadventure.model.effects;

/*
object to represent a temporary stat applied
 */
public class TempStat implements Effect {

    public static final String STR = "STR";
    public static final String INT = "INT";
    public static final String CON = "CON";
    public static final String SPD = "SPD";
    public static final String EVASION = "Evasion";
    public static final String BLOCK = "Block";
    public static final String TEMP_HEALTH = "Temporary health";
    public static final String TEMP_MANA = "Temporary mana";

    private String type;
    private int duration;
    private boolean isInfinite;

    public TempStat(String type, int duration) {

    }

    public TempStat(String type, boolean isInfinite) {

    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public void decrementDuration() {
        this.duration--;
    }

    @Override
    public boolean getIsInfinite() {
        return isInfinite;
    }

    @Override
    public void checkValidType(String type) {
        if (!type.equals(STR) && !type.equals(INT) && !type.equals(CON)
                && !type.equals(SPD) && !type.equals(EVASION) && !type.equals(BLOCK) && !type.equals(TEMP_HEALTH) && !type.equals(TEMP_MANA)) {
            throw new IllegalArgumentException();
        }
    }

    public boolean isTempStat() {
        return type.equals(STR) || type.equals(INT) || type.equals(CON) || type.equals(SPD)
                || type.equals(EVASION) || type.equals(BLOCK) || type.equals(TEMP_HEALTH) || type.equals(TEMP_MANA);
    }
}
