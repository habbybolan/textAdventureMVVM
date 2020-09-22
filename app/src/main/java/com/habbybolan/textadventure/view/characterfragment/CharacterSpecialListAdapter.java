package com.habbybolan.textadventure.view.characterfragment;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.CharacterDotSpecialDetailsBinding;
import com.habbybolan.textadventure.model.effects.SpecialEffect;

import java.util.List;

public class CharacterSpecialListAdapter extends RecyclerView.Adapter<CharacterSpecialListAdapter.ViewHolder> {

    private List<SpecialEffect> specialList;

    public CharacterSpecialListAdapter(List<SpecialEffect> specialList) {
        this.specialList = specialList;
    }

    // holds reference of the binding
    public class ViewHolder extends RecyclerView.ViewHolder{

        private final CharacterDotSpecialDetailsBinding binding;

        public ViewHolder(CharacterDotSpecialDetailsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(SpecialEffect special) {
            binding.setType(special.getType());
            binding.setDuration(String.valueOf(special.getDuration()));
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
        SpecialEffect special = specialList.get(position);
        holder.bind(special);
    }

    @Override
    public int getItemCount() {
        return specialList != null ? specialList.size() : 0;
    }

}
