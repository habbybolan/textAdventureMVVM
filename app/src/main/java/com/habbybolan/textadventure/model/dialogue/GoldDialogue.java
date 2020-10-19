package com.habbybolan.textadventure.model.dialogue;

import com.habbybolan.textadventure.R;

import org.json.JSONException;
import org.json.JSONObject;

public class GoldDialogue implements DialogueType {

    private int amount;
    public GoldDialogue(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public int getViewType() {
        return R.layout.dialogue_gold_details;
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(DIALOGUE_TYPE, TYPE_GOLD);
        jsonObject.put(AMOUNT, amount);
        return jsonObject;
    }
}
