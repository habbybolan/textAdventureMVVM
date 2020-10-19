package com.habbybolan.textadventure.model.dialogue;

import com.habbybolan.textadventure.R;

import org.json.JSONException;
import org.json.JSONObject;

public class ExpDialogue implements DialogueType {

    private int amount;
    public ExpDialogue(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public int getViewType() {
        return R.layout.dialogue_exp_details;
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(DIALOGUE_TYPE, TYPE_EXP);
        jsonObject.put(AMOUNT, amount);
        return jsonObject;
    }
}
