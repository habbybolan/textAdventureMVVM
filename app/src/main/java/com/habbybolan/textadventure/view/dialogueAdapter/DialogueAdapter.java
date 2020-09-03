package com.habbybolan.textadventure.view.dialogueAdapter;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.model.dialogue.Dialogue;
import com.habbybolan.textadventure.model.dialogue.EffectDialogue;

import java.util.ArrayList;
import java.util.List;

public class DialogueAdapter extends DataBindAdapter {

    private DialogueBinding dialogueBinding;
    private EffectDialogueBinding effectDialogueBinding;
    private List<Integer> layoutsAtPosition = new ArrayList<>();
    public DialogueAdapter() {
        dialogueBinding = new DialogueBinding(this);
        effectDialogueBinding = new EffectDialogueBinding(this);
    }

    // Return the total item count of DataBinders
    @Override
    public int getItemCount() {
        return dialogueBinding.getItemCount()+effectDialogueBinding.getItemCount();
    }

    // Define the mapping logic between the adapter position and view type.
    @Override
    public int getItemViewType(int position) {
        return R.layout.dialogue_details;
    }

    // Return the DataBinder instance based on the view type
    @Override
    public DataBinder getDataBinder(int viewType) {
        return dialogueBinding;
    }

    // Define convert logic to the adapter position from the position in the specified DataBinder
    @Override
    public int getPosition(DataBinder binder, int binderPosition) {
        return 0;
        // todo: what is this for??
    }

    // Define convert logic to the position in the DataBinder from the adapter position
    @Override
    public int getBinderPosition(int position) {
        return dialogueBinding.getItemCount()-1;
    }

    // add a new normal dialogue
    public void addNewDialogue(Dialogue dialogue) {
        layoutsAtPosition.add(R.layout.dialogue_details);
        dialogueBinding.addNewDialogue(dialogue);
        notifyItemInserted(getItemCount()-1);
    }

    // add an effect dialogue (Dot/Special effect)
    public void addNewEffectDialogue(EffectDialogue dialogue) {
        layoutsAtPosition.add(R.layout.dialogue_effect_details);
        effectDialogueBinding.addNewEffectDialogue(dialogue);
        notifyItemInserted(getItemCount()-1);
    }
}
