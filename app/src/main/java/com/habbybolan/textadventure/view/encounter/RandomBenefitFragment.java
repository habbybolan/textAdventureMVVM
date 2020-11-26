package com.habbybolan.textadventure.view.encounter;

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
import com.habbybolan.textadventure.databinding.FragmentRandomBenefitBinding;
import com.habbybolan.textadventure.databinding.InventorySnippetBinding;
import com.habbybolan.textadventure.model.inventory.InventoryEntity;
import com.habbybolan.textadventure.view.ButtonInflaters;
import com.habbybolan.textadventure.view.CustomPopupWindow;
import com.habbybolan.textadventure.view.dialogueAdapter.DialogueRecyclerView;
import com.habbybolan.textadventure.view.inventoryinfo.CreateInventoryInfoActivity;
import com.habbybolan.textadventure.viewmodel.encounters.RandomBenefitViewModel;

import org.json.JSONException;

/**
 * Gives the player a random Inventory object reward.
 * If manually creating encounter, use RandomBenefitModel to create the encounter JSON.
 */
public class RandomBenefitFragment extends EncounterDialogueFragment implements EncounterFragment{

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
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        benefitBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_random_benefit, container, false);
        return benefitBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        benefitVM = new ViewModelProvider(this).get(RandomBenefitViewModel.class);
        try {
            rv = setUpEncounterBeginning(benefitVM, this, benefitBinding.rvDialogue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // checks which state the trap encounter is in and starts that new state
    @Override
    public void checkState(int state) {
        switch(state) {
            // first state
            case RandomBenefitViewModel.dialogueState:
                benefitBinding.layoutBtnOptions.removeAllViews();
                dialogueState(benefitVM, benefitBinding.layoutBtnOptions);
                break;
            // reward state
            case RandomBenefitViewModel.ExpGoldRewardState:
                benefitBinding.layoutBtnOptions.removeAllViews();
                setExpGoldRewards();
                break;
            case RandomBenefitViewModel.inventoryRewardState:
                benefitBinding.frameInventorySnippet.setVisibility(View.VISIBLE);
                setInventoryRewards();
                break;
                // last state
            case RandomBenefitViewModel.endState:
                benefitBinding.frameInventorySnippet.setVisibility(View.GONE);
                benefitBinding.layoutBtnOptions.removeAllViews();
                endState();
                break;

        }
    }

    /**
     * Sets the gold and exp rewards if there are any.
     */
    private void setExpGoldRewards() {
        benefitVM.createExpReward();
        benefitVM.createGoldReward();
        benefitVM.incrementStateIndex();
    }

    /**
     * Sets the Inventory rewards. Separate from gold and exp rewards so when the saved encounter is created again,
     * The gold and exp rewards won't double up.
     * As each Inventory rewards is taken/left, removed from inventoryRewards so it can't taken again.
     */
    private void setInventoryRewards() {
        // create the rewards
        benefitVM.createInventoryReward();
        // display the first reward
        displayNewReward();

        // clicker and button for picking up the reward
        String pickUpText = getResources().getString(R.string.pick_up_inventory);
        DefaultButtonDetailsBinding bindingPickUp = ButtonInflaters.setDefaultButton(benefitBinding.layoutBtnOptions, pickUpText, getActivity());
        bindingPickUp.btnDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!benefitVM.addNewInventory()) {
                    CustomPopupWindow.setTempMessage(benefitVM.getFullMessageString(), getContext(), new PopupWindow(), benefitBinding.benefitContainer);
                } else {
                    displayNewReward();
                }
            }
        });

        // clicker and button for leaving reward
        String leaveText = getResources().getString(R.string.leave_inventory);
        DefaultButtonDetailsBinding bindingLeave = ButtonInflaters.setDefaultButton(benefitBinding.layoutBtnOptions, leaveText, getActivity());
        bindingLeave.btnDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                benefitVM.removeTopRewardAndContinue();
                displayNewReward();
            }
        });
    }

    /**
     * Display the next Inventory inventory reward using inventory_snippet.
     */
    private void displayNewReward() {
        if (benefitVM.isMoreRewards()) {
            final InventoryEntity inventoryEntity = benefitVM.getNextInventory();
            InventorySnippetBinding snippetBinding = benefitBinding.snippetBinding;
            snippetBinding.setInventoryName(benefitVM.getNextInventoryName());
            snippetBinding.setInventoryPic(benefitVM.getNextInventoryPicResource());
            snippetBinding.inventoryInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CreateInventoryInfoActivity.createInventoryInfoActivity(getActivity(), inventoryEntity);
                }
            });
        }
    }

    /**
     * Final end state to leave the encounter.
     */
    @Override
    public void endState() {
        // set up button to leave
        setLeaveButton(benefitBinding.layoutBtnOptions);
    }

    @Override
    public void onStop() {
        super.onStop();
        benefitVM.saveGame(rv);
    }

}
