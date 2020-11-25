package com.habbybolan.textadventure.model.effects;

public class TempBarFactory {

    public static TempBar createTempHealth(int duration, int amount) {
        return new TempBar(Effect.TEMP_HEALTH, duration, amount);
    }

    public static TempBar createIndefiniteHealth(int amount) {
        return new TempBar(Effect.TEMP_HEALTH, amount);
    }

    public static TempBar createTempMana(int duration, int amount) {
        return new TempBar(Effect.TEMP_MANA, duration, amount);
    }

    public static TempBar createIndefiniteMana(int amount) {
        return new TempBar(Effect.TEMP_MANA, amount);
    }
}
