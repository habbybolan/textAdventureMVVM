package com.habbybolan.textadventure.view.encounter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.DefaultButtonDetailsBinding;
import com.habbybolan.textadventure.databinding.FragmentTrapBinding;
import com.habbybolan.textadventure.view.ButtonInflaters;
import com.habbybolan.textadventure.view.CustomPopupWindow;
import com.habbybolan.textadventure.view.dialogueAdapter.DialogueRecyclerView;
import com.habbybolan.textadventure.viewmodel.MainGameViewModel;
import com.habbybolan.textadventure.viewmodel.characterEntityViewModels.CharacterViewModel;
import com.habbybolan.textadventure.viewmodel.encounters.TrapEncounterViewModel;

import org.json.JSONException;
import org.json.JSONObject;

public class TrapFragment extends EncounterDialogueFragment implements EncounterFragment{

    private FragmentTrapBinding trapBinder;
    private TrapEncounterViewModel trapVM;

    private MainGameViewModel mainGameVM = MainGameViewModel.getInstance();
    private CharacterViewModel characterVM = CharacterViewModel.getInstance();
    private JSONObject encounter = mainGameVM.getEncounter().getEncounterJSON();
    private DialogueRecyclerView rv;


    public TrapFragment() {}

    public static TrapFragment newInstance() {
        return new TrapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            trapVM = new TrapEncounterViewModel(encounter, getActivity());
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
            rv = setUpEncounterBeginning(trapVM, this, trapBinder.rvDialogue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return trapBinder.getRoot();
    }

    // checks which state the trap encounter is in and starts that new state
    @Override
    public void checkState(int state) {
        switch(state) {
            // first state
            case TrapEncounterViewModel.firstState:
                trapBinder.layoutBtnOptions.removeAllViews();
                dialogueState(trapVM, trapBinder.layoutBtnOptions);
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

        String dodge = "Dodge Trap";
        final DefaultButtonDetailsBinding bindingDodge = ButtonInflaters.setDefaultButton(trapBinder.layoutBtnOptions, dodge, getActivity());
        bindingDodge.btnDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    trapVM.secondStateEscapeTrap();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        String useItem = "Use Item";
        DefaultButtonDetailsBinding bindingUseItem = ButtonInflaters.setDefaultButton(trapBinder.layoutBtnOptions, useItem, getActivity());
        bindingUseItem.btnDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!trapVM.secondStateUseItem())
                    CustomPopupWindow.setTempMessage("You don't possess any item to escape.", getContext(), new PopupWindow(), trapBinder.trapContainer);
            }
        });
    }

    // third and final state
        // set up the button to leave the encounter and goto next
    @Override
    public void endState() {
        characterVM.setStateInventoryObserver(true);
        setLeaveButton(trapBinder.layoutBtnOptions);
    }

    @Override
    public void onStop() {
        super.onStop();
        trapVM.saveGame(rv);
    }
}
