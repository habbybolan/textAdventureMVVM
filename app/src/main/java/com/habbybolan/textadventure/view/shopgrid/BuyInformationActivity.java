package com.habbybolan.textadventure.view.shopgrid;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.ActivityBuyInformationBinding;
import com.habbybolan.textadventure.view.encounter.ShopFragment;
import com.habbybolan.textadventure.view.inventoryinfo.InventoryInfoFragment;
import com.habbybolan.textadventure.viewmodel.InventoryInfoViewModel;


/**
 * displays the weapon/item/ability clicked on from gridView in shop encounter
 */

public class BuyInformationActivity extends AppCompatActivity {

    private ActivityBuyInformationBinding binding;
    private InventoryInfoViewModel inventoryInfoViewModel;

    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // remove toolbar
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) getSupportActionBar().hide();

        // todo: change to work with view model and fragment changes
        binding = DataBindingUtil.setContentView(this, R.layout.activity_buy_information);
        String stringInventory = getIntent().getStringExtra(InventoryInfoFragment.INVENTORY_SERIALIZED);
        int cost = getIntent().getIntExtra(InventoryInfoFragment.COST, 0);
        position = getIntent().getIntExtra(InventoryInfoFragment.POSITION, 0);

        if (stringInventory == null || cost == -1 || position == -1) throw new IllegalArgumentException("Value not sent through intent");
        InventoryInfoViewModel.newInstance();
        inventoryInfoViewModel = InventoryInfoViewModel.getInstance();
        setInventoryInfoListener();
        inventoryInfoViewModel.setInventoryInfoObservable(stringInventory);
    }

    // sets up the serialized inventory object
    private void setInventoryInfoListener() {
        final Observable.OnPropertyChangedCallback callback = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                setFragmentInfo();
            }
        };
        inventoryInfoViewModel.getInventoryInfoObservable().addOnPropertyChangedCallback(callback);
    }

    // sets up the serialized inventory object
    private void setFragmentInfo() {
        InventoryInfoFragment fragment = InventoryInfoFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_buy_info, fragment)
                .commit();
    }

    public void goBack(View v) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    /**
     * Button functionality for btn_confirm that calls ShopFragment to buy the inventory object at position.
     * @param view  btn_confirm view.
     */
    public void confirmBuy(View view) {
        ShopFragment.getInstance().buyInventory(position);
        finish();
    }
}
