package com.habbybolan.textadventure.view.characterfragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.databinding.ObservableList;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.FragmentCharacterBinding;
import com.habbybolan.textadventure.viewmodel.characterEntityViewModels.CharacterViewModel;

public class CharacterFragment extends Fragment {

    private CharacterViewModel characterVM;
    private FragmentCharacterBinding characterBinding;

    public CharacterFragment(CharacterViewModel characterVM) {
        this.characterVM = characterVM;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        characterBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_character, container, false);
        characterBinding.setCharacterVM(characterVM);
        setUpItemRecyclerViewer();
        setUpWeaponRecyclerViewer();
        setUpAbilityRecyclerViewer();
        setUpDOTRecyclerViewer();
        setUpSpecialRecyclerViewer();
        return characterBinding.getRoot();
    }

    private void setUpAbilityRecyclerViewer() {
        RecyclerView recyclerView = characterBinding.rvAbilityList;
        final CharacterAbilityListAdapter adapter = new CharacterAbilityListAdapter(characterVM.getAbilities(), characterVM, new CharacterListClickListener() {
            @Override
            public void onLongClicked(int index) {
                characterVM.removeAbilityAtIndex(index);
            }
        });
        recyclerView.setAdapter(adapter);
        // set the layout manager to position the items
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // listener for when an ability is added to character
        Observable.OnPropertyChangedCallback callbackAdd = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                adapter.updateAbilityChange();
            }
        };
        characterVM.getAbilityObserverAdd().addOnPropertyChangedCallback(callbackAdd);
        // listener for when an ability is removed from character
        Observable.OnPropertyChangedCallback callbackRemove = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                adapter.updateAbilityChange();
            }
        };
        characterVM.getAbilityObserverRemove().addOnPropertyChangedCallback(callbackRemove);
    }

    private void setUpWeaponRecyclerViewer() {
        RecyclerView recyclerView = characterBinding.rvWeaponList;
        final CharacterWeaponListAdapter adapter = new CharacterWeaponListAdapter(characterVM.getWeapons(), characterVM, new CharacterListClickListener() {
            @Override
            public void onLongClicked(int index) {
                characterVM.removeWeaponAtIndex(index);
            }
        });
        recyclerView.setAdapter(adapter);
        // set the layout manager to position the items
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // listener for when an ability is added to character
        Observable.OnPropertyChangedCallback callbackAdd = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                adapter.updateWeaponChange();
            }
        };
        characterVM.getWeaponObserverAdd().addOnPropertyChangedCallback(callbackAdd);
        // listener for when an ability is removed from character
        Observable.OnPropertyChangedCallback callbackRemove = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                adapter.updateWeaponChange();
            }
        };
        characterVM.getWeaponObserverRemove().addOnPropertyChangedCallback(callbackRemove);
    }

    private void setUpItemRecyclerViewer() {
        RecyclerView recyclerView = characterBinding.rvItemList;
        final CharacterItemListAdapter adapter = new CharacterItemListAdapter(characterVM.getItems(), characterVM,  new CharacterListClickListener() {
            @Override
            public void onLongClicked(int index) {
                characterVM.removeItemAtIndex(index);
            }
        }, new CharacterListClickListener() {
            @Override
            public void onLongClicked(int index) {
                characterVM.consumeItemAtIndex(index);
            }
        });
        recyclerView.setAdapter(adapter);
        // set the layout manager to position the items
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // listener for when an item is added to character
        Observable.OnPropertyChangedCallback callbackAdd = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                adapter.updateItemChange();
            }
        };
        characterVM.getItemObserverAdd().addOnPropertyChangedCallback(callbackAdd);

        // listener for when an item is removed from character
        Observable.OnPropertyChangedCallback callbackRemove = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                adapter.updateItemChange();
            }
        };
        characterVM.getItemObserverRemove().addOnPropertyChangedCallback(callbackRemove);
    }

    // recyclerViewer and observers for DOT effects
    private void setUpDOTRecyclerViewer() {
        RecyclerView recyclerView = characterBinding.rvDots;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        final CharacterDotListAdapter adapter = new CharacterDotListAdapter(characterVM.getDotList());
        recyclerView.setAdapter(adapter);


        // observed whenever CharacterViewModel observes change in dotList
        characterVM.getDotObserver().addOnListChangedCallback(new ObservableList.OnListChangedCallback() {
            @Override
            public void onChanged(ObservableList sender) {}
            @Override
            public void onItemRangeChanged(ObservableList sender, int positionStart, int itemCount) {
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onItemRangeInserted(ObservableList sender, int positionStart, int itemCount) {
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onItemRangeMoved(ObservableList sender, int fromPosition, int toPosition, int itemCount) {}
            @Override
            public void onItemRangeRemoved(ObservableList sender, int positionStart, int itemCount) {
                adapter.notifyDataSetChanged();
            }
        });
    }

    // recyclerViewer and observers for special effects
    private void setUpSpecialRecyclerViewer() {
        RecyclerView recyclerView = characterBinding.rvSpecialEffects;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        final CharacterSpecialListAdapter adapter = new CharacterSpecialListAdapter(characterVM.getSpecialList());
        recyclerView.setAdapter(adapter);


        // observed whenever CharacterViewModel observes change in dotList
        characterVM.getSpecialObserver().addOnListChangedCallback(new ObservableList.OnListChangedCallback() {
            @Override
            public void onChanged(ObservableList sender) {
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onItemRangeChanged(ObservableList sender, int positionStart, int itemCount) {
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onItemRangeInserted(ObservableList sender, int positionStart, int itemCount) {
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onItemRangeMoved(ObservableList sender, int fromPosition, int toPosition, int itemCount) {}
            @Override
            public void onItemRangeRemoved(ObservableList sender, int positionStart, int itemCount) {
                adapter.notifyDataSetChanged();
            }
        });
    }
}
