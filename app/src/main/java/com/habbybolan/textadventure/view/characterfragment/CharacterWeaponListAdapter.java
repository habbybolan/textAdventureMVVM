package com.habbybolan.textadventure.view.characterfragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.recyclerview.widget.RecyclerView;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.CharacterWeaponListDetailsBinding;
import com.habbybolan.textadventure.model.inventory.weapon.Weapon;
import com.habbybolan.textadventure.viewmodel.characterEntityViewModels.CharacterViewModel;

import java.util.ArrayList;

public class CharacterWeaponListAdapter extends RecyclerView.Adapter<CharacterWeaponListAdapter.ViewHolder> {

    private ArrayList<Weapon> weapons;
    private CharacterListDropConsumeClickListener dropListener;
    private CharacterListInfoClickListener infoClickListener;
    private CharacterViewModel characterVM;

    public CharacterWeaponListAdapter(ArrayList<Weapon> weapons, CharacterViewModel characterVM, CharacterListDropConsumeClickListener dropListener, CharacterListInfoClickListener infoClickListener) {
        this.weapons = weapons;
        this.dropListener = dropListener;
        this.infoClickListener = infoClickListener;
        this.characterVM = characterVM;
    }

    // inflate the layout used for the recyclerView
    @NonNull
    @Override
    public CharacterWeaponListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CharacterWeaponListDetailsBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.character_weapon_list_details, parent, false);
        return new CharacterWeaponListAdapter.ViewHolder(binding, dropListener, infoClickListener, characterVM);
    }

    // set details of the views
    @Override
    public void onBindViewHolder(@NonNull CharacterWeaponListAdapter.ViewHolder holder, final int position) {
        Weapon weapon = weapons.get(position);
        holder.bind(weapon);
        holder.setDropConsumeListener();
    }

    // create the views inside the recyclerViewer
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CharacterWeaponListDetailsBinding binding;
        CharacterViewModel characterVM;

        ViewHolder(CharacterWeaponListDetailsBinding binding, final CharacterListDropConsumeClickListener dropListener, final CharacterListInfoClickListener infoListener, CharacterViewModel characterVM) {
            super(binding.getRoot());
            this.binding = binding;
            this.characterVM = characterVM;

            // deleted the inventory element if long click
            binding.btnDropWeapon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dropListener.onClick("Hold to drop weapon");
                }
            });

            // only displays a message to hold drop button if trying to remove
            binding.btnDropWeapon.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    dropListener.onLongClicked(getAdapterPosition());
                    return true;
                }
            });

            // display info of weapon clicked
            binding.infoWeapon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    infoListener.onClick(getAdapterPosition());
                }
            });
        }

        public void bind(Weapon weapon) {
            binding.setWeapon(weapon);
            binding.executePendingBindings();
        }

        void setDropConsumeListener() {
            Boolean state = characterVM.getStateInventoryObserver().get();
            if (state != null) binding.btnDropWeapon.setEnabled(state);
            // listener for when an ability is added to character
            Observable.OnPropertyChangedCallback callbackAdd = new Observable.OnPropertyChangedCallback() {
                @Override
                public void onPropertyChanged(Observable sender, int propertyId) {
                    Boolean bool = characterVM.getStateInventoryObserver().get();
                    if (bool != null) {
                        if (!bool) {
                            // disable buttons and discolor
                            binding.btnDropWeapon.setEnabled(false);
                        } else {
                            // enable buttons and give normal color
                            binding.btnDropWeapon.setEnabled(true);
                        }
                    }
                }
            };
            characterVM.getStateInventoryObserver().addOnPropertyChangedCallback(callbackAdd);
        }
    }

    @Override
    public int getItemCount() {
        return weapons != null ? weapons.size() : 0;
    }

    // adds new ability to the list
    public void updateWeaponChange() {
        notifyDataSetChanged();
    }
}
