package com.habbybolan.textadventure.model.dialogue;

import com.habbybolan.textadventure.R;

import org.json.JSONException;
import org.json.JSONObject;


/*
Dialogue object to display added effects to player character
  including Dot and Special effects, and temp stat/bar increases
*/

public class EffectDialogue implements DialogueType {

    // type of Dot/Special effect
    private String type;
    // duration of the effect added
    private int duration;
    // image/icon of the effect
    private int imageResource;
    // if the duration is infinite/attached to an item
    private boolean isIndefinite;


    public EffectDialogue(String type, int duration, int imageResource, boolean isIndefinite) {
        this.type = type;
        this.duration = duration;
        this.imageResource = imageResource;
        this.isIndefinite = isIndefinite;
    }

    public String getType() {
        return type;
    }
    public int getDuration() {
        return duration;
    }
    public int getImageResource() {
        return imageResource;
    }

    @Override
    public int getViewType() {
        return R.layout.dialogue_effect_details;
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(DIALOGUE_TYPE, TYPE_EFFECT);
        jsonObject.put(TYPE, type);
        jsonObject.put(DURATION, duration);
        jsonObject.put(IMAGE_RESOURCE, imageResource);
        jsonObject.put(IS_INDEFINITE, isIndefinite);
        return jsonObject;
    }
}
