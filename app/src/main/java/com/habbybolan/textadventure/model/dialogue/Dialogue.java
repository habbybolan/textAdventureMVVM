package com.habbybolan.textadventure.model.dialogue;

import com.habbybolan.textadventure.R;
/*
Represents a simple dialogue object to be shown in the DialogueAdapter
 */
public class Dialogue implements DialogueTypes {
    private String dialogue;

    public Dialogue(String dialogue) {
        this.dialogue = dialogue;
    }

    public String getDialogue() {
        return dialogue;
    }


    @Override
    public int getViewType() {
        return R.layout.dialogue_details;
    }
}
