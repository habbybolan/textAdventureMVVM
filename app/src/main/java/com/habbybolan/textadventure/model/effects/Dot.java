package com.habbybolan.textadventure.model.effects;

import com.habbybolan.textadventure.R;

/*
Object to represent Damage Over time effects
 */
public class Dot implements Effect {

    public static final String FIRE = "fire";
    public static final int FIRE_DURATION = 1;
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

    private String type;
    private int duration;
    private boolean isInfinite = false;

    // creates a dot with a specific duration
    public Dot(String type, int duration) {
        checkValidType(type);
        this.type = type;
        this.duration = duration;
    }

    // creates a dot with a default duration
    public Dot(String type, boolean isInfinite) {
        checkValidType(type);
        this.type = type;
        setDefaultDuration();
        this.isInfinite = isInfinite;
    }

    // throws IllegalArgumentException if the type for the dot is invalid
    @Override
    public void checkValidType(String type) {
        if (!type.equals(Dot.FIRE) && !type.equals(Dot.POISON) && !type.equals(Dot.BLEED)
                && !type.equals(Dot.FROSTBURN) && !type.equals(Dot.HEALTH_DOT) && !type.equals(Dot.MANA_DOT)) {
            throw new IllegalArgumentException();
        }
    }

    // return true if String is a dot
    public static boolean isDot(String type) {
        return (type.equals(FIRE) || type.equals(POISON) || type.equals(BLEED) || type.equals(FROSTBURN) || type.equals(HEALTH_DOT) || type.equals(MANA_DOT));
    }

    // sets the default duration of the dot
    private void setDefaultDuration() {
        switch (type) {
            case Dot.FIRE:
                setDuration(FIRE_DURATION);
                break;
            case Dot.POISON:
                setDuration(POISON_DURATION);
                break;
            case Dot.BLEED:
                setDuration(BLEED_DURATION);
                break;
            case Dot.FROSTBURN:
                setDuration(FROSTBURN_DURATION);
                break;
            case Dot.HEALTH_DOT:
                setDuration(HEAL_DOT_DURATION);
                break;
            case Dot.MANA_DOT:
                setDuration(MANA_DOT_DURATION);
                break;
            default: // shouldn't reach this
                break;
        }
    }

    public int getIcon() {
        switch (type) {
            case Dot.FIRE:
                return R.drawable.fire_icon;
            case Dot.BLEED:
                return R.drawable.bleed_icon;
            case Dot.POISON:
                return R.drawable.poison_icon;
            case Dot.FROSTBURN:
                return R.drawable.frostburn_icon;
            case Dot.HEALTH_DOT:
                return R.drawable.health_dot_icon;
            case Dot.MANA_DOT:
                return R.drawable.mana_dot_icon;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public boolean getIsInfinite() {
        return isInfinite;
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

    // equivalence of Dot objects based on their type name
    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o.getClass() != getClass()) return false;
        if (o == this) return true;
        Dot dot = (Dot) o;
        return dot.getType().equals(getType());
    }
}
