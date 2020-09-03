package com.habbybolan.textadventure.view.dialogueAdapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.DialogueEffectDetailsBinding;
import com.habbybolan.textadventure.model.dialogue.EffectDialogue;

import java.util.ArrayList;
import java.util.List;

public class EffectDialogueBinding extends DataBinder<EffectDialogueBinding.ViewHolder> {

    List<EffectDialogue> effectDialogue = new ArrayList<>();
    EffectDialogueBinding(DataBindAdapter dataBindAdapter) {
        super(dataBindAdapter);
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
        public ViewHolder(DialogueEffectDetailsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(EffectDialogue effectDialogue) {
            binding.setEffectName(effectDialogue.getType());
            binding.setDuration(String.valueOf(effectDialogue.getDuration()));
        }
    }

    @Override
    public int getItemCount() {
        return effectDialogue.size();
    }

    public void addNewEffectDialogue(EffectDialogue dialogue) {
        effectDialogue.add(dialogue);
    }

}
