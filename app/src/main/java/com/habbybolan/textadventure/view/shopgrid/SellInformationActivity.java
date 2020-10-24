package com.habbybolan.textadventure.view.shopgrid;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.ActivitySellInformationBinding;
import com.habbybolan.textadventure.view.encounter.ShopFragment;
import com.habbybolan.textadventure.view.inventoryinfo.InventoryInfoFragment;


public class SellInformationActivity extends AppCompatActivity {

    private String stringInventory;
    private int cost;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // remove toolbar
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) getSupportActionBar().hide();

        ActivitySellInformationBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_sell_information);
        stringInventory = getIntent().getStringExtra(InventoryInfoFragment.INVENTORY_SERIALIZED);
        cost = getIntent().getIntExtra(InventoryInfoFragment.COST, -1);
        position = getIntent().getIntExtra(InventoryInfoFragment.POSITION, -1);
        if (stringInventory == null || cost == -1 || position == -1) throw new IllegalArgumentException("Value not sent through intent");
        setFragmentInfo(stringInventory);
    }

    // sets up the serialized inventory object
    private void setFragmentInfo(String stringInventory) {
        InventoryInfoFragment fragment = InventoryInfoFragment.newInstance(stringInventory);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_sell_info, fragment)
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
     * Button functionality for btn_confirm that calls ShopFragment to sell the inventory object at position.
     * @param view  btn_confirm view.
     */
    public void confirmSell(View view) {
        ShopFragment.getInstance().sellInventory(position);
        finish();
    }

    /*
    // sets up the button if the object to buy is an Weapon
    private void setUpWeaponInfo(Weapon weapon) {
        Button btn = findViewById(R.id.btn_confirm);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                character.setGold(character.getGold() + cost, model);
                character.removeWeapon(weapon, model);
                sellGridAdapter.removeGridModel(position);
                finish();
            }
        });
    }

    // sets up the button if the object to buy is an Ability
    private void setUpAbilityInfo(Ability ability) {
        Button btn = findViewById(R.id.btn_confirm);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                character.setGold(character.getGold() + cost, model);
                character.removeAbility(ability, model);
                sellGridAdapter.removeGridModel(position);
                finish();
            }
        });
    }

    // sets up the button if the object to buy is an Item
    private void setUpItemInfo(Item item) {
        Button btn = findViewById(R.id.btn_confirm);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                character.setGold(character.getGold() + cost, model);
                character.removeItem(item, model);
                sellGridAdapter.removeGridModel(position);
                finish();
            }
        });
    }*/
}
