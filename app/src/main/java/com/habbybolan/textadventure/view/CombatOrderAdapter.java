package com.habbybolan.textadventure.view;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.CombatOrderDetailsBinding;
import com.habbybolan.textadventure.model.characterentity.CharacterEntity;

import java.util.ArrayList;

public class CombatOrderAdapter extends RecyclerView.Adapter<CombatOrderAdapter.ViewHolder> {
    // list holding the character and all enemies in the combat encounter
    private ArrayList<CharacterEntity> allEntities;

    public CombatOrderAdapter(ArrayList<CharacterEntity> allEntities) {
        this.allEntities = allEntities;
    }

    @NonNull
    @Override
    public CombatOrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        CombatOrderDetailsBinding bindingDialogue = DataBindingUtil.inflate(layoutInflater, R.layout.combat_order_details, parent, false);
        return new CombatOrderAdapter.ViewHolder(bindingDialogue);
    }

    @Override
    public void onBindViewHolder(@NonNull CombatOrderAdapter.ViewHolder holder, int position) {
        CharacterEntity entity = allEntities.get(position);
        if (entity.getIsAlive()) {
            // show alive icon
            holder.bind(entity.getDrawableResID());
        } else {
            // show dead icon
            holder.bind(entity.getDrawableDeadResID());
        }
    }

    @Override
    public int getItemCount() {
        return allEntities.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        CombatOrderDetailsBinding binding;

        ViewHolder(CombatOrderDetailsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(int iconResource) {
            binding.setIconResID(iconResource);
            binding.executePendingBindings();
        }
    }

    // update the list to express entity is dead
    public void updateEntityDead(int index) {
        notifyItemChanged(index);
    }

    /**
     *  Update changed to CombatOrderCurr array.
     */
    public void nextTurnCurr() {
        notifyDataSetChanged();
    }

    /**
     * Update the images of any icon that may have changed.
     */
    public void nextTurnNext() {
        notifyDataSetChanged();
    }

    /**
     * Update changed to CombatOrderLast array.
     */
    public void nextTurnLast() {
        notifyDataSetChanged();
    }

    // list is sorted and more than one item can change position
    public void listSorted() {
        notifyDataSetChanged();
    }
}
