package com.habbybolan.textadventure.viewmodel;

import android.content.Context;
import android.view.View;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.habbybolan.textadventure.BR;
import com.habbybolan.textadventure.R;

import java.io.File;

public class MainActivityViewModel extends BaseObservable {

    private int newGameVisibility = View.VISIBLE;
    private int existingGameVisibility = View.VISIBLE;
    private int encounterState;

    public void initiate(Context context) {
        newGameVisibility = newGameVisibility(context);
        existingGameVisibility = existingGameVisibility(context);
    }
    // visibility for existing game buttons
    public int existingGameVisibility(Context context) {
        if (hasCharacter(context)) return View.VISIBLE;
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
    public int newGameVisibility(Context context) {
        if (hasCharacter(context)) return View.GONE;
        return View.VISIBLE;
    }

    // returns true if a character exists
    private boolean hasCharacter(Context context) {
        File file = new File(context.getFilesDir(), context.getResources().getString(R.string.fileCharacter));
        // if character data exists, bring up the continue fragment
        return fileExists(file);
    }

    // return true if the file exists
    private boolean fileExists(File file) {
        return (file.exists());
    }

    // deletes a character and the stored previous encounters from local storage
    public void deleteCharacter(Context context) {
        File fileCharacter = new File(context.getFilesDir(), context.getResources().getString(R.string.fileCharacter));
        if (fileExists(fileCharacter)) {
            boolean deleted = fileCharacter.delete();
        }
        File filePrevEncounters = new File(context.getFilesDir(), context.getResources().getString(R.string.filePrevEncounters));
        if (fileExists(filePrevEncounters)) {
            boolean deleted = filePrevEncounters.delete();
        }
        setNewGameVisibility(View.VISIBLE);
        setExistingGameVisibility(View.GONE);
    }

}
