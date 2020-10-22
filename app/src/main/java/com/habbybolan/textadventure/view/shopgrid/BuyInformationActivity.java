package com.habbybolan.textadventure.view.shopgrid;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.ActivityBuyInformationBinding;
import com.habbybolan.textadventure.view.encounter.ShopFragment;
import com.habbybolan.textadventure.view.inventoryinfo.InventoryInfoFragment;


/**
 * displays the weapon/item/ability clicked on from gridView in shop encounter
 */

public class BuyInformationActivity extends AppCompatActivity {

    private ActivityBuyInformationBinding binding;

    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // remove toolbar
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) getSupportActionBar().hide();

        binding = DataBindingUtil.setContentView(this, R.layout.activity_buy_information);
        String stringInventory = getIntent().getStringExtra(InventoryInfoFragment.INVENTORY_SERIALIZED);
        int cost = getIntent().getIntExtra(InventoryInfoFragment.COST, 0);
        position = getIntent().getIntExtra(InventoryInfoFragment.POSITION, 0);

        if (stringInventory == null) throw new IllegalArgumentException();
        setFragmentInfo(stringInventory);
    }

    // sets up the serialized inventory object
    private void setFragmentInfo(String stringInventory) {
        InventoryInfoFragment fragment = InventoryInfoFragment.newInstance(stringInventory);
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

    /*
    // sets up the button if the object to buy is an Weapon
    private void setUpWeaponBtn(Weapon weapon, Activity activity, Toast toast) {
        Button btn = findViewById(R.id.btn_confirm);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (character.getGold() < gridModel.getCost()) {
                    // not enough gold, tell user they can't buy weapon/item/ability
                    ToastCustomMessage.showToast(activity, getResources().getString(R.string.not_enough_gold), toast);
                } else if (character.getWeapons().size() < Character.MAX_WEAPONS) {
                    // not full on weapons, can buy the weapon
                    character.addWeapon(weapon, model);
                    afterPurchase();
                } else {
                    // full on weapons, prompt user to drop a weapon
                    ToastCustomMessage.showToast(activity, getResources().getString(R.string.full_on) + "weapons", toast);
                }
            }
        });
    }

    // sets up the button if the object to buy is an Ability
    private void setUpAbilityBtn(Ability ability, Activity activity, Toast toast) {
        Button btn = findViewById(R.id.btn_confirm);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (character.getGold() < gridModel.getCost()) {
                    // not enough gold, tell user they can't buy weapon/item/ability
                    ToastCustomMessage.showToast(activity, getResources().getString(R.string.not_enough_gold), toast);
                } else if (character.getAbilities().size() < Character.MAX_ABILITIES) {
                    // not full on abilities, can buy the weapon
                    character.addAbility(ability, model);
                    afterPurchase();
                } else {
                    // full on abilities, prompt user to drop an ability
                    ToastCustomMessage.showToast(activity, getResources().getString(R.string.full_on) + "abilities", toast);
                }
            }
        });
    }

    // sets up the button if the object to buy is an Item
    private void setUpItemBtn(Item item, Activity activity, Toast toast) {
        Button btn = findViewById(R.id.btn_confirm);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (character.getGold() < gridModel.getCost()) {
                    // not enough gold, tell user they can't buy weapon/item/ability
                    ToastCustomMessage.showToast(activity, getResources().getString(R.string.not_enough_gold), toast);
                } else if (character.getItems().size() < Character.MAX_ITEMS) {
                    // not full on abilities, can buy the weapon
                    character.addItem(item, model);
                    afterPurchase();
                } else {
                    // full on Items
                    ToastCustomMessage.showToast(activity, getResources().getString(R.string.full_on) + "items", toast);
                }
            }
        });
    }

    // after adding the Weapon/Item/Ability to character inventory
    private void afterPurchase() {
        character.setGold(character.getGold() - cost, model);
        SaveDataLocally save = new SaveDataLocally(getApplicationContext());
        save.saveCharacterLocally(character);
        buyGridAdapter.removeGridModel(position);
        finish();
    }*/
}
