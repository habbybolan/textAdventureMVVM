package com.habbybolan.textadventure.view.inventoryinfo;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.ActivityInventoryInfoBinding;
import com.habbybolan.textadventure.viewmodel.InventoryInfoViewModel;

public class InventoryInfoActivity extends AppCompatActivity {

    ActivityInventoryInfoBinding binding;
    InventoryInfoViewModel inventoryInfoViewModel; ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove toolbar
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) getSupportActionBar().hide();

        binding = DataBindingUtil.setContentView(this, R.layout.activity_inventory_info);
        String stringInventory = getIntent().getStringExtra(InventoryInfoFragment.INVENTORY_SERIALIZED);

        if (stringInventory == null) throw new IllegalArgumentException();
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
                createNewFragment();
            }
        };
        inventoryInfoViewModel.getInventoryInfoObservable().addOnPropertyChangedCallback(callback);
    }

    private void createNewFragment() {
        InventoryInfoFragment fragment = InventoryInfoFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .add(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    // destroy activity on back pressed
    public void onBackPressed() {
        finish();
    }
}
