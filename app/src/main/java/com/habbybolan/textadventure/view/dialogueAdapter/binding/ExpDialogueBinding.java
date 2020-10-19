package com.habbybolan.textadventure.view.dialogueAdapter.binding;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.DialogueExpDetailsBinding;
import com.habbybolan.textadventure.model.dialogue.DialogueType;
import com.habbybolan.textadventure.model.dialogue.ExpDialogue;
import com.habbybolan.textadventure.view.dialogueAdapter.DataBinder;
import com.habbybolan.textadventure.view.dialogueAdapter.DialogueAdapter;

import java.util.ArrayList;
import java.util.List;

public class ExpDialogueBinding extends DataBinder<ExpDialogueBinding.ViewHolder> {

    private List<ExpDialogue> expDialogue = new ArrayList<>();
    public ExpDialogueBinding(DialogueAdapter dialogueAdapter) {
        super(dialogueAdapter);
    }

    @Override
    public ExpDialogueBinding.ViewHolder newViewHolder(ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        DialogueExpDetailsBinding bindingDialogue = DataBindingUtil.inflate(layoutInflater, R.layout.dialogue_exp_details, parent, false);
        return new ExpDialogueBinding.ViewHolder(bindingDialogue);
    }

    @Override
    public void bindViewHolder(ExpDialogueBinding.ViewHolder holder, int position) {
        ExpDialogue textObject = expDialogue.get(position);
        holder.bind(textObject);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        DialogueExpDetailsBinding binding;
        ViewHolder(DialogueExpDetailsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ExpDialogue expDialogue) {
            binding.setExpChange(String.valueOf(expDialogue.getAmount()));
            binding.executePendingBindings();
        }
    }

    @Override
    public int getItemCount() {
        return expDialogue.size();
    }


    @Override
    public void addDialogue(DialogueType dialogue) {
        if (dialogue.getClass() != ExpDialogue.class) throw new IllegalArgumentException();
        ExpDialogue addDialogue = (ExpDialogue) dialogue;
        expDialogue.add(addDialogue);
    }
}
