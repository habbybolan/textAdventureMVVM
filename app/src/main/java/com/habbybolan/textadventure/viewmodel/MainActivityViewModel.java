package com.habbybolan.textadventure.viewmodel;

import android.content.Context;
import android.view.View;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.habbybolan.textadventure.BR;
import com.habbybolan.textadventure.repository.LocallySavedFiles;

public class MainActivityViewModel extends BaseObservable {

    private int newGameVisibility = View.VISIBLE;
    private int existingGameVisibility = View.VISIBLE;
    private LocallySavedFiles locallySavedFiles;

    public MainActivityViewModel(Context context) {
        locallySavedFiles = new LocallySavedFiles(context);
        newGameVisibility = newGameVisibility();
        existingGameVisibility = existingGameVisibility();
    }

    // visibility for existing game buttons
    public int existingGameVisibility() {
        if (locallySavedFiles.hasCharacter()) return View.VISIBLE;
        return View.GONE;
    }

    @Bindable
    public int getNewGameVisibility() {
        return newGameVisibility;
    }
    public void setNewGameVisibility(int newGameVisibility) {
        this.newGameVisibility = newGameVisibility;
        notifyPropertyChanged(BR.newGameVisibility);
    }

    @Bindable
    public int getExistingGameVisibility() {
        return existingGameVisibility;
    }
    public void setExistingGameVisibility(int existingGameVisibility) {
        this.existingGameVisibility = existingGameVisibility;
        notifyPropertyChanged(BR.existingGameVisibility);
    }

    // visibility for new game button
    public int newGameVisibility() {
        if (locallySavedFiles.hasCharacter()) return View.GONE;
        return View.VISIBLE;
    }

    // deletes a character and the stored saved encounters from local storage
    public void deleteCharacter() {
        locallySavedFiles.resetGameFiles();
        setNewGameVisibility(View.VISIBLE);
        setExistingGameVisibility(View.GONE);
    }
}
