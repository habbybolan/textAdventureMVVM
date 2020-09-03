package com.habbybolan.textadventure.view.dialogueAdapter;

import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

public abstract class DataBinder <T extends RecyclerView.ViewHolder> {

    private DataBindAdapter dataBindAdapter;

    public DataBinder(DataBindAdapter dataBindAdapter) {
        this.dataBindAdapter = dataBindAdapter;
    }

    abstract public T newViewHolder(ViewGroup parent);

    abstract public void bindViewHolder(T holder, int position);

    abstract public int getItemCount();
}
