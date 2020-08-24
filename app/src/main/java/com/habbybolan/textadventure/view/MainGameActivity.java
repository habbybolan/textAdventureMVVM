package com.habbybolan.textadventure.view;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.ActivityMainGameBinding;
import com.habbybolan.textadventure.view.characterfragment.CharacterFragment;
import com.habbybolan.textadventure.view.encounters.trapencounter.TrapFragment;
import com.habbybolan.textadventure.viewmodel.CharacterViewModel;
import com.habbybolan.textadventure.viewmodel.MainGameViewModel;

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
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        mainGameViewModel = new MainGameViewModel(getApplicationContext());
        characterViewModel = new CharacterViewModel(getApplicationContext());
        mainGameBinding = DataBindingUtil.setContentView(this, R.layout.activity_main_game);
        mainGameBinding.setLifecycleOwner(this);
        mainGameBinding.setMainGameViewModel(mainGameViewModel);
        mainGameBinding.setCharacterViewModel(characterViewModel);

        CharacterFragment characterFragment = new CharacterFragment(characterViewModel);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container_character, characterFragment)
                .commit();

        /*gameFragment = new GameFragment(characterViewModel, mainGameViewModel);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container_game, gameFragment)
                .commit()
         */
        observeEncounterChange();
        mainGameViewModel.getEncounterType().notifyChange();
    }

    // observed whenever MainGameViewModel changes the encounter, changing the fragment to the appropriate one
    private void observeEncounterChange() {
        Observable.OnPropertyChangedCallback callback = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                alterEncounterFragment();
            }
        };
        mainGameViewModel.getEncounterType().addOnPropertyChangedCallback(callback);
    }

    // changes the fragment for fragment_container_game to appropriate fragment for the encounter type
    public void alterEncounterFragment() {
        encounterType = mainGameViewModel.getEncounterType().get();
        switch (encounterType) {
            /*case DUNGEON_TYPE:
                isDungeon = true;
                DungeonEncounter dungeonEncounter = new DungeonEncounter(getContext(), character, damage);
                dungeonEncounter.setInitialChoice(encounter);
                break; !g
            case MULTI_LEVEL_TYPE:
                isMultiLevel = true;
                MultiLevelEncounter multiLevelEncounter = new MultiLevelEncounter(getContext(), character, damage, view, this, model);
                multiLevelEncounter.setInitialMultiLevel(encounter);
                break;
            case CHOICE_TYPE:
                ChoiceEncounter choiceEncounter = new ChoiceEncounter(getContext(), character, damage, view, this, model);
                choiceEncounter.setInitialChoice(encounter);
                break;
            case COMBAT_TYPE:
                CombatEncounter combatObject = new CombatEncounter(getContext(), character, damage, view, this);
                combatObject.setInitialCombat(encounter, view);
                break;*/
            case MainGameViewModel.TRAP_TYPE:
                TrapFragment trapFragment = new TrapFragment(mainGameViewModel, characterViewModel);
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragment_container_game, trapFragment)
                        .commit();
                break;
            /*case SHOP_TYPE:
                enterLeaveShop(encounter);
                break;
            case BENEFIT_TYPE:
                BenefitEncounter benefitObject = new BenefitEncounter(getContext(), character, model, view, this);
                benefitObject.setInitialBenefit(encounter.getString("dialogue"));
                break;
            case SMALL_BENEFIT_TYPE:
                SmallBenefitEncounter smallBenefitEncounter = new SmallBenefitEncounter(getContext(), character, model, view, this);
                smallBenefitEncounter.setInitialSmallBenefit(encounter.getString("dialogue"));
                break;
            case QUEST_TYPE:
                QuestEncounter questEncounter = new QuestEncounter(getContext(), character, damage, view, this, model);
                questEncounter.setInitialQuest(encounter.getJSONObject("encounter"));
                break;*/
            default: //  shouldn't reach here
                throw new IllegalArgumentException();
        }
    }

}
