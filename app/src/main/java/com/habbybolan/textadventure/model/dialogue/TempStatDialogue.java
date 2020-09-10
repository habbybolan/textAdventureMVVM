package com.habbybolan.textadventure.model.dialogue;

import com.habbybolan.textadventure.R;

public class TempStatDialogue implements DialogueTypes {


    private String type;
    private int amount;
    private int duration;

    public TempStatDialogue(String type, int amount, int duration) {
        this.type = type;
        this.amount = amount;
        this.duration = duration;
    }

    public String getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public int getViewType() {
        return R.layout.dialogue_temp_stat_details;
    }
}
