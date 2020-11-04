package com.habbybolan.textadventure.viewmodel;

import androidx.databinding.ObservableField;

import com.habbybolan.textadventure.model.InventoryInfoModel;
import com.habbybolan.textadventure.model.inventory.Ability;
import com.habbybolan.textadventure.model.inventory.Inventory;
import com.habbybolan.textadventure.model.inventory.Item;
import com.habbybolan.textadventure.model.inventory.weapon.Attack;
import com.habbybolan.textadventure.model.inventory.weapon.SpecialAttack;
import com.habbybolan.textadventure.model.inventory.weapon.Weapon;

import org.json.JSONException;
import org.json.JSONObject;

public class InventoryInfoViewModel {

    private InventoryInfoViewModel() {}

    private static InventoryInfoViewModel instance;
    public static void newInstance() {
        instance = new InventoryInfoViewModel();
    }
    public static InventoryInfoViewModel getInstance() {
        if (instance == null) throw new IllegalArgumentException("Must initiate the view model first");
        return instance;
    }

    // Observer for a new inventory info object to look at.
    private ObservableField<String> inventoryInfoObservable = new ObservableField<>();
    public ObservableField<String> getInventoryInfoObservable() {
        return inventoryInfoObservable;
    }

    public void setInventoryInfoObservable(String inventoryString) {
        inventoryInfoObservable.set(inventoryString);
    }


    public Inventory getInventory() {
        return getInventoryFromString(inventoryInfoObservable.get());
    }


    private Inventory getInventoryFromString(String inventoryString) {
        String type = "";
        try {
            JSONObject jsonObject = new JSONObject(inventoryString);
            type = jsonObject.getString(Inventory.INVENTORY_TYPE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        switch (type) {
            case Inventory.TYPE_ABILITY:
                return new Ability(inventoryString);
            case Inventory.TYPE_ITEM:
                return new Item(inventoryString);
            case Inventory.TYPE_WEAPON:
                return new Weapon(inventoryString);
            case Inventory.TYPE_ATTACK:
                return new Attack(inventoryString);
            case Inventory.TYPE_S_ATTACK:
                return new SpecialAttack(inventoryString);
            default:
                throw new IllegalArgumentException();
        }
    }

    public String getAbilityInfo(Ability ability) {
        return InventoryInfoModel.getAbilityInfo(ability);
    }

    public String getItemInfo(Item item) {
        return InventoryInfoModel.getItemInfo(item);
    }

    public String getWeaponInfo(Weapon weapon) {
        return InventoryInfoModel.getWeaponInfo(weapon);
    }

    public String getAttackInfo(Attack attack) {
        return InventoryInfoModel.getAttackInfo(attack);
    }

    public String getSpecialAttackInfo(SpecialAttack specialAttack) {
        return InventoryInfoModel.getSpecialAttackInfo(specialAttack);
    }

    private ObservableField<String> observeInventoryChange = new ObservableField<>();
    public String getObserveInventoryChange() {
        return String.valueOf(observeInventoryChange);
    }
    public void setObserveInventoryChange(String inventoryString) {

    }
}
