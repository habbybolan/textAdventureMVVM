package com.habbybolan.textadventure.model.encounter;

import com.habbybolan.textadventure.model.locations.Outdoor;

import org.json.JSONException;
import org.json.JSONObject;

public class ShopModel extends EncounterModel {

    /**
     * Creates a single line dialogue JSON encounter of a Shop encounter.
     * @param dialogue  The single line of dialogue.
     * @return          The JSON encounter of the Shop Encounter.
     */
    public JSONObject createShopEncounter(String[] dialogue) throws JSONException {
        JSONObject randomBenefitJSON = new JSONObject();
        // store the type for random benefit encounter
        randomBenefitJSON.put("type", Outdoor.SHOP_TYPE);
        JSONObject dialogueJSON = new JSONObject();
        // store dialogue to be displayed
        dialogueJSON.put("dialogue", dialogue);
        randomBenefitJSON.put("dialogue", createDialogue(dialogue));
        return randomBenefitJSON;
    }
}
