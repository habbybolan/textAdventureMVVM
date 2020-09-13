package com.habbybolan.textadventure.model.dialogue;

import com.habbybolan.textadventure.R;

import org.json.JSONException;
import org.json.JSONObject;

public class StatDialogue implements DialogueType {

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

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(DIALOGUE_TYPE, TYPE_STAT);
        jsonObject.put(TYPE, type);
        jsonObject.put(AMOUNT, amount);
        return jsonObject;
    }
}
