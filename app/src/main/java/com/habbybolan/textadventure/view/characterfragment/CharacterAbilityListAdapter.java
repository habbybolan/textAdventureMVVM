package com.habbybolan.textadventure.view.characterfragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.recyclerview.widget.RecyclerView;

import com.habbybolan.textadventure.BR;
import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.CharacterAbilityListDetailsBinding;
import com.habbybolan.textadventure.model.inventory.Ability;
import com.habbybolan.textadventure.viewmodel.CharacterViewModel;

import java.util.ArrayList;

public class CharacterAbilityListAdapter extends RecyclerView.Adapter<CharacterAbilityListAdapter.ViewHolder> {

    private ArrayList<Ability> abilities;
    private CharacterListClickListener clickListener;
    private CharacterViewModel characterVM;

    CharacterAbilityListAdapter(ArrayList<Ability> abilities, CharacterViewModel characterVM, CharacterListClickListener clickListener) {
        this.abilities = abilities;
        this.clickListener = clickListener;
        this.characterVM = characterVM;
    }

    // inflate the layout used for the recyclerView
    @NonNull
    @Override
    public CharacterAbilityListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CharacterAbilityListDetailsBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.character_ability_list_details, parent, false);
        return new ViewHolder(binding, clickListener, characterVM);
    }

    // set details of the views
    @Override
    public void onBindViewHolder(@NonNull CharacterAbilityListAdapter.ViewHolder holder, final int position) {
        Ability ability = abilities.get(position);
        holder.bind(ability);
        holder.setDropConsumeListener();
    }

    // create the views inside the recyclerViewer
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public CharacterAbilityListDetailsBinding binding;
        CharacterListClickListener listener;
        CharacterViewModel characterVM;

        ViewHolder(CharacterAbilityListDetailsBinding binding, CharacterListClickListener listener, CharacterViewModel characterVM) {
            super(binding.getRoot());
            this.binding = binding;
            this.listener = listener;
            this.characterVM = characterVM;

            binding.btnDropAbility.setOnClickListener(this);
            binding.btnDropAbility.setOnLongClickListener(this);
        }

        void bind(Object obj) {
            binding.setVariable(BR.ability, obj);
            binding.executePendingBindings();
        }

        void setDropConsumeListener() {
            Boolean state = characterVM.getStateInventoryObserver().get();
            if (state != null) binding.btnDropAbility.setEnabled(state);
            // listener for when an ability is added to character
            Observable.OnPropertyChangedCallback callbackAdd = new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable sender, int propertyId) {
                    Boolean bool = characterVM.getStateInventoryObserver().get();
                    if (bool != null) {
                        if (!bool) {
                            // disable buttons and discolor
                            binding.btnDropAbility.setEnabled(false);
                        } else {
                            // enable buttons and give normal color
                            binding.btnDropAbility.setEnabled(true);
                        }
                    }
                }
            };
            characterVM.getStateInventoryObserver().addOnPropertyChangedCallback(callbackAdd);
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
    void updateAbilityChange() {
        notifyDataSetChanged();
    }

}
