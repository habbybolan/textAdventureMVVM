package com.habbybolan.textadventure.view.InventoryListAdapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.InventorySnippetBinding;
import com.habbybolan.textadventure.model.inventory.Action;

import java.util.ArrayList;
/*
adapter for Inventory objects inside list of combat encounter
 */
public class AbilityItemListAdapter extends RecyclerView.Adapter<AbilityItemListAdapter.ViewHolder> {

    private ArrayList<? extends Action> inventoryObjects;
    private ActionClickListener actionClickListener;
    private boolean isEnabled = true;

    // index of selected item, -1 if nothing selected
    private int selectedIndex = -1;

    AbilityItemListAdapter(ArrayList<? extends Action> inventoryObjects, ActionClickListener actionClickListener) {
        this.inventoryObjects = inventoryObjects;
        this.actionClickListener = actionClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        InventorySnippetBinding bindingDialogue = DataBindingUtil.inflate(layoutInflater, R.layout.inventory_snippet, parent, false);
        return new AbilityItemListAdapter.ViewHolder(bindingDialogue, actionClickListener, this);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        String name = inventoryObjects.get(position).getName();
        int iconResource = inventoryObjects.get(position).getPictureResource();
        holder.bind(name, iconResource);
        // disable/enable name click and inventory info button
        holder.binding.txtName.setEnabled(isEnabled);
        holder.binding.inventoryInfo.setEnabled(isEnabled);
        // if the position is the selectedIndex, then color the text red, otherwise black
        if (position == selectedIndex) holder.binding.txtName.setTextColor(Color.RED);
        else holder.binding.txtName.setTextColor(Color.BLACK);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        InventorySnippetBinding binding;
        ActionClickListener actionClickListener;

        ViewHolder(final InventorySnippetBinding binding, final ActionClickListener actionClickListener, final AbilityItemListAdapter adapter) {
            super(binding.getRoot());
            this.binding = binding;
            this.actionClickListener = actionClickListener;

            binding.txtName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.notifyItemChanged(adapter.selectedIndex);
                    adapter.selectedIndex = getAdapterPosition();
                    adapter.notifyItemChanged(adapter.selectedIndex);
                    actionClickListener.onClicked(adapter.inventoryObjects.get(getAdapterPosition()));
                }
            });

            binding.inventoryInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    actionClickListener.onInfoClick(adapter.inventoryObjects.get(getAdapterPosition()));
                }
            });
        }

        void bind(String name, int iconResource) {
            binding.setInventoryPic(iconResource);
            binding.setInventoryName(name);
            binding.executePendingBindings();
        }
    }


    @Override
    public int getItemCount() {
        return inventoryObjects != null ? inventoryObjects.size() : 0;
    }

    // adds new ability to the list
    void updateChange() {
        notifyDataSetChanged();
    }

    // if there is a selected index stored, then remove and update all elements
    void unSelectIfOneSelected() {
        if (selectedIndex >= 0) {
            int selectedPrev = selectedIndex;
            selectedIndex = -1;
            notifyItemChanged(selectedPrev);
        }
    }

    /**
     * Disabled and unSelects all views inside rv.
     * @param isEnabled true if enabling views, false if disabling
     */
    void disableAllViews(boolean isEnabled) {
        if (selectedIndex >= 0) {
            selectedIndex = -1;
        }
        this.isEnabled = isEnabled;
        notifyDataSetChanged();
    }
}
