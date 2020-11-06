package com.habbybolan.textadventure.view;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.ActivityMainGameBinding;
import com.habbybolan.textadventure.view.characterfragment.CharacterFragment;
import com.habbybolan.textadventure.view.encounter.BreakFragment;
import com.habbybolan.textadventure.view.encounter.CheckFragment;
import com.habbybolan.textadventure.view.encounter.ChoiceBenefitFragment;
import com.habbybolan.textadventure.view.encounter.ChoiceFragment;
import com.habbybolan.textadventure.view.encounter.CombatDungeonFragment;
import com.habbybolan.textadventure.view.encounter.CombatFragment;
import com.habbybolan.textadventure.view.encounter.MultiDungeonFragment;
import com.habbybolan.textadventure.view.encounter.RandomBenefitFragment;
import com.habbybolan.textadventure.view.encounter.ShopFragment;
import com.habbybolan.textadventure.view.encounter.TrapFragment;
import com.habbybolan.textadventure.viewmodel.MainGameViewModel;
import com.habbybolan.textadventure.viewmodel.characterEntityViewModels.CharacterViewModel;

import org.json.JSONException;

/*
The activity that holds the majority of the game, including the CharacterFragment and all included encounter fragments
 */
public class MainGameActivity extends AppCompatActivity {

    ActivityMainGameBinding mainGameBinding;
    MainGameViewModel mainGameViewModel;
    CharacterViewModel characterViewModel;

    String encounterType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        // remove toolbar
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) getSupportActionBar().hide();
        // if viewModels not yet initiated, so initiate them
        if (!CharacterViewModel.isInitiated()) {
            characterViewModel = CharacterViewModel.init(getApplicationContext());
            mainGameViewModel = MainGameViewModel.init(getApplicationContext(), characterViewModel);
        } else {
            characterViewModel = CharacterViewModel.getInstance();
            mainGameViewModel = MainGameViewModel.getInstance();
        }
        characterDeathListener();
        mainGameBinding = DataBindingUtil.setContentView(this, R.layout.activity_main_game);
        mainGameBinding.setMainGameViewModel(mainGameViewModel);
        mainGameBinding.setCharacterViewModel(characterViewModel);

        CharacterFragment characterFragment = new CharacterFragment(characterViewModel);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container_character, characterFragment)
                .show(characterFragment)
                .commit();

        observeEncounterChange();
        // opens a game encounter, new one is none saved, otherwise open saved one
        try {
            mainGameViewModel.openGameEncounter();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // listener for when the character reaches 0 health
    private void characterDeathListener() {
        // todo: death by observing health changes and checking if character is dead (<= 0 health)
    }


    // observed whenever MainGameViewModel changes the encounter, changing the fragment to the appropriate one
    private void observeEncounterChange() {
        Observable.OnPropertyChangedCallback callback = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                alterEncounterFragment();
            }
        };
        mainGameViewModel.getEncounterObservable().addOnPropertyChangedCallback(callback);
    }

    /**
     * Replaces the fragment in container fragment_container_game to the new encounter fragment to enter.
     * Creates the new fragment to enter, sets up its animations, and enters the fragment.
     */
    public void alterEncounterFragment() {
        encounterType = mainGameViewModel.getEncounter().getType();
        switch (encounterType) {
            case MainGameViewModel.COMBAT_DUNGEON_TYPE:
                CombatDungeonFragment combatDungeonFragment = CombatDungeonFragment.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_top)
                        .replace(R.id.fragment_container_game, combatDungeonFragment)
                        .commit();
                break;
            case MainGameViewModel.MULTI_DUNGEON_TYPE:
                MultiDungeonFragment multiDungeonFragment = MultiDungeonFragment.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_top)
                        .replace(R.id.fragment_container_game, multiDungeonFragment)
                        .commit();
                break;
            case MainGameViewModel.CHOICE_TYPE:
                ChoiceFragment choiceFragment = ChoiceFragment.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_top)
                        .replace(R.id.fragment_container_game, choiceFragment)
                        .commit();
                break;
            case MainGameViewModel.COMBAT_TYPE:
                CombatFragment combatFragment = CombatFragment.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_top)
                        .replace(R.id.fragment_container_game, combatFragment)
                        .commit();
                break;
            case MainGameViewModel.TRAP_TYPE:
                TrapFragment trapFragment = TrapFragment.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_top)
                        .replace(R.id.fragment_container_game, trapFragment)
                        .commit();
                break;
            case MainGameViewModel.SHOP_TYPE:
                ShopFragment shopFragment = ShopFragment.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_top)
                        .replace(R.id.fragment_container_game, shopFragment)
                        .commit();
                break;
            case MainGameViewModel.CHOICE_BENEFIT_TYPE:
                ChoiceBenefitFragment choiceBenefitFragment = ChoiceBenefitFragment.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_top)
                        .replace(R.id.fragment_container_game, choiceBenefitFragment)
                        .commitAllowingStateLoss();
                break;
            case MainGameViewModel.RANDOM_BENEFIT_TYPE:
                RandomBenefitFragment randomBenefitFragment = RandomBenefitFragment.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_top)
                        .replace(R.id.fragment_container_game, randomBenefitFragment)
                        .commit();
                break;
            case MainGameViewModel.CHECK_TYPE:
                CheckFragment checkFragment = CheckFragment.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_top)
                        .replace(R.id.fragment_container_game, checkFragment)
                        .commit();
                break;
            case MainGameViewModel.BREAK_TYPE:
                BreakFragment breakFragment = BreakFragment.newInstance();
                getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_bottom, R.anim.exit_to_top)
                        .replace(R.id.fragment_container_game, breakFragment)
                        .commit();
                break;
            default: //  shouldn't reach here
                throw new IllegalArgumentException("No encounter of " + encounterType);
        }
    }

    // disable the back button
    @Override
    public void onBackPressed() {
    }

}
