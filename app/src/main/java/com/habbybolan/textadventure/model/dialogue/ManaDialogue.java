package com.habbybolan.textadventure.model.dialogue;

import com.habbybolan.textadventure.R;

public class ManaDialogue implements DialogueTypes {

    private int manaChange;

    public ManaDialogue(int manaChange) {
        this.manaChange = manaChange;
    }

    public int getManaChange() {
        return manaChange;
    }

    @Override
    public int getViewType() {
        return R.layout.dialogue_mana_details;
    }

    public int getIcon() {
        return R.drawable.mana_icon;
    }
}
