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
import com.habbybolan.textadventure.model.inventory.Item;

import java.util.ArrayList;

public class CharacterItemListAdapter extends RecyclerView.Adapter<CharacterItemListAdapter.ViewHolder> {

    private ArrayList<Item> items;
    private int selectedPosition = 0;
    CharacterListClickListener listener;

    public CharacterItemListAdapter(ArrayList<Item> items, CharacterListClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    // create the views inside the recyclerViewer
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        // each data item is just a string in this case
        private TextView itemLabel;
        private TextView itemDetails;
        private Button btnDrop;
        private CharacterListClickListener listener;
        private ArrayList<Item> items;

        public ViewHolder(View characterView, CharacterListClickListener listener, ArrayList<Item> items) {
            super(characterView);
            this.listener = listener;
            this.items = items;
            itemLabel = characterView.findViewById(R.id.item_label);
            itemDetails = characterView.findViewById(R.id.item_detail);
            btnDrop = characterView.findViewById(R.id.btn_drop_item);
            btnDrop.setOnClickListener(this);
            btnDrop.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(v.getContext(), "Hold to drop item", Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onLongClick(View v) {
            listener.onLongClicked(items.get(getAdapterPosition()));
            return true;
        }
    }


    // inflate the layout used for the recyclerView
    @NonNull
    @Override
    public CharacterItemListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Inflate the custom layout
        View characterView = LayoutInflater.from(parent.getContext()).inflate(R.layout.character_item_list_details, parent, false);
        // Return a new holder instance
        return new ViewHolder(characterView, listener, items);
    }

    // set details of the views
    @Override
    public void onBindViewHolder(@NonNull CharacterItemListAdapter.ViewHolder holder, final int position) {
        String itemLabelText = "Item " + (position+1);
        final TextView itemLabel = holder.itemLabel;
        itemLabel.setText(itemLabelText);

        String itemDetailText = items.get(position).getName();
        final TextView itemDetail = holder.itemDetails;
        itemDetail.setText(itemDetailText);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    // adds new item to the list
    public void addNewItem(Item newItem) {
        items.add(newItem);
        notifyItemInserted(items.size()-1);
    }

    // remove an item from the list
    public void removeItem(Item itemToRemove) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).equals(itemToRemove)) {
                removeItemAtIndex(i);
                break;
            }
        }
    }
    // remove an item from the list
    private void removeItemAtIndex(int index) {
        items.remove(index);
        notifyItemRemoved(index);
    }
}
