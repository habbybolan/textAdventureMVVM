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
import com.habbybolan.textadventure.databinding.DefaultButtonDetailsBinding;
import com.habbybolan.textadventure.databinding.FragmentChoiceBenefitBinding;
import com.habbybolan.textadventure.databinding.InventorySnippetBinding;
import com.habbybolan.textadventure.model.dialogue.Dialogue;
import com.habbybolan.textadventure.model.dialogue.DialogueType;
import com.habbybolan.textadventure.model.inventory.Inventory;
import com.habbybolan.textadventure.view.dialogueAdapter.DialogueRecyclerView;
import com.habbybolan.textadventure.viewmodel.CharacterViewModel;
import com.habbybolan.textadventure.viewmodel.MainGameViewModel;
import com.habbybolan.textadventure.viewmodel.encounters.ChoiceBenefitViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/*
deals with a choice benefit encounter
  - gives the character a choice of a random permanent state increase, temporary stat increase, or Inventory reward
  - 3 main UI states
     - dialogueState
     - chooseBenefitTypeState
     - endState
 */
public class ChoiceBenefitFragment extends Fragment implements EncounterFragment {

    private MainGameViewModel mainGameVM = MainGameViewModel.getInstance();
    private CharacterViewModel characterVM = CharacterViewModel.getInstance();
    private JSONObject encounter = mainGameVM.getJSONEncounter();
    private FragmentChoiceBenefitBinding benefitBinding;
    private ChoiceBenefitViewModel benefitVM;
    private int state = 0;

    private ArrayList<DialogueType> dialogueList;

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
        dialogueList = new ArrayList<>();;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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
        if (benefitVM.getIsSaved()) {
            dialogueList = benefitVM.getDialogueList();
            benefitVM.setSavedInventory();
        }
        // set up Recycler Viewer that holds all dialogue
        setUpDialogueRV();
        stateListener();
        // called after stateLister set up, signalling first state to enter
        benefitVM.gotoState();
    }

    @Override
    public void setUpDialogueRV() {
        // if there is a saved game, then retrieve the dialogueList that was previously added
        rv = new DialogueRecyclerView(getContext(), benefitBinding.rvDialogue, characterVM, dialogueList);

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
    }

    @Override
    public void checkState() {
        switch(state) {
            // first state
            case ChoiceBenefitViewModel.firstState:
                benefitBinding.layoutBtnOptions.removeAllViews();
                dialogueState();
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
        benefitVM.setSaveRecovered();
    }

    @Override
    public void dialogueState() {
        try {
            JSONObject dialogue = benefitVM.getFirstStateJSON();
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        View viewPerm = getLayoutInflater().inflate(R.layout.default_button_details, null);
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
