package com.habbybolan.textadventure.view.encounter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.FragmentTrapBinding;
import com.habbybolan.textadventure.view.dialogueAdapter.DialogueRecyclerView;
import com.habbybolan.textadventure.viewmodel.CharacterViewModel;
import com.habbybolan.textadventure.viewmodel.MainGameViewModel;
import com.habbybolan.textadventure.viewmodel.encounters.TrapEncounterViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class TrapFragment extends EncounterDialogueFragment implements EncounterFragment{

    private FragmentTrapBinding trapBinder;
    private TrapEncounterViewModel trapEncounterVM;

    private MainGameViewModel mainGameVM = MainGameViewModel.getInstance();
    private CharacterViewModel characterVM = CharacterViewModel.getInstance();
    private JSONObject encounter = mainGameVM.getJSONEncounter();
    private DialogueRecyclerView rv;


    public TrapFragment() {}

    public static TrapFragment newInstance() {
        return new TrapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            trapEncounterVM = new TrapEncounterViewModel(encounter, getActivity());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        trapBinder = DataBindingUtil.inflate(inflater, R.layout.fragment_trap, container,false);

        try {
            setUpEncounterBeginning();
        } catch (JSONException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return trapBinder.getRoot();
    }

    // sets up the RV dialogue adapter and the encounter state to enter
    private void setUpEncounterBeginning() throws JSONException, ExecutionException, InterruptedException {
        trapEncounterVM.setSavedData();
        // set up Recycler Viewer that holds all dialogue
        rv = new DialogueRecyclerView(getContext(), trapBinder.rvDialogue, trapEncounterVM.getDialogueList());
        setUpDialogueRV(rv, trapEncounterVM);
        stateListener(trapEncounterVM.getStateIndex(), trapEncounterVM, this);
        // called after stateLister set up, signalling first state to enter
        trapEncounterVM.gotoBeginningState(mainGameVM);
    }

    // checks which state the trap encounter is in and starts that new state
    @Override
    public void checkState(int state) {
        switch(state) {
            // first state
            case TrapEncounterViewModel.firstState:
                trapBinder.layoutBtnOptions.removeAllViews();
                dialogueState(trapEncounterVM, trapBinder.layoutBtnOptions);
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
        characterVM.setStateInventoryObserver(true);
        setLeaveButton(mainGameVM, trapBinder.layoutBtnOptions);
    }

    @Override
    public void onStop() {
        super.onStop();
        characterVM.saveCharacter();
        trapEncounterVM.saveEncounter(rv.getDialogueList());
    }
}
