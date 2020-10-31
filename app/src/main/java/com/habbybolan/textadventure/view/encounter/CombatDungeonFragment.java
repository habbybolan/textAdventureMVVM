package com.habbybolan.textadventure.view.encounter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.FragmentCombatDungeonBinding;
import com.habbybolan.textadventure.view.dialogueAdapter.DialogueRecyclerView;
import com.habbybolan.textadventure.viewmodel.MainGameViewModel;
import com.habbybolan.textadventure.viewmodel.characterEntityViewModels.CharacterViewModel;
import com.habbybolan.textadventure.viewmodel.encounters.CombatDungeonViewModel;

import org.json.JSONException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CombatDungeonFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CombatDungeonFragment extends EncounterDialogueFragment implements EncounterFragment {

    private MainGameViewModel mainGameVM;
    private CharacterViewModel characterVM;
    private CombatDungeonViewModel combatDungeonVM;
    private FragmentCombatDungeonBinding combatDungeonBinding;

    private DialogueRecyclerView rv;

    private static CombatDungeonFragment instance = null;

    public CombatDungeonFragment() {
        // Required empty public constructor
    }

    public static CombatDungeonFragment getInstance() {
        if (instance == null) throw new AssertionError("Create instance first");
        return instance;
    }

    public static CombatDungeonFragment newInstance() {
        return new CombatDungeonFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainGameVM = MainGameViewModel.getInstance();
        characterVM = CharacterViewModel.getInstance();
        try {
            combatDungeonVM = new CombatDungeonViewModel(getActivity());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        combatDungeonBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_combat_dungeon, container, false);
        try {
            rv = setUpEncounterBeginning(combatDungeonVM, this, combatDungeonBinding.rvDialogue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return combatDungeonBinding.getRoot();
    }

    @Override
    public void checkState(int state) {
        switch(state) {
            // first state
            case CombatDungeonViewModel.firstState:
                dialogueState(combatDungeonVM, combatDungeonBinding.layoutBtnOptions);
                break;
            // last state
            case CombatDungeonViewModel.secondState:
                endState();
                break;
        }
    }

    @Override
    public void endState() {
        removeDialogueContinueButton(combatDungeonBinding.layoutBtnOptions);
        combatDungeonBinding.btnEnter.setVisibility(View.VISIBLE);
        combatDungeonBinding.btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // clicker functionality to enter the multi dungeon
                combatDungeonVM.clickEnter();
            }
        });
        combatDungeonBinding.btnLeave.setVisibility(View.VISIBLE);
        combatDungeonBinding.btnLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // clicker functionality to not enter multi dungeon
                combatDungeonVM.clickLeave();
            }
        });
    }
}
