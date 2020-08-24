package com.habbybolan.textadventure.view.characterfragment;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.model.inventory.Ability;

import java.util.ArrayList;

public class CharacterAbilityListAdapter extends RecyclerView.Adapter<CharacterAbilityListAdapter.ViewHolder> {

    private ArrayList<Ability> abilities = new ArrayList<>();
    private int selectedPosition = 0;
    private CharacterListClickListener clickListener;

    public CharacterAbilityListAdapter(ArrayList<Ability> abilities, CharacterListClickListener clickListener) {
        this.abilities = abilities;
        this.clickListener = clickListener;
    }

    // inflate the layout used for the recyclerView
    @NonNull
    @Override
    public CharacterAbilityListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Inflate the custom layout
        View characterView = LayoutInflater.from(parent.getContext()).inflate(R.layout.character_ability_list_details, parent, false);
        // Return a new holder instance
        return new CharacterAbilityListAdapter.ViewHolder(characterView, clickListener);
    }

    // set details of the views
    @Override
    public void onBindViewHolder(@NonNull CharacterAbilityListAdapter.ViewHolder holder, final int position) {
        String abilityLabelText = "Item " + (position + 1);
        final TextView abilityLabel = holder.abilityLabel;
        abilityLabel.setText(abilityLabelText);

        String abilityDetailText = abilities.get(position).getName();
        final TextView abilityDetail = holder.abilityDetails;
        abilityDetail.setText(abilityDetailText);

        if (position != selectedPosition) abilityDetail.setTextColor(Color.BLACK);
        else abilityDetail.setTextColor(Color.RED);
        abilityDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPosition >= 0) notifyItemChanged(selectedPosition);
                selectedPosition = position;
                abilityDetail.setTextColor(Color.RED);
            }
        });
    }



    // create the views inside the recyclerViewer
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        // each data item is just a string in this case
        private TextView abilityLabel;
        private TextView abilityDetails;
        private Button btnDrop;
        private CharacterListClickListener listener;

        public ViewHolder(View characterView, CharacterListClickListener listener) {
            super(characterView);
            this.listener = listener;
            abilityLabel = characterView.findViewById(R.id.ability_label);
            abilityDetails = characterView.findViewById(R.id.ability_detail);
            btnDrop = characterView.findViewById(R.id.btn_drop_ability);

            btnDrop.setOnClickListener(this);
            btnDrop.setOnLongClickListener(this);
        }

        // only displays a message to hold drop button if trying to remove
        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(), "Hold to drop ability scroll", Toast.LENGTH_SHORT).show();
        }
        // deleted the inventory element if long click
        @Override
        public boolean onLongClick(View v) {
            listener.onLongClicked(getAdapterPosition());
            return true;
        }
    }



    @Override
    public int getItemCount() {
        return abilities != null ? abilities.size() : 0;
    }
}
