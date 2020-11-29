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
import com.habbybolan.textadventure.databinding.CharacterAbilityListDetailsBinding;
import com.habbybolan.textadventure.model.inventory.Ability;
import com.habbybolan.textadventure.viewmodel.characterEntityViewModels.CharacterViewModel;

import java.util.ArrayList;

public class CharacterAbilityListAdapter extends RecyclerView.Adapter<CharacterAbilityListAdapter.ViewHolder> {

    private ArrayList<Ability> abilities;
    private CharacterListDropConsumeClickListener dropListener;
    private CharacterListInfoClickListener infoClickListener;
    private CharacterViewModel characterVM;

    CharacterAbilityListAdapter(ArrayList<Ability> abilities, CharacterViewModel characterVM, CharacterListDropConsumeClickListener dropListener, CharacterListInfoClickListener infoClickListener) {
        this.abilities = abilities;
        this.dropListener = dropListener;
        this.infoClickListener = infoClickListener;
        this.characterVM = characterVM;
    }

    // inflate the layout used for the recyclerView
    @NonNull
    @Override
    public CharacterAbilityListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CharacterAbilityListDetailsBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.character_ability_list_details, parent, false);
        return new ViewHolder(binding, dropListener, infoClickListener, characterVM);
    }

    // set details of the views
    @Override
    public void onBindViewHolder(@NonNull CharacterAbilityListAdapter.ViewHolder holder, final int position) {
        Ability ability = abilities.get(position);
        holder.bind(ability);
        holder.setDropConsumeListener();
    }

    // create the views inside the recyclerViewer
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CharacterAbilityListDetailsBinding binding;
        CharacterViewModel characterVM;

        ViewHolder(CharacterAbilityListDetailsBinding binding, final CharacterListDropConsumeClickListener dropListener, final CharacterListInfoClickListener infoClickListener, CharacterViewModel characterVM) {
            super(binding.getRoot());
            this.binding = binding;
            this.characterVM = characterVM;

            // only displays a message to hold drop button if trying to remove
            binding.btnDropAbility.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dropListener.onClick("Hold to drop ability scroll");
                }
            });

            // deleted the inventory element if long click
            binding.btnDropAbility.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    dropListener.onLongClicked(getAdapterPosition());
                    return true;
                }
            });

            // display info of Ability clicked
            binding.infoAbility.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    infoClickListener.onClick(getAdapterPosition());
                }
            });
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
