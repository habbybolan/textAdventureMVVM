package com.habbybolan.textadventure.view.dialogueAdapter.binding;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.DialogueEffectDetailsBinding;
import com.habbybolan.textadventure.model.dialogue.DialogueType;
import com.habbybolan.textadventure.model.dialogue.EffectDialogue;
import com.habbybolan.textadventure.view.dialogueAdapter.DataBinder;
import com.habbybolan.textadventure.view.dialogueAdapter.DialogueAdapter;

import java.util.ArrayList;
import java.util.List;

public class EffectDialogueBinding extends DataBinder<EffectDialogueBinding.ViewHolder> {

    private List<EffectDialogue> effectDialogue = new ArrayList<>();
    public EffectDialogueBinding(DialogueAdapter dialogueAdapter) {
        super(dialogueAdapter);
    }

    @Override
    public ViewHolder newViewHolder(ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        DialogueEffectDetailsBinding bindingDialogue = DataBindingUtil.inflate(layoutInflater, R.layout.dialogue_effect_details, parent, false);
        return new ViewHolder(bindingDialogue);
    }

    @Override
    public void bindViewHolder(ViewHolder holder, int position) {
        EffectDialogue textObject = effectDialogue.get(position);
        holder.bind(textObject);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        DialogueEffectDetailsBinding binding;
        ViewHolder(DialogueEffectDetailsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(EffectDialogue effectDialogue) {
            binding.setEffectName(effectDialogue.getType());
            binding.setDuration(String.valueOf(effectDialogue.getDuration()));
            binding.setEffectPicture(effectDialogue.getImageResource());
            binding.executePendingBindings();
        }
    }

    @Override
    public int getItemCount() {
        return effectDialogue.size();
    }


    @Override
    public void addDialogue(DialogueType dialogue) {
        if (dialogue.getClass() != EffectDialogue.class) throw new IllegalArgumentException();
        EffectDialogue addDialogue = (EffectDialogue) dialogue;
        effectDialogue.add(addDialogue);
    }

}
