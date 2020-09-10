package com.habbybolan.textadventure.model.dialogue;

import com.habbybolan.textadventure.R;

public class StatDialogue implements DialogueTypes {

    private String type;
    private int amount;

    public StatDialogue(String type, int amount) {
        this.type = type;
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public int getViewType() {
        return R.layout.dialogue_stat_details;
    }
}
