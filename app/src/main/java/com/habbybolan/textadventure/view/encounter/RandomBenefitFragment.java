package com.habbybolan.textadventure.view.encounter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.fragment.app.Fragment;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.FragmentRandomBenefitBinding;
import com.habbybolan.textadventure.databinding.InventorySnippetBinding;
import com.habbybolan.textadventure.model.dialogue.Dialogue;
import com.habbybolan.textadventure.model.inventory.Inventory;
import com.habbybolan.textadventure.view.dialogueAdapter.DialogueRecyclerView;
import com.habbybolan.textadventure.viewmodel.CharacterViewModel;
import com.habbybolan.textadventure.viewmodel.MainGameViewModel;
import com.habbybolan.textadventure.viewmodel.encounters.RandomBenefitViewModel;

import org.json.JSONException;
import org.json.JSONObject;


public class RandomBenefitFragment extends Fragment implements EncounterFragment{


    private MainGameViewModel mainGameVM;
    private CharacterViewModel characterVM;
    private JSONObject encounter;
    private FragmentRandomBenefitBinding benefitBinding;
    private RandomBenefitViewModel benefitVM;
    private int state = 0;
    private Inventory inventoryToRetrieve;

    public RandomBenefitFragment(MainGameViewModel mainGameVM, CharacterViewModel characterVM, JSONObject encounter) {
        this.mainGameVM = mainGameVM;
        this.characterVM = characterVM;
        this.encounter = encounter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        benefitBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_random_benefit, container, false);
        try {
            benefitVM = new RandomBenefitViewModel(mainGameVM, characterVM, encounter, getActivity());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setUpDialogueRV();
        // create state listener and go into first state
        stateListener();

        return benefitBinding.getRoot();
    }

    public void setUpDialogueRV() {
        benefitBinding.layoutBtnOptions.removeAllViews();
        final DialogueRecyclerView rv = new DialogueRecyclerView(getContext(), benefitBinding.rvDialogue, characterVM);

        // observed whenever MainGameViewModel changes the encounter, changing the fragment to the appropriate one
        Observable.OnPropertyChangedCallback callback = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                Dialogue dialogue = new Dialogue(benefitVM.getNewDialogue().get());
                rv.addDialogue(dialogue);
            }
        };
        benefitVM.getNewDialogue().addOnPropertyChangedCallback(callback);
    }

    @Override
    public void stateListener() {
        final Observable.OnPropertyChangedCallback callback = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                //Integer stateInteger = trapEncounterVM.getStateIndex().get();
                state = benefitVM.getStateIndexValue();
                checkState();
            }
        };
        benefitVM.getStateIndex().addOnPropertyChangedCallback(callback);
        Integer state = benefitVM.getStateIndex().get();
        if (state != null) this.state = state;
        checkState();
    }

    // checks which state the trap encounter is in and starts that new state
    @Override
    public void checkState() {
        switch(state) {
            // first state
            case RandomBenefitViewModel.firstState:
                try {
                    dialogueState();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            // second state
            case RandomBenefitViewModel.secondState:
                checkBenefitState();
                break;
            // last state
            case RandomBenefitViewModel.thirdState:
                endState();
                break;
        }
    }

    private void checkBenefitState() {
        benefitBinding.layoutBtnOptions.removeAllViews();
        Button btnCheck = new Button(getContext());
        String check = "Check";
        btnCheck.setText(check);
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // remove check button and add inventorySnippet layout to FrameLayout
                benefitBinding.layoutBtnOptions.removeAllViews();
                Inventory inventoryToRetrieve = benefitVM.checkState();
                setUpInventorySnippet(inventoryToRetrieve);
            }
        });
        benefitBinding.layoutBtnOptions.addView(btnCheck);
    }

    // inventory snippet of new Inventory object to retrieve
    private void setUpInventorySnippet(Inventory inventoryToRetrieve) {
        this.inventoryToRetrieve = inventoryToRetrieve;
        View view = getLayoutInflater().inflate(R.layout.inventory_snippet, null);
        InventorySnippetBinding snippetBinding = DataBindingUtil.bind(view);

        snippetBinding.setInventoryName(inventoryToRetrieve.getName());
        snippetBinding.setInventoryPic(inventoryToRetrieve.getPictureResource());
        benefitBinding.frameInventorySnippet.addView(view);
        benefitVM.incrementStateIndex();
    }


    // first state
    // set up the dialogue with recyclerView
    @Override
    public void dialogueState() throws JSONException {
        JSONObject dialogue = encounter.getJSONObject("dialogue");
        // if there is only 1 dialogue snippet, then don't create a 'continue' button
        if (!dialogue.has("next")) {
            benefitVM.firstDialogueState();
        } else {
            // otherwise, multiple dialogue snippets
            benefitVM.firstDialogueState();
            Button btnContinue = new Button(getContext());
            btnContinue.setText(getResources().getString(R.string.continue_dialogue));
            btnContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        benefitVM.firstDialogueState();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            benefitBinding.layoutBtnOptions.addView(btnContinue);
        }
    }

    // third and final state
        // set up the button to leave the encounter and to pick up item
    @Override
    public void endState() {
        benefitBinding.layoutBtnOptions.removeAllViews();
        Button leaveButton = new Button(getContext());
        leaveButton.setText(getResources().getString(R.string.leave_encounter));
        leaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainGameVM.gotoNextEncounter();
            }
        });
        benefitBinding.layoutBtnOptions.addView(leaveButton);

        final Button btnPickUp = new Button(getContext());
        btnPickUp.setText(getResources().getString(R.string.pick_up_inventory));
        btnPickUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!benefitVM.addNewInventory(inventoryToRetrieve))
                    Toast.makeText(getContext(), benefitVM.getToastString(inventoryToRetrieve), Toast.LENGTH_SHORT).show();
                 else
                     benefitBinding.layoutBtnOptions.removeView(btnPickUp);
            }
        });
        benefitBinding.layoutBtnOptions.addView(btnPickUp);
    }
}
