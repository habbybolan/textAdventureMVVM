package com.habbybolan.textadventure.view.encounter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.fragment.app.Fragment;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.FragmentTrapBinding;
import com.habbybolan.textadventure.model.dialogue.Dialogue;
import com.habbybolan.textadventure.view.dialogueAdapter.DialogueRecyclerView;
import com.habbybolan.textadventure.viewmodel.CharacterViewModel;
import com.habbybolan.textadventure.viewmodel.MainGameViewModel;
import com.habbybolan.textadventure.viewmodel.encounters.TrapEncounterViewModel;

import org.json.JSONException;
import org.json.JSONObject;

public class TrapFragment extends Fragment implements EncounterFragment{

    private JSONObject encounter;
    private FragmentTrapBinding trapBinder;
    private TrapEncounterViewModel trapEncounterVM;

    private MainGameViewModel mainGameVM;
    private CharacterViewModel characterVM;
    private int state = 0;


    public TrapFragment(MainGameViewModel mainGameVM, CharacterViewModel characterVM, JSONObject encounter) {
        this.mainGameVM = mainGameVM;
        this.characterVM = characterVM;
        this.encounter = encounter;
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
        try {
            trapEncounterVM = new TrapEncounterViewModel(mainGameVM, characterVM, encounter, getActivity());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setUpDialogueRV();
        // create state listener and go into first state
        stateListener();

        return trapBinder.getRoot();
    }

    // listens to when the trap state changes
    @Override
    public void stateListener() {
        final Observable.OnPropertyChangedCallback callback = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                //Integer stateInteger = trapEncounterVM.getStateIndex().get();
                state = trapEncounterVM.getStateIndexValue();
                checkState();
            }
        };
        trapEncounterVM.getStateIndex().addOnPropertyChangedCallback(callback);
        Integer state = trapEncounterVM.getStateIndex().get();
        if (state != null) this.state = state;
        checkState();
    }

    // checks which state the trap encounter is in and starts that new state
    @Override
    public void checkState() {
        switch(state) {
            // first state
            case TrapEncounterViewModel.firstState:
                trapBinder.layoutBtnOptions.removeAllViews();
                try {
                    dialogueState();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
                // second state
            case TrapEncounterViewModel.secondState:
                trapBinder.layoutBtnOptions.removeAllViews();
                beforeTrapState();
                break;
                // last state
            case TrapEncounterViewModel.thirdState:
                trapBinder.layoutBtnOptions.removeAllViews();
                endState();
                break;
        }
    }


    // set up Recycler viewer and Observable data for when further dialogue is added
    public void setUpDialogueRV() {
        final DialogueRecyclerView rv = new DialogueRecyclerView(getContext(), trapBinder.rvDialogue, characterVM);

        // observed whenever MainGameViewModel changes the encounter, changing the fragment to the appropriate one
        Observable.OnPropertyChangedCallback callback = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                Dialogue dialogue = new Dialogue(trapEncounterVM.getNewDialogue().get());
                rv.addDialogue(dialogue);
            }
        };
        trapEncounterVM.getNewDialogue().addOnPropertyChangedCallback(callback);
    }

    // first state
        // set up the dialogue with recyclerView
    @Override
    public void dialogueState() throws JSONException {
        JSONObject dialogue = encounter.getJSONObject("dialogue");
        // if there is only 1 dialogue snippet, then don't create a 'continue' button
        if (!dialogue.has("next")) {
            trapEncounterVM.firstDialogueState();
        } else {
            // otherwise, multiple dialogue snippets
            trapEncounterVM.firstDialogueState();
            Button btnContinue = new Button(getContext());
            btnContinue.setText(getResources().getString(R.string.continue_dialogue));
            btnContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        trapEncounterVM.firstDialogueState();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            trapBinder.layoutBtnOptions.addView(btnContinue);
        }
    }

    // second state
        // set up buttons to either attempt to dodge trap, or use escape item
    private void beforeTrapState() {
        // remove ability to drop/consume Inventory Objects
        characterVM.setStateInventoryObserver(false);
        Button btnDodgeTrap = new Button(getContext());
        String dodge = "Dodge Trap";
        btnDodgeTrap.setText(dodge);
        btnDodgeTrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    trapEncounterVM.secondStateEscapeTrap();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        trapBinder.layoutBtnOptions.addView(btnDodgeTrap);

        Button btnUseItem = new Button(getContext());
        String useItem = "Use Item";
        btnUseItem.setText(useItem);
        btnUseItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trapEncounterVM.secondStateUseItem();
            }
        });
        trapBinder.layoutBtnOptions.addView(btnUseItem);
    }

    // third and final state
        // set up the button to leave the encounter and goto next
    @Override
    public void endState() {
        // restore ability to drop/consume Inventory objects
        characterVM.setStateInventoryObserver(true);
        Button leaveButton = new Button(getContext());
        leaveButton.setText(getResources().getString(R.string.leave_encounter));
        leaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainGameVM.gotoNextEncounter();
            }
        });
        trapBinder.layoutBtnOptions.addView(leaveButton);
    }
}
