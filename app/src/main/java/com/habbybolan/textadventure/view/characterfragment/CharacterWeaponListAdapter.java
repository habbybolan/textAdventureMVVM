package com.habbybolan.textadventure.view.characterfragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.habbybolan.textadventure.BR;
import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.CharacterWeaponListDetailsBinding;
import com.habbybolan.textadventure.model.inventory.weapon.Weapon;

import java.util.ArrayList;

public class CharacterWeaponListAdapter extends RecyclerView.Adapter<CharacterWeaponListAdapter.ViewHolder> {

    private ArrayList<Weapon> weapons;
    private CharacterListClickListener clickListener;

    public CharacterWeaponListAdapter(ArrayList<Weapon> weapons, CharacterListClickListener clickListener) {
        this.weapons = weapons;
        this.clickListener = clickListener;
    }

    // inflate the layout used for the recyclerView
    @NonNull
    @Override
    public CharacterWeaponListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CharacterWeaponListDetailsBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.character_weapon_list_details, parent, false);
        return new CharacterWeaponListAdapter.ViewHolder(binding, clickListener);
    }

    // set details of the views
    @Override
    public void onBindViewHolder(@NonNull CharacterWeaponListAdapter.ViewHolder holder, final int position) {
        Weapon weapon = weapons.get(position);
        holder.bind(weapon);
    }

    // create the views inside the recyclerViewer
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public CharacterWeaponListDetailsBinding binding;
        CharacterListClickListener listener;

        ViewHolder(CharacterWeaponListDetailsBinding binding, CharacterListClickListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            this.listener = listener;

            binding.btnDropWeapon.setOnClickListener(this);
            binding.btnDropWeapon.setOnLongClickListener(this);
        }

        public void bind(Object obj) {
            binding.setVariable(BR.weapon, obj);
            binding.executePendingBindings();
        }

        // only displays a message to hold drop button if trying to remove
        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(), "Hold to drop weapon", Toast.LENGTH_SHORT).show();
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
        return weapons != null ? weapons.size() : 0;
    }

    // adds new ability to the list
    public void updateNewWeaponIndex(int index) {
        notifyItemInserted(index);
    }

    public void updateRemovedWeaponIndex(int index) {
        notifyItemRemoved(index);
    }
}
