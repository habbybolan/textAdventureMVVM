package com.habbybolan.textadventure.model.dialogue;

import com.habbybolan.textadventure.R;

import org.json.JSONException;
import org.json.JSONObject;

public class InventoryDialogue implements DialogueType {

    // image resource of inventory object
    private int imageResource;
    // name of inventory object
    private String name;
    private String type;
    private boolean isAdded;


    public InventoryDialogue(String name, int imageResource, String type, boolean isAdded) {
        this.imageResource = imageResource;
        this.name = name;
        this.type = type;
        this.isAdded = isAdded;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getName() {
        return name;
    }


    @Override
    public int getViewType() {
        return R.layout.dialogue_inventory_details;
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(DIALOGUE_TYPE, TYPE_INVENTORY);
        jsonObject.put(TYPE, type);
        jsonObject.put(NAME, name);
        jsonObject.put(IMAGE_RESOURCE, imageResource);
        jsonObject.put(IS_ADDED, isAdded);
        return jsonObject;
    }

    public int getIcon() {
        return imageResource;
    }

    public String getType() {
        return type;
    }

    public boolean getIsAdded() {
        return isAdded;
    }
}
