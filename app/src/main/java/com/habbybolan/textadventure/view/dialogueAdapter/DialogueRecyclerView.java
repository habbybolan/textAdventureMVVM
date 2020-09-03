package com.habbybolan.textadventure.view.dialogueAdapter;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.habbybolan.textadventure.model.dialogue.Dialogue;
import com.habbybolan.textadventure.viewmodel.CharacterViewModel;

/*
creates the dialogue recyclerView, updating on each character action
 connected to a specific fragment/activity
 */
public class DialogueRecyclerView {

    private DialogueAdapter adapter;
    public DialogueRecyclerView(Context context, RecyclerView recyclerView, CharacterViewModel characterVM) {

        // set the layout manager to position the items
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        this.adapter = new DialogueAdapter();
        recyclerView.setAdapter(adapter);

        // set up OnCLickListeners for changes in player Character

        /*
        // observer for
        Observable.OnPropertyChangedCallback callback = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                Dialogue dialogue = new Dialogue(trapEncounterVM.getNewDialogue().get());
                rv.addDialogue(dialogue);
            }
        };
        characterVM.getNewDialogue().addOnPropertyChangedCallback(callback);*/
    }

    // called by fragment/activity to add dialogue to the active dialogue RV
    public void addDialogue(Dialogue dialogue) {
        adapter.addNewDialogue(dialogue);
    }
}
