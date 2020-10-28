package com.habbybolan.textadventure.view.encounter;

import android.view.View;
import android.widget.GridLayout;

import androidx.databinding.Observable;
import androidx.databinding.ObservableField;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.DefaultButtonDetailsBinding;
import com.habbybolan.textadventure.view.ButtonInflaters;
import com.habbybolan.textadventure.view.dialogueAdapter.DialogueRecyclerView;
import com.habbybolan.textadventure.viewmodel.MainGameViewModel;
import com.habbybolan.textadventure.viewmodel.encounters.EncounterViewModel;

import org.json.JSONException;
import org.json.JSONObject;

/*
deals with the functionality and UI of the dialogue and state switching of a fragment
 */
class EncounterDialogueFragment extends Fragment {

    /**
     * deals with adding dialogue to the dialogue recycler viewer, calling the necessary method through a callback
     * @param rv    The recycler Viewer model used to call the adding dialogue method
     * @param vm    View model of the current encounter the dialogue is being added from
     */
    void setUpDialogueRV(final DialogueRecyclerView rv, final EncounterViewModel vm) {
        // observed whenever MainGameViewModel changes the encounter, changing the fragment to the appropriate one
        Observable.OnPropertyChangedCallback callback = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                rv.addDialogue(vm.getNewDialogueValue());
            }
        };
        vm.getNewDialogue().addOnPropertyChangedCallback(callback);
    }

    /**
     * Creates the buttons and goes through the dialogue lines if more exist. Beginning of encounters
     * start with either one dialogue line, or multiple. For the multiple, a continue button is created to go through
     * each line, calling firstDialogueState to deal with the actual text and moving on if no more lines left.
     * @param vm            The view model that deals with the current encounter
     * @param gridLayout    The button layout to add the continue button to
     */
    void dialogueState(final EncounterViewModel vm, GridLayout gridLayout) {
        try {
            JSONObject dialogue = vm.getFirstStateJSON();
            // if there is only 1 dialogue snippet, then don't create a 'continue' button
            if (!dialogue.has("next")) {
                vm.firstDialogueState();
            } else {
                // otherwise, multiple dialogue snippets
                vm.firstDialogueState();
                // set continue button to the default button view
                String txtContinue = getResources().getString(R.string.continue_dialogue);
                DefaultButtonDetailsBinding binding = ButtonInflaters.setDefaultButton(gridLayout, txtContinue, getActivity());
                binding.btnDefault.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MainGameViewModel.getInstance().gotoNextRandomEncounter();
                    }
                });
                binding.btnDefault.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            vm.firstDialogueState();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper for endState to set up the button to leave encounter. Pressing the leave button leaves the current encounter and signals to go to next
     * through the MainGameViewModel
     * @param gridLayout    The Grid layout to place the leave button inside.
     */
    void setLeaveButton(GridLayout gridLayout) {
        String leaveText = getResources().getString(R.string.leave_encounter);
        DefaultButtonDetailsBinding binding = ButtonInflaters.setDefaultButton(gridLayout, leaveText, getActivity());
        binding.btnDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainGameViewModel.getInstance().gotoNextRandomEncounter();
            }
        });
    }

    /**
     * A listener that calls checkState method of the current encounter to go to the proper View state when the state index value is changed
     * @param stateIndex    The index of the View state that is next to display
     * @param vm            View Model of the current encounter
     * @param fragment      The fragment of the current encounter, associated with the vm
     */
    private void stateListener(ObservableField<Integer> stateIndex, final EncounterViewModel vm, final EncounterFragment fragment) {
        final Observable.OnPropertyChangedCallback callback = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                int state = vm.getStateIndexValue();
                fragment.checkState(state);
            }
        };
        stateIndex.addOnPropertyChangedCallback(callback);
    }

    /**
     * Sets up the dialogue recyclerViewer that holds all dialogue and sets up the beginning fragment state to enter
     * @param viewModel         The viewModel associated with the current encounter.
     * @param fragment          The fragment associated with the current encounter.
     * @param rv                The view for the RecyclerView to set up the DialogueRecyclerView
     * @throws JSONException    For any JSON formatting error.
     */
    DialogueRecyclerView setUpEncounterBeginning(EncounterViewModel viewModel, EncounterFragment fragment, RecyclerView rv) throws JSONException {
        viewModel.setSavedData();
        // set up Recycler Viewer that holds all dialogue
        DialogueRecyclerView dialogueRV = new DialogueRecyclerView(getContext(), rv, viewModel.getDialogueList());
        setUpDialogueRV(dialogueRV, viewModel);
        stateListener(viewModel.getStateIndex(), viewModel, fragment);
        // called after stateLister set up, signalling first state to enter
        viewModel.gotoBeginningState();
        return dialogueRV;
    }
}
