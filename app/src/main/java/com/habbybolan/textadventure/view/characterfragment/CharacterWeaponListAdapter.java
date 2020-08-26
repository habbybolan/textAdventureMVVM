package com.habbybolan.textadventure.view.characterfragment;

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

    private ArrayList<Weapon> weapons;
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
        private ArrayList<Weapon> weapons;

        public ViewHolder(View characterView, CharacterListClickListener listener, ArrayList<Weapon> weapons) {
            super(characterView);
            this.listener = listener;
            this.weapons = weapons;
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
            listener.onLongClicked(weapons.get(getAdapterPosition()));
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
        return new CharacterWeaponListAdapter.ViewHolder(characterView, listener, weapons);
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
    }

    @Override
    public int getItemCount() {
        return weapons != null ? weapons.size() : 0;
    }

    // adds new weapon to the list
    public void addNewWeapon(Weapon newWeapon) {
        weapons.add(newWeapon);
        notifyItemInserted(weapons.size()-1);
    }

    // remove a weapon from the list
    public void removeWeapon(Weapon weaponToRemove) {
        for (int i = 0; i < weapons.size(); i++) {
            if (weapons.get(i).equals(weaponToRemove)) {
                removeWeaponAtIndex(i);
                break;
            }
        }
    }
    // remove an weapon from the list
    private void removeWeaponAtIndex(int index) {
        weapons.remove(index);
        notifyItemRemoved(index);
    }
}
