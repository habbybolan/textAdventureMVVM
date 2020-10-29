package com.habbybolan.textadventure.view.encounter;

import android.content.Intent;
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
import com.habbybolan.textadventure.view.inventoryinfo.InventoryInfoActivity;
import com.habbybolan.textadventure.view.inventoryinfo.InventoryInfoFragment;
import com.habbybolan.textadventure.viewmodel.MainGameViewModel;
import com.habbybolan.textadventure.viewmodel.characterEntityViewModels.CharacterViewModel;
import com.habbybolan.textadventure.viewmodel.encounters.RandomBenefitViewModel;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Gives the player a random Inventory object reward.
 */
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
            rv = setUpEncounterBeginning(benefitVM, this, benefitBinding.rvDialogue);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return benefitBinding.getRoot();
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
            // last state
            case RandomBenefitViewModel.secondState:
                benefitBinding.layoutBtnOptions.removeAllViews();
                endState();
                break;
        }
    }

    // inventory snippet of new Inventory object to retrieve
    private void setUpInventorySnippet(final Inventory inventoryToRetrieve) {
        View view = getLayoutInflater().inflate(R.layout.inventory_snippet, null);
        InventorySnippetBinding snippetBinding = DataBindingUtil.bind(view);

        snippetBinding.setInventoryName(inventoryToRetrieve.getName());
        snippetBinding.setInventoryPic(inventoryToRetrieve.getPictureResource());
        benefitBinding.frameInventorySnippet.addView(view);
        snippetBinding.inventoryInfoAttack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), InventoryInfoActivity.class);
                try {
                    intent.putExtra(InventoryInfoFragment.INVENTORY_SERIALIZED, inventoryToRetrieve.serializeToJSON().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
            }
        });
    }

    // third and final state
        // set up the button to leave the encounter and to pick up item
    @Override
    public void endState() {
        benefitVM.setTangible();
        // set up button to leave
        setLeaveButton(benefitBinding.layoutBtnOptions);
        // set up button to receive reward if one exists
        setReceiveInventory();
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

    @Override
    public void onStop() {
        super.onStop();
        benefitVM.saveGame(rv);
    }

}
