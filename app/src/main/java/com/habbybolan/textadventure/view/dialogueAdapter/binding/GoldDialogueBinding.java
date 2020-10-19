package com.habbybolan.textadventure.view.dialogueAdapter.binding;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.DialogueGoldDetailsBinding;
import com.habbybolan.textadventure.model.dialogue.DialogueType;
import com.habbybolan.textadventure.model.dialogue.GoldDialogue;
import com.habbybolan.textadventure.view.dialogueAdapter.DataBinder;
import com.habbybolan.textadventure.view.dialogueAdapter.DialogueAdapter;

import java.util.ArrayList;
import java.util.List;

public class GoldDialogueBinding extends DataBinder<GoldDialogueBinding.ViewHolder> {

    private List<GoldDialogue> goldDialogue = new ArrayList<>();
    public GoldDialogueBinding(DialogueAdapter dialogueAdapter) {
        super(dialogueAdapter);
    }

    @Override
    public GoldDialogueBinding.ViewHolder newViewHolder(ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        DialogueGoldDetailsBinding bindingDialogue = DataBindingUtil.inflate(layoutInflater, R.layout.dialogue_gold_details, parent, false);
        return new GoldDialogueBinding.ViewHolder(bindingDialogue);
    }

    @Override
    public void bindViewHolder(GoldDialogueBinding.ViewHolder holder, int position) {
        GoldDialogue textObject = goldDialogue.get(position);
        holder.bind(textObject);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        DialogueGoldDetailsBinding binding;
        ViewHolder(DialogueGoldDetailsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(GoldDialogue goldDialogue) {
            binding.setGoldChange(String.valueOf(goldDialogue.getAmount()));
            binding.executePendingBindings();
        }
    }

    @Override
    public int getItemCount() {
        return goldDialogue.size();
    }


    @Override
    public void addDialogue(DialogueType dialogue) {
        if (dialogue.getClass() != GoldDialogue.class) throw new IllegalArgumentException();
        GoldDialogue addDialogue = (GoldDialogue) dialogue;
        goldDialogue.add(addDialogue);
    }
}

