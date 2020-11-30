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
import com.habbybolan.textadventure.databinding.WeaponListDetailsBinding;
import com.habbybolan.textadventure.model.inventory.weapon.Weapon;

import java.util.ArrayList;

public class WeaponListAdapter extends RecyclerView.Adapter<WeaponListAdapter.ViewHolder> {

    private ArrayList<Weapon> weapons;
    private ActionClickListener actionClickListener;

    // index of selected item, -1 if nothing selected
    private int mExpandedPosition = -1;
    private int previousExpandedPosition = -1;
    private int selectedPosition = -1;
    private boolean isEnabled = true;

    WeaponListAdapter(ArrayList<Weapon> weapons, ActionClickListener actionClickListener) {
        this.weapons = weapons;
        this.actionClickListener = actionClickListener;
    }

    @NonNull
    @Override
    public WeaponListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        WeaponListDetailsBinding bindingDialogue = DataBindingUtil.inflate(layoutInflater, R.layout.weapon_list_details, parent, false);
        return new WeaponListAdapter.ViewHolder(bindingDialogue, actionClickListener, this);
    }

    @Override
    public void onBindViewHolder(@NonNull final WeaponListAdapter.ViewHolder holder, final int position) {
        final boolean isExpanded = position == mExpandedPosition;
        holder.binding.attackContainer.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.binding.sAttackContainer.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        // bindings for the inflated inventory_snippet layout inside weapon_list_details snippet
        InventorySnippetBinding attackBinding = holder.binding.attackSnippet;
        InventorySnippetBinding sAttackBinding = holder.binding.sAttackSnippet;

        // if this is the view to expand, then expand and select the proper expanded view
        if (isExpanded) {
            previousExpandedPosition = position;
            // expanded view is an attack
            if (selectedPosition == 0) {
                attackBinding.txtName.setTextColor(Color.RED);
                sAttackBinding.txtName.setTextColor(Color.BLACK);
            }
            // expanded view is a special attack
            else if (selectedPosition == 1) {
                attackBinding.txtName.setTextColor(Color.BLACK);
                sAttackBinding.txtName.setTextColor(Color.RED);
            } else {
                attackBinding.txtName.setTextColor(Color.BLACK);
                sAttackBinding.txtName.setTextColor(Color.BLACK);
            }
        } else {
            attackBinding.txtName.setTextColor(Color.BLACK);
            sAttackBinding.txtName.setTextColor(Color.BLACK);
        }

        /* If specific view is already expanded, then set on not expanded. Otherwise,
            expand the new position clicked and compress the previously clicked view.
         */
        holder.binding.weaponContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpandedPosition = isExpanded ? -1 : position;
                // compress old
                notifyItemChanged(previousExpandedPosition);
                // expand new
                notifyItemChanged(position);
            }
        });

        // get all resources to set into the views
        String weaponName = weapons.get(position).getName();
        int weaponIconResource = weapons.get(position).getPictureResource();
        String attackName = weapons.get(position).getAttack().getName();
        int attackIconResource = weapons.get(position).getAttack().getPictureResource();
        String sAttackName = weapons.get(position).getSpecialAttack().getSpecialAttackName();
        int sAttackIconResource = weapons.get(position).getSpecialAttack().getPictureResource();
        // disable/enable name click
        holder.binding.weaponContainer.setEnabled(isEnabled);
        holder.bind(weaponName, weaponIconResource, attackName, attackIconResource, sAttackName, sAttackIconResource);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        WeaponListDetailsBinding binding;
        ActionClickListener actionClickListener;

        // bindings for the inflated inventory_snippet layout inside weapon_list_details snippet
        InventorySnippetBinding attackBinding;
        InventorySnippetBinding sAttackBinding;

        ViewHolder(final WeaponListDetailsBinding binding, final ActionClickListener actionClickListener, final WeaponListAdapter adapter) {
            super(binding.getRoot());
            this.binding = binding;
            this.actionClickListener = actionClickListener;

            // bindings for the inflated inventory_snippet layout inside weapon_list_details snippet
            attackBinding = binding.attackSnippet;
            sAttackBinding = binding.sAttackSnippet;

            // selected attack
            attackBinding.txtName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.selectedPosition = 0;
                    actionClickListener.onClicked(adapter.weapons.get(getAdapterPosition()).getAttack());
                    adapter.notifyDataSetChanged();
                }
            });
            // selected attack info button
            attackBinding.inventoryInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    actionClickListener.onInfoClick(adapter.weapons.get(getAdapterPosition()).getAttack());
                }
            });

            // selected special attack
            sAttackBinding.txtName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.notifyItemChanged(adapter.selectedPosition);
                    adapter.selectedPosition = 1;
                    actionClickListener.onClicked(adapter.weapons.get(getAdapterPosition()).getSpecialAttack());
                    adapter.notifyItemChanged(getAdapterPosition());
                }
            });
            // selected special attack info button
            sAttackBinding.inventoryInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    actionClickListener.onInfoClick(adapter.weapons.get(getAdapterPosition()).getSpecialAttack());
                }
            });
        }

        void bind(String weaponName, int weaponIconResource, String attackName, int attackIconResource, String sAttackName, int sAttackIconResource) {
            binding.setWeaponIconResource(weaponIconResource);
            binding.setWeaponName(weaponName);
            attackBinding.setInventoryPic(attackIconResource);
            attackBinding.setInventoryName(attackName);
            sAttackBinding.setInventoryPic(sAttackIconResource);
            sAttackBinding.setInventoryName(sAttackName);
            binding.executePendingBindings();
        }
    }


    @Override
    public int getItemCount() {
        return weapons != null ? weapons.size() : 0;
    }

    // adds new ability to the list
    void updateChange() {
        notifyDataSetChanged();
    }

    // if there is a selected index stored, then remove and update all elements
    void unSelectIfOneSelected() {
        int position = mExpandedPosition;
        mExpandedPosition = -1;
        selectedPosition = -1;
        notifyItemChanged(position);
    }

    /**
     * Disabled and unSelects all views inside rv.
     * @param isEnabled true if enabling views, false if disabling
     */
    void disableAllViews(boolean isEnabled) {
        mExpandedPosition = -1;
        selectedPosition = -1;
        this.isEnabled = isEnabled;
        notifyDataSetChanged();
    }
}
