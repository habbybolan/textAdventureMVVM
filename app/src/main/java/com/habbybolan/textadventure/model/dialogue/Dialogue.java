package com.habbybolan.textadventure.model.dialogue;

import com.habbybolan.textadventure.R;

import org.json.JSONException;
import org.json.JSONObject;

/*
Represents a simple dialogue object to be shown in the DialogueAdapter
 */
public class Dialogue implements DialogueType {
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

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(DIALOGUE_TYPE, TYPE_DIALOGUE);
        jsonObject.put(DIALOGUE, dialogue);
        return jsonObject;
    }
}
