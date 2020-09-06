package com.habbybolan.textadventure.view.dialogueAdapter.binding;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.DialogueHealthDetailsBinding;
import com.habbybolan.textadventure.model.dialogue.DialogueTypes;
import com.habbybolan.textadventure.model.dialogue.HealthDialogue;
import com.habbybolan.textadventure.view.dialogueAdapter.DataBinder;
import com.habbybolan.textadventure.view.dialogueAdapter.DialogueAdapter;

import java.util.ArrayList;
import java.util.List;

public class HealthDialogueBinding extends DataBinder<HealthDialogueBinding.ViewHolder> {

    private List<HealthDialogue> healthDialogue = new ArrayList<>();
    public HealthDialogueBinding(DialogueAdapter dialogueAdapter) {
        super(dialogueAdapter);
    }

    @Override
    public HealthDialogueBinding.ViewHolder newViewHolder(ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        DialogueHealthDetailsBinding bindingDialogue = DataBindingUtil.inflate(layoutInflater, R.layout.dialogue_health_details, parent, false);
        return new HealthDialogueBinding.ViewHolder(bindingDialogue);
    }

    @Override
    public void bindViewHolder(HealthDialogueBinding.ViewHolder holder, int position) {
        HealthDialogue textObject = healthDialogue.get(position);
        holder.bind(textObject);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        DialogueHealthDetailsBinding binding;
        ViewHolder(DialogueHealthDetailsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(HealthDialogue healthDialogue) {
            binding.setHealthChange(String.valueOf(healthDialogue.getHealthChange()));
            binding.setHealthPicture(healthDialogue.getIcon());
            binding.executePendingBindings();
        }
    }

    @Override
    public int getItemCount() {
        return healthDialogue.size();
    }


    @Override
    public void addDialogue(DialogueTypes dialogue) {
        if (dialogue.getClass() != HealthDialogue.class) throw new IllegalArgumentException();
        HealthDialogue addDialogue = (HealthDialogue) dialogue;
        healthDialogue.add(addDialogue);
    }
}
