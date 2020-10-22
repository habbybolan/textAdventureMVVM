package com.habbybolan.textadventure.view.inventoryinfo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.FragmentInventoryInfoBinding;
import com.habbybolan.textadventure.model.inventory.Ability;
import com.habbybolan.textadventure.model.inventory.Inventory;
import com.habbybolan.textadventure.model.inventory.Item;
import com.habbybolan.textadventure.model.inventory.weapon.Attack;
import com.habbybolan.textadventure.model.inventory.weapon.SpecialAttack;
import com.habbybolan.textadventure.model.inventory.weapon.Weapon;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 */
public class InventoryInfoFragment extends Fragment {

    private FragmentInventoryInfoBinding binding;
    public static final String INVENTORY_SERIALIZED = "INVENTORY_SERIALIZED";
    public static final String COST = "cost";
    public static final String POSITION = "position";

    private String inventoryString;


    private InventoryInfoFragment() {
        // Required empty public constructor
    }

    private InventoryInfoFragment(String inventoryString) {
        this.inventoryString = inventoryString;
    }


    // TODO: Rename and change types and number of parameters
    public static InventoryInfoFragment newInstance(String inventoryString) {
        return new InventoryInfoFragment(inventoryString);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_inventory_info, container, false);
        try {
            setInventory();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return binding.getRoot();
    }

    // sets up the serialized inventory object
    private void setInventory() throws JSONException {
        JSONObject jsonObject = new JSONObject(inventoryString);
        String type = jsonObject.getString(Inventory.INVENTORY_TYPE);
        switch (type) {
            case Inventory.TYPE_ABILITY:
                setAbilityInfo(new Ability(inventoryString));
                break;
            case Inventory.TYPE_ITEM:
                setItemInfo(new Item(inventoryString));
                break;
            case Inventory.TYPE_WEAPON:
                setWeaponInfo(new Weapon(inventoryString));
                break;
            case Inventory.TYPE_ATTACK:
                setAttackInfo(new Attack(inventoryString));
                break;
            case Inventory.TYPE_S_ATTACK:
                setSpecialAttackInfo(new SpecialAttack(inventoryString));
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
}
