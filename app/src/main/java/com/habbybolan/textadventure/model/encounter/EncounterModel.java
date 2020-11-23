package com.habbybolan.textadventure.model.encounter;

import org.json.JSONException;
import org.json.JSONObject;

abstract class EncounterModel {

    /**
     * Create the dialogue JSONObject of an encounter.
     * @param dialogue  The arrayList of dialogue lines to add to the dialogue object.
     * @return          The JSONObject of the dialogue.
     */
    static JSONObject createDialogue(String[] dialogue) throws JSONException {
        JSONObject dialogueJSON = new JSONObject();
        dialogueJSON.put("dialogue", dialogue[0]);
        // store dialogue to be displayed
        nextDialogue(1, dialogue, dialogueJSON);
        return dialogueJSON;
    }

    /**
     * Recursive helper for creating the nested JSONObject dialogue structure.
     * @param i                 Index of the current dialogue line in dialogue.
     * @param dialogue          ArrayList holding all dialogue strings.
     * @param dialogueJSON      The current Dialogue JSONObject nested inside the main JSONObject
     */
    private static void nextDialogue(int i, String[] dialogue, JSONObject dialogueJSON) throws JSONException {
        if (i < dialogue.length) {
            JSONObject tempDialogueJSON = new JSONObject();
            tempDialogueJSON.put("dialogue", dialogue[i]);
            dialogueJSON.put("next", tempDialogueJSON);
            nextDialogue(++i, dialogue, tempDialogueJSON);
        }
    }

}
