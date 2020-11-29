package com.habbybolan.textadventure.view.deathscreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.ActivityDeathScreenBinding;
import com.habbybolan.textadventure.repository.LocallySavedFiles;
import com.habbybolan.textadventure.view.MainActivity;
import com.habbybolan.textadventure.viewmodel.DeathScreenViewModel;
import com.habbybolan.textadventure.viewmodel.characterEntityViewModels.CharacterViewModel;

public class DeathScreenActivity extends AppCompatActivity {
    CharacterViewModel characterVM = CharacterViewModel.getInstance();
    ActivityDeathScreenBinding binding;

    DeathScreenViewModel deathScreenViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove toolbar
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) getSupportActionBar().hide();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_death_screen);
        deathScreenViewModel = new DeathScreenViewModel(getApplication(), characterVM);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setCharacterInfoRecyclerView();
    }

    /**
     * Sets up and populates the recyclerView with character info/stats.
     */
    private void setCharacterInfoRecyclerView() {
        RecyclerView recyclerView = binding.rvCharacterInfo;
        CharacterInfoAdapter adapter = new CharacterInfoAdapter(deathScreenViewModel.getText());
        recyclerView.setAdapter(adapter);
        deleteSave();
    }

    /**
     * Delete the character and saved encounter data from local storage.
     */
    private void deleteSave() {
        LocallySavedFiles locallySavedFiles = new LocallySavedFiles(getApplicationContext());
        locallySavedFiles.resetGameFiles();
    }

    /**
     * On new game button click, goto MainActivity to start a new game.
     * @param v Button view
     */
    public void newGameButtonClick(View v) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        // do nothing
    }
}
