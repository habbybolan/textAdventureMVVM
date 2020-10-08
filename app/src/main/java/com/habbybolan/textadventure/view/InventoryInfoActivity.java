package com.habbybolan.textadventure.view;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.ActivityInventoryInfoBinding;
import com.habbybolan.textadventure.model.inventory.Ability;
import com.habbybolan.textadventure.model.inventory.Inventory;
import com.habbybolan.textadventure.model.inventory.Item;
import com.habbybolan.textadventure.model.inventory.weapon.Attack;
import com.habbybolan.textadventure.model.inventory.weapon.SpecialAttack;
import com.habbybolan.textadventure.model.inventory.weapon.Weapon;
import com.habbybolan.textadventure.view.encounter.CombatFragment;

public class InventoryInfoActivity extends AppCompatActivity {

    ActivityInventoryInfoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove toolbar
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) getSupportActionBar().hide();

        binding = DataBindingUtil.setContentView(this, R.layout.activity_inventory_info);
        String stringInventory = getIntent().getStringExtra(CombatFragment.INVENTORY_SERIALIZED);
        String type = getIntent().getStringExtra(CombatFragment.TYPE);

        if (type == null || stringInventory == null) throw new IllegalArgumentException();
        setInventoryObject(stringInventory, type);
    }

    // sets up the serialized inventory object
    private void setInventoryObject(String stringInventory, String type) {
        switch (type) {
            case Inventory.TYPE_ABILITY:
                setAbilityInfo(new Ability(stringInventory));
                break;
            case Inventory.TYPE_ITEM:
                setItemInfo(new Item(stringInventory));
                break;
            case Inventory.TYPE_WEAPON:
                setWeaponInfo(new Weapon(stringInventory));
                break;
            case Inventory.TYPE_ATTACK:
                setAttackInfo(new Attack(stringInventory));
                break;
            case Inventory.TYPE_S_ATTACK:
                setSpecialAttackInfo(new SpecialAttack(stringInventory));
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    // displays all info for the special attack
    private void setSpecialAttackInfo(SpecialAttack specialAttack) {
        // top info
        binding.setName(specialAttack.getName());
        binding.setImageIconResource(specialAttack.getPictureResource());
        // todo: special attack info
    }

    // displays all info for the attack
    private void setAttackInfo(Attack attack) {
        // top info
        binding.setName(attack.getName());
        binding.setImageIconResource(attack.getPictureResource());
        // todo: attack info
    }

    // displays all info for the weapon
    private void setWeaponInfo(Weapon weapon) {
        // top info
        binding.setName(weapon.getName());
        binding.setImageIconResource(weapon.getPictureResource());
        // todo: weapon info
    }

    // displays all info for the item
    private void setItemInfo(Item item) {
        // top info
        binding.setName(item.getName());
        binding.setImageIconResource(item.getPictureResource());
        // todo: item info
    }

    // displays all info for the ability
    private void setAbilityInfo(Ability ability) {
        // top info
        binding.setName(ability.getName());
        binding.setImageIconResource(ability.getPictureResource());
        // todo: ability info
    }

    @Override
    // destroy activity on back pressed
    public void onBackPressed() {
        finish();
    }
}
