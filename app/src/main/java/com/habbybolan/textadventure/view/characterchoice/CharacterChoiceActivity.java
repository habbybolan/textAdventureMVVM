package com.habbybolan.textadventure.view.characterchoice;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.ActivityCharacterChoiceBinding;
import com.habbybolan.textadventure.repository.SaveDataLocally;
import com.habbybolan.textadventure.view.MainGameActivity;
import com.habbybolan.textadventure.viewmodel.CharacterChoiceViewModel;

public class CharacterChoiceActivity extends AppCompatActivity {
    ActivityCharacterChoiceBinding dataBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) getSupportActionBar().hide();
        // todo: ability to read context from CharacterChoiceViewModel
        //CharacterChoiceResourceProvider resourceProvider = new CharacterChoiceResourceProvider(getApplicationContext());
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_character_choice);
        dataBinding.setCharacterChoiceViewModel(new CharacterChoiceViewModel(getApplicationContext()));
        setUpRecyclerViewer();
    }


    private void setUpRecyclerViewer() {
        RecyclerView recyclerView = dataBinding.rvCharacterList;
        CharacterListAdapter adapter = new CharacterListAdapter(dataBinding.getCharacterChoiceViewModel());
        recyclerView.setAdapter(adapter);
        // set the layout manager to position the items
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void selectCharacter(View v) {
        String className = dataBinding.getCharacterChoiceViewModel().getClassName();

        SaveDataLocally save = new SaveDataLocally(getApplicationContext());
        save.saveNewCharacterLocally(className);

        Intent intent = new Intent(this, MainGameActivity.class);
        startActivity(intent);
    }

    // disable the back button
    @Override
    public void onBackPressed() {}
}
