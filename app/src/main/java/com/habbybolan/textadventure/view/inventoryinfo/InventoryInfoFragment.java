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
import com.habbybolan.textadventure.model.inventory.InventoryEntity;
import com.habbybolan.textadventure.model.inventory.Item;
import com.habbybolan.textadventure.model.inventory.weapon.Attack;
import com.habbybolan.textadventure.model.inventory.weapon.SpecialAttack;
import com.habbybolan.textadventure.model.inventory.weapon.Weapon;
import com.habbybolan.textadventure.viewmodel.InventoryInfoViewModel;

import org.json.JSONException;

/**
 * Fragment for displaying any Inventory's information. Creates Inventory snippets if it contains
 * another Inventory object inside the inventory object. Uses the InventoryInfoViewModel observable to
 * notify a new Inner Inventory Object has been clicked.
 */
public class InventoryInfoFragment extends Fragment {

    private FragmentInventoryInfoBinding binding;
    public static final String INVENTORY_SERIALIZED = "INVENTORY_SERIALIZED";
    public static final String COST = "cost";
    public static final String POSITION = "position";

    private static final String INVENTORY_ARG = "inventory";
    private String inventoryString;

    private InventoryInfoViewModel inventoryInfoVM = InventoryInfoViewModel.getInstance();

    private InventoryInfoFragment() {
    }

    // TODO: Rename and change types and number of parameters
    public static InventoryInfoFragment newInstance(String inventoryString) {
        InventoryInfoFragment fragment = new InventoryInfoFragment();
        // set up inventory String for configuration changes
        Bundle args = new Bundle();
        args.putString(INVENTORY_ARG, inventoryString);
        fragment.setArguments(args);
        return new InventoryInfoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            // get the saved, bundled inventory string
            inventoryString = savedInstanceState.getString(INVENTORY_ARG);
        } else {
            // otherwise, no save exists, so retrieve the inventory string from the viewModel
            inventoryString = inventoryInfoVM.getInventoryString();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_inventory_info, container, false);
        setInventory();
        return binding.getRoot();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(INVENTORY_ARG, inventoryInfoVM.getInventoryString());
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    /**
     * Calls the proper method to set up the UI info on the specific stored inventory.
     */
    private void setInventory() {
        InventoryEntity inventoryEntity = inventoryInfoVM.getInventoryFromString(inventoryString);
        if (inventoryEntity.isAbility())
            setAbilityInfo((Ability) inventoryEntity);
        else if (inventoryEntity.isItem())
            setItemInfo((Item) inventoryEntity);
        else if (inventoryEntity.isWeapon())
            setWeaponInfo((Weapon) inventoryEntity);
        else if (inventoryEntity.isAttack())
            setAttackInfo((Attack) inventoryEntity);
        else if (inventoryEntity.isSpecialAttack())
            setSpecialAttackInfo((SpecialAttack) inventoryEntity);
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
            snippetBinding.setInventoryPic(specialAttack.getAbility().getPictureResource());
            binding.inventorySnippet.addView(view);
            // clicker to go into new Ability fragment
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
            // clicker to go into new Attack fragment
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
            // clicker to go into new Special Attack fragment
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
            snippetBinding.setInventoryPic(item.getAbility().getPictureResource());
            binding.inventorySnippet.addView(view);
            // clicker to go into new Ability fragment
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
