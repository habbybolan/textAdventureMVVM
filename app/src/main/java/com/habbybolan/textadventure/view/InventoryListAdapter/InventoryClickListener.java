package com.habbybolan.textadventure.view.InventoryListAdapter;

import com.habbybolan.textadventure.model.inventory.Inventory;

/*
list item click interface that notifies what index of an Inventory object was selected
 */
public interface InventoryClickListener {
    public void onClicked(Inventory object);
}
