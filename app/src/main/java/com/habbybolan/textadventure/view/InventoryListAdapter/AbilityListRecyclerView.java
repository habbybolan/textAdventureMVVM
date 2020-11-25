package com.habbybolan.textadventure.view.InventoryListAdapter;

import android.content.Context;

import androidx.databinding.ObservableList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.habbybolan.textadventure.viewmodel.characterEntityViewModels.CharacterViewModel;
import com.habbybolan.textadventure.viewmodel.encounters.CombatViewModel;

public class AbilityListRecyclerView implements InventoryListRecyclerView{

    private AbilityItemListAdapter adapter;
    private CharacterViewModel characterVM;
    private CombatViewModel combatVM;


    public AbilityListRecyclerView(Context context, RecyclerView recyclerView, CharacterViewModel characterVM, CombatViewModel combatVM, ActionClickListener actionClickListener) {
        // set the layout manager to position the items
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        this.combatVM = combatVM;
        adapter = new AbilityItemListAdapter(characterVM.getAbilities(), actionClickListener);
        recyclerView.setAdapter(adapter);
        this.characterVM = characterVM;
        setInventoryListeners();
    }

    @Override
    public void setInventoryListeners() {

        // observed whenever changes in character abilities
        characterVM.getAbilities().addOnListChangedCallback(new ObservableList.OnListChangedCallback() {
            @Override
            public void onChanged(ObservableList sender) {}
            @Override
            public void onItemRangeChanged(ObservableList sender, int positionStart, int itemCount) {}
            @Override
            public void onItemRangeInserted(ObservableList sender, int positionStart, int itemCount) {
                adapter.updateChange();
            }
            @Override
            public void onItemRangeMoved(ObservableList sender, int fromPosition, int toPosition, int itemCount) {}
            @Override
            public void onItemRangeRemoved(ObservableList sender, int positionStart, int itemCount) {
                adapter.updateChange();
            }
        });
    }

    // if there is a selected index stored, then remove and update all elements
    @Override
    public void unSelectIfOneSelected() {
        adapter.unSelectIfOneSelected();
    }
}
