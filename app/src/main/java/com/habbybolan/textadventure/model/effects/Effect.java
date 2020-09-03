package com.habbybolan.textadventure.model.effects;

public interface Effect {

    String getType();
    int getDuration();
    void setDuration(int duration);
    void decrementDuration();
    boolean getIsInfinite();
    void checkValidType(String type);
}
