package com.habbybolan.textadventure.model.inventory.weapon;

import com.habbybolan.textadventure.model.inventory.Action;

public abstract class WeaponAction extends Action {


    protected Weapon parentWeapon;

    public Weapon getParentWeapon() {
        return parentWeapon;
    }
}
