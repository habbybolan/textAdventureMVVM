package com.habbybolan.textadventure.view.dialogueAdapter.binding;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.DialogueManaDetailsBinding;
import com.habbybolan.textadventure.model.dialogue.DialogueType;
import com.habbybolan.textadventure.model.dialogue.ManaDialogue;
import com.habbybolan.textadventure.view.dialogueAdapter.DataBinder;
import com.habbybolan.textadventure.view.dialogueAdapter.DialogueAdapter;

import java.util.ArrayList;
import java.util.List;

public class ManaDialogueBinding extends DataBinder<ManaDialogueBinding.ViewHolder> {

    private List<ManaDialogue> manaDialogue = new ArrayList<>();
    public ManaDialogueBinding(DialogueAdapter dialogueAdapter) {
        super(dialogueAdapter);
    }

    @Override
    public ManaDialogueBinding.ViewHolder newViewHolder(ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        DialogueManaDetailsBinding bindingDialogue = DataBindingUtil.inflate(layoutInflater, R.layout.dialogue_mana_details, parent, false);
        return new ManaDialogueBinding.ViewHolder(bindingDialogue);
    }

    @Override
    public void bindViewHolder(ManaDialogueBinding.ViewHolder holder, int position) {
        ManaDialogue textObject = manaDialogue.get(position);
        holder.bind(textObject);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        DialogueManaDetailsBinding binding;
        ViewHolder(DialogueManaDetailsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ManaDialogue manaDialogue) {
            binding.setManaChange(String.valueOf(manaDialogue.getAmount()));
            binding.setManaPicture(manaDialogue.getIcon());
            binding.executePendingBindings();
        }
    }

    @Override
    public int getItemCount() {
        return manaDialogue.size();
    }


    @Override
    public void addDialogue(DialogueType dialogue) {
        if (dialogue.getClass() != ManaDialogue.class) throw new IllegalArgumentException();
        ManaDialogue addDialogue = (ManaDialogue) dialogue;
        manaDialogue.add(addDialogue);
    }
}
