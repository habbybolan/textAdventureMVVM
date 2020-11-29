package com.habbybolan.textadventure.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.ActivityMainGameBinding;
import com.habbybolan.textadventure.view.characterfragment.CharacterFragment;
import com.habbybolan.textadventure.view.deathscreen.DeathScreenActivity;
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
        characterViewModel = CharacterViewModel.init(getApplication());
        mainGameViewModel = MainGameViewModel.init(getApplication(), characterViewModel);
        setDeathListener();
        mainGameBinding = DataBindingUtil.setContentView(this, R.layout.activity_main_game);
        mainGameBinding.setMainGameViewModel(mainGameViewModel);
        mainGameBinding.setCharacterViewModel(characterViewModel);
        // game fragment shows first, select its category button
        mainGameBinding.btnGame.setSelected(true);

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

    /**
     * Listener for when the player character dies.
     */
    private void setDeathListener() {
        characterViewModel.getIsPlayerDeadObserver().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                killPlayer();
            }
        });
    }

    /**
     * Called when the player character dies, notifying listener. (health reaches 0).
     */
    public void killPlayer() {
        Intent intent = new Intent(this, DeathScreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        //overridePendingTransition(R.anim.enter_from_right, R.anim.fade_out);
        // todo: reroute main thread control flow to DeathScreenActivity
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
                        .commit();
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

    /**
     * Clicker for btn_character to swap to character fragment
     * @param v Button view
     */
    public void characterButtonClick(View v) {
        if (mainGameViewModel.isCharacterFragmentGone()) {
            mainGameViewModel.gotoCharacterFragment();
            mainGameBinding.btnCharacter.setSelected(true);
            mainGameBinding.btnGame.setSelected(false);
        }
    }

    /**
     * Clicker for btn_game to swap to game fragment
     * @param v button view
     */
    public void gameButtonClick(View v) {
        if (mainGameViewModel.isGameFragmentGone()) {
            mainGameViewModel.gotoGameFragment();
            mainGameBinding.btnCharacter.setSelected(false);
            mainGameBinding.btnGame.setSelected(true);
        }
    }

    // disable the back button
    @Override
    public void onBackPressed() {}

}
