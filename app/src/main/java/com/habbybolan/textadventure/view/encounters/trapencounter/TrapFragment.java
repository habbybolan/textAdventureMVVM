package com.habbybolan.textadventure.view.encounters.trapencounter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.FragmentTrapBinding;
import com.habbybolan.textadventure.viewmodel.CharacterViewModel;
import com.habbybolan.textadventure.viewmodel.MainGameViewModel;
import com.habbybolan.textadventure.viewmodel.encounters.TrapEncounterViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TrapFragment extends Fragment {

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
            trapEncounterVM = new TrapEncounterViewModel(mainGameVM, characterVM, encounter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setUpDialogueRV();
        // create state listener and go into first state
        TrapStateListener();
        return trapBinder.getRoot();
    }

    // listens to when the trap state changes
    private void TrapStateListener() {
        final Observable.OnPropertyChangedCallback callback = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                //Integer stateInteger = trapEncounterVM.getStateIndex().get();
                state = trapEncounterVM.getStateIndex().get();
                checkState();
            }
        };
        trapEncounterVM.getStateIndex().addOnPropertyChangedCallback(callback);
        Integer state = trapEncounterVM.getStateIndex().get();
        if (state != null) this.state = state;
        checkState();
    }

    // checks which state the trap encounter is in and starts that new state
    private void checkState() {
        switch(state) {
            // first state
            case TrapEncounterViewModel.firstState:
                dialogueState();
                break;
                // second state
            case TrapEncounterViewModel.secondState:
                beforeTrapState();
                break;
                // last state
            case TrapEncounterViewModel.thirdState:
                afterTrapState();
                break;
        }
    }


    // set up Recycler viewer and Observable data for when further dialogue is added
    private void setUpDialogueRV() {
        trapBinder.layoutBtnOptions.removeAllViews();
        RecyclerView recyclerView = trapBinder.rvDialogue;
        // set the layout manager to position the items
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<String> arrayList = new ArrayList<>();
        final TrapDialogueListAdapter adapter = new TrapDialogueListAdapter(arrayList);
        recyclerView.setAdapter(adapter);

        // observed whenever MainGameViewModel changes the encounter, changing the fragment to the appropriate one
        Observable.OnPropertyChangedCallback callback = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                adapter.addNewDialogue(trapEncounterVM.getNewDialogue().get());
            }
        };
        trapEncounterVM.getNewDialogue().addOnPropertyChangedCallback(callback);
    }

    // first state
        // set up the dialogue with recyclerView
    private void dialogueState() {
        Button btnContinue = new Button(getContext());
        btnContinue.setText(getResources().getString(R.string.continue_dialogue));
        btnContinue.setTypeface(mainGameVM.getFont());
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    trapEncounterVM.firstState();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        trapBinder.layoutBtnOptions.addView(btnContinue);
    }

    // second state
        // set up buttons to either attempt to dodge trap, or use escape item
    private void beforeTrapState() {
        trapBinder.layoutBtnOptions.removeAllViews();
        Button btnDodgeTrap = new Button(getContext());
        //btnDodgeTrap.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.txt_size_normal));
        btnDodgeTrap.setTypeface(mainGameVM.getFont());
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
        btnUseItem.setTypeface(mainGameVM.getFont());
        String useItem = "Use Item";
        btnUseItem.setText(useItem);
        btnUseItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo: using trap item
                /*try {
                    trapEncounterVM.secondStateUseItem();
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
            }
        });
        trapBinder.layoutBtnOptions.addView(btnUseItem);
    }

    // third and final state
        // set up the button to leave the encounter and goto next
    private void afterTrapState() {
        trapBinder.layoutBtnOptions.removeAllViews();
        Button leaveButton = new Button(getContext());
        leaveButton.setText(getResources().getString(R.string.leave_encounter));
        leaveButton.setTypeface(mainGameVM.getFont());
        leaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainGameVM.gotoNextEncounter();
            }
        });
        trapBinder.layoutBtnOptions.addView(leaveButton);
    }
}
