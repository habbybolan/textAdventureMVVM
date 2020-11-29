package com.habbybolan.textadventure.view.characterfragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.recyclerview.widget.RecyclerView;

import com.habbybolan.textadventure.BR;
import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.CharacterItemListDetailsBinding;
import com.habbybolan.textadventure.model.inventory.Item;
import com.habbybolan.textadventure.viewmodel.characterEntityViewModels.CharacterViewModel;

import java.util.ArrayList;

public class CharacterItemListAdapter extends RecyclerView.Adapter<CharacterItemListAdapter.ViewHolder> {
    private ArrayList<Item> items;
    private CharacterListDropConsumeClickListener dropListener;
    private CharacterListDropConsumeClickListener consumeListener;
    private CharacterListInfoClickListener infoListener;
    private CharacterViewModel characterVM;

    public CharacterItemListAdapter(ArrayList<Item> items, CharacterViewModel characterVM, CharacterListDropConsumeClickListener dropListener, CharacterListDropConsumeClickListener consumeListener, CharacterListInfoClickListener infoListener) {
        this.items = items;
        this.dropListener = dropListener;
        this.consumeListener = consumeListener;
        this.infoListener = infoListener;
        this.characterVM = characterVM;
    }

    // inflate the layout used for the recyclerView
    @NonNull
    @Override
    public CharacterItemListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CharacterItemListDetailsBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.character_item_list_details, parent, false);
        return new CharacterItemListAdapter.ViewHolder(binding, dropListener, consumeListener, infoListener, characterVM);
    }

    // set details of the views
    @Override
    public void onBindViewHolder(@NonNull CharacterItemListAdapter.ViewHolder holder, final int position) {
        Item item = items.get(position);
        holder.bind(item);
        holder.setDropConsumeListener();
    }

    // create the views inside the recyclerViewer
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CharacterItemListDetailsBinding binding;
        CharacterListDropConsumeClickListener dropListener;
        CharacterListDropConsumeClickListener consumeListener;
        CharacterViewModel characterVM;

        ViewHolder(CharacterItemListDetailsBinding binding, final CharacterListDropConsumeClickListener dropListener, final CharacterListDropConsumeClickListener consumeListener, final CharacterListInfoClickListener infoListener, CharacterViewModel characterVM) {
            super(binding.getRoot());
            this.binding = binding;
            this.dropListener = dropListener;
            this.consumeListener = consumeListener;
            this.characterVM = characterVM;

            // ** drop button **

            // displays a message to hold drop button if trying to remove
            binding.btnDropItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dropListener.onClick("Hold to drop item");
                }
            });

            // deleted the inventory item if long click
            binding.btnDropItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    dropListener.onLongClicked(getAdapterPosition());
                    return true;
                }
            });

            // ** consume button **

            // displays message to hold consume button if wants to consume
            binding.btnConsumeItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    consumeListener.onClick("Hold to consume item");
                }
            });

            // consumes the item clicked
            binding.btnConsumeItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    consumeListener.onLongClicked(getAdapterPosition());
                    return true;
                }
            });

            // image button to display info of weapon clicked
            binding.infoItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    infoListener.onClick(getAdapterPosition());
                }
            });
        }

        void bind(Object obj) {
            binding.setVariable(BR.item, obj);
            binding.executePendingBindings();
        }

        void setDropConsumeListener() {
            Boolean state = characterVM.getStateInventoryObserver().get();
            if (state != null) {
                binding.btnDropItem.setEnabled(state);
                binding.btnConsumeItem.setEnabled(state);
            }
            // listener for when an ability is added to character
            Observable.OnPropertyChangedCallback callbackAdd = new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable sender, int propertyId) {
                    Boolean bool = characterVM.getStateInventoryObserver().get();
                    if (bool != null) {
                        if (!bool) {
                            // disable buttons and discolor
                            binding.btnDropItem.setEnabled(false);
                            binding.btnConsumeItem.setEnabled(false);
                        } else {
                            // enable buttons and give normal color
                            binding.btnDropItem.setEnabled(true);
                            binding.btnConsumeItem.setEnabled(true);
                        }
                    }
                }
            };
            characterVM.getStateInventoryObserver().addOnPropertyChangedCallback(callbackAdd);
        }
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    // adds new ability to the list
    public void updateItemChange() {
        notifyDataSetChanged();
    }

}
