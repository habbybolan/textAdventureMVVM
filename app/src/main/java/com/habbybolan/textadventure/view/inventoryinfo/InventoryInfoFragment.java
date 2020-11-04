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
import com.habbybolan.textadventure.databinding.InventorySnippetBinding;
import com.habbybolan.textadventure.model.inventory.Ability;
import com.habbybolan.textadventure.model.inventory.Inventory;
import com.habbybolan.textadventure.model.inventory.Item;
import com.habbybolan.textadventure.model.inventory.weapon.Attack;
import com.habbybolan.textadventure.model.inventory.weapon.SpecialAttack;
import com.habbybolan.textadventure.model.inventory.weapon.Weapon;
import com.habbybolan.textadventure.viewmodel.InventoryInfoViewModel;

import org.json.JSONException;

/**
 *
 */
public class InventoryInfoFragment extends Fragment {

    private FragmentInventoryInfoBinding binding;
    public static final String INVENTORY_SERIALIZED = "INVENTORY_SERIALIZED";
    public static final String COST = "cost";
    public static final String POSITION = "position";

    private InventoryInfoViewModel inventoryInfoVM = InventoryInfoViewModel.getInstance();

    private InventoryInfoFragment() {}

    // TODO: Rename and change types and number of parameters
    public static InventoryInfoFragment newInstance() {
        return new InventoryInfoFragment();
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
        setInventory();
        return binding.getRoot();
    }

    /**
     * Calls the proper method to set up the UI info on the specific stored inventory.
     */
    private void setInventory() {
        Inventory inventory = inventoryInfoVM.getInventory();
        if (inventory.isAbility())
            setAbilityInfo((Ability) inventory);
        else if (inventory.isItem())
            setItemInfo((Item) inventory);
        else if (inventory.isWeapon())
            setWeaponInfo((Weapon) inventory);
        else if (inventory.isAttack())
            setAttackInfo((Attack) inventory);
        else if (inventory.isSpecialAttack())
            setSpecialAttackInfo((SpecialAttack) inventory);
        else
            throw new IllegalArgumentException();
    }

    // displays all info for the special attack
    private void setSpecialAttackInfo(final SpecialAttack specialAttack) {
        // top info
        binding.setName(specialAttack.getName());
        binding.setImageIconResource(specialAttack.getPictureResource());
        binding.setInfo(inventoryInfoVM.getSpecialAttackInfo(specialAttack));

        // ability inventory snippet
        if (specialAttack.getAbility() != null) {
            View view = getLayoutInflater().inflate(R.layout.inventory_snippet, null);
            InventorySnippetBinding snippetBinding = DataBindingUtil.bind(view);
            snippetBinding.setInventoryName(specialAttack.getAbility().getName());
            snippetBinding.setInventoryPic(specialAttack.getAbility().getAbilityID());
            binding.inventorySnippet.addView(view);
            snippetBinding.inventoryInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        inventoryInfoVM.setInventoryInfoObservable(specialAttack.getAbility().serializeToJSON().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    // displays all info for the attack
    private void setAttackInfo(final Attack attack) {
        // top info
        binding.setName(attack.getName());
        binding.setImageIconResource(attack.getPictureResource());
        binding.setInfo(inventoryInfoVM.getAttackInfo(attack));
    }

    // displays all info for the weapon
    private void setWeaponInfo(final Weapon weapon) {
        // top info
        binding.setName(weapon.getName());
        binding.setImageIconResource(weapon.getPictureResource());
        binding.setInfo(inventoryInfoVM.getWeaponInfo(weapon));
        if (weapon.getAttack() != null) {
            // attack inventory snippet
            View view = getLayoutInflater().inflate(R.layout.inventory_snippet, null);
            InventorySnippetBinding snippetBinding = DataBindingUtil.bind(view);
            snippetBinding.setInventoryName(weapon.getAttack().getName());
            snippetBinding.setInventoryPic(weapon.getAttack().getPictureResource());
            binding.inventorySnippet.addView(view);
            snippetBinding.inventoryInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        inventoryInfoVM.setInventoryInfoObservable(weapon.getAttack().serializeToJSON().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        if (weapon.getSpecialAttack() != null) {
            // special attack inventory snippet
            View view = getLayoutInflater().inflate(R.layout.inventory_snippet, null);
            InventorySnippetBinding snippetBinding = DataBindingUtil.bind(view);
            snippetBinding.setInventoryName(weapon.getSpecialAttack().getName());
            snippetBinding.setInventoryPic(weapon.getSpecialAttack().getPictureResource());
            binding.inventorySnippet.addView(view);
            snippetBinding.inventoryInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        inventoryInfoVM.setInventoryInfoObservable(weapon.getSpecialAttack().serializeToJSON().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    // displays all info for the item
    private void setItemInfo(final Item item) {
        // top info
        binding.setName(item.getName());
        binding.setImageIconResource(item.getPictureResource());
        binding.setInfo(inventoryInfoVM.getItemInfo(item));

        // ability inventory snippet
        if (item.getAbility() != null) {
            View view = getLayoutInflater().inflate(R.layout.inventory_snippet, null);
            InventorySnippetBinding snippetBinding = DataBindingUtil.bind(view);
            snippetBinding.setInventoryName(item.getAbility().getName());
            snippetBinding.setInventoryPic(item.getAbility().getAbilityID());
            binding.inventorySnippet.addView(view);
            snippetBinding.inventoryInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        inventoryInfoVM.setInventoryInfoObservable(item.getAbility().serializeToJSON().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    // displays all info for the ability
    private void setAbilityInfo(final Ability ability) {
        // top info
        binding.setName(ability.getName());
        binding.setImageIconResource(ability.getPictureResource());
        binding.setInfo(inventoryInfoVM.getAbilityInfo(ability));
    }
}
