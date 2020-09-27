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
import com.habbybolan.textadventure.databinding.FragmentChoiceBenefitBinding;
import com.habbybolan.textadventure.databinding.InventorySnippetBinding;
import com.habbybolan.textadventure.model.inventory.Inventory;
import com.habbybolan.textadventure.view.dialogueAdapter.DialogueRecyclerView;
import com.habbybolan.textadventure.viewmodel.characterEntityViewModels.CharacterViewModel;
import com.habbybolan.textadventure.viewmodel.MainGameViewModel;
import com.habbybolan.textadventure.viewmodel.encounters.ChoiceBenefitViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/*
deals with a choice benefit encounter
  - gives the character a choice of a random permanent state increase, temporary stat increase, or Inventory reward
  - state 1: dialogue state
  - state 2: choose benefit type
  - state 3: end state
 */
public class ChoiceBenefitFragment extends EncounterDialogueFragment implements EncounterFragment {

    private MainGameViewModel mainGameVM = MainGameViewModel.getInstance();
    private CharacterViewModel characterVM = CharacterViewModel.getInstance();
    private JSONObject encounter = mainGameVM.getJSONEncounter();
    private FragmentChoiceBenefitBinding benefitBinding;
    private ChoiceBenefitViewModel benefitVM;
    private DialogueRecyclerView rv;

    public ChoiceBenefitFragment() {}

    public static ChoiceBenefitFragment newInstance() {
        return new ChoiceBenefitFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            benefitVM = new ChoiceBenefitViewModel(mainGameVM, characterVM, encounter, getActivity());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        benefitBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_choice_benefit, container, false);

        try {
            setUpEncounterBeginning();
        } catch (JSONException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        // create state listener and go into first state
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

    @Override
    public void checkState(int state) {
        switch(state) {
            // first state
            case ChoiceBenefitViewModel.firstState:
                benefitBinding.layoutBtnOptions.removeAllViews();
                dialogueState(benefitVM, benefitBinding.layoutBtnOptions);
                break;
            // second stateW
            case ChoiceBenefitViewModel.secondState:
                benefitBinding.layoutBtnOptions.removeAllViews();
                chooseBenefitTypeState();
                break;
            // last state
            case ChoiceBenefitViewModel.thirdState:
                benefitBinding.layoutBtnOptions.removeAllViews();
                endState();
                break;
        }
        // set the isSaved to false to signal that the save has been retrieved
        benefitVM.setIsSaved(false);
    }

    // state to choose the dialogue
    private void chooseBenefitTypeState() {
        // temp. stat increase button
        View viewTemp = getLayoutInflater().inflate(R.layout.default_button_details, null);
        DefaultButtonDetailsBinding defaultBindingTemp = DataBindingUtil.bind(viewTemp);
        String tempText = "Inventory";
        defaultBindingTemp.setTitle(tempText);
        defaultBindingTemp.btnDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                benefitVM.setTangible();
                benefitVM.incrementStateIndex();
            }
        });

        // perm. stat increase button
        View viewPerm = getLayoutInflater().inflate(R.layout.default_button_details,null);
        DefaultButtonDetailsBinding defaultBindingPerm = DataBindingUtil.bind(viewPerm);
        String permText = "Perm. Increase";
        defaultBindingPerm.setTitle(permText);
        defaultBindingPerm.btnDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                benefitVM.setPermIncrease();
                benefitVM.incrementStateIndex();
            }
        });

        // gain inventory item button
        View viewInv = getLayoutInflater().inflate(R.layout.default_button_details, null);
        DefaultButtonDetailsBinding defaultBindingInv = DataBindingUtil.bind(viewInv);
        String invText = "Temp. Increase";
        defaultBindingInv.setTitle(invText);
        defaultBindingInv.btnDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                benefitVM.setTempIncrease();
                benefitVM.incrementStateIndex();
            }
        });

        benefitBinding.layoutBtnOptions.addView(viewTemp);
        benefitBinding.layoutBtnOptions.addView(viewPerm);
        benefitBinding.layoutBtnOptions.addView(viewInv);
    }

    // inventory snippet of new Inventory object to retrieve
    private void setUpInventorySnippet(final Inventory inventoryToRetrieve) {
        final View view = getLayoutInflater().inflate(R.layout.inventory_snippet, null);
        InventorySnippetBinding snippetBinding = DataBindingUtil.bind(view);

        snippetBinding.setInventoryName(inventoryToRetrieve.getName());
        snippetBinding.setInventoryPic(inventoryToRetrieve.getPictureResource());
        benefitBinding.frameInventorySnippet.addView(view);
    }

    @Override
    public void endState() {
        // set up button to leave
        setLeaveButton(mainGameVM, benefitBinding.layoutBtnOptions);
        // set up button to receive reward if one exists
        setReceiveInventory();
    }

    // helper for create button to receive Inventory reward if one exists
    private void setReceiveInventory() {
        // if an inventory reward exists, then create button for it
        if (benefitVM.getInventoryToRetrieve() != null) {
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

    @Override
    public void onStop() {
        super.onStop();
        characterVM.saveCharacter();
        benefitVM.saveEncounter(rv.getDialogueList());
    }
}
