package com.habbybolan.textadventure.view.encounter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.FragmentMultiDungeonBinding;
import com.habbybolan.textadventure.view.dialogueAdapter.DialogueRecyclerView;
import com.habbybolan.textadventure.viewmodel.MainGameViewModel;
import com.habbybolan.textadventure.viewmodel.characterEntityViewModels.CharacterViewModel;
import com.habbybolan.textadventure.viewmodel.encounters.MultiDungeonViewModel;

import org.json.JSONException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MultiDungeonFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MultiDungeonFragment  extends EncounterDialogueFragment implements EncounterFragment {

    private FragmentMultiDungeonBinding multiDungeonBinding;
    private MainGameViewModel mainGameVM;
    private CharacterViewModel characterVM;
    private MultiDungeonViewModel multiDungeonVM;
    private DialogueRecyclerView rv;


    private static MultiDungeonFragment instance = null;

    private MultiDungeonFragment() {}

    public static MultiDungeonFragment newInstance() {
        instance = new MultiDungeonFragment();
        return instance;
    }

    /**
     * Check if an instance exists
     * @return  true if a ShopFragment instance exists
     */
    public static boolean isInstance() {
        return instance != null;
    }

    public static MultiDungeonFragment getInstance() {
        if (instance == null) throw new AssertionError("Create instance first");
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainGameVM = MainGameViewModel.getInstance();
        characterVM = CharacterViewModel.getInstance();
        try {
            multiDungeonVM = new MultiDungeonViewModel(getActivity());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        multiDungeonBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_multi_dungeon, container, false);
        try {
            rv = setUpEncounterBeginning(multiDungeonVM, this, multiDungeonBinding.rvDialogue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return multiDungeonBinding.getRoot();
    }



    @Override
    public void checkState(int state) {
        multiDungeonBinding.layoutBtnOptions.removeAllViews();
        switch(state) {
            // first state
            case MultiDungeonViewModel.firstState:
                dialogueState(multiDungeonVM, multiDungeonBinding.layoutBtnOptions);
                break;
            // last state
            case MultiDungeonViewModel.secondState:
                endState();
                break;
        }
    }

    @Override
    public void endState() {

    }

    /**
     * Calls clicker functionality in MultiDungeonViewModel for entering the dungeon.
     * @param v     Button view
     */
    public void clickEnter(View v) {
        multiDungeonVM.clickEnter();
    }

    /**
     * Calls clicker functionality in MultiDungeonViewModel for not entering the dungeon.
     * @param v     Button view
     */
    public void clickLeave(View v) {
        multiDungeonVM.clickLeave();
    }

    @Override
    public void onStop() {
        super.onStop();
        multiDungeonVM.saveGame(rv);
    }
}
