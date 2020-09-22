package com.habbybolan.textadventure.view.InventoryListAdapter;

import android.content.Context;

import androidx.databinding.Observable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.habbybolan.textadventure.viewmodel.CharacterViewModel;

public class WeaponListRecyclerView implements InventoryListRecyclerView {

    private InventoryListAdapter adapter;
    private CharacterViewModel characterVM;

    public WeaponListRecyclerView(Context context, RecyclerView recyclerView, CharacterViewModel characterVM, InventoryClickListener inventoryClickListener) {
        // set the layout manager to position the items
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        adapter = new InventoryListAdapter(characterVM.getWeapons(), inventoryClickListener);
        recyclerView.setAdapter(adapter);
        this.characterVM = characterVM;
        setInventoryListeners();
    }

    @Override
    public void setInventoryListeners() {
        // listener for when an ability is added to character
        Observable.OnPropertyChangedCallback callbackAdd = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                adapter.updateChange();
            }
        };
        characterVM.getWeaponObserverAdd().addOnPropertyChangedCallback(callbackAdd);
        // listener for when an ability is removed from character
        Observable.OnPropertyChangedCallback callbackRemove = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                adapter.updateChange();
            }
        };
        characterVM.getWeaponObserverRemove().addOnPropertyChangedCallback(callbackRemove);
    }

    // if there is a selected index stored, then remove and update all elements
    @Override
    public void unSelectIfOneSelected() {
        adapter.unSelectIfOneSelected();
    }
}
