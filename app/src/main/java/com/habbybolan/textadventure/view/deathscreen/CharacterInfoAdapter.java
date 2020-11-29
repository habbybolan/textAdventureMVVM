package com.habbybolan.textadventure.view.deathscreen;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.CharacterInfoDetailsBinding;

import java.util.ArrayList;

public class CharacterInfoAdapter extends RecyclerView.Adapter<CharacterInfoAdapter.ViewHolder> {

    private ArrayList<String> text;

    CharacterInfoAdapter(ArrayList<String> text) {
        this.text = text;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        CharacterInfoDetailsBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.character_info_details, parent, false);
        return new CharacterInfoAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(text.get(position));
    }

    @Override
    public int getItemCount() {
        return text.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        CharacterInfoDetailsBinding binding;

        ViewHolder(CharacterInfoDetailsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(String text) {
            binding.setText(text);
            binding.executePendingBindings();
        }
    }
}
