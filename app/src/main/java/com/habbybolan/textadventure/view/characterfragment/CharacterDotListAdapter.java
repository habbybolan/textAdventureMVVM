package com.habbybolan.textadventure.view.characterfragment;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.CharacterDotSpecialDetailsBinding;
import com.habbybolan.textadventure.model.effects.Dot;

import java.util.List;

public class CharacterDotListAdapter extends RecyclerView.Adapter<CharacterDotListAdapter.ViewHolder>{

    private List<Dot> dotList;

    CharacterDotListAdapter(List<Dot> dotList) {
        this.dotList = dotList;
    }

    // holds reference of the binding
    static class ViewHolder extends RecyclerView.ViewHolder{

        private final CharacterDotSpecialDetailsBinding binding;

        ViewHolder(CharacterDotSpecialDetailsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Dot dot) {
            binding.setType(dot.getType());
            binding.setDuration(String.valueOf(dot.getDuration()));
            binding.executePendingBindings();
        }
    }

    // inflate the layout
    @NonNull
    @Override
    public CharacterDotListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        CharacterDotSpecialDetailsBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.character_dot_special_details, parent, false);
        return new ViewHolder(binding);
    }

    // add the UI elements to the recyclerViewer
    @Override
    public void onBindViewHolder(CharacterDotListAdapter.ViewHolder holder, int position) {
        Dot dot = dotList.get(position);
        holder.bind(dot);
    }

    @Override
    public int getItemCount() {
        return dotList != null ? dotList.size() : 0;
    }

    /*
    // adds new dot effect to the list if it doesn't already exist
    void newDotAdded() {
        notifyItemInserted(dotList.size() - 1);
    }
    // deleted a dot effect from the list
    void dotRemoved(int index) {
        notifyItemRemoved(index);
    }*/

}
