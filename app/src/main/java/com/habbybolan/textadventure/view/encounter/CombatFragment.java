package com.habbybolan.textadventure.view.encounter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.recyclerview.widget.RecyclerView;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.DefaultButtonDetailsBinding;
import com.habbybolan.textadventure.databinding.FragmentCombatBinding;
import com.habbybolan.textadventure.model.characterentity.CharacterEntity;
import com.habbybolan.textadventure.model.inventory.Inventory;
import com.habbybolan.textadventure.view.CombatOrderAdapter;
import com.habbybolan.textadventure.view.InventoryListAdapter.AbilityListRecyclerView;
import com.habbybolan.textadventure.view.InventoryListAdapter.InventoryClickListener;
import com.habbybolan.textadventure.view.InventoryListAdapter.ItemListRecyclerView;
import com.habbybolan.textadventure.view.InventoryListAdapter.WeaponListRecyclerView;
import com.habbybolan.textadventure.view.dialogueAdapter.DialogueRecyclerView;
import com.habbybolan.textadventure.viewmodel.MainGameViewModel;
import com.habbybolan.textadventure.viewmodel.characterEntityViewModels.CharacterViewModel;
import com.habbybolan.textadventure.viewmodel.characterEntityViewModels.EnemyViewModel;
import com.habbybolan.textadventure.viewmodel.encounters.CombatViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/*
fragment for combat encounter
    state 1: dialogue
    state 2: before combat - go into fight or run attempt
    state 3: set up combat
        // swap between state 3 and 4 until either character dies, or all enemies die
    state 4: player character turn
    state 5: enemy turn
    state 6: reward state
    state 7 end state

 */
public class CombatFragment extends EncounterDialogueFragment implements EncounterFragment {

    private MainGameViewModel mainGameVM = MainGameViewModel.getInstance();
    private CharacterViewModel characterVM = CharacterViewModel.getInstance();
    private JSONObject encounter = mainGameVM.getJSONEncounter();
    private FragmentCombatBinding combatBinding;
    private CombatViewModel combatVM;
    private DialogueRecyclerView rv;

    private AbilityListRecyclerView abilityRV;
    private WeaponListRecyclerView weaponRV;
    private ItemListRecyclerView itemRV;

    private CombatOrderAdapter currListAdapter;
    private CombatOrderAdapter nextListAdapter;
    private CombatOrderAdapter lastListAdapter;


    public CombatFragment() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        combatBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_combat, container, false);
        combatBinding.setCharacterVM(characterVM);
        try {
            setUpEncounterBeginning();
        } catch (JSONException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        return combatBinding.getRoot();
    }

    // sets up the RV dialogue adapter and the encounter state to enter
    private void setUpEncounterBeginning() throws InterruptedException, ExecutionException, JSONException {
        combatVM.setSavedData();
        // set up Recycler Viewer that holds all dialogue
        rv = new DialogueRecyclerView(getContext(), combatBinding.rvDialogue, combatVM.getDialogueList());
        setUpDialogueRV(rv, combatVM);
        stateListener(combatVM.getStateIndex(), combatVM, this);
        // called after stateLister set up, signalling first state to enter
        combatVM.gotoBeginningState(mainGameVM);
    }

    @Override
    public void checkState(int state) {
        try {
            switch (state) {
                // first state
                case CombatViewModel.firstState:
                    combatBinding.layoutBtnOptions.removeAllViews();
                    dialogueState(combatVM, combatBinding.layoutBtnOptions);
                    break;
                // second state
                case CombatViewModel.secondState:
                    combatBinding.layoutBtnOptions.removeAllViews();
                    beforeCombatState();
                    break;
                case CombatViewModel.thirdState:
                    combatBinding.layoutBtnOptions.removeAllViews();
                    setCombatState();
                    break;
                case CombatViewModel.fourthState:
                    characterTurnState();
                    break;
                case CombatViewModel.fifthState:
                    enemyTurnState();
                    break;
                case CombatViewModel.sixthState:
                    combatBinding.layoutBtnOptions.removeAllViews();
                    // todo: reward state
                    break;
                // last state
                case CombatViewModel.seventhState:
                    combatBinding.layoutBtnOptions.removeAllViews();
                    endState();
                    break;
            }
            // set the isSaved to false to signal that the save has been retrieved
            combatVM.setIsSaved(false);
        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }
    }

    // UI before combat, asking player to enter combat or attempt to escape
    private void beforeCombatState() {
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
        setUpRunButtonClick();
    }

    // buttons for attempting to escape the combat encounter
    private void setUpRunButtonClick() {
        combatBinding.btnRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //combatVM.attemptRun();
            }
        });
        combatBinding.btnRun.setVisibility(View.VISIBLE);
    }

    private void setContinueButtonClick() {
        combatBinding.btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                combatVM.enemyAction();
                notifyCombatOrderNextTurn();
            }
        });
    }

    // the clickers for category options that swaps between the recyclerViewers that show character inventory options
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

    // on weapon category button pressed
    private void onWeaponCategoryClick() {
        combatBinding.rvWeapons.setVisibility(View.VISIBLE);
        combatBinding.rvAbilities.setVisibility(View.GONE);
        combatBinding.rvItems.setVisibility(View.GONE);
    }

    // on ability category button pressed
    private void onAbilityCategoryClick() {
        combatBinding.rvWeapons.setVisibility(View.GONE);
        combatBinding.rvAbilities.setVisibility(View.VISIBLE);
        combatBinding.rvItems.setVisibility(View.GONE);
    }

    // on item category button pressed
    private void onItemCategoryClick() {
        combatBinding.rvWeapons.setVisibility(View.GONE);
        combatBinding.rvAbilities.setVisibility(View.GONE);
        combatBinding.rvItems.setVisibility(View.VISIBLE);
    }

    // creates the icons for the enemies that will be selected for applying the character action
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

    // helper for performing character action on icon click, or displaying action error message
    private void characterActionOnClick(CharacterEntity target) {
        if (combatVM.characterAction(target)) {
            // action is valid and performed, so update the combat ordering
            notifyCombatOrderNextTurn();
        } else {
            // otherwise the action failed, get the error message from combatVM and display with Toast
            String error = combatVM.getActionError(target);
            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        }
    }

    // UI for combat state
    private void setCombatState() throws InterruptedException, ExecutionException, JSONException {
        combatBinding.rvCategoryOptions.setVisibility(View.VISIBLE);
        combatBinding.characterActionIcon.setVisibility(View.VISIBLE);
        setClickersForCategoryOptions();
        combatVM.createCombat();
        setCombatOrdering();
        setCombatDialogueListener();

        setContinueButtonClick();
        setActionSelect();

        setInventoryRecyclerViewers();
        combatVM.setFirstTurn();
    }

    // creates the inventory recycler viewers that hold all the character actions
    private void setInventoryRecyclerViewers() {
        // clickers for saving selected inventory action and un-select others
        abilityRV = new AbilityListRecyclerView(getContext(), combatBinding.rvAbilities, characterVM, combatVM, new InventoryClickListener() {
            @Override
            public void onClicked(Inventory object) {
                // set selected action the selected Ability action
                combatVM.setSelectedInventoryAction(object);
                weaponRV.unSelectIfOneSelected();
                itemRV.unSelectIfOneSelected();
            }
        });

        weaponRV = new WeaponListRecyclerView(getContext(), combatBinding.rvWeapons, characterVM, combatVM, new InventoryClickListener() {
            @Override
            public void onClicked(Inventory object) {
                // set selected action the selected Weapon action
                combatVM.setSelectedInventoryAction(object);
                abilityRV.unSelectIfOneSelected();
                itemRV.unSelectIfOneSelected();
            }
        });

        itemRV = new ItemListRecyclerView(getContext(), combatBinding.rvItems, characterVM, combatVM, new InventoryClickListener() {
            @Override
            public void onClicked(Inventory object) {
                // set selected action the selected Item action
                combatVM.setSelectedInventoryAction(object);
                abilityRV.unSelectIfOneSelected();
                weaponRV.unSelectIfOneSelected();
            }
        });

    }

    // un-select all items in the inventory RVs
    private void unSelectAll() {
        abilityRV.unSelectIfOneSelected();
        itemRV.unSelectIfOneSelected();
        weaponRV.unSelectIfOneSelected();
    }

    // listener that adds the combat dialogue showing the attacker, target, and the action used on target
    private void setCombatDialogueListener() {
        Observable.OnPropertyChangedCallback callback = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                rv.addNewCombatDialogue(combatVM.getNewCombatActionDialogueValue());
            }
        };
        combatVM.getNewCombatActionDialogue().addOnPropertyChangedCallback(callback);
    }

    // deals with the character action UI in combat
    private void characterTurnState() {
        unSelectAll();
        isCharacterViewsEnabled(true);
        combatBinding.btnContinue.setVisibility(View.GONE);
        combatBinding.btnRun.setVisibility(View.VISIBLE);
    }

    // sets the character views interaction enabled or disabled
    private void isCharacterViewsEnabled(boolean isEnabled) {
        // enable/disable character selectable icon
        combatBinding.characterActionIcon.setEnabled(isEnabled);
        for (int i = 0; i < combatBinding.enemyActionIcons.getChildCount(); i++) {
            // add/disable each enemy selectable icon inside enemyActionIcons
            combatBinding.enemyActionIcons.getChildAt(i).setEnabled(isEnabled);
        }
    }

    // deals with the enemy action UI in combat
    private void enemyTurnState() {
        unSelectAll();
        isCharacterViewsEnabled(false);
        combatBinding.btnContinue.setVisibility(View.VISIBLE);
        combatBinding.btnRun.setVisibility(View.GONE);
    }

    // updates all the RV for the 3 combat order lists
    private void notifyCombatOrderNextTurn() {
        currListAdapter.nextTurn();
        nextListAdapter.nextTurn();
        lastListAdapter.nextTurn();
    }

    // set up initial RecyclerViewer adapters
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

    @Override
    public void endState() {
        // set up button to leave
        setLeaveButton(mainGameVM, combatBinding.layoutBtnOptions);

    }
}
