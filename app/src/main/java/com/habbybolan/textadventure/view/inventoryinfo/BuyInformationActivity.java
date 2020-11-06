package com.habbybolan.textadventure.view.inventoryinfo;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.view.encounter.ShopFragment;
import com.habbybolan.textadventure.viewmodel.InventoryInfoViewModel;


/**
 * displays the weapon/item/ability clicked on from gridView in shop encounter buy grid.
 * Creates a button to buy the Inventory object being displayed.
 */
public class BuyInformationActivity extends InventoryInfoDisplay {

    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // remove toolbar
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_buy_information);

        String stringInventory = getIntent().getStringExtra(InventoryInfoFragment.INVENTORY_SERIALIZED);
        int cost = getIntent().getIntExtra(InventoryInfoFragment.COST, 0);
        position = getIntent().getIntExtra(InventoryInfoFragment.POSITION, 0);

        if (stringInventory == null || cost == -1 || position == -1) throw new IllegalArgumentException("Value not sent through intent");
        InventoryInfoViewModel.newInstance();
        inventoryInfoViewModel = InventoryInfoViewModel.getInstance();
        setInventoryInfoListener();
        inventoryInfoViewModel.setInventoryInfoObservable(stringInventory);
    }

    /**
     * Calls functionality for the back button pressed.
     * @param view  Back button view
     */
    public void backButton(View view) {
        backButtonFunctionality();
    }

    /**
     * Button functionality for btn_confirm that calls ShopFragment to buy the inventory object at position.
     * @param view  btn_confirm view.
     */
    public void confirmBuy(View view) {
        ShopFragment.getInstance().buyInventory(position);
        finish();
    }


    @Override
    int getFragmentContainer() {
        return R.id.fragment_buy_info;
    }
}
