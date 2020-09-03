package com.habbybolan.textadventure.view.dialogueAdapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.DialogueDetailsBinding;
import com.habbybolan.textadventure.model.dialogue.Dialogue;

import java.util.ArrayList;
import java.util.List;
/*
binder for the normal dialogue text to add to RecyclerViewer
 */
public class DialogueBinding extends DataBinder<DialogueBinding.ViewHolder> {

    private List<String> dialogue = new ArrayList<>();

    DialogueBinding(DataBindAdapter dataBindingAdapter) {
        super(dataBindingAdapter);
    }

    @Override
    public ViewHolder newViewHolder(ViewGroup parent) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        DialogueDetailsBinding bindingDialogue = DataBindingUtil.inflate(layoutInflater, R.layout.dialogue_details, parent, false);
        return new ViewHolder(bindingDialogue);
    }

    @Override
    public void bindViewHolder(ViewHolder holder, int position) {
        String text = dialogue.get(position);
        holder.bind(text);
    }

    @Override
    public int getItemCount() {
        return dialogue.size();
    }

    public void setDataSet(List<String> dataSet) {
        dialogue.addAll(dataSet);
    }

    void addNewDialogue(Dialogue newDialogue) {
        dialogue.add(newDialogue.getDialogue());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        DialogueDetailsBinding binding;
        ViewHolder(DialogueDetailsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(String text) {
            binding.setNewDialogue(text);
            binding.executePendingBindings();
        }
    }

}
