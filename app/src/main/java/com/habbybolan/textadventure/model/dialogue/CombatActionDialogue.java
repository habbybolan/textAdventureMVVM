package com.habbybolan.textadventure.model.dialogue;

import com.habbybolan.textadventure.R;

import org.json.JSONException;
import org.json.JSONObject;

public class CombatActionDialogue implements DialogueType {

    private String attackerName;
    private String targetName;
    private String actionName;

    public CombatActionDialogue(String attackerName, String targetName, String actionName) {
        this.attackerName = attackerName;
        this.targetName = targetName;
        this.actionName = actionName;
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
        return jsonObject;
    }
}
