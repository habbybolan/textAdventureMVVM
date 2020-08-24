package com.habbybolan.textadventure.view.characterfragment;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.CharacterDotSpecialDetailsBinding;

import java.util.List;

public class CharacterDotListAdapter extends RecyclerView.Adapter<CharacterDotListAdapter.ViewHolder>{

    private List<String> dotList;

    public CharacterDotListAdapter(List<String> dotList) {
        this.dotList = dotList;
    }

    // holds reference of the binding
    public class ViewHolder extends RecyclerView.ViewHolder{

        private final CharacterDotSpecialDetailsBinding binding;

        public ViewHolder(CharacterDotSpecialDetailsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(String dialogue) {
            binding.setDotType(dialogue);
            binding.executePendingBindings();
        }
    }

    // inflate the layout
    @NonNull
    @Override
    public CharacterDotListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        CharacterDotSpecialDetailsBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.character_dot_special_details, parent, false);
        return new CharacterDotListAdapter.ViewHolder(binding);
    }

    // add the UI elements to the recyclerViewer
    @Override
    public void onBindViewHolder(CharacterDotListAdapter.ViewHolder holder, int position) {
        String dot = dotList.get(position);
        holder.bind(dot);
    }

    @Override
    public int getItemCount() {
        return dotList.size();
    }

    // adds new dot effect to the list if it doesn't already exist
    public void addNewDot(String dot) {
        if (!dotList.contains(dot)) {
            dotList.add(dot);
            notifyItemInserted(dotList.size() - 1);
        }
    }

    // deleted a dot effect from the list
    public void removeDot(String deleteDot) {
        for (int i = 0; i < dotList.size(); i++) {
            // check if the dot exists in the list
            if (deleteDot.equals(dotList.get(i))) {
                dotList.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

}
