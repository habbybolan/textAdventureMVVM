package com.habbybolan.textadventure.model.effects;

import com.habbybolan.textadventure.R;

/*
object to represent a temporary stat applied
 */
public class TempStat extends Effect {

    private String type;
    private int duration;
    private boolean isIndefinite;
    private int amount;

    public TempStat(String type, int duration, int amount) {
        this.type = type;
        this.duration = duration;
        this.amount = amount;
    }

    public TempStat(String type, int amount) {
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
        if (!type.equals(STR) && !type.equals(INT) && !type.equals(CON)
                && !type.equals(SPD)) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public int getIcon() {
        switch (type) {
            case Effect.STR:
                return R.drawable.sword;
            case Effect.INT:
                return R.drawable.sword;
            case Effect.CON:
                return R.drawable.sword;
            default:
                // SPD
                return R.drawable.sword;
        }
    }

}
