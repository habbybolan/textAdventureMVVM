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
import com.habbybolan.textadventure.model.inventory.weapon.Weapon;

import java.util.ArrayList;

public class CharacterWeaponListAdapter extends RecyclerView.Adapter<CharacterWeaponListAdapter.ViewHolder> {

    private ArrayList<Weapon> weapons = new ArrayList<>();
    private int selectedPosition = 0;
    CharacterListClickListener listener;

    public CharacterWeaponListAdapter(ArrayList<Weapon> weapons, CharacterListClickListener listener) {
        this.weapons = weapons;
        this.listener = listener;
    }

    // create the views inside the recyclerViewer
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        // each data item is just a string in this case
        private TextView weaponLabel;
        private TextView weaponDetails;
        private Button btnDrop;
        private CharacterListClickListener listener;

        public ViewHolder(View characterView, CharacterListClickListener listener) {
            super(characterView);
            this.listener = listener;
            weaponLabel = characterView.findViewById(R.id.weapon_label);
            weaponDetails = characterView.findViewById(R.id.weapon_detail);
            btnDrop = characterView.findViewById(R.id.btn_drop_weapon);
            btnDrop.setOnClickListener(this);
            btnDrop.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(), "Hold to drop weapon", Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onLongClick(View v) {
            listener.onLongClicked(getAdapterPosition());
            return true;
        }
    }


    // inflate the layout used for the recyclerView
    @NonNull
    @Override
    public CharacterWeaponListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Inflate the custom layout
        View characterView = LayoutInflater.from(parent.getContext()).inflate(R.layout.character_weapon_list_details, parent, false);
        // Return a new holder instance
        return new CharacterWeaponListAdapter.ViewHolder(characterView, listener);
    }

    // set details of the views
    @Override
    public void onBindViewHolder(@NonNull CharacterWeaponListAdapter.ViewHolder holder, final int position) {
        String weaponLabelText = "Weapon " + (position+1);
        final TextView weaponLabel = holder.weaponLabel;
        weaponLabel.setText(weaponLabelText);

        String weaponDetailText = weapons.get(position).getName();
        final TextView weaponDetail = holder.weaponDetails;
        weaponDetail.setText(weaponDetailText);

        if (position != selectedPosition) weaponDetail.setTextColor(Color.BLACK);
        else weaponDetail.setTextColor(Color.RED);
        weaponDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if any items are selected first
                if (selectedPosition >= 0) notifyItemChanged(selectedPosition);
                selectedPosition = position;
                weaponDetail.setTextColor(Color.RED);
            }
        });
    }

    @Override
    public int getItemCount() {
        return weapons.size();
    }
}
