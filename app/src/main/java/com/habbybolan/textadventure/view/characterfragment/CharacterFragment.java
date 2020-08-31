package com.habbybolan.textadventure.view.characterfragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.FragmentCharacterBinding;
import com.habbybolan.textadventure.viewmodel.CharacterViewModel;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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
        final CharacterAbilityListAdapter adapter = new CharacterAbilityListAdapter(characterVM.getAbilities(), new CharacterListClickListener() {
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
                Integer index = characterVM.getAbilityObserverAdd().get();
                if (index != null) adapter.updateNewAbilityIndex(index);
            }
        };
        characterVM.getAbilityObserverAdd().addOnPropertyChangedCallback(callbackAdd);
        // listener for when an ability is removed from character
        Observable.OnPropertyChangedCallback callbackRemove = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                Integer index = characterVM.getAbilityObserverRemove().get();
                if (index != null) adapter.updateRemovedAbilityIndex(index);
            }
        };
        characterVM.getAbilityObserverRemove().addOnPropertyChangedCallback(callbackRemove);
    }

    private void setUpWeaponRecyclerViewer() {
        RecyclerView recyclerView = characterBinding.rvWeaponList;
        final CharacterWeaponListAdapter adapter = new CharacterWeaponListAdapter(characterVM.getWeapons(), new CharacterListClickListener() {
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
                Integer index = characterVM.getWeaponObserverAdd().get();
                if (index != null) adapter.updateNewWeaponIndex(index);
            }
        };
        characterVM.getWeaponObserverAdd().addOnPropertyChangedCallback(callbackAdd);
        // listener for when an ability is removed from character
        Observable.OnPropertyChangedCallback callbackRemove = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                Integer index = characterVM.getWeaponObserverRemove().get();
                if (index != null) adapter.updateRemovedWeaponIndex(index);
            }
        };
        characterVM.getWeaponObserverRemove().addOnPropertyChangedCallback(callbackRemove);
    }

    private void setUpItemRecyclerViewer() {
        RecyclerView recyclerView = characterBinding.rvItemList;
        final CharacterItemListAdapter adapter = new CharacterItemListAdapter(characterVM.getItems(), new CharacterListClickListener() {
            @Override
            public void onLongClicked(int index) {
                characterVM.removeItemAtIndex(index);
            }
        });
        recyclerView.setAdapter(adapter);
        // set the layout manager to position the items
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // listener for when an item is added to character
        Observable.OnPropertyChangedCallback callbackAdd = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                Integer index = characterVM.getItemObserverAdd().get();
                if (index != null) adapter.updateNewItemIndex(index);
            }
        };
        characterVM.getItemObserverAdd().addOnPropertyChangedCallback(callbackAdd);

        // listener for when an item is removed from character
        Observable.OnPropertyChangedCallback callbackRemove = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                Integer index = characterVM.getItemObserverRemove().get();
                if (index != null) adapter.updateRemovedItemIndex(index);
            }
        };
        characterVM.getItemObserverRemove().addOnPropertyChangedCallback(callbackRemove);
    }

    // recyclerViewer and observers for DOT effects
    private void setUpDOTRecyclerViewer() {
        RecyclerView recyclerView = characterBinding.rvDots;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        final CharacterDotListAdapter adapter = new CharacterDotListAdapter(characterVM.mapIntoList(characterVM.getDotMap()));
        recyclerView.setAdapter(adapter);

        // observed whenever CharacterViewModel adds to dotMap
        Observable.OnPropertyChangedCallback callbackAdd = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                adapter.addNewDot(characterVM.getDotObserverAdd().get());
            }
        };
        characterVM.getDotObserverAdd().addOnPropertyChangedCallback(callbackAdd);

        // observed whenever CharacterViewModel removes from dotMap
        Observable.OnPropertyChangedCallback callbackDelete = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                adapter.removeDot(characterVM.getDotObserverRemove().get());
            }
        };
        characterVM.getDotObserverRemove().addOnPropertyChangedCallback(callbackDelete);
    }

    // recyclerViewer and observers for special effects
    private void setUpSpecialRecyclerViewer() {
        RecyclerView recyclerView = characterBinding.rvSpecialEffects;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        final CharacterSpecialListAdapter adapter = new CharacterSpecialListAdapter(characterVM.mapIntoList(characterVM.getSpecialMap()));
        recyclerView.setAdapter(adapter);

        // observed whenever CharacterViewModel adds to dotMap
        Observable.OnPropertyChangedCallback callbackAdd = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                adapter.addNewSpecial(characterVM.getSpecialObserverAdd().get());
            }
        };
        characterVM.getSpecialObserverAdd().addOnPropertyChangedCallback(callbackAdd);

        // observed whenever CharacterViewModel removes from dotMap
        Observable.OnPropertyChangedCallback callbackDelete = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                adapter.removeSpecial(characterVM.getSpecialObserverRemove().get());
            }
        };
        characterVM.getSpecialObserverRemove().addOnPropertyChangedCallback(callbackDelete);
    }
}
