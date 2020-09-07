package com.habbybolan.textadventure.view.dialogueAdapter.binding;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.DialogueInventoryDetailsBinding;
import com.habbybolan.textadventure.model.dialogue.DialogueTypes;
import com.habbybolan.textadventure.model.dialogue.InventoryDialogue;
import com.habbybolan.textadventure.model.inventory.Inventory;
import com.habbybolan.textadventure.view.dialogueAdapter.DataBinder;
import com.habbybolan.textadventure.view.dialogueAdapter.DialogueAdapter;

import java.util.ArrayList;
import java.util.List;

public class InventoryDialogueBinding extends DataBinder<InventoryDialogueBinding.ViewHolder> {

    private List<InventoryDialogue> inventoryDialogue = new ArrayList<>();
    public InventoryDialogueBinding(DialogueAdapter dialogueAdapter) {
        super(dialogueAdapter);
    }

    @Override
    public InventoryDialogueBinding.ViewHolder newViewHolder(ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        DialogueInventoryDetailsBinding bindingDialogue = DataBindingUtil.inflate(layoutInflater, R.layout.dialogue_inventory_details, parent, false);
        return new InventoryDialogueBinding.ViewHolder(bindingDialogue);
    }

    @Override
    public void bindViewHolder(InventoryDialogueBinding.ViewHolder holder, int position) {
        InventoryDialogue textObject = inventoryDialogue.get(position);
        holder.bind(textObject);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        DialogueInventoryDetailsBinding binding;
        ViewHolder(DialogueInventoryDetailsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(InventoryDialogue inventoryDialogue) {
            String inventoryName = "";
            if (inventoryDialogue.getIsAdded()) inventoryName += "+1 " + inventoryDialogue.getName();
            else inventoryName += "-1 " + inventoryDialogue.getName();
            if (inventoryDialogue.getType().equals(Inventory.TYPE_ABILITY)) inventoryName += " scroll";
            binding.setInventoryName(inventoryName);
            binding.setInventoryPicture(inventoryDialogue.getImageResource());
            binding.executePendingBindings();
        }
    }

    @Override
    public int getItemCount() {
        return inventoryDialogue.size();
    }


    @Override
    public void addDialogue(DialogueTypes dialogue) {
        if (dialogue.getClass() != InventoryDialogue.class) throw new IllegalArgumentException();
        InventoryDialogue addDialogue = (InventoryDialogue) dialogue;
        inventoryDialogue.add(addDialogue);
    }
}
