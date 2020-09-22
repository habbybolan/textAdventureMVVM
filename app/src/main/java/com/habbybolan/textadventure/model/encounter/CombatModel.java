package com.habbybolan.textadventure.model.encounter;

import com.habbybolan.textadventure.model.characterentity.CharacterEntity;

import java.util.ArrayList;
import java.util.Random;

/*
model that does the business logic of the combat encounter
 */
public class CombatModel {

    // a speed check that returns true if speed * multiplier is above a random value
    public boolean runSpeedCheck(int speed) {
        int RUN_SPEED_MULTIPLIER = 5;
        Random rand = new Random();
        int numToCheck = rand.nextInt(100);
        return speed * RUN_SPEED_MULTIPLIER > numToCheck;
    }

    // helper for setCombatOrdering - bubble sort to sort both lists
    // index = 0 if setting up initial combat ordering, 1 if re-sorting the combat ordering
    public void sortCombatOrdering(int index, ArrayList<CharacterEntity> combatOrder) {
        // bubble sort combat ordering
        for (int i = index; i < combatOrder.size() - 1; i++) {
            for (int j = index; j < combatOrder.size() - i - 1 + index; j++) {
                if (combatOrder.get(j).getSpeed() < combatOrder.get(j + 1).getSpeed()) {
                    CharacterEntity temp = combatOrder.get(j + 1);
                    combatOrder.set(j + 1, combatOrder.get(j));
                    combatOrder.set(j, temp);
                }
            }
        }
    }

    // take characterEntity from combatOrderCurr and add to combatOrderLast
        // if combatOrderCurr is empty, then move all from combatOrder
    public void moveEntityToBackOfCombatOrder(ArrayList<CharacterEntity> combatOrderCurr, ArrayList<CharacterEntity> combatOrderNext, ArrayList<CharacterEntity> combatOrderLast) {
        CharacterEntity tempEntity = combatOrderCurr.get(0);
        combatOrderCurr.remove(0);
        combatOrderLast.add(tempEntity);

        if (combatOrderCurr.isEmpty()) {
            combatOrderCurr.addAll(combatOrderNext);
            combatOrderLast.clear();
        }
    }
}
