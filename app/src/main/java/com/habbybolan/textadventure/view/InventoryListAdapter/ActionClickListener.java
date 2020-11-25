package com.habbybolan.textadventure.view.InventoryListAdapter;

import com.habbybolan.textadventure.model.inventory.Action;

/*
list item click interface that notifies what index of an Inventory object was selected
 */
public interface ActionClickListener {
    void onClicked(Action object);

    void onInfoClick(Action object);
}
