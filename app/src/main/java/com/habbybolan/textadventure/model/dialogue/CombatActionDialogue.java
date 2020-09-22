package com.habbybolan.textadventure.model.dialogue;

import com.habbybolan.textadventure.R;

import org.json.JSONException;
import org.json.JSONObject;

public class CombatActionDialogue implements DialogueType {

    private String attackerName;
    private String targetName;
    private String actionName;
    private int amount;

    public CombatActionDialogue(String attackerName, String targetName, String actionName, int amount) {
        this.attackerName = attackerName;
        this.targetName = targetName;
        this.actionName = actionName;
        this.amount = amount;
    }

    public String getAttackerName() {
        return attackerName;
    }
    public String getTargetName() {
        return targetName;
    }
    public String getActionName() {
        return actionName;
    }
    public int getAmount() {
        return amount;
    }

    @Override
    public int getViewType() {
        return R.layout.dialogue_combat_action_details;
    }

    @Override
    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(DIALOGUE_TYPE, TYPE_COMBAT_ACTION);
        jsonObject.put(TARGET, targetName);
        jsonObject.put(ATTACKER, attackerName);
        jsonObject.put(ACTION, actionName);
        jsonObject.put(AMOUNT, amount);
        return jsonObject;
    }
}
