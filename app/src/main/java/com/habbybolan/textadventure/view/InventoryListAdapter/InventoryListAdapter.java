package com.habbybolan.textadventure.view.InventoryListAdapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.InventoryListDetailsBinding;
import com.habbybolan.textadventure.model.inventory.Inventory;

import java.util.ArrayList;
/*
adapter for Inventory objects inside list of combat encounter
 */
public class InventoryListAdapter extends RecyclerView.Adapter<InventoryListAdapter.ViewHolder> {

    private ArrayList<? extends Inventory> inventoryObjects;
    private InventoryClickListener inventoryClickListener;

    // index of selected item, -1 if nothing selected
    private int selectedIndex = -1;

    InventoryListAdapter(ArrayList<? extends Inventory> inventoryObjects, InventoryClickListener inventoryClickListener) {
        this.inventoryObjects = inventoryObjects;
        this.inventoryClickListener = inventoryClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        InventoryListDetailsBinding bindingDialogue = DataBindingUtil.inflate(layoutInflater, R.layout.inventory_list_details, parent, false);
        return new InventoryListAdapter.ViewHolder(bindingDialogue, inventoryClickListener, this);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        String name = inventoryObjects.get(position).getName();
        int iconResource = inventoryObjects.get(position).getPictureResource();
        holder.bind(name, iconResource);
        // if the position is the selectedIndex, then color the text red, otherwise black
        if (position == selectedIndex) holder.binding.txtName.setTextColor(Color.RED);
        else holder.binding.txtName.setTextColor(Color.BLACK);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        InventoryListDetailsBinding binding;
        InventoryClickListener inventoryClickListener;

        ViewHolder(final InventoryListDetailsBinding binding, final InventoryClickListener inventoryClickListener, final InventoryListAdapter adapter) {
            super(binding.getRoot());
            this.binding = binding;
            this.inventoryClickListener = inventoryClickListener;

            binding.inventoryContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.notifyItemChanged(adapter.selectedIndex);
                    adapter.selectedIndex = getAdapterPosition();
                    adapter.notifyItemChanged(adapter.selectedIndex);
                    inventoryClickListener.onClicked(getAdapterPosition());
                }
            });
        }

        void bind(String name, int iconResource) {
            binding.setIconResource(iconResource);
            binding.setName(name);
            binding.executePendingBindings();
        }
    }


    @Override
    public int getItemCount() {
        return inventoryObjects != null ? inventoryObjects.size() : 0;
    }

    // adds new ability to the list
    public void updateChange() {
        notifyDataSetChanged();
    }

    // if there is a selected index stored, then remove and update all elements
    public void unSelectIfOneSelected() {
        if (selectedIndex >= 0) {
            selectedIndex = -1;
            notifyDataSetChanged();
        }
    }
}
