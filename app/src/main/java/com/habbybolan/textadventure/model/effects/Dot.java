package com.habbybolan.textadventure.model.effects;

import com.habbybolan.textadventure.R;

/*
Object to represent Damage Over time effects
 */
public class Dot extends Effect {

    private String type;
    private int duration;
    private boolean isIndefinite = false;

    // creates a dot with a specific duration
    public Dot(String type, int duration) {
        checkValidType(type);
        this.type = type;
        this.duration = duration;
    }

    // creates a dot with a default duration
    public Dot(String type, boolean isIndefinite) {
        checkValidType(type);
        this.type = type;
        this.isIndefinite = isIndefinite;
        if (!isIndefinite) setDefaultDuration();
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

    @Override
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
    public boolean getIsIndefinite() {
        return isIndefinite;
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
