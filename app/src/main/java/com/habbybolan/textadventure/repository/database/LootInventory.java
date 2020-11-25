package com.habbybolan.textadventure.repository.database;

import android.content.Context;

public class LootInventory {

    DatabaseAdapter databaseAdapter;
    public LootInventory(Context context) {
        databaseAdapter = new DatabaseAdapter(context);
        databaseAdapter.open();
    }


    public void closeDatabase() {
        databaseAdapter.close();
    }
}
