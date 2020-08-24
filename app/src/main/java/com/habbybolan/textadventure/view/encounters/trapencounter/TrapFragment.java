package com.habbybolan.textadventure.view.encounters.trapencounter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.FragmentTrapBinding;
import com.habbybolan.textadventure.viewmodel.CharacterViewModel;
import com.habbybolan.textadventure.viewmodel.MainGameViewModel;
import com.habbybolan.textadventure.viewmodel.encounters.TrapEncounterViewModel;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TrapFragment extends Fragment {

    private JSONObject encounter;
    private FragmentTrapBinding trapBinder;
    TrapEncounterViewModel trapEncounterVM;

    MainGameViewModel mainGameVM;
    CharacterViewModel characterVM;


    public TrapFragment(MainGameViewModel mainGameVM, CharacterViewModel characterVM) {
        this.mainGameVM = mainGameVM;
        this.characterVM = characterVM;
        encounter = mainGameVM.getJSONEncounter();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        trapBinder = DataBindingUtil.inflate(inflater, R.layout.fragment_trap, container,false);
        trapEncounterVM = new TrapEncounterViewModel(mainGameVM, characterVM);
        setUpDialogueRV();
        return trapBinder.getRoot();
    }

    // set up Recycler viewer and Observable data for when further dialogue is added
    private void setUpDialogueRV() {
        RecyclerView recyclerView = trapBinder.rvDialogue;
        // set the layout manager to position the items
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<String> arrayList = new ArrayList<>();
        final TrapDialogueListAdapter adapter = new TrapDialogueListAdapter(arrayList);
        recyclerView.setAdapter(adapter);

        // observed whenever MainGameViewModel changes the encounter, changing the fragment to the appropriate one
        Observable.OnPropertyChangedCallback callback = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                adapter.addNewDialogue("add");
            }
        };
        trapEncounterVM.getNewDialogue().addOnPropertyChangedCallback(callback);
    }
}
