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

    CharacterViewModel characterViewModel;
    private FragmentCharacterBinding characterBinding;

    public CharacterFragment(CharacterViewModel characterViewModel) {
        this.characterViewModel = characterViewModel;
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
        View view = inflater.inflate(R.layout.fragment_character, container, false);
        setUpItemRecyclerViewer(view);
        setUpWeaponRecyclerViewer(view);
        setUpAbilityRecyclerViewer(view);
        setUpDOTRecyclerViewer(view);
        setUpSpecialRecyclerViewer(view);
        return characterBinding.getRoot();
    }

    private void setUpAbilityRecyclerViewer(View view) {
        RecyclerView recyclerView = characterBinding.rvAbilityList;
        CharacterAbilityListAdapter adapter = new CharacterAbilityListAdapter(characterViewModel.getCharacter().getAbilities(), new CharacterListClickListener() {
            @Override
            public void onLongClicked(int position) {
                // todo: remove ability
            }
        });
        recyclerView.setAdapter(adapter);
        // set the layout manager to position the items
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setUpWeaponRecyclerViewer(View view) {
        RecyclerView recyclerView = characterBinding.rvWeaponList;
        CharacterWeaponListAdapter adapter = new CharacterWeaponListAdapter(characterViewModel.getCharacter().getWeapons(), new CharacterListClickListener() {
            @Override
            public void onLongClicked(int position) {
                // todo: remove weapon
            }
        });
        recyclerView.setAdapter(adapter);
        // set the layout manager to position the items
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setUpItemRecyclerViewer(View view) {
        RecyclerView recyclerView = characterBinding.rvItemList;
        CharacterItemListAdapter adapter = new CharacterItemListAdapter(characterViewModel.getCharacter().getItems(), new CharacterListClickListener() {
            @Override
            public void onLongClicked(int position) {
                // todo: remove item
            }
        });
        recyclerView.setAdapter(adapter);
        // set the layout manager to position the items
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    // recyclerViewer and observers for DOT effects
    private void setUpDOTRecyclerViewer(View view) {
        RecyclerView recyclerView = characterBinding.rvDots;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        final CharacterDotListAdapter adapter = new CharacterDotListAdapter(characterViewModel.mapIntoList(characterViewModel.getDotMap()));
        recyclerView.setAdapter(adapter);

        // observed whenever CharacterViewModel adds to dotMap
        Observable.OnPropertyChangedCallback callbackAdd = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                adapter.addNewDot(characterViewModel.getDotObserverAdd().get());
            }
        };
        characterViewModel.getDotObserverAdd().addOnPropertyChangedCallback(callbackAdd);

        // observed whenever CharacterViewModel removes from dotMap
        Observable.OnPropertyChangedCallback callbackDelete = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                adapter.removeDot(characterViewModel.getDotObserverRemove().get());
            }
        };
        characterViewModel.getDotObserverRemove().addOnPropertyChangedCallback(callbackDelete);
    }

    // recyclerViewer and observers for special effects
    private void setUpSpecialRecyclerViewer(View view) {
        RecyclerView recyclerView = characterBinding.rvSpecialEffects;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        final CharacterSpecialListAdapter adapter = new CharacterSpecialListAdapter(characterViewModel.mapIntoList(characterViewModel.getSpecialMap()));
        recyclerView.setAdapter(adapter);

        // observed whenever CharacterViewModel adds to dotMap
        Observable.OnPropertyChangedCallback callbackAdd = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                adapter.addNewSpecial(characterViewModel.getSpecialObserverAdd().get());
            }
        };
        characterViewModel.getSpecialObserverAdd().addOnPropertyChangedCallback(callbackAdd);

        // observed whenever CharacterViewModel removes from dotMap
        Observable.OnPropertyChangedCallback callbackDelete = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                adapter.removeSpecial(characterViewModel.getSpecialObserverRemove().get());
            }
        };
        characterViewModel.getSpecialObserverRemove().addOnPropertyChangedCallback(callbackDelete);
    }
}
