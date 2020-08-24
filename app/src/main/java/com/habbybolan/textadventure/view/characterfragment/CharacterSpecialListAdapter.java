package com.habbybolan.textadventure.view.characterfragment;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.CharacterDotSpecialDetailsBinding;

import java.util.List;

public class CharacterSpecialListAdapter extends RecyclerView.Adapter<CharacterSpecialListAdapter.ViewHolder> {

    private List<String> specialList;

    public CharacterSpecialListAdapter(List<String> specialList) {
        this.specialList = specialList;
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
    public CharacterSpecialListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        CharacterDotSpecialDetailsBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.character_dot_special_details, parent, false);
        return new CharacterSpecialListAdapter.ViewHolder(binding);
    }

    // add the UI elements to the recyclerViewer
    @Override
    public void onBindViewHolder(CharacterSpecialListAdapter.ViewHolder holder, int position) {
        String special = specialList.get(position);
        holder.bind(special);
    }

    @Override
    public int getItemCount() {
        return specialList.size();
    }

    // adds new dot effect to the list if it doesn't already exist
    public void addNewSpecial(String special) {
        if (!specialList.contains(special)) {
            specialList.add(special);
            notifyItemInserted(specialList.size() - 1);
        }
    }

    // deleted a dot effect from the list
    public void removeSpecial(String deleteSpecial) {
        for (int i = 0; i < specialList.size(); i++) {
            // check if the dot exists in the list
            if (deleteSpecial.equals(specialList.get(i))) {
                specialList.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }
}
