package com.habbybolan.textadventure.view.encounter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.FragmentCombatBinding;
import com.habbybolan.textadventure.view.dialogueAdapter.DialogueRecyclerView;
import com.habbybolan.textadventure.viewmodel.CharacterViewModel;
import com.habbybolan.textadventure.viewmodel.MainGameViewModel;
import com.habbybolan.textadventure.viewmodel.encounters.CombatViewModel;

import org.json.JSONObject;

public class CombatFragment extends EncounterDialogueFragment implements EncounterFragment {

    private MainGameViewModel mainGameVM = MainGameViewModel.getInstance();
    private CharacterViewModel characterVM = CharacterViewModel.getInstance();
    private JSONObject encounter = mainGameVM.getJSONEncounter();
    private FragmentCombatBinding combatBinding;
    private CombatViewModel combatVM;
    private DialogueRecyclerView rv;

    public CombatFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CombatFragment newInstance() {
        return new CombatFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_combat, container, false);
    }

    @Override
    public void checkState(int state) {
        switch(state) {
            // first state
            case CombatViewModel.firstState:
                combatBinding.layoutBtnOptions.removeAllViews();
                dialogueState(combatVM, combatBinding.layoutBtnOptions);
                break;
            // second stateW
            case CombatViewModel.secondState:
                combatBinding.layoutBtnOptions.removeAllViews();
                combatState();
                break;
            case CombatViewModel.thirdState:
                combatBinding.layoutBtnOptions.removeAllViews();
                // todo: reward state
                break;
            // last state
            case CombatViewModel.fourthState:
                combatBinding.layoutBtnOptions.removeAllViews();
                endState();
                break;
        }
        // set the isSaved to false to signal that the save has been retrieved
        combatVM.setIsSaved(false);
    }

    // state for setting up the UI for the combat
    public void combatState() {

    }
    @Override
    public void endState() {

    }
}
