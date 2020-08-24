package com.habbybolan.textadventure.view.encounters.trapencounter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.TrapDialogueDetailsBinding;

import java.util.List;

public class TrapDialogueListAdapter extends RecyclerView.Adapter<TrapDialogueListAdapter.ViewHolder> {

    private List<String> dialogue;
    public TrapDialogueListAdapter(List<String> dialogue) {
        this.dialogue = dialogue;
    }

    // holds reference of the binding
    public class ViewHolder extends RecyclerView.ViewHolder{

        private final TrapDialogueDetailsBinding binding;

        public ViewHolder(TrapDialogueDetailsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(String dialogue) {
            binding.setNewDialogue(dialogue);
            binding.executePendingBindings();
        }
    }

    // inflate the layout
    @NonNull
    @Override
    public TrapDialogueListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        TrapDialogueDetailsBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.trap_dialogue_details, parent, false);
        return new ViewHolder(binding);
    }

    // add the UI elements to the recyclerViewer
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String dialogue = this.dialogue.get(position);
        holder.bind(dialogue);
    }

    @Override
    public int getItemCount() {
        return dialogue.size();
    }

    // adds new dialogue to the list
    public void addNewDialogue(String newDialogue) {
        dialogue.add(newDialogue);
        notifyItemInserted(dialogue.size()-1);
    }

}
