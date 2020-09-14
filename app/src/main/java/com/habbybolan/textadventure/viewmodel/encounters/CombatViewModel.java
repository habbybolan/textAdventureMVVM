package com.habbybolan.textadventure.viewmodel.encounters;

import android.content.Context;

import com.habbybolan.textadventure.model.dialogue.DialogueType;
import com.habbybolan.textadventure.model.encounter.CombatModel;
import com.habbybolan.textadventure.model.inventory.Inventory;
import com.habbybolan.textadventure.viewmodel.CharacterViewModel;
import com.habbybolan.textadventure.viewmodel.MainGameViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
/*
View model to deal with data of the combat encounter
 */
public class CombatViewModel extends EncounterViewModel {
    private MainGameViewModel mainGameVM;
    private CharacterViewModel characterVM;
    private JSONObject encounter;
    private Context context;
    private CombatModel combatModel;

    public static final int firstState = 1;
    public static final int secondState = 2;
    public static final int thirdState = 3;
    public static final int fourthState = 4;

    private Inventory inventoryToRetrieve = null;


    @Override
    void saveEncounter(ArrayList<DialogueType> dialogueList) {

    }

    @Override
    void setSavedData() throws JSONException, ExecutionException, InterruptedException {

    }
}
