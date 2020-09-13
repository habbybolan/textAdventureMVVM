package com.habbybolan.textadventure.model.dialogue;


import com.habbybolan.textadventure.R;

import org.json.JSONException;
import org.json.JSONObject;

/*
Dialogue object to display the change in health of player character
 */
public class HealthDialogue implements DialogueType {

    private int amount;

    public HealthDialogue(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }


    @Override
    public int getViewType() {
        return R.layout.dialogue_health_details;
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(DIALOGUE_TYPE, TYPE_HEALTH);
        jsonObject.put(AMOUNT, amount);
        return jsonObject;
    }

    public int getIcon() {
        return R.drawable.heart_icon;
    }
}
