package com.habbybolan.textadventure.view.dialogueAdapter.binding;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.DialogueStatDetailsBinding;
import com.habbybolan.textadventure.model.dialogue.DialogueType;
import com.habbybolan.textadventure.model.dialogue.StatDialogue;
import com.habbybolan.textadventure.view.dialogueAdapter.DataBinder;
import com.habbybolan.textadventure.view.dialogueAdapter.DialogueAdapter;

import java.util.ArrayList;
import java.util.List;

public class StatDialogueBinding extends DataBinder<StatDialogueBinding.ViewHolder> {

private List<StatDialogue> statDialogue = new ArrayList<>();
public StatDialogueBinding(DialogueAdapter dialogueAdapter) {
        super(dialogueAdapter);
        }

@Override
public StatDialogueBinding.ViewHolder newViewHolder(ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        DialogueStatDetailsBinding bindingDialogue = DataBindingUtil.inflate(layoutInflater, R.layout.dialogue_stat_details, parent, false);
        return new StatDialogueBinding.ViewHolder(bindingDialogue);
        }

@Override
public void bindViewHolder(StatDialogueBinding.ViewHolder holder, int position) {
        StatDialogue textObject = statDialogue.get(position);
        holder.bind(textObject);
        }

static class ViewHolder extends RecyclerView.ViewHolder {

    DialogueStatDetailsBinding binding;
    ViewHolder(DialogueStatDetailsBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    void bind(StatDialogue statDialogue) {
        binding.setAmount(String.valueOf(statDialogue.getAmount()));
        binding.setType(statDialogue.getType());
        binding.executePendingBindings();
    }
}

    @Override
    public int getItemCount() {
        return statDialogue.size();
    }


    @Override
    public void addDialogue(DialogueType dialogue) {
        if (dialogue.getClass() != StatDialogue.class) throw new IllegalArgumentException();
        StatDialogue addDialogue = (StatDialogue) dialogue;
        statDialogue.add(addDialogue);
    }
}
