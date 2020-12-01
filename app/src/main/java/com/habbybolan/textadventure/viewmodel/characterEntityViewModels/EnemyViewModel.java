package com.habbybolan.textadventure.viewmodel.characterEntityViewModels;

import android.content.Context;

import com.habbybolan.textadventure.model.characterentity.Enemy;
import com.habbybolan.textadventure.repository.database.CreateEnemy;

public class EnemyViewModel extends CharacterEntityViewModel {

    private Enemy enemy;

    public EnemyViewModel(String jsonString) {
        enemy = new Enemy(jsonString);
        characterEntity = enemy;
    }

    public EnemyViewModel(String type, int tier, int numStatPoints, int ID, Context context) {
        CreateEnemy createEnemy = new CreateEnemy(context);
        enemy = createEnemy.getEnemy(type, tier, numStatPoints);
        characterEntity = enemy;
        enemy.setID(ID);
        createEnemy.closeDatabase();
    }

    public Enemy getEnemy() {
        return enemy;
    }
    public int getID() {
        return enemy.getId();
    }
}
