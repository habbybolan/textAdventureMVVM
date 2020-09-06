package com.habbybolan.textadventure.model.dialogue;


import com.habbybolan.textadventure.R;

/*
Dialogue object to display the change in health of player character
 */
public class HealthDialogue implements DialogueTypes{

    private int healthChange;

    public HealthDialogue(int healthChange) {
        this.healthChange = healthChange;
    }

    public int getHealthChange() {
        return healthChange;
    }


    @Override
    public int getViewType() {
        return R.layout.dialogue_health_details;
    }

    public int getIcon() {
        return R.drawable.heart_icon;
    }
}
