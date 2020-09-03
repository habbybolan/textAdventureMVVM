package com.habbybolan.textadventure.model.effects;

/*
Object to represent special effects
 */
public class SpecialEffect implements Effect {

    public static final String STUN = "stun";
    public static final String CONFUSE = "confuse";
    public static final String INVINCIBILITY = "invincible";
    public static final String SILENCE = "silence";
    public static final String INVISIBILITY = "invisible";

    private String type;
    private int duration;
    private boolean isInfinite = false;

    public SpecialEffect(String type, int duration) {
        checkValidType(type);
        this.type = type;
        this.duration = duration;
    }

    public SpecialEffect(String type, boolean isInfinite) {
        checkValidType(type);
        this.type = type;
        this.isInfinite = isInfinite;
    }

    @Override
    public boolean getIsInfinite() {
        return isInfinite;
    }

    @Override
    public void checkValidType(String type) {
        if (!type.equals(STUN) && !type.equals(CONFUSE) && !type.equals(INVINCIBILITY)
                && !type.equals(INVISIBILITY) && !type.equals(SILENCE)) {
            throw new IllegalArgumentException();
        }
    }

    // return true if String is a dot
    public static boolean isSpecial(String type) {
        return (type.equals(STUN) || type.equals(CONFUSE) || type.equals(INVINCIBILITY) || type.equals(INVISIBILITY) || type.equals(SILENCE));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o.getClass() != getClass()) return false;
        if (o == this) return true;
        SpecialEffect specialEffect = (SpecialEffect) o;
        return specialEffect.getType().equals(getType());
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
}