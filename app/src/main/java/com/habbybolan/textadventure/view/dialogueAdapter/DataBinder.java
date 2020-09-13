package com.habbybolan.textadventure.view.dialogueAdapter;

import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.habbybolan.textadventure.model.dialogue.DialogueType;

public abstract class DataBinder <T extends RecyclerView.ViewHolder> {

    private DialogueAdapter dialogueAdapter;

    public DataBinder(DialogueAdapter dialogueAdapter) {
        this.dialogueAdapter = dialogueAdapter;
    }

    abstract public T newViewHolder(ViewGroup parent);

    abstract public void bindViewHolder(T holder, int position);

    abstract public int getItemCount();

    abstract public void addDialogue(DialogueType dialogue);
}
