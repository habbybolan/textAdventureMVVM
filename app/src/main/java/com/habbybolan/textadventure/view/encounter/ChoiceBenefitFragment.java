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
import com.habbybolan.textadventure.model.inventory.Inventory;
import com.habbybolan.textadventure.view.dialogueAdapter.DialogueRecyclerView;
import com.habbybolan.textadventure.viewmodel.CharacterViewModel;
import com.habbybolan.textadventure.viewmodel.MainGameViewModel;
import com.habbybolan.textadventure.viewmodel.encounters.ChoiceBenefitViewModel;

import org.json.JSONException;
import org.json.JSONObject;

public class ChoiceBenefitFragment extends Fragment implements EncounterFragment {

    private MainGameViewModel mainGameVM;
    private CharacterViewModel characterVM;
    private JSONObject encounter;
    private FragmentChoiceBenefitBinding benefitBinding;
    private ChoiceBenefitViewModel benefitVM;
    private int state = 0;
    private Inventory inventoryToRetrieve = null;
    private DefaultButtonDetailsBinding defaultButtonDetailsBinding;


    public ChoiceBenefitFragment(MainGameViewModel mainGameVM, CharacterViewModel characterVM, JSONObject encounter) {
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
        benefitBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_choice_benefit, container, false);
        defaultButtonDetailsBinding = DataBindingUtil.inflate(inflater, R.layout.default_button_details, container, false);
        try {
            benefitVM = new ChoiceBenefitViewModel(mainGameVM, characterVM, encounter, getActivity());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setUpDialogueRV();
        // create state listener and go into first state
        stateListener();

        return benefitBinding.getRoot();
    }

    @Override
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

    @Override
    public void checkState() {
        switch(state) {
            // first state
            case ChoiceBenefitViewModel.firstState:
                try {
                    dialogueState();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            // second stateW
            case ChoiceBenefitViewModel.secondState:
                chooseBenefitTypeState();
                break;
            // last state
            case ChoiceBenefitViewModel.thirdState:
                endState();
                break;
        }
    }

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

    // state to choose the dialogue
    private void chooseBenefitTypeState() {
        benefitBinding.layoutBtnOptions.removeAllViews();
        // temp. stat increase button
        View viewTemp = getLayoutInflater().inflate(R.layout.default_button_details, null);
        DefaultButtonDetailsBinding defaultBindingTemp = DataBindingUtil.bind(viewTemp);
        String tempText = "Inventory";
        defaultBindingTemp.setTitle(tempText);
        defaultBindingTemp.btnDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpInventorySnippet(benefitVM.getTangible());
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
        benefitBinding.layoutBtnOptions.removeAllViews();
        this.inventoryToRetrieve = inventoryToRetrieve;
        final View view = getLayoutInflater().inflate(R.layout.inventory_snippet, null);
        InventorySnippetBinding snippetBinding = DataBindingUtil.bind(view);

        snippetBinding.setInventoryName(inventoryToRetrieve.getName());
        snippetBinding.setInventoryPic(inventoryToRetrieve.getPictureResource());
        benefitBinding.frameInventorySnippet.addView(view);
    }


    @Override
    public void endState() {
        benefitBinding.layoutBtnOptions.removeAllViews();

        // set up button to leave
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

        if (inventoryToRetrieve != null) {
            final View viewPickUp = getLayoutInflater().inflate(R.layout.default_button_details, null);
            DefaultButtonDetailsBinding defaultBindingPickUp = DataBindingUtil.bind(viewPickUp);
            String permText = "Pick Up";
            defaultBindingPickUp.setTitle(permText);
            defaultBindingPickUp.btnDefault.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!benefitVM.addNewInventory(inventoryToRetrieve))
                        Toast.makeText(getContext(), benefitVM.getToastString(inventoryToRetrieve), Toast.LENGTH_SHORT).show();
                    else
                        benefitBinding.layoutBtnOptions.removeView(viewPickUp);
                }
            });
            benefitBinding.layoutBtnOptions.addView(viewPickUp);
        }
    }
}
