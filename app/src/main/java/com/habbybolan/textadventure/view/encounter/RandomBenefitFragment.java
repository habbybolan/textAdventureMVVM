package com.habbybolan.textadventure.view.encounter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.DefaultButtonDetailsBinding;
import com.habbybolan.textadventure.databinding.FragmentRandomBenefitBinding;
import com.habbybolan.textadventure.databinding.InventorySnippetBinding;
import com.habbybolan.textadventure.model.inventory.Inventory;
import com.habbybolan.textadventure.view.dialogueAdapter.DialogueRecyclerView;
import com.habbybolan.textadventure.viewmodel.characterEntityViewModels.CharacterViewModel;
import com.habbybolan.textadventure.viewmodel.MainGameViewModel;
import com.habbybolan.textadventure.viewmodel.encounters.RandomBenefitViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;


public class RandomBenefitFragment extends EncounterDialogueFragment implements EncounterFragment{


    private MainGameViewModel mainGameVM = MainGameViewModel.getInstance();
    private CharacterViewModel characterVM = CharacterViewModel.getInstance();
    private JSONObject encounter = mainGameVM.getJSONEncounter();

    private FragmentRandomBenefitBinding benefitBinding;
    private RandomBenefitViewModel benefitVM;
    private DialogueRecyclerView rv;

    public RandomBenefitFragment() {}

    public static RandomBenefitFragment newInstance() {
        return new RandomBenefitFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            benefitVM = new RandomBenefitViewModel(mainGameVM, characterVM, encounter, getActivity());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        benefitBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_random_benefit, container, false);

        try {
            setUpEncounterBeginning();
        } catch (JSONException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        return benefitBinding.getRoot();
    }

    // sets up the RV dialogue adapter and the encounter state to enter
    private void setUpEncounterBeginning() throws JSONException, ExecutionException, InterruptedException {
        benefitVM.setSavedData();
        // set up Recycler Viewer that holds all dialogue
        rv = new DialogueRecyclerView(getContext(), benefitBinding.rvDialogue, benefitVM.getDialogueList());
        setUpDialogueRV(rv, benefitVM);
        stateListener(benefitVM.getStateIndex(), benefitVM, this);
        // called after stateLister set up, signalling first state to enter
        benefitVM.gotoBeginningState(mainGameVM);
    }

    // checks which state the trap encounter is in and starts that new state
    @Override
    public void checkState(int state) {
        switch(state) {
            // first state
            case RandomBenefitViewModel.firstState:
                benefitBinding.layoutBtnOptions.removeAllViews();
                dialogueState(benefitVM, benefitBinding.layoutBtnOptions);
                break;
            // second state
            case RandomBenefitViewModel.secondState:
                benefitBinding.layoutBtnOptions.removeAllViews();
                checkBenefitState();
                break;
            // last state
            case RandomBenefitViewModel.thirdState:
                benefitBinding.layoutBtnOptions.removeAllViews();
                endState();
                break;
        }
    }

    // second state entered
    private void checkBenefitState() {
        View viewCheck = getLayoutInflater().inflate(R.layout.default_button_details, null);
        DefaultButtonDetailsBinding defaultBindingCheck = DataBindingUtil.bind(viewCheck);
        String check = "Check";
        defaultBindingCheck.setTitle(check);
        defaultBindingCheck.btnDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                benefitVM.setTangible();
                benefitVM.incrementStateIndex();
            }
        });
        benefitBinding.layoutBtnOptions.addView(viewCheck);
    }

    // inventory snippet of new Inventory object to retrieve
    private void setUpInventorySnippet(Inventory inventoryToRetrieve) {
        View view = getLayoutInflater().inflate(R.layout.inventory_snippet, null);
        InventorySnippetBinding snippetBinding = DataBindingUtil.bind(view);

        snippetBinding.setInventoryName(inventoryToRetrieve.getName());
        snippetBinding.setInventoryPic(inventoryToRetrieve.getPictureResource());
        benefitBinding.frameInventorySnippet.addView(view);
    }

    // third and final state
        // set up the button to leave the encounter and to pick up item
    @Override
    public void endState() {
        // set up button to leave
        setLeaveButton();
        // set up button to receive reward if one exists
        setReceiveInventory();
    }

    // helper for endState to set up the button to leave encounter
    private void setLeaveButton() {
        final View viewLeave = getLayoutInflater().inflate(R.layout.default_button_details, null);
        DefaultButtonDetailsBinding defaultBindingLeave = DataBindingUtil.bind(viewLeave);
        String leaveText = getResources().getString(R.string.leave_encounter);
        defaultBindingLeave.setTitle(leaveText);
        defaultBindingLeave.btnDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainGameVM.gotoNextEncounter();
            }
        });
        benefitBinding.layoutBtnOptions.addView(viewLeave);
    }

    // helper for create button to receive Inventory reward if one exists and display it
    private void setReceiveInventory() {
        setUpInventorySnippet(benefitVM.getInventoryToRetrieve());
        final View viewPickUp = getLayoutInflater().inflate(R.layout.default_button_details, null);
        DefaultButtonDetailsBinding defaultBindingPickUp = DataBindingUtil.bind(viewPickUp);
        String permText = "Pick Up";
        defaultBindingPickUp.setTitle(permText);
        defaultBindingPickUp.btnDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!benefitVM.addNewInventory(benefitVM.getInventoryToRetrieve()))
                    // Inventory full, display Toast message
                    Toast.makeText(getContext(), benefitVM.getToastString(benefitVM.getInventoryToRetrieve()), Toast.LENGTH_SHORT).show();
                else {
                    // remove the button to pick up Inventory reward
                    benefitBinding.layoutBtnOptions.removeView(viewPickUp);
                    // remove reward so that it can't be saved to storage as it's already taken
                    benefitVM.removeInventoryToRetrieve();
                }
            }
        });
        benefitBinding.layoutBtnOptions.addView(viewPickUp);
    }

}
