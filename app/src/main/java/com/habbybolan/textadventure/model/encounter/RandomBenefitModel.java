package com.habbybolan.textadventure.model.encounter;

import android.content.Context;

import com.habbybolan.textadventure.model.inventory.Inventory;
import com.habbybolan.textadventure.model.locations.Outdoor;
import com.habbybolan.textadventure.repository.database.DatabaseAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;
import java.util.concurrent.ExecutionException;

/*
holds the business logic for the Random Benefit encounter
 */
public class RandomBenefitModel extends EncounterModel{

    public RandomBenefitModel() {}

    // finds a random Inventory loot
    public Inventory getRandomInventory(Context context, int encounterState) {
        // todo: 
        Random rand = new Random();
        DatabaseAdapter adapter = new DatabaseAdapter(context);
        // randomly choose Inventory object, Weapon/Item/Ability
        int val = rand.nextInt(3);
        Inventory inventory = null;
        try {
            switch (val) {
                case 0:
                    inventory = adapter.getRandomWeapons(1).get(0);
                    break;
                case 1:
                    inventory = adapter.getRandomAbilities(1).get(0);
                    break;
                case 2:
                    inventory = adapter.getRandomItems(1).get(0);
                    break;
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return inventory;
    }

    /**
     * Creates a single line dialogue JSON encounter of a Random Benefit encounter.
     * @param dialogue  The single line of dialogue.
     * @return          The JSON encounter of the Random Benefit Encounter.
     */
    public JSONObject createRandomBenefitEncounter(String[] dialogue) throws JSONException {
        JSONObject randomBenefitJSON = new JSONObject();
        // store the type for random benefit encounter
        randomBenefitJSON.put("type", Outdoor.RANDOM_BENEFIT_TYPE);
        // store dialogue to be displayed
        randomBenefitJSON.put("dialogue", createDialogue(dialogue));
        return randomBenefitJSON;
    }
}
