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
import com.habbybolan.textadventure.databinding.CharacterAbilityListDetailsBinding;
import com.habbybolan.textadventure.model.inventory.Ability;

import java.util.ArrayList;

public class CharacterAbilityListAdapter extends RecyclerView.Adapter<CharacterAbilityListAdapter.ViewHolder> {

    private ArrayList<Ability> abilities;
    private CharacterListClickListener clickListener;

    public CharacterAbilityListAdapter(ArrayList<Ability> abilities, CharacterListClickListener clickListener) {
        this.abilities = abilities;
        this.clickListener = clickListener;
    }

    // inflate the layout used for the recyclerView
    @NonNull
    @Override
    public CharacterAbilityListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CharacterAbilityListDetailsBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.character_ability_list_details, parent, false);
        return new ViewHolder(binding, clickListener);
    }

    // set details of the views
    @Override
    public void onBindViewHolder(@NonNull CharacterAbilityListAdapter.ViewHolder holder, final int position) {
        Ability ability = abilities.get(position);
        holder.bind(ability);
    }

    // create the views inside the recyclerViewer
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public CharacterAbilityListDetailsBinding binding;
        CharacterListClickListener listener;

        ViewHolder(CharacterAbilityListDetailsBinding binding, CharacterListClickListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            this.listener = listener;

            binding.btnDropAbility.setOnClickListener(this);
            binding.btnDropAbility.setOnLongClickListener(this);
        }

        public void bind(Object obj) {
            binding.setVariable(BR.ability, obj);
            binding.executePendingBindings();
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

    // adds new ability to the list
    public void updateNewAbilityIndex(int index) {
        notifyItemInserted(index);
    }

    public void updateRemovedAbilityIndex(int index) {
        notifyItemRemoved(index);
    }
}
