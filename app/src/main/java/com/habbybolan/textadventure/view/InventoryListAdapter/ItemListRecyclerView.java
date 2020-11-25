package com.habbybolan.textadventure.view.InventoryListAdapter;

import android.content.Context;

import androidx.databinding.Observable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.habbybolan.textadventure.model.inventory.Item;
import com.habbybolan.textadventure.viewmodel.characterEntityViewModels.CharacterViewModel;
import com.habbybolan.textadventure.viewmodel.encounters.CombatViewModel;

import java.util.ArrayList;

public class ItemListRecyclerView implements InventoryListRecyclerView{
    private AbilityItemListAdapter adapter;
    private CharacterViewModel characterVM;
    ArrayList<Item> items = new ArrayList<>();;
    CombatViewModel combatVM;

    public ItemListRecyclerView(Context context, RecyclerView recyclerView, CharacterViewModel characterVM, CombatViewModel combatVM, ActionClickListener actionClickListener) {
        // set the layout manager to position the items
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        this.combatVM = combatVM;
        // list of items that are either consumable or have an ability
        for (Item item : characterVM.getItems()) {
            if (isValidActionItem(item)) items.add(item);
        }
        adapter = new AbilityItemListAdapter(items, actionClickListener);
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
                // only add valid items that can be used in combat
                Item item = characterVM.getItemObserverAdd().get();
                if (item != null) {
                    if (isValidActionItem(item)) {
                        items.add(item);
                        adapter.updateChange();
                    }
                }
            }
        };
        characterVM.getItemObserverAdd().addOnPropertyChangedCallback(callbackAdd);
        // listener for when an ability is removed from character
        Observable.OnPropertyChangedCallback callbackRemove = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                // only remove items that can be used in combat - otherwise they wont exist in the list
                Item item = characterVM.getItemObserverRemove().get();
                if (item != null) {
                    if (isValidActionItem(item)) {
                        items.remove(item);
                        combatVM.checkIfRemovedInventoryIsSelected(item);
                        adapter.updateChange();
                    }
                }

            }
        };
        characterVM.getItemObserverRemove().addOnPropertyChangedCallback(callbackRemove);
    }

    // returns true if the item is a consumable or has an ability
    private boolean isValidActionItem(Item item) {
        return item.getIsConsumable() || item.getAbility() != null;
    }
    // if there is a selected index stored, then remove and update all elements
    @Override
    public void unSelectIfOneSelected() {
        adapter.unSelectIfOneSelected();
    }
}
