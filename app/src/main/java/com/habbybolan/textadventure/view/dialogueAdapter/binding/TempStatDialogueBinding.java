package com.habbybolan.textadventure.view.dialogueAdapter.binding;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.DialogueTempStatDetailsBinding;
import com.habbybolan.textadventure.model.dialogue.DialogueTypes;
import com.habbybolan.textadventure.model.dialogue.TempStatDialogue;
import com.habbybolan.textadventure.view.dialogueAdapter.DataBinder;
import com.habbybolan.textadventure.view.dialogueAdapter.DialogueAdapter;

import java.util.ArrayList;
import java.util.List;

public class TempStatDialogueBinding extends DataBinder<TempStatDialogueBinding.ViewHolder> {

    private List<TempStatDialogue> tempStatDialogue = new ArrayList<>();
    public TempStatDialogueBinding(DialogueAdapter dialogueAdapter) {
        super(dialogueAdapter);
    }

    @Override
    public TempStatDialogueBinding.ViewHolder newViewHolder(ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        DialogueTempStatDetailsBinding bindingDialogue = DataBindingUtil.inflate(layoutInflater, R.layout.dialogue_temp_stat_details, parent, false);
        return new TempStatDialogueBinding.ViewHolder(bindingDialogue);
    }

    @Override
    public void bindViewHolder(TempStatDialogueBinding.ViewHolder holder, int position) {
        TempStatDialogue textObject = tempStatDialogue.get(position);
        holder.bind(textObject);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        DialogueTempStatDetailsBinding binding;
        ViewHolder(DialogueTempStatDetailsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(TempStatDialogue statDialogue) {
            binding.setAmount(String.valueOf(statDialogue.getAmount()));
            binding.setType(statDialogue.getType());
            binding.setDuration(String.valueOf(statDialogue.getDuration()));
            binding.executePendingBindings();
        }
    }

    @Override
    public int getItemCount() {
        return tempStatDialogue.size();
    }


    @Override
    public void addDialogue(DialogueTypes dialogue) {
        if (dialogue.getClass() != TempStatDialogue.class) throw new IllegalArgumentException();
        TempStatDialogue addDialogue = (TempStatDialogue) dialogue;
        tempStatDialogue.add(addDialogue);
    }
}
