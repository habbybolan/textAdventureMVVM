package com.habbybolan.textadventure.model.dialogue;

import com.habbybolan.textadventure.R;

import org.json.JSONException;
import org.json.JSONObject;

public class TempStatDialogue implements DialogueType {


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

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(DIALOGUE_TYPE, TYPE_TEMP_STAT);
        jsonObject.put(TYPE, type);
        jsonObject.put(AMOUNT, amount);
        jsonObject.put(DURATION, duration);
        return jsonObject;
    }
}
