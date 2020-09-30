package com.habbybolan.textadventure.view;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.habbybolan.textadventure.R;

public class InventoryInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_info);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
