package com.habbybolan.textadventure.model.dialogue;

import com.habbybolan.textadventure.R;

public class InventoryDialogue implements DialogueTypes {

    // image resource of inventory object
    private int imageResource;
    // name of inventory object
    private String name;

    public InventoryDialogue(int imageResource, String name) {
        this.imageResource = imageResource;
        this.name = name;
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

    public int getIcon() {
        return imageResource;
    }
}
