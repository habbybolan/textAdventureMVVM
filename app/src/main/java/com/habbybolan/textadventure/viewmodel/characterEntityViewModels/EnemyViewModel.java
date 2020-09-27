package com.habbybolan.textadventure.viewmodel.characterEntityViewModels;

import com.habbybolan.textadventure.model.characterentity.Enemy;

public class EnemyViewModel extends CharacterEntityViewModel {

    private Enemy enemy;

    public EnemyViewModel(Enemy enemy) {
        this.enemy = enemy;
        this.characterEntity = enemy;
    }

    public Enemy getEnemy() {
        return enemy;
    }
}
