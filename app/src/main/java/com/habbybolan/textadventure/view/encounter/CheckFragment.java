package com.habbybolan.textadventure.view.encounter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.FragmentCheckBinding;
import com.habbybolan.textadventure.view.dialogueAdapter.DialogueRecyclerView;
import com.habbybolan.textadventure.viewmodel.MainGameViewModel;
import com.habbybolan.textadventure.viewmodel.characterEntityViewModels.CharacterViewModel;
import com.habbybolan.textadventure.viewmodel.encounters.CheckViewModel;

import org.json.JSONException;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CheckFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CheckFragment extends EncounterDialogueFragment implements EncounterFragment {

    private static CheckFragment instance = null;
    private MainGameViewModel mainGameVM = MainGameViewModel.getInstance();
    private CharacterViewModel characterVM = CharacterViewModel.getInstance();
    private CheckViewModel checkVM;
    private FragmentCheckBinding checkBinding;
    private DialogueRecyclerView rv;

    private CheckFragment() {}

    public static CheckFragment newInstance() {
        instance = new CheckFragment();
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainGameVM = MainGameViewModel.getInstance();
        characterVM = CharacterViewModel.getInstance();
        try {
            checkVM = new CheckViewModel(getActivity());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        checkBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_check, container, false);
        try {
            rv = setUpEncounterBeginning(checkVM, this, checkBinding.rvDialogue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return checkBinding.getRoot();
    }

    @Override
    public void checkState(int state) {
        switch(state) {
            // first state
            case CheckViewModel.firstState:
                dialogueState(checkVM, checkBinding.layoutBtnOptions);
                break;
            // last state
            case CheckViewModel.secondState:
                endState();
                break;
        }
    }

    @Override
    public void endState() {
        removeDialogueContinueButton(checkBinding.layoutBtnOptions);
        checkBinding.btnContinue.setVisibility(View.VISIBLE);
        checkBinding.btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // clicker functionality to continue in multi dungeon
                checkVM.clickContinue();
            }
        });
        checkBinding.btnLeave.setVisibility(View.VISIBLE);
        checkBinding.btnLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // clicker functionality to leave the multi dungeon
                checkVM.clickLeave();
            }
        });
    }


    @Override
    public void onStop() {
        super.onStop();
        checkVM.saveGame(rv);
    }
}
