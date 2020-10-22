package com.habbybolan.textadventure.view.inventoryinfo;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.ActivityInventoryInfoBinding;

public class InventoryInfoActivity extends AppCompatActivity {

    ActivityInventoryInfoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove toolbar
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) getSupportActionBar().hide();

        binding = DataBindingUtil.setContentView(this, R.layout.activity_inventory_info);
        String stringInventory = getIntent().getStringExtra(InventoryInfoFragment.INVENTORY_SERIALIZED);

        if (stringInventory == null) throw new IllegalArgumentException();
        setFragmentInfo(stringInventory);
    }

    // sets up the serialized inventory object
    private void setFragmentInfo(String stringInventory) {
        InventoryInfoFragment fragment = InventoryInfoFragment.newInstance(stringInventory);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    // destroy activity on back pressed
    public void onBackPressed() {
        finish();
    }
}
