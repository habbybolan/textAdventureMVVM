package com.habbybolan.textadventure.view.encounter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.FragmentBreakBinding;
import com.habbybolan.textadventure.view.dialogueAdapter.DialogueRecyclerView;
import com.habbybolan.textadventure.viewmodel.MainGameViewModel;
import com.habbybolan.textadventure.viewmodel.characterEntityViewModels.CharacterViewModel;
import com.habbybolan.textadventure.viewmodel.encounters.BreakViewModel;
import com.habbybolan.textadventure.viewmodel.encounters.CheckViewModel;

import org.json.JSONException;

/**
 * Encounter that can initiate between encounters. Flavor text to break up encounters.
 */
public class BreakFragment extends EncounterDialogueFragment implements EncounterFragment {

    private MainGameViewModel mainGameVM = MainGameViewModel.getInstance();
    private CharacterViewModel characterVM = CharacterViewModel.getInstance();
    private FragmentBreakBinding binding;
    private DialogueRecyclerView rv;
    private BreakViewModel breakVM;

    public BreakFragment() {}

    // TODO: Rename and change types and number of parameters
    public static BreakFragment newInstance() {
        return new BreakFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainGameVM = MainGameViewModel.getInstance();
        characterVM = CharacterViewModel.getInstance();
        try {
            breakVM = new BreakViewModel(getActivity());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_break, container, false);
        try {
            rv = setUpEncounterBeginning(breakVM, this, binding.rvDialogue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return binding.getRoot();
    }

    @Override
    public void checkState(int state) {
        switch(state) {
            // first state
            case CheckViewModel.firstState:
                dialogueState(breakVM, binding.layoutBtnOptions);
                break;
            // last state
            case CheckViewModel.secondState:
                endState();
                break;
        }
    }

    @Override
    public void endState() {
        binding.layoutBtnOptions.removeAllViews();
        setLeaveButton(binding.layoutBtnOptions);
    }

    @Override
    public void onStop() {
        super.onStop();
        breakVM.saveGame(rv);
    }
}
