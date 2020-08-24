package com.habbybolan.textadventure.view.encounters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.habbybolan.textadventure.viewmodel.CharacterViewModel;
import com.habbybolan.textadventure.viewmodel.MainGameViewModel;

public class GameFragment extends Fragment {
    private CharacterViewModel characterViewModel;
    private MainGameViewModel mainGameViewModel;

    public GameFragment(CharacterViewModel characterViewModel, MainGameViewModel mainGameViewModel) {
        this.characterViewModel = characterViewModel;
        this.mainGameViewModel = mainGameViewModel;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // todo: swap back to this and test
        return inflater.inflate(mainGameViewModel.getEncounterLayout(), container, false);

        /*View view = inflater.inflate(R.layout.trap_layout, container, false);
        Button button = new Button(getContext());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainGameViewModel.gotoNextEncounter();
            }
        });
        LinearLayout linearLayout = view.findViewById(R.id.trap_container);
        linearLayout.addView(button);*/
    }
}
