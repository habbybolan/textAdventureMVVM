package com.habbybolan.textadventure.model;

import android.content.Context;

import org.json.JSONObject;

/*
creates new encounters, retrieved from JSON asset file
   holds logic for continuing and multi-levelled encounter
 */
public class MainGameModel {

    private JSONObject encounter;

    Context context;
    public MainGameModel(Context context) {
        this.context = context;
    }

    // creates an completely new JSONObject encounter
    public String setNewEncounter() {
        return "";
    }

    // continues into the encounter sub-encounters, retrieving the next sub-encounter
    public String continueSubEncounter() {
        return "";
    }


    public JSONObject getEncounter() {
        return encounter;
    }
}
