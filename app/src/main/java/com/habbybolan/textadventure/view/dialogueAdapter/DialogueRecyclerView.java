package com.habbybolan.textadventure.view.dialogueAdapter;

import android.content.Context;

import androidx.databinding.Observable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.habbybolan.textadventure.model.dialogue.Dialogue;
import com.habbybolan.textadventure.model.dialogue.EffectDialogue;
import com.habbybolan.textadventure.model.dialogue.HealthDialogue;
import com.habbybolan.textadventure.model.dialogue.InventoryDialogue;
import com.habbybolan.textadventure.model.dialogue.ManaDialogue;
import com.habbybolan.textadventure.model.effects.Dot;
import com.habbybolan.textadventure.model.effects.SpecialEffect;
import com.habbybolan.textadventure.model.inventory.Ability;
import com.habbybolan.textadventure.model.inventory.Inventory;
import com.habbybolan.textadventure.model.inventory.Item;
import com.habbybolan.textadventure.model.inventory.weapon.Weapon;
import com.habbybolan.textadventure.viewmodel.CharacterViewModel;

/*
creates the dialogue recyclerView, updating on each character action
 connected to a specific fragment/activity
 */
public class DialogueRecyclerView {

    private DialogueAdapter adapter;
    private CharacterViewModel characterVM;

    public DialogueRecyclerView(Context context, RecyclerView recyclerView, CharacterViewModel characterVM) {

        // set the layout manager to position the items
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        this.adapter = new DialogueAdapter();
        recyclerView.setAdapter(adapter);
        this.characterVM = characterVM;

        // set up OnCLickListeners for changes in player Character
            // Effect listeners
        setDotListener();
        setSpecialListener();
            // bar listeners
        setHealthListener();
        setManaListener();
            // inventory listeners
        setAbilityListener();
        setWeaponListener();
        setItemListener();
    }

    private void setItemListener() {
        Observable.OnPropertyChangedCallback callBackItemAdd = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                Item item = characterVM.getItemObserverAdd().get();
                if (item != null) {
                    InventoryDialogue inventoryDialogue = new InventoryDialogue(item.getName(), item.getPictureResource(), Inventory.TYPE_ITEM, true);
                    adapter.addNewDialogue(inventoryDialogue);
                }
            }
        };
        characterVM.getItemObserverAdd().addOnPropertyChangedCallback(callBackItemAdd);

        Observable.OnPropertyChangedCallback callBackItemRemove = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                Item item = characterVM.getItemObserverRemove().get();
                if (item != null) {
                    InventoryDialogue inventoryDialogue = new InventoryDialogue(item.getName(), item.getPictureResource(), Inventory.TYPE_ITEM, false);
                    adapter.addNewDialogue(inventoryDialogue);
                }
            }
        };
        characterVM.getItemObserverRemove().addOnPropertyChangedCallback(callBackItemRemove);
    }

    private void setWeaponListener() {
        Observable.OnPropertyChangedCallback callBackWeaponAdd = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                Weapon weapon = characterVM.getWeaponObserverAdd().get();
                if (weapon != null) {
                    InventoryDialogue inventoryDialogue = new InventoryDialogue(weapon.getName(), weapon.getPictureResource(), Inventory.TYPE_WEAPON, true);
                    adapter.addNewDialogue(inventoryDialogue);
                }
            }
        };
        characterVM.getWeaponObserverAdd().addOnPropertyChangedCallback(callBackWeaponAdd);

        Observable.OnPropertyChangedCallback callBackWeaponRemove = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                Weapon weapon = characterVM.getWeaponObserverRemove().get();
                if (weapon != null) {
                    InventoryDialogue inventoryDialogue = new InventoryDialogue(weapon.getName(), weapon.getPictureResource(), Inventory.TYPE_WEAPON, false);
                    adapter.addNewDialogue(inventoryDialogue);
                }
            }
        };
        characterVM.getWeaponObserverRemove().addOnPropertyChangedCallback(callBackWeaponRemove);
    }

    private void setAbilityListener() {
        Observable.OnPropertyChangedCallback callBackAbilityAdd = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                Ability ability = characterVM.getAbilityObserverAdd().get();
                if (ability != null) {
                    InventoryDialogue inventoryDialogue = new InventoryDialogue(ability.getName(), ability.getPictureResource(), Inventory.TYPE_ABILITY, true);
                    adapter.addNewDialogue(inventoryDialogue);
                }
            }
        };
        characterVM.getAbilityObserverAdd().addOnPropertyChangedCallback(callBackAbilityAdd);

        Observable.OnPropertyChangedCallback callBackAbilityRemove = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                Ability ability = characterVM.getAbilityObserverRemove().get();
                if (ability != null) {
                    InventoryDialogue inventoryDialogue = new InventoryDialogue(ability.getName(), ability.getPictureResource(), Inventory.TYPE_ABILITY, false);
                    adapter.addNewDialogue(inventoryDialogue);
                }
            }
        };
        characterVM.getAbilityObserverRemove().addOnPropertyChangedCallback(callBackAbilityRemove);


    }

    // listener for change in health
    private void setHealthListener() {
        Observable.OnPropertyChangedCallback callBack = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                HealthDialogue healthDialogue = characterVM.getHealthObserve().get();
                if (healthDialogue != null) {
                    adapter.addNewDialogue(healthDialogue);
                }
            }
        };
        characterVM.getHealthObserve().addOnPropertyChangedCallback(callBack);
    }

    // listener for change in health
    private void setManaListener() {
        Observable.OnPropertyChangedCallback callBack = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                ManaDialogue manaDialogue = characterVM.getManaObserve().get();
                if (manaDialogue != null) {
                    adapter.addNewDialogue(manaDialogue);
                }
            }
        };
        characterVM.getManaObserve().addOnPropertyChangedCallback(callBack);
    }

    // called by fragment/activity to add dialogue to the active dialogue RV
    public void addDialogue(Dialogue dialogue) {
        adapter.addNewDialogue(dialogue);
    }

    private void setDotListener() {
        // observed whenever CharacterViewModel observes change in dotList
        Observable.OnPropertyChangedCallback callBack = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                Dot dot = characterVM.getUpdateAllDotAdd().get();
                if (dot != null) {
                    EffectDialogue effectDialogue = new EffectDialogue(dot.getType(), dot.getDuration(), dot.getIcon(), dot.getIsInfinite());
                    adapter.addNewDialogue(effectDialogue);
                }
            }
        };
        characterVM.getUpdateAllDotAdd().addOnPropertyChangedCallback(callBack);
    }

    private void setSpecialListener() {
        // observed whenever CharacterViewModel observes change in dotList
        Observable.OnPropertyChangedCallback callBack = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                SpecialEffect special = characterVM.getUpdateAllSpecialAdd().get();
                if (special != null) {
                    EffectDialogue effectDialogue = new EffectDialogue(special.getType(), special.getDuration(), special.getIcon(), special.getIsInfinite());
                    adapter.addNewDialogue(effectDialogue);
                }
            }
        };
        characterVM.getUpdateAllSpecialAdd().addOnPropertyChangedCallback(callBack);
    }
}
