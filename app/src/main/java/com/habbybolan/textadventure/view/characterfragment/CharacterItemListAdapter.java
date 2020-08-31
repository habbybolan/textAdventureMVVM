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
import com.habbybolan.textadventure.databinding.CharacterItemListDetailsBinding;
import com.habbybolan.textadventure.model.inventory.Item;

import java.util.ArrayList;

public class CharacterItemListAdapter extends RecyclerView.Adapter<CharacterItemListAdapter.ViewHolder> {
    private ArrayList<Item> items;
    private CharacterListClickListener clickListener;

    public CharacterItemListAdapter(ArrayList<Item> items, CharacterListClickListener clickListener) {
        this.items = items;
        this.clickListener = clickListener;
    }

    // inflate the layout used for the recyclerView
    @NonNull
    @Override
    public CharacterItemListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CharacterItemListDetailsBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.character_item_list_details, parent, false);
        return new CharacterItemListAdapter.ViewHolder(binding, clickListener);
    }

    // set details of the views
    @Override
    public void onBindViewHolder(@NonNull CharacterItemListAdapter.ViewHolder holder, final int position) {
        Item item = items.get(position);
        holder.bind(item);
    }

    // create the views inside the recyclerViewer
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public CharacterItemListDetailsBinding binding;
        CharacterListClickListener listener;

        ViewHolder(CharacterItemListDetailsBinding binding, CharacterListClickListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            this.listener = listener;

            binding.btnDropItem.setOnClickListener(this);
            binding.btnDropItem.setOnLongClickListener(this);
        }

        void bind(Object obj) {
            binding.setVariable(BR.item, obj);
            binding.executePendingBindings();
        }

        // only displays a message to hold drop button if trying to remove
        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(), "Hold to drop item", Toast.LENGTH_SHORT).show();
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
        return items != null ? items.size() : 0;
    }

    // adds new ability to the list
    public void updateNewItemIndex(int index) {
        notifyItemInserted(index);
    }

    public void updateRemovedItemIndex(int index) {
        notifyItemRemoved(index);
    }
}
