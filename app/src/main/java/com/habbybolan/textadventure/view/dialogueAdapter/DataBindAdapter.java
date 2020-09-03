package com.habbybolan.textadventure.view.dialogueAdapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class DataBindAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    // inflate the layout
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return getDataBinder(viewType).newViewHolder(parent);
    }

    @Override
    public abstract int getItemCount();

    @Override
    public abstract int getItemViewType(int position);

    public abstract <T extends DataBinder> T getDataBinder(int viewType);

    public abstract int getPosition(DataBinder binder, int binderPosition);

    public abstract int getBinderPosition(int position);

    // add the UI elements to the recyclerViewer
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        int binderPosition = getBinderPosition(position);
        getDataBinder(holder.getItemViewType()).bindViewHolder(holder, binderPosition);
    }

}
