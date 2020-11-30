package com.habbybolan.textadventure.view.encounter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.DefaultButtonDetailsBinding;
import com.habbybolan.textadventure.databinding.FragmentChoiceBinding;
import com.habbybolan.textadventure.view.ButtonInflaters;
import com.habbybolan.textadventure.view.dialogueAdapter.DialogueRecyclerView;
import com.habbybolan.textadventure.viewmodel.characterEntityViewModels.CharacterViewModel;
import com.habbybolan.textadventure.viewmodel.encounters.ChoiceViewModel;

import org.json.JSONException;

/**
 * Choice encounter
 */
public class ChoiceFragment extends EncounterDialogueFragment implements EncounterFragment {

    private CharacterViewModel characterVM = CharacterViewModel.getInstance();
    private ChoiceViewModel choiceViewModel;
    private FragmentChoiceBinding choiceBinding;

    private DialogueRecyclerView rv;

    private static ShopFragment instance = null;

    public ChoiceFragment() {}

    public static ShopFragment getInstance() {
        if (instance == null) throw new AssertionError("Create instance first");
        return instance;
    }

    public static ChoiceFragment newInstance() {
        return new ChoiceFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        choiceBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_choice, container, false);
        return choiceBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        choiceViewModel = new ViewModelProvider(this).get(ChoiceViewModel.class);
        try {
            rv = setUpEncounterBeginning(choiceViewModel, this, choiceBinding.rvDialogue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void checkState(int state) {
        characterVM.setStateInventoryObserver(true);
        choiceBinding.layoutBtnOptions.removeAllViews();
        switch (state) {
            case ChoiceViewModel.firstState:
                // dialogue state
                characterVM.setStateInventoryObserver(true);
                dialogueState(choiceViewModel, choiceBinding.layoutBtnOptions);
                break;
            case ChoiceViewModel.secondState:
                // choose option state
                characterVM.setStateInventoryObserver(true);
                endState();
                break;
        }
    }

    /**
     * State to choose the option to go with, either:
     *  leaving the encounter
     *  or choosing an option that leads to another encounter to start
     */
    @Override
    public void endState() {
        for (int i = 0; i < choiceViewModel.getNumOptions(); i++) {
            DefaultButtonDetailsBinding binding = ButtonInflaters.setDefaultButton(choiceBinding.layoutBtnOptions, choiceViewModel.getOptionText(i), getActivity());
            final int index = i;
            binding.btnDefault.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    choiceViewModel.applyAction(index);
                }
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        saveGame(rv, choiceViewModel);
    }

}