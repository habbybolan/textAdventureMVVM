package com.habbybolan.textadventure.model.effects;

import com.habbybolan.textadventure.R;

public class TempBar extends Effect {


    private String type;
    private int duration;
    private boolean isIndefinite;
    private int amount;

    public TempBar(String type, int duration, int amount) {
        this.type = type;
        this.duration = duration;
        this.amount = amount;
    }

    public TempBar(String type, int amount) {
        this.type = type;
        isIndefinite = true;
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public void decrementAmount(int amount) {
        this.amount -= amount;
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
    public boolean getIsIndefinite() {
        return isIndefinite;
    }

    @Override
    public void checkValidType(String type) {
        if (!type.equals(TEMP_HEALTH) && !type.equals(TEMP_MANA)) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public int getIcon() {
        switch (type) {
            case Effect.TEMP_HEALTH:
                return R.drawable.heart_icon;
            default:
                // otherwise, temp mana
                return R.drawable.mana_icon;
        }
    }
}
