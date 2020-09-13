package com.habbybolan.textadventure.view.dialogueAdapter;

import android.content.Context;

import androidx.databinding.Observable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.habbybolan.textadventure.model.dialogue.Dialogue;
import com.habbybolan.textadventure.model.dialogue.DialogueType;
import com.habbybolan.textadventure.model.dialogue.EffectDialogue;
import com.habbybolan.textadventure.model.dialogue.HealthDialogue;
import com.habbybolan.textadventure.model.dialogue.InventoryDialogue;
import com.habbybolan.textadventure.model.dialogue.ManaDialogue;
import com.habbybolan.textadventure.model.dialogue.StatDialogue;
import com.habbybolan.textadventure.model.dialogue.TempStatDialogue;
import com.habbybolan.textadventure.model.effects.Dot;
import com.habbybolan.textadventure.model.effects.SpecialEffect;
import com.habbybolan.textadventure.model.effects.TempBar;
import com.habbybolan.textadventure.model.effects.TempStat;
import com.habbybolan.textadventure.model.inventory.Ability;
import com.habbybolan.textadventure.model.inventory.Inventory;
import com.habbybolan.textadventure.model.inventory.Item;
import com.habbybolan.textadventure.model.inventory.weapon.Weapon;
import com.habbybolan.textadventure.viewmodel.CharacterViewModel;

import java.util.ArrayList;

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
        setBarListener();
            // inventory listeners
        setAbilityListener();
        setWeaponListener();
        setItemListener();
            // stat
        setTempStatListener();
        setStatListener();
    }

    public DialogueRecyclerView(Context context, RecyclerView recyclerView, CharacterViewModel characterVM, ArrayList<DialogueType> dialogueList) {

        // set the layout manager to position the items
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        this.adapter = new DialogueAdapter(dialogueList);
        recyclerView.setAdapter(adapter);
        this.characterVM = characterVM;

        // set up OnCLickListeners for changes in player Character
        // Effect listeners
        setDotListener();
        setSpecialListener();
        // bar listeners
        setHealthListener();
        setManaListener();
        setBarListener();
        // inventory listeners
        setAbilityListener();
        setWeaponListener();
        setItemListener();
        // stat
        setTempStatListener();
        setStatListener();
    }

    // Dialogue for adding new Item
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

    // Dialogue for adding new Weapon
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

    // Dialogue for adding new Ability scroll
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

    // Dialogue for adding basic String text
    public void addDialogue(Dialogue dialogue) {
        adapter.addNewDialogue(dialogue);
    }

    // dialogue for adding Dot Effects
    private void setDotListener() {
        // observed whenever CharacterViewModel observes change in dotList
        Observable.OnPropertyChangedCallback callBack = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                Dot dot = characterVM.getUpdateAllDotAdd().get();
                if (dot != null) {
                    EffectDialogue effectDialogue = new EffectDialogue(dot.getType(), dot.getDuration(), dot.getIcon(), dot.getIsIndefinite());
                    adapter.addNewDialogue(effectDialogue);
                }
            }
        };
        characterVM.getUpdateAllDotAdd().addOnPropertyChangedCallback(callBack);
    }

    // dialogue for adding Special Effects
    private void setSpecialListener() {
        // observed whenever CharacterViewModel observes change in dotList
        Observable.OnPropertyChangedCallback callBack = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                SpecialEffect special = characterVM.getUpdateAllSpecialAdd().get();
                if (special != null) {
                    EffectDialogue effectDialogue = new EffectDialogue(special.getType(), special.getDuration(), special.getIcon(), special.getIsIndefinite());
                    adapter.addNewDialogue(effectDialogue);
                }
            }
        };
        characterVM.getUpdateAllSpecialAdd().addOnPropertyChangedCallback(callBack);
    }

    // Dialogue for adding new stats
    private void setTempStatListener() {
        // observer for when stat is increased
        Observable.OnPropertyChangedCallback callBackIncr = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                TempStat tempStat = characterVM.getUpdateAllStatIncrAdd().get();
                if (tempStat != null) {
                    TempStatDialogue statDialogue = new TempStatDialogue(tempStat.getType(), tempStat.getAmount(), tempStat.getDuration());
                    adapter.addNewDialogue(statDialogue);
                }
            }
        };
        characterVM.getUpdateAllStatIncrAdd().addOnPropertyChangedCallback(callBackIncr);

        // observer for when stat is decreased
        Observable.OnPropertyChangedCallback callBackDecr = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                TempStat tempStat = characterVM.getUpdateAllStatDecrAdd().get();
                if (tempStat != null) {
                    EffectDialogue effectDialogue = new EffectDialogue(tempStat.getType(), tempStat.getDuration(), tempStat.getIcon(), tempStat.getIsIndefinite());
                    adapter.addNewDialogue(effectDialogue);
                }
            }
        };
        characterVM.getUpdateAllStatDecrAdd().addOnPropertyChangedCallback(callBackDecr);
    }

    // Dialogue for adding temp Mana/health increases
    private void setBarListener() {
        // observer for when temp health is added
        Observable.OnPropertyChangedCallback callBackIncr = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                TempBar tempBar = characterVM.getUpdateAllBarAdd().get();
                if (tempBar != null) {
                    TempStatDialogue tempStatDialogue = new TempStatDialogue(tempBar.getType(), tempBar.getAmount(), tempBar.getDuration());
                    adapter.addNewDialogue(tempStatDialogue);
                }
            }
        };
        characterVM.getUpdateAllBarAdd().addOnPropertyChangedCallback(callBackIncr);
    }

    private void setStatListener() {
        // observer for when stat is decreased
        Observable.OnPropertyChangedCallback callBackDecr = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                TempStat tempStat = characterVM.getUpdateAllStatChange().get();
                if (tempStat != null) {
                    StatDialogue statDialogue = new StatDialogue(tempStat.getType(), tempStat.getAmount());
                    adapter.addNewDialogue(statDialogue);
                }
            }
        };
        characterVM.getUpdateAllStatChange().addOnPropertyChangedCallback(callBackDecr);
    }

    public ArrayList<DialogueType> getDialogueList() {
        return adapter.getDialogueList();
    }


}
