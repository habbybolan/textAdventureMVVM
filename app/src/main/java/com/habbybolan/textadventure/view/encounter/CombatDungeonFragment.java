package com.habbybolan.textadventure.view.encounter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.FragmentCombatDungeonBinding;
import com.habbybolan.textadventure.view.CustomPopupWindow;
import com.habbybolan.textadventure.view.dialogueAdapter.DialogueRecyclerView;
import com.habbybolan.textadventure.viewmodel.MainGameViewModel;
import com.habbybolan.textadventure.viewmodel.characterEntityViewModels.CharacterViewModel;
import com.habbybolan.textadventure.viewmodel.encounters.CombatDungeonViewModel;

import org.json.JSONException;

/**

 */
public class CombatDungeonFragment extends EncounterDialogueFragment implements EncounterFragment {

    private CombatDungeonViewModel combatDungeonVM;
    private FragmentCombatDungeonBinding combatDungeonBinding;

    private DialogueRecyclerView rv;

    private static CombatDungeonFragment instance = null;

    public CombatDungeonFragment() {}

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        combatDungeonBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_combat_dungeon, container, false);
        return combatDungeonBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        combatDungeonVM = new ViewModelProvider(this).get(CombatDungeonViewModel.class);
        try {
            rv = setUpEncounterBeginning(combatDungeonVM, this, combatDungeonBinding.rvDialogue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        // popup view, only show one at a time
        final PopupWindow popupWindow = new PopupWindow();
        combatDungeonBinding.btnInfo.setVisibility(View.VISIBLE);
        combatDungeonBinding.btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing()) {
                    // remove the popup if showing
                    popupWindow.dismiss();
                } else {
                    // otherwise, set up the popup dungeon
                    CustomPopupWindow.setDungeonInfo("A difficult dungeon with only combat. Cannot leave once entered unless dungeon is finished.",
                            getContext(), popupWindow, combatDungeonBinding.scrollViewContainer);
                }
            }
        });
        combatDungeonBinding.btnEnter.setVisibility(View.VISIBLE);
        combatDungeonBinding.btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // remove the popup if showing
                if (popupWindow.isShowing()) popupWindow.dismiss();
                // clicker functionality to enter the multi dungeon
                combatDungeonVM.clickEnter();
            }
        });
        combatDungeonBinding.btnLeave.setVisibility(View.VISIBLE);
        combatDungeonBinding.btnLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // remove the popup if showing
                if (popupWindow.isShowing()) popupWindow.dismiss();
                // clicker functionality to not enter multi dungeon
                combatDungeonVM.clickLeave();
            }
        });
    }
}
