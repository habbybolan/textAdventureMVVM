package com.habbybolan.textadventure.model.dialogue;

import com.habbybolan.textadventure.R;

import org.json.JSONException;
import org.json.JSONObject;

public class ManaDialogue implements DialogueType {

    private int amount;

    public ManaDialogue(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public int getViewType() {
        return R.layout.dialogue_mana_details;
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(DIALOGUE_TYPE, TYPE_MANA);
        jsonObject.put(AMOUNT, amount);
        return jsonObject;
    }

    public int getIcon() {
        return R.drawable.mana_icon;
    }
}
