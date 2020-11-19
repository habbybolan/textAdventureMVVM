package com.habbybolan.textadventure.view.encounter;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.DefaultButtonDetailsBinding;
import com.habbybolan.textadventure.databinding.FragmentChoiceBenefitBinding;
import com.habbybolan.textadventure.databinding.InventorySnippetBinding;
import com.habbybolan.textadventure.model.inventory.Inventory;
import com.habbybolan.textadventure.view.ButtonInflaters;
import com.habbybolan.textadventure.view.CustomPopupWindow;
import com.habbybolan.textadventure.view.dialogueAdapter.DialogueRecyclerView;
import com.habbybolan.textadventure.view.inventoryinfo.InventoryInfoActivity;
import com.habbybolan.textadventure.view.inventoryinfo.InventoryInfoFragment;
import com.habbybolan.textadventure.viewmodel.encounters.ChoiceBenefitViewModel;

import org.json.JSONException;

/**
 * gives the character a choice of a:
 *      random permanent stat increase,
 *      temporary stat increase,
 *      or Inventory reward
 */
public class ChoiceBenefitFragment extends EncounterDialogueFragment implements EncounterFragment {

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
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        benefitBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_choice_benefit, container, false);
        // create state listener and go into first state
        return benefitBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        benefitVM = new ViewModelProvider(this).get(ChoiceBenefitViewModel.class);
        try {
            rv = setUpEncounterBeginning(benefitVM, this, benefitBinding.rvDialogue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        String txtInventory = "Inventory";
        DefaultButtonDetailsBinding bindingInventory = ButtonInflaters.setDefaultButton(benefitBinding.layoutBtnOptions, txtInventory, getActivity());
        bindingInventory.btnDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                benefitVM.setTangible();
                benefitVM.incrementStateIndex();
            }
        });

        // perm. stat increase button
        String permText = "Perm. Increase";
        DefaultButtonDetailsBinding bindingPerm = ButtonInflaters.setDefaultButton(benefitBinding.layoutBtnOptions, permText, getActivity());
        bindingPerm.btnDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                benefitVM.setPermIncrease();
                benefitVM.incrementStateIndex();
            }
        });


        // gain inventory item button
        String tempText = "Temp. Increase";
        DefaultButtonDetailsBinding bindingTemp = ButtonInflaters.setDefaultButton(benefitBinding.layoutBtnOptions, tempText, getActivity());
        bindingTemp.btnDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                benefitVM.setTempIncrease();
                benefitVM.incrementStateIndex();
            }
        });
    }

    // inventory snippet of new Inventory object to retrieve
    private void setUpInventorySnippet(final Inventory inventoryToRetrieve) {
        final View view = getLayoutInflater().inflate(R.layout.inventory_snippet, null);
        InventorySnippetBinding snippetBinding = DataBindingUtil.bind(view);

        snippetBinding.setInventoryName(inventoryToRetrieve.getName());
        snippetBinding.setInventoryPic(inventoryToRetrieve.getPictureResource());
        benefitBinding.frameInventorySnippet.addView(view);
        snippetBinding.inventoryInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), InventoryInfoActivity.class);
                try {
                    intent.putExtra(InventoryInfoFragment.INVENTORY_SERIALIZED, inventoryToRetrieve.serializeToJSON().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.fade_out);
            }
        });
    }

    @Override
    public void endState() {
        // set up button to leave
        setLeaveButton(benefitBinding.layoutBtnOptions);
        // set up button to receive reward if one exists
        setReceiveInventory();
    }

    // helper for create button to receive Inventory reward if one exists
    private void setReceiveInventory() {
        // if an inventory reward exists, then create button for it
        if (benefitVM.isInventoryToRetrieve()) {
            setUpInventorySnippet(benefitVM.getInventoryToRetrieve());
            final View viewPickUp = getLayoutInflater().inflate(R.layout.default_button_details, null);
            DefaultButtonDetailsBinding defaultBindingPickUp = DataBindingUtil.bind(viewPickUp);
            String permText = "Pick Up";
            assert defaultBindingPickUp != null;
            defaultBindingPickUp.setTitle(permText);
            defaultBindingPickUp.btnDefault.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!benefitVM.addNewInventory(benefitVM.getInventoryToRetrieve()))
                        // Inventory full, display popup window message
                        CustomPopupWindow.setTempMessage(benefitVM.getFullMessageString(benefitVM.getInventoryToRetrieve()), getContext(), new PopupWindow(), benefitBinding.benefitContainer);
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
        benefitVM.saveGame(rv);
    }
}
