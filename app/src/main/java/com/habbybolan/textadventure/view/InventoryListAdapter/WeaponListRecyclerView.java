package com.habbybolan.textadventure.view.InventoryListAdapter;

import android.content.Context;

import androidx.databinding.Observable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.habbybolan.textadventure.model.inventory.weapon.Weapon;
import com.habbybolan.textadventure.viewmodel.characterEntityViewModels.CharacterViewModel;
import com.habbybolan.textadventure.viewmodel.encounters.CombatViewModel;

public class WeaponListRecyclerView implements InventoryListRecyclerView {

    private WeaponListAdapter adapter;
    private CharacterViewModel characterVM;
    private CombatViewModel combatVM;

    public WeaponListRecyclerView(Context context, RecyclerView recyclerView, CharacterViewModel characterVM, CombatViewModel combatVM, ActionClickListener actionClickListener) {
        // set the layout manager to position the items
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        this.combatVM = combatVM;
        adapter = new WeaponListAdapter(characterVM.getWeapons(), actionClickListener);
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
                Weapon weapon = characterVM.getWeaponObserverRemove().get();
                if (weapon != null) {
                    combatVM.checkIfRemovedInventoryIsSelected(weapon);
                    adapter.updateChange();
                }
            }
        };
        characterVM.getWeaponObserverRemove().addOnPropertyChangedCallback(callbackRemove);
    }

    // if there is a selected index stored, then remove and update all elements and collapses list
    @Override
    public void unSelectIfOneSelected() {
        adapter.unSelectIfOneSelected();
    }

    @Override
    public void disableAllViews(boolean isEnabled) {
        adapter.disableAllViews(isEnabled);
    }
}
