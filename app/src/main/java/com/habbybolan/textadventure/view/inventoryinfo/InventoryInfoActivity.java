package com.habbybolan.textadventure.view.inventoryinfo;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.viewmodel.InventoryInfoViewModel;

public class InventoryInfoActivity extends InventoryInfoDisplay {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove toolbar
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_inventory_info);

        String stringInventory = getIntent().getStringExtra(InventoryInfoFragment.INVENTORY_SERIALIZED);

        if (stringInventory == null) throw new IllegalArgumentException();
        InventoryInfoViewModel.newInstance();
        inventoryInfoViewModel = InventoryInfoViewModel.getInstance();
        setInventoryInfoListener();
        inventoryInfoViewModel.setInventoryInfoObservable(stringInventory);
    }

    @Override
    int getFragmentContainer() {
        return R.id.container_info_fragment;
    }
}
