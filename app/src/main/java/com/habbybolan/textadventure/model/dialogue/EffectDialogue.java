package com.habbybolan.textadventure.model.dialogue;

import com.habbybolan.textadventure.R;

public class EffectDialogue implements DialogueTypes {

    // type of Dot/Special effect
    private String type;
    // duration of the effect added
    private int duration;
    // image/icon of the effect
    private int imageResource;


    public EffectDialogue(String type, int duration, int imageResource) {
        this.type = type;
        this.duration = duration;
        this.imageResource = imageResource;
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
}
