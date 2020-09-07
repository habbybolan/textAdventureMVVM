package com.habbybolan.textadventure.view.dialogueAdapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.model.dialogue.DialogueTypes;
import com.habbybolan.textadventure.view.dialogueAdapter.binding.DialogueBinding;
import com.habbybolan.textadventure.view.dialogueAdapter.binding.EffectDialogueBinding;
import com.habbybolan.textadventure.view.dialogueAdapter.binding.HealthDialogueBinding;
import com.habbybolan.textadventure.view.dialogueAdapter.binding.InventoryDialogueBinding;
import com.habbybolan.textadventure.view.dialogueAdapter.binding.ManaDialogueBinding;

import java.util.ArrayList;

public class DialogueAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<DialogueTypes> dialogueList;
    private DialogueBinding dialogueBinding;
    private EffectDialogueBinding effectDialogueBinding;
    private HealthDialogueBinding healthDialogueBinding;
    private ManaDialogueBinding manaDialogueBinding;
    private InventoryDialogueBinding inventoryDialogueBinding;

    public DialogueAdapter() {
        dialogueList = new ArrayList<>();
        dialogueBinding = new DialogueBinding(this);
        effectDialogueBinding = new EffectDialogueBinding(this);
        healthDialogueBinding = new HealthDialogueBinding(this);
        manaDialogueBinding = new ManaDialogueBinding(this);
        inventoryDialogueBinding = new InventoryDialogueBinding(this);
    }

    // Return the total item count of DataBinders
    @Override
    public int getItemCount() {
        return dialogueList != null ? dialogueList.size() : 0;
    }

    // Define the mapping logic between the adapter position and view type.
    @Override
    public int getItemViewType(int position) {
        return dialogueList.get(position).getViewType();
    }

    // Return the DataBinder instance based on the view type
    public DataBinder getDataBinder(int viewType) {
        if (viewType == R.layout.dialogue_details) return dialogueBinding;
        else if (viewType == R.layout.dialogue_effect_details) return effectDialogueBinding;
        else if (viewType == R.layout.dialogue_health_details) return healthDialogueBinding;
        else if (viewType == R.layout.dialogue_mana_details) return manaDialogueBinding;
        else return inventoryDialogueBinding;
    }

    /*// Define convert logic to the adapter position from the position in the specified DataBinder
    public int getPosition(DataBinder binder, int binderPosition) {
        return 0;
    }*/

    // Define convert logic to the position in the DataBinder from the adapter position
    public int getBinderPosition(DataBinder binder) {
        return binder.getItemCount()-1;
    }

    // add the UI elements to the recyclerViewer
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        DataBinder binder = getDataBinder(holder.getItemViewType());
        binder.addDialogue(dialogueList.get(position));
        int binderPosition = getBinderPosition(binder);
        binder.bindViewHolder(holder, binderPosition);
    }

    // inflate the layout
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return getDataBinder(viewType).newViewHolder(parent);
    }

    // add a new normal dialogue
    void addNewDialogue(DialogueTypes dialogue) {
        dialogueList.add(0, dialogue);
        notifyItemInserted(0);
    }
}
