package com.habbybolan.textadventure.view.encounter;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.recyclerview.widget.RecyclerView;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.DefaultButtonDetailsBinding;
import com.habbybolan.textadventure.databinding.FragmentCombatBinding;
import com.habbybolan.textadventure.model.characterentity.CharacterEntity;
import com.habbybolan.textadventure.model.inventory.Action;
import com.habbybolan.textadventure.model.inventory.Inventory;
import com.habbybolan.textadventure.view.CombatOrderAdapter;
import com.habbybolan.textadventure.view.CustomPopupWindow;
import com.habbybolan.textadventure.view.InventoryListAdapter.AbilityListRecyclerView;
import com.habbybolan.textadventure.view.InventoryListAdapter.InventoryClickListener;
import com.habbybolan.textadventure.view.InventoryListAdapter.ItemListRecyclerView;
import com.habbybolan.textadventure.view.InventoryListAdapter.WeaponListRecyclerView;
import com.habbybolan.textadventure.view.dialogueAdapter.DialogueRecyclerView;
import com.habbybolan.textadventure.view.inventoryinfo.InventoryInfoActivity;
import com.habbybolan.textadventure.view.inventoryinfo.InventoryInfoFragment;
import com.habbybolan.textadventure.viewmodel.MainGameViewModel;
import com.habbybolan.textadventure.viewmodel.characterEntityViewModels.CharacterViewModel;
import com.habbybolan.textadventure.viewmodel.characterEntityViewModels.EnemyViewModel;
import com.habbybolan.textadventure.viewmodel.encounters.CombatViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/**
fragment for combat encounter
    state 1: dialogue
    state 2: before combat - go into fight or run attempt
    state 3: set up combat
        // swap between state 3 and 4 until either character dies, or all enemies die
    state 4: player character turn
    state 5: enemy turn
    state 6: end state
 */
public class CombatFragment extends EncounterDialogueFragment implements EncounterFragment {

    private MainGameViewModel mainGameVM = MainGameViewModel.getInstance();
    private CharacterViewModel characterVM = CharacterViewModel.getInstance();
    private JSONObject encounter = mainGameVM.getEncounter().getEncounterJSON();
    private FragmentCombatBinding combatBinding;
    private CombatViewModel combatVM;
    private DialogueRecyclerView rv;

    private AbilityListRecyclerView abilityRV;
    private WeaponListRecyclerView weaponRV;
    private ItemListRecyclerView itemRV;

    private CombatOrderAdapter currListAdapter;
    private CombatOrderAdapter nextListAdapter;
    private CombatOrderAdapter lastListAdapter;

    private CombatFragment() {
        // Required empty public constructor
    }

    public static CombatFragment newInstance() {
        return new CombatFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mainGameVM = MainGameViewModel.getInstance();
            characterVM = CharacterViewModel.getInstance();
            combatVM = new CombatViewModel(encounter, getActivity());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        combatBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_combat, container, false);
        combatBinding.setCharacterVM(characterVM);
        try {
            rv = setUpEncounterBeginning(combatVM, this, combatBinding.rvDialogue);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return combatBinding.getRoot();
    }

    /**
     * Initiates all info including recycler viewers, enemies, and button functionality for the combat state.
     */
    private void initiateCombatInfo()  {
        try {
            setUpInCombatRunButtonClick();
            setClickersForCategoryOptions();
            // if no saved encounter, then create necessary info
            if (!combatVM.getIsSaved())
                combatVM.createCombat();
            setCombatOrdering();
            setCombatDialogueListener();

            setContinueButtonClick();
            setActionSelect();

            setInventoryRecyclerViewers();
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void checkState(int state) {
        switch (state) {
            case CombatViewModel.firstState:
                // dialogue state
                isCharacterViewsEnabled(true);
                combatBinding.layoutBtnOptions.removeAllViews();
                dialogueState(combatVM, combatBinding.layoutBtnOptions);
                break;
            case CombatViewModel.secondState:
                // before combat state (fight/run buttons)
                isCharacterViewsEnabled(true);
                // remove ability to drop/consume Inventory Objects
                characterVM.setStateInventoryObserver(false);
                combatBinding.layoutBtnOptions.removeAllViews();
                beforeCombatState();
                break;
            case CombatViewModel.thirdState:
                // remove ability to drop/consume Inventory Objects
                characterVM.setStateInventoryObserver(false);
                // set up combat views state
                combatBinding.layoutBtnOptions.removeAllViews();
                isCharacterViewsEnabled(false);
                startCombatState();
                break;
            case CombatViewModel.fourthState:
                // remove ability to drop/consume Inventory Objects
                characterVM.setStateInventoryObserver(false);
                // character turn state
                isCharacterViewsEnabled(true);
                characterTurnState();
                break;
            case CombatViewModel.fifthState:
                // remove ability to drop/consume Inventory Objects
                characterVM.setStateInventoryObserver(false);
                // enemy turn state
                isCharacterViewsEnabled(false);
                enemyTurnState();
                break;
            case CombatViewModel.sixthState:
                // remove ability to drop/consume Inventory Objects
                characterVM.setStateInventoryObserver(true);
                // ending state for accepting rewards and button to leave encounter
                isCharacterViewsEnabled(true);
                combatBinding.inCombatContainer.removeAllViews();
                combatBinding.layoutBtnOptions.removeAllViews();
                endState();
                break;
        }
        // set the isSaved to false to signal that the save has been retrieved
        combatVM.setIsSaved(false);
    }

    // UI before combat, asking player to enter combat or attempt to escape
    private void beforeCombatState() {
        setUpFightButton();
        setUpBeforeCombatRunButton();
    }

    /**
     * helper to programmatically create the button the run from combat encounter before combat starts
     */
    private void setUpBeforeCombatRunButton() {
        View viewRun = getLayoutInflater().inflate(R.layout.default_button_details, null);
        DefaultButtonDetailsBinding defaultBindingFight = DataBindingUtil.bind(viewRun);
        String runText = "Run";
        defaultBindingFight.setTitle(runText);
        defaultBindingFight.btnDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // todo: before combat run button
            }
        });
        combatBinding.layoutBtnOptions.addView(viewRun);
    }

    /**
     * helper to programmatically created button to enter the fight
     */
    private void setUpFightButton() {
        View viewFight = getLayoutInflater().inflate(R.layout.default_button_details, null);
        DefaultButtonDetailsBinding defaultBindingFight = DataBindingUtil.bind(viewFight);
        String fightText = "Fight";
        defaultBindingFight.setTitle(fightText);
        defaultBindingFight.btnDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // goto next state
                combatVM.incrementStateIndex();
            }
        });
        combatBinding.layoutBtnOptions.addView(viewFight);
    }

    /** buttons for attempting to escape the combat encounter
     *
     */
    private void setUpInCombatRunButtonClick() {
        combatBinding.btnRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                combatVM.attemptInCombatRun();
                notifyCombatOrderNextTurn();
            }
        });
    }

    /**
     * button for continuing through the enemy actions
     */
    private void setContinueButtonClick() {
        combatBinding.btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // applies the enemy action and updates the combat order lists.
                combatVM.enemyAction();
                notifyCombatOrderNextTurn();
            }
        });
    }

    /**
     * the clickers for category options that swaps between the recyclerViewers that show character inventory options
     */
    private void setClickersForCategoryOptions() {
        combatBinding.rvCategoryWeapons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onWeaponCategoryClick();
            }
        });
        combatBinding.rvCategoryAbilities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAbilityCategoryClick();
            }
        });
        combatBinding.rvCategoryItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemCategoryClick();
            }
        });
    }

    /**
     * on weapon category button pressed
     */
    private void onWeaponCategoryClick() {
        combatBinding.rvWeapons.setVisibility(View.VISIBLE);
        combatBinding.rvAbilities.setVisibility(View.GONE);
        combatBinding.rvItems.setVisibility(View.GONE);
    }

    /**
     * on ability category button pressed
     */
    private void onAbilityCategoryClick() {
        combatBinding.rvWeapons.setVisibility(View.GONE);
        combatBinding.rvAbilities.setVisibility(View.VISIBLE);
        combatBinding.rvItems.setVisibility(View.GONE);
    }

    /**
     * on item category button pressed
     */
    private void onItemCategoryClick() {
        combatBinding.rvWeapons.setVisibility(View.GONE);
        combatBinding.rvAbilities.setVisibility(View.GONE);
        combatBinding.rvItems.setVisibility(View.VISIBLE);
    }

    /**
     * creates the icons for the enemies that will be selected for applying the character action
     */
    private void setActionSelect() {
        final int sizeDP = getResources().getDimensionPixelSize(R.dimen.combat_enemy_action_icon_size);
        for (EnemyViewModel enemyVM : combatVM.getEnemies()) {
            ImageView enemyIconSelectable = new ImageView(getContext());
            // converted value from dp to int for layoutParam
            ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(sizeDP, sizeDP);
            enemyIconSelectable.setLayoutParams(params);
            enemyIconSelectable.setTag(enemyVM.getEnemy());

            enemyIconSelectable.setImageResource(enemyVM.getEnemy().getDrawableResID());
            // on click, use the selected action
            enemyIconSelectable.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.icon_action_target));
                    // perform action
                    characterActionOnClick((CharacterEntity) v.getTag());
                }
            });
            combatBinding.enemyActionIcons.addView(enemyIconSelectable);
        }
        combatBinding.characterActionIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.icon_action_target));
                // perform action
                characterActionOnClick(characterVM.getCharacter());
            }
        });
    }

    /**
     * Helper for performing character action on icon click, or displaying action error message.
     * @param target    The CharacterEntity action is used on
     */
    private void characterActionOnClick(CharacterEntity target) {
        if (combatVM.characterAction(target)) {
            // action is valid and performed, so update the combat ordering
            notifyCombatOrderNextTurn();
        } else {
            // otherwise the action failed, get the error message from combatVM and display with popup window
            String error = combatVM.getActionError(target);
            CustomPopupWindow.setTempMessage(error, getContext(), new PopupWindow(), combatBinding.container);
        }
    }

    /**
     * Shows the category recyclerViewer for the character actions and the target-able action icons. The first turn
     * is also started here based on the CharacterEntity in the front of the combat ordering by calling setFirstTurn.
     */
    private void startCombatState() {
        combatBinding.inCombatContainer.setVisibility(View.VISIBLE);
        initiateCombatInfo();
        combatVM.setFirstTurn();
    }

    /**
     *  creates the inventory recycler viewers that hold all the character actions
     */
    private void setInventoryRecyclerViewers() {
        // clickers for saving selected inventory action and un-select others
        abilityRV = new AbilityListRecyclerView(getContext(), combatBinding.rvAbilities, characterVM, combatVM, new InventoryClickListener() {
            @Override
            public void onClicked(Inventory object) {
                // set selected action the selected Ability action
                combatVM.setSelectedInventoryAction((Action) object);
                weaponRV.unSelectIfOneSelected();
                itemRV.unSelectIfOneSelected();
            }

            @Override
            public void onInfoClick(Inventory object) {
                InventoryActivity(object);
            }
        });

        weaponRV = new WeaponListRecyclerView(getContext(), combatBinding.rvWeapons, characterVM, combatVM, new InventoryClickListener() {
            @Override
            public void onClicked(Inventory object) {
                // set selected action the selected Weapon action
                combatVM.setSelectedInventoryAction((Action) object);
                abilityRV.unSelectIfOneSelected();
                itemRV.unSelectIfOneSelected();
            }

            @Override
            public void onInfoClick(Inventory object) {
                InventoryActivity(object);
            }
        });

        itemRV = new ItemListRecyclerView(getContext(), combatBinding.rvItems, characterVM, combatVM, new InventoryClickListener() {
            @Override
            public void onClicked(Inventory object) {
                // set selected action the selected Item action
                combatVM.setSelectedInventoryAction((Action) object);
                abilityRV.unSelectIfOneSelected();
                weaponRV.unSelectIfOneSelected();
            }

            @Override
            public void onInfoClick(Inventory object) {
                InventoryActivity(object);
            }
        });

    }

    /**
     *  Creates the new information activity for the Inventory object clicked. Puts current Activity on back stack and fills the screen
     *  with the information activity to show necessary information of Inventory object clicked.
     * @param object    Inventory used as an action whose information will be displayed.
     */
    private void InventoryActivity(Inventory object) {
        Intent intent = new Intent(getContext(), InventoryInfoActivity.class);
        intent.putExtra(InventoryInfoFragment.INVENTORY_SERIALIZED, combatVM.serializeInventory(object));
        startActivity(intent);
    }

    /**
     *  un-select all items in the inventory RVs
     */
    private void unSelectAllActionRV() {
        abilityRV.unSelectIfOneSelected();
        itemRV.unSelectIfOneSelected();
        weaponRV.unSelectIfOneSelected();
    }

    /**
     *  listener that adds the combat dialogue showing the attacker, target, and the action used on target
     */
    private void setCombatDialogueListener() {
        Observable.OnPropertyChangedCallback callback = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                rv.addNewCombatDialogue(combatVM.getNewCombatActionDialogueValue());
            }
        };
        combatVM.getNewCombatActionDialogue().addOnPropertyChangedCallback(callback);
    }

    /**
     *  deals with the character action UI in combat
     */
    private void characterTurnState() {
        if (combatBinding.inCombatContainer.getVisibility() == View.GONE)
            combatBinding.inCombatContainer.setVisibility(View.VISIBLE);
        unSelectAllActionRV();
        combatBinding.btnContinue.setVisibility(View.GONE);
        combatBinding.btnRun.setVisibility(View.VISIBLE);
    }

    /**
     *  sets the character views interaction enabled or disabled
     * @param isEnabled     The boolean value to enable character views
     */
    private void isCharacterViewsEnabled(boolean isEnabled) {
        // enable/disable character selectable icon
        combatBinding.characterActionIcon.setEnabled(isEnabled);
        for (int i = 0; i < combatBinding.enemyActionIcons.getChildCount(); i++) {
            // add/disable each enemy selectable icon inside enemyActionIcons
            combatBinding.enemyActionIcons.getChildAt(i).setEnabled(isEnabled);
        }
        combatBinding.inCombatContainer.setEnabled(isEnabled);
    }

    /**
     *  deals with the enemy action UI in combat
     */
    private void enemyTurnState() {
        if (combatBinding.inCombatContainer.getVisibility() == View.GONE)
            combatBinding.inCombatContainer.setVisibility(View.VISIBLE);
        unSelectAllActionRV();
        combatBinding.btnContinue.setVisibility(View.VISIBLE);
        combatBinding.btnRun.setVisibility(View.GONE);
    }

    /**
     *  Updates the curr (first) and last (third) list of the combat ordering.
     *  The next (second) list doesn't change, thus doesn't need to be updated.
     */
    private void notifyCombatOrderNextTurn() {
        currListAdapter.nextTurnCurr();
        nextListAdapter.nextTurnNext();
        lastListAdapter.nextTurnLast();
    }

    /**
     *  set up initial RecyclerViewer adapters for the combat ordering inside the 3 lists. combatOrderCurr deals with the current round
     *  of turns, combatOrderNext deals with the next round, and combatOrderLast deals with the third if combatOrderCurr is not filled.
     *  combatOrderCurr filled if it contains same number of elements as combatOrderNext. (all enemies and character included)
     */
    private void setCombatOrdering() {
        // current list RV setup
        RecyclerView currRV = combatBinding.combatCurr;
        currListAdapter = new CombatOrderAdapter(combatVM.getCombatOrderCurr());
        currRV.setAdapter(currListAdapter);
        // next list RV setup
        RecyclerView nextRV = combatBinding.combatNext;
        nextListAdapter = new CombatOrderAdapter(combatVM.getCombatOrderNext());
        nextRV.setAdapter(nextListAdapter);
        // last list RV setup
        RecyclerView lastRV = combatBinding.combatLast;
        lastListAdapter = new CombatOrderAdapter(combatVM.getCombatOrderLast());
        lastRV.setAdapter(lastListAdapter);
    }

    /**
     *  set up leave button and the reward for completing combat, as well as button to pick up any Inventory reward
     */
    @Override
    public void endState() {
        // set up button to leave
        setLeaveButton(combatBinding.layoutBtnOptions);
    }

    @Override
    public void onStop() {
        super.onStop();
        combatVM.saveGame(rv);
    }
}
