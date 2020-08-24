package com.habbybolan.textadventure.view.characterchoice;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.viewmodel.CharacterChoiceViewModel;

/*
RecyclerView list that displays a specific class' data upon click
 */
public class CharacterListAdapter extends RecyclerView.Adapter<CharacterListAdapter.ViewHolder> {

    private int selectedPosition = 0;
    private String[] characters;
    private CharacterChoiceViewModel model;

    public CharacterListAdapter(CharacterChoiceViewModel model) {
        // initialize characters from CharacterSelectViewModel
        characters = new String[4];
        characters[0] = CharacterChoiceViewModel.wizard;
        characters[1] = CharacterChoiceViewModel.paladin;
        characters[2] = CharacterChoiceViewModel.archer;
        characters[3] = CharacterChoiceViewModel.warrior;
        this.model = model;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View characterView = inflater.inflate(R.layout.character_list_details, parent, false);
        // Return a new holder instance
        return new ViewHolder(characterView);
    }

    // Provide a direct reference to each of the views within a data item
    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;

        ViewHolder(View characterView) {
            super(characterView);
            nameTextView = characterView.findViewById(R.id.character_name);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        String character = characters[position];

        final TextView textView = holder.nameTextView;
        textView.setText(character);
        // select/un-select list element depending on the selectedPosition
        if (position != selectedPosition) textView.setTextColor(Color.BLACK);
        else textView.setTextColor(Color.RED);
        // sets the new selected class with red text
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if any items are selected first
                if (selectedPosition >= 0) notifyItemChanged(selectedPosition);
                selectedPosition = position;
                textView.setTextColor(Color.RED);
                model.selectCharacter(textView.getText().toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return characters.length;
    }
}
