package com.habbybolan.textadventure.model.dialogue;

import org.json.JSONException;
import org.json.JSONObject;

public interface DialogueType {

    // key values for JSON storing of DialogueType
    String DIALOGUE_TYPE = "type_dialogue";
    String TYPE = "type";
    String NAME = "name";
    String DIALOGUE = "dialogue";
    String IMAGE_RESOURCE = "image_resource";
    String IS_ADDED = "is_added";
    String IS_INDEFINITE = "is_indefinite";
    String DURATION = "duration";
    String AMOUNT = "amount";

    // types of dialogue as value for key DIALOGUE_TYPE
    String TYPE_DIALOGUE = "dialogue_type";
    String TYPE_EFFECT = "effect_dialogue_type";
    String TYPE_HEALTH = "health_dialogue_type";
    String TYPE_MANA = "mana_dialogue_type";
    String TYPE_INVENTORY = "inventory_dialogue_type";
    String TYPE_STAT = "stat_dialogue_type";
    String TYPE_TEMP_STAT = "temp_stat_dialogue_type";

    int getViewType();
    JSONObject toJSON() throws JSONException;
}
