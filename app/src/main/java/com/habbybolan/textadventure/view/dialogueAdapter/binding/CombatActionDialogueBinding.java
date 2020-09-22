package com.habbybolan.textadventure.view.dialogueAdapter.binding;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.DialogueCombatActionDetailsBinding;
import com.habbybolan.textadventure.model.dialogue.CombatActionDialogue;
import com.habbybolan.textadventure.model.dialogue.DialogueType;
import com.habbybolan.textadventure.view.dialogueAdapter.DataBinder;
import com.habbybolan.textadventure.view.dialogueAdapter.DialogueAdapter;

import java.util.ArrayList;
import java.util.List;

public class CombatActionDialogueBinding extends DataBinder<CombatActionDialogueBinding.ViewHolder> {

    private List<CombatActionDialogue> combatDialogue = new ArrayList<>();
    public CombatActionDialogueBinding(DialogueAdapter dialogueAdapter) {
        super(dialogueAdapter);
    }

    @Override
    public CombatActionDialogueBinding.ViewHolder newViewHolder(ViewGroup parent) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        DialogueCombatActionDetailsBinding bindingDialogue = DataBindingUtil.inflate(layoutInflater, R.layout.dialogue_combat_action_details, parent, false);
        return new CombatActionDialogueBinding.ViewHolder(bindingDialogue);
    }

    @Override
    public void bindViewHolder(CombatActionDialogueBinding.ViewHolder holder, int position) {
        CombatActionDialogue dialogue = combatDialogue.get(position);
        holder.bind(dialogue);
    }

    @Override
    public int getItemCount() {
        return combatDialogue.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {

        DialogueCombatActionDetailsBinding binding;
        ViewHolder(DialogueCombatActionDetailsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(CombatActionDialogue dialogue) {
            binding.setActionName(dialogue.getActionName());
            binding.setAttackerName(dialogue.getAttackerName());
            binding.setTargetName(dialogue.getTargetName());
            binding.setAmount(dialogue.getAmount());
            binding.executePendingBindings();
        }
    }

    @Override
    public void addDialogue(DialogueType dialogue) {
        if (dialogue.getClass() != CombatActionDialogue.class) throw new IllegalArgumentException();
        CombatActionDialogue addDialogue = (CombatActionDialogue) dialogue;
        this.combatDialogue.add(addDialogue);
    }
}
