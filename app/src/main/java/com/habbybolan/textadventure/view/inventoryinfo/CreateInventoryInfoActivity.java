package com.habbybolan.textadventure.view.inventoryinfo;

import android.app.Activity;
import android.content.Intent;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.model.inventory.Inventory;

import org.json.JSONException;

public class CreateInventoryInfoActivity {

    public static void createInventoryInfoActivity(Activity context, Inventory inventory) {
        Intent intent = new Intent(context, InventoryInfoActivity.class);
        try {
            intent.putExtra(InventoryInfoFragment.INVENTORY_SERIALIZED, inventory.serializeToJSON().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.enter_from_right, R.anim.fade_out);
    }
}
