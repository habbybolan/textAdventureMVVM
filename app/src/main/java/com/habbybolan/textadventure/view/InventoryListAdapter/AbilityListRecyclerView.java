package com.habbybolan.textadventure.view.InventoryListAdapter;

import android.content.Context;

import androidx.databinding.Observable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.habbybolan.textadventure.model.inventory.Ability;
import com.habbybolan.textadventure.viewmodel.characterEntityViewModels.CharacterViewModel;
import com.habbybolan.textadventure.viewmodel.encounters.CombatViewModel;

public class AbilityListRecyclerView implements InventoryListRecyclerView{

    private AbilityItemListAdapter adapter;
    private CharacterViewModel characterVM;
    private CombatViewModel combatVM;


    public AbilityListRecyclerView(Context context, RecyclerView recyclerView, CharacterViewModel characterVM, CombatViewModel combatVM, InventoryClickListener inventoryClickListener) {
        // set the layout manager to position the items
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        this.combatVM = combatVM;
        adapter = new AbilityItemListAdapter(characterVM.getAbilities(), inventoryClickListener);
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
        characterVM.getAbilityObserverAdd().addOnPropertyChangedCallback(callbackAdd);
        // listener for when an ability is removed from character
        Observable.OnPropertyChangedCallback callbackRemove = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                Ability ability = characterVM.getAbilityObserverRemove().get();
                if (ability != null) {
                    combatVM.checkIfRemovedInventoryIsSelected(ability);
                    adapter.updateChange();
                }
            }
        };
        characterVM.getAbilityObserverRemove().addOnPropertyChangedCallback(callbackRemove);
    }

    // if there is a selected index stored, then remove and update all elements
    @Override
    public void unSelectIfOneSelected() {
        adapter.unSelectIfOneSelected();
    }
}
