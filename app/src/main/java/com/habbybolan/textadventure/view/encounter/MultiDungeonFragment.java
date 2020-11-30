package com.habbybolan.textadventure.view.encounter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.FragmentMultiDungeonBinding;
import com.habbybolan.textadventure.view.CustomPopupWindow;
import com.habbybolan.textadventure.view.dialogueAdapter.DialogueRecyclerView;
import com.habbybolan.textadventure.viewmodel.encounters.MultiDungeonViewModel;

import org.json.JSONException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MultiDungeonFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MultiDungeonFragment  extends EncounterDialogueFragment implements EncounterFragment {

    private FragmentMultiDungeonBinding multiDungeonBinding;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        multiDungeonBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_multi_dungeon, container, false);
        return multiDungeonBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        multiDungeonVM = new ViewModelProvider(requireActivity()).get(MultiDungeonViewModel.class);
        try {
            rv = setUpEncounterBeginning(multiDungeonVM, this, multiDungeonBinding.rvDialogue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void checkState(int state) {
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
        removeDialogueContinueButton(multiDungeonBinding.layoutBtnOptions);
        // popup view, only show one at a time
        final PopupWindow popupWindow = new PopupWindow();
        multiDungeonBinding.btnInfo.setVisibility(View.VISIBLE);
        multiDungeonBinding.btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing()) {
                    // remove the popup if showing
                    popupWindow.dismiss();
                } else {
                    // otherwise, set up the popup dungeon
                    CustomPopupWindow.setDungeonInfo("A dungeon with a multitude of different encounters, more difficult than usual." +
                            " Reach the end and there may be loot awaiting you. Although, you may leave at any point.",
                            getContext(), popupWindow, multiDungeonBinding.scrollviewContainer);
                }
            }
        });

        // button to enter the multi dungeon
        multiDungeonBinding.btnEnter.setVisibility(View.VISIBLE);
        multiDungeonBinding.btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // remove the popup if showing
                if (popupWindow.isShowing()) popupWindow.dismiss();
                // clicker functionality to enter the multi dungeon
                multiDungeonVM.clickEnter();
            }
        });

        // button to leave the multi dungeon
        multiDungeonBinding.btnLeave.setVisibility(View.VISIBLE);
        multiDungeonBinding.btnLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // remove the popup if showing
                if (popupWindow.isShowing()) popupWindow.dismiss();
                // clicker functionality to not enter multi dungeon
                multiDungeonVM.clickLeave();
            }
        });

    }

    @Override
    public void onStop() {
        super.onStop();
        saveGame(rv, multiDungeonVM);
    }
}
