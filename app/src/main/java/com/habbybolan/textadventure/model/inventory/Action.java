package com.habbybolan.textadventure.model.inventory;

public abstract class Action implements InventoryEntity {

    protected int cooldownCurr = 0;
    protected int cooldownMax = 0;

    public boolean isActionReady() {
        return cooldownCurr == 0;
    }

    public void setActionUsed() {
        cooldownCurr = cooldownMax;
    }

    public void decrementCooldownCurr() {
        if (cooldownCurr > 0) cooldownCurr--;
    }

    /**
     * Sets the cooldown to 0, making it ready for use again
     */
    public void resetCooldown() {
        cooldownCurr = 0;
    }

    public int getCooldownCurr() {
        return cooldownCurr;
    }
    public int getCooldownMax() {
        return cooldownMax;
    }

}
