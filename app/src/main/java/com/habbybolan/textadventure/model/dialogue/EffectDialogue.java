package com.habbybolan.textadventure.model.dialogue;

import com.habbybolan.textadventure.R;


/*
Dialogue object to display added effects to player character
  including Dot and Special effects
*/

public class EffectDialogue implements DialogueTypes {

    // type of Dot/Special effect
    private String type;
    // duration of the effect added
    private int duration;
    // image/icon of the effect
    private int imageResource;
    // if the duration is infinite/attached to an item
    private boolean isInfinite;


    public EffectDialogue(String type, int duration, int imageResource, boolean isInfinite) {
        this.type = type;
        this.duration = duration;
        this.imageResource = imageResource;
        this.isInfinite = isInfinite;
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
