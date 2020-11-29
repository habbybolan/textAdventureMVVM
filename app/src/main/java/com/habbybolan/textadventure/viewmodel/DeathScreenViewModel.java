package com.habbybolan.textadventure.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.habbybolan.textadventure.viewmodel.characterEntityViewModels.CharacterViewModel;

import java.util.ArrayList;

public class DeathScreenViewModel extends AndroidViewModel {

    private CharacterViewModel characterViewModel;
    private ArrayList<String> text = new ArrayList<>();


    public DeathScreenViewModel(@NonNull Application application, CharacterViewModel characterViewModel) {
        super(application);
        this.characterViewModel = characterViewModel;
        populateText();
    }

    /**
     * Populate text ArrayList with character data.
     */
    public void populateText() {
        text.add("line 1");
        text.add("line 2");
        text.add("line 3");
        text.add("line 3");
        text.add("line 3");
        text.add("line 3");
        text.add("line 3");
        text.add("line 3");
        text.add("line 3");
        text.add("line 3");
        text.add("line 3");
        text.add("line 3");
        text.add("line 3");
    }

    public ArrayList<String> getText() {
        return text;
    }
}
