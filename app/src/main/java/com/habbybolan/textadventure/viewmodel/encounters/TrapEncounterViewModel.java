package com.habbybolan.textadventure.viewmodel.encounters;

import android.view.View;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableField;

import com.habbybolan.textadventure.BR;
import com.habbybolan.textadventure.viewmodel.CharacterViewModel;
import com.habbybolan.textadventure.viewmodel.MainGameViewModel;

public class TrapEncounterViewModel extends BaseObservable {
    final String firstState = "dialogue";
    final String secondState = "beforeTrap";
    final String thirdState = "AfterTrap";
    // holds all the UI states of the Trap encounter
    String[] states = new String[]{firstState, secondState, thirdState};

    private CharacterViewModel characterVM;
    MainGameViewModel mainGameVM;
    public TrapEncounterViewModel(MainGameViewModel mainGameVM, CharacterViewModel characterVM) {
        this.characterVM = characterVM;
        this.mainGameVM = mainGameVM;
        stateIndex.set(0);
    }

    ObservableField<Integer> stateIndex = new ObservableField<>();
    @Bindable
    public ObservableField<Integer> getStateIndex() {
        return stateIndex;
    }
    public void incrementStateIndex() {
        Integer newState = stateIndex.get();
        stateIndex.set(newState);
        notifyPropertyChanged(BR.stateIndex);
    }

    // updates when a new bit of dialogue needs to be shown
    private ObservableField<String> newDialogue = new ObservableField<>();
    @Bindable
    public ObservableField<String> getNewDialogue() {
        return newDialogue;
    }
    public void setNewDialogue(String newDialogue) {
        this.newDialogue.set(newDialogue);
        notifyPropertyChanged(BR.newDialogue);
    }

    public void gotoNextEncounter(View v) {
        mainGameVM.gotoNextEncounter();
    }
}
