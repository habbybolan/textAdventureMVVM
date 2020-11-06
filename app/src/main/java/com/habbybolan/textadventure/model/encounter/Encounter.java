package com.habbybolan.textadventure.model.encounter;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Model for an encounter that holds the encounter JSON, its dialogue, and its type.
 */
public class Encounter {

    // the type of encounter
    private String type;
    // dialogue of the encounter
    private JSONObject dialogue;
    // the entire encounter JSON
    private JSONObject encounterJSON;


    public Encounter(JSONObject encounterJSON) {
        this.encounterJSON = encounterJSON;
        try {
            type = encounterJSON.getString(TYPE);
            dialogue = encounterJSON.getJSONObject(DIALOGUE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getEncounterJSON() {
        return encounterJSON;
    }
    public String getType() {
        return type;
    }
    public JSONObject getDialogue() {
        return dialogue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o.getClass() != getClass()) return false;
        Encounter e = (Encounter) o;
        return e.encounterJSON.equals(encounterJSON);
    }

    public static String TYPE = "type";
    public static String DIALOGUE = "dialogue";
}
