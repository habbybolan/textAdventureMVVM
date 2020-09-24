package com.habbybolan.textadventure.view.InventoryListAdapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.WeaponListDetailsBinding;
import com.habbybolan.textadventure.model.inventory.weapon.Weapon;

import java.util.ArrayList;

public class WeaponListAdapter extends RecyclerView.Adapter<WeaponListAdapter.ViewHolder> {

    private ArrayList<Weapon> weapons;
    private InventoryClickListener inventoryClickListener;

    // index of selected item, -1 if nothing selected
    private int mExpandedPosition = -1;
    private int previousExpandedPosition = -1;

    private int selectedPosition = -1;

    WeaponListAdapter(ArrayList<Weapon> weapons, InventoryClickListener inventoryClickListener) {
        this.weapons = weapons;
        this.inventoryClickListener = inventoryClickListener;
    }

    @NonNull
    @Override
    public WeaponListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        WeaponListDetailsBinding bindingDialogue = DataBindingUtil.inflate(layoutInflater, R.layout.weapon_list_details, parent, false);
        return new WeaponListAdapter.ViewHolder(bindingDialogue, inventoryClickListener, this);
    }

    @Override
    public void onBindViewHolder(@NonNull final WeaponListAdapter.ViewHolder holder, final int position) {
        final boolean isExpanded = position == mExpandedPosition;
        holder.binding.attackContainer.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.binding.sAttackContainer.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        if (isExpanded) {
            previousExpandedPosition = position;
            if (selectedPosition == 0) {
                holder.binding.txtNameAttack.setTextColor(Color.RED);
                holder.binding.txtNameSAttack.setTextColor(Color.BLACK);
            }
            else if (selectedPosition == 1) {
                holder.binding.txtNameAttack.setTextColor(Color.BLACK);
                holder.binding.txtNameSAttack.setTextColor(Color.RED);
            } else {
                holder.binding.txtNameAttack.setTextColor(Color.BLACK);
                holder.binding.txtNameSAttack.setTextColor(Color.BLACK);
            }
        } else {
            holder.binding.txtNameAttack.setTextColor(Color.BLACK);
            holder.binding.txtNameSAttack.setTextColor(Color.BLACK);
        }

        holder.binding.weaponContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpandedPosition = isExpanded ? -1 : position;
                notifyItemChanged(previousExpandedPosition);
                notifyItemChanged(position);
            }
        });

        String weaponName = weapons.get(position).getName();
        int weaponIconResource = weapons.get(position).getPictureResource();
        String attackName = weapons.get(position).getAttack().getName();
        int attackIconResource = weapons.get(position).getAttack().getPictureResource();
        String sAttackName = weapons.get(position).getSpecialAttack().getSpecialAttackName();
        int sAttackIconResource = weapons.get(position).getSpecialAttack().getPictureResource();
        holder.bind(weaponName, weaponIconResource, attackName, attackIconResource, sAttackName, sAttackIconResource);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        WeaponListDetailsBinding binding;
        InventoryClickListener inventoryClickListener;

        ViewHolder(final WeaponListDetailsBinding binding, final InventoryClickListener inventoryClickListener, final WeaponListAdapter adapter) {
            super(binding.getRoot());
            this.binding = binding;
            this.inventoryClickListener = inventoryClickListener;


            binding.attackContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.selectedPosition = 0;
                    inventoryClickListener.onClicked(adapter.weapons.get(getAdapterPosition()).getAttack());
                    adapter.notifyDataSetChanged();
                }
            });

            binding.sAttackContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.notifyItemChanged(adapter.selectedPosition);
                    adapter.selectedPosition = 1;
                    inventoryClickListener.onClicked(adapter.weapons.get(getAdapterPosition()).getSpecialAttack());
                    adapter.notifyItemChanged(getAdapterPosition());
                }
            });
        }

        void bind(String weaponName, int weaponIconResource, String attackName, int attackIconResource, String sAttackName, int sAttackIconResource) {
            binding.setWeaponIconResource(weaponIconResource);
            binding.setWeaponName(weaponName);
            binding.setAttackIconResource(attackIconResource);
            binding.setAttackName(attackName);
            binding.setSAttackIconResource(sAttackIconResource);
            binding.setSAttackName(sAttackName);
            binding.executePendingBindings();
        }
    }


    @Override
    public int getItemCount() {
        return weapons != null ? weapons.size() : 0;
    }

    // adds new ability to the list
    public void updateChange() {
        notifyDataSetChanged();
    }

    // if there is a selected index stored, then remove and update all elements
    public void unSelectIfOneSelected() {
        int position = mExpandedPosition;
        mExpandedPosition = -1;
        selectedPosition = -1;
        notifyItemChanged(position);
    }
}
