package com.habbybolan.textadventure.view.encounter;

import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.databinding.ObservableField;
import androidx.fragment.app.Fragment;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.DefaultButtonDetailsBinding;
import com.habbybolan.textadventure.model.dialogue.Dialogue;
import com.habbybolan.textadventure.view.dialogueAdapter.DialogueRecyclerView;
import com.habbybolan.textadventure.viewmodel.MainGameViewModel;
import com.habbybolan.textadventure.viewmodel.encounters.EncounterViewModel;

import org.json.JSONException;
import org.json.JSONObject;

/*
deals with the functionality and UI of the dialogue and state switching of a fragment
 */
class EncounterDialogueFragment extends Fragment {

    void setUpDialogueRV(final DialogueRecyclerView rv, final EncounterViewModel vm) {
        // observed whenever MainGameViewModel changes the encounter, changing the fragment to the appropriate one
        Observable.OnPropertyChangedCallback callback = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                Dialogue dialogue = new Dialogue(vm.getNewDialogueValue());
                rv.addDialogue(dialogue);
            }
        };
        vm.getNewDialogue().addOnPropertyChangedCallback(callback);
    }

    void dialogueState(final EncounterViewModel vm, GridLayout gridLayout) {
        try {
            JSONObject dialogue = vm.getFirstStateJSON();
            // if there is only 1 dialogue snippet, then don't create a 'continue' button
            if (!dialogue.has("next")) {
                vm.firstDialogueState();
            } else {
                // otherwise, multiple dialogue snippets
                vm.firstDialogueState();
                Button btnContinue = new Button(getContext());
                btnContinue.setText(getResources().getString(R.string.continue_dialogue));
                btnContinue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            vm.firstDialogueState();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                gridLayout.addView(btnContinue);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // helper for endState to set up the button to leave encounter
    void setLeaveButton(final MainGameViewModel vm, GridLayout gridLayout) {
        final View viewLeave = getLayoutInflater().inflate(R.layout.default_button_details, null);
        DefaultButtonDetailsBinding defaultBindingLeave = DataBindingUtil.bind(viewLeave);
        String leaveText = getResources().getString(R.string.leave_encounter);
        defaultBindingLeave.setTitle(leaveText);
        defaultBindingLeave.btnDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vm.gotoNextEncounter();
            }
        });
        gridLayout.addView(viewLeave);
    }

    public void stateListener(ObservableField<Integer> stateIndex, final EncounterViewModel vm, final EncounterFragment fragment) {
        final Observable.OnPropertyChangedCallback callback = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                int state = vm.getStateIndexValue();
                fragment.checkState(state);
            }
        };
        stateIndex.addOnPropertyChangedCallback(callback);
    }
}
