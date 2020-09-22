package com.habbybolan.textadventure.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.ActivityMainBinding;
import com.habbybolan.textadventure.view.characterchoice.CharacterChoiceActivity;
import com.habbybolan.textadventure.viewmodel.MainActivityViewModel;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove toolbar
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) getSupportActionBar().hide();
        ActivityMainBinding dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        dataBinding.setViewModel(new MainActivityViewModel());

        dataBinding.getViewModel().initiate(getApplicationContext());
    }

    // starts a new game, sending user to choose their character
    public void newGame(View v) {
        Intent intent = new Intent(getApplicationContext(), CharacterChoiceActivity.class);
        startActivity(intent);
    }

    // button to continue the saved game
    public void continueGame(View v) {
        Intent intent = new Intent(getApplicationContext(), MainGameActivity.class);
        startActivity(intent);
    }
}
