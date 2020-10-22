package com.habbybolan.textadventure.view.dialogueAdapter;

import android.content.Context;

import androidx.databinding.Observable;
import androidx.databinding.ObservableList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.habbybolan.textadventure.model.dialogue.CombatActionDialogue;
import com.habbybolan.textadventure.model.dialogue.Dialogue;
import com.habbybolan.textadventure.model.dialogue.DialogueType;
import com.habbybolan.textadventure.model.dialogue.EffectDialogue;
import com.habbybolan.textadventure.model.dialogue.ExpDialogue;
import com.habbybolan.textadventure.model.dialogue.GoldDialogue;
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
import com.habbybolan.textadventure.viewmodel.characterEntityViewModels.CharacterViewModel;
import com.habbybolan.textadventure.viewmodel.characterEntityViewModels.EnemyViewModel;

import java.util.ArrayList;

/*
creates the dialogue recyclerView, updating on each character action
 connected to a specific fragment/activity
 */
public class DialogueRecyclerView {

    private DialogueAdapter adapter;
    private CharacterViewModel characterVM;

    public DialogueRecyclerView(Context context, RecyclerView recyclerView, ArrayList<DialogueType> dialogueList) {

        // set the layout manager to position the items
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        this.adapter = new DialogueAdapter(dialogueList);
        recyclerView.setAdapter(adapter);
        characterVM = CharacterViewModel.getInstance();

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
        // xp/gold
        setGoldListener();
        setExpListener();
    }

    // Dialogue for adding basic String text
    public void addDialogue(Dialogue dialogue) {
        adapter.addNewDialogue(dialogue);
    }
    // Dialogue for adding combat actions from enemies and character
    public void addNewCombatDialogue(CombatActionDialogue dialogue) {
        adapter.addNewDialogue(dialogue);
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
                Integer health = characterVM.getHealthObserve().get();
                if (health != null) {
                    adapter.addNewDialogue(new HealthDialogue(health));
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
                Integer mana = characterVM.getManaObserve().get();
                if (mana != null) {
                    adapter.addNewDialogue(new ManaDialogue(mana));
                }
            }
        };
        characterVM.getManaObserve().addOnPropertyChangedCallback(callBack);
    }
    // dialogue for adding Dot Effects
    private void setDotListener() {
        // observed whenever CharacterViewModel observes change in dotList
        characterVM.getDotObserver().addOnListChangedCallback(new ObservableList.OnListChangedCallback() {
            @Override
            public void onChanged(ObservableList sender) {}
            @Override
            public void onItemRangeChanged(ObservableList sender, int positionStart, int itemCount) {}
            @Override
            public void onItemRangeInserted(ObservableList sender, int positionStart, int itemCount) {
                Dot dot = characterVM.getDotObserver().get(positionStart);
                if (dot != null) {
                    EffectDialogue effectDialogue = new EffectDialogue(dot.getType(), dot.getDuration(), dot.getIcon(), dot.getIsIndefinite());
                    adapter.addNewDialogue(effectDialogue);
                }
            }
            @Override
            public void onItemRangeMoved(ObservableList sender, int fromPosition, int toPosition, int itemCount) {}
            @Override
            public void onItemRangeRemoved(ObservableList sender, int positionStart, int itemCount) {}
        });
    }
    // dialogue for adding Special Effects
    private void setSpecialListener() {
        // observed whenever CharacterViewModel observes change in dotList
        characterVM.getSpecialObserver().addOnListChangedCallback(new ObservableList.OnListChangedCallback() {
            @Override
            public void onChanged(ObservableList sender) {}
            @Override
            public void onItemRangeChanged(ObservableList sender, int positionStart, int itemCount) {}
            @Override
            public void onItemRangeInserted(ObservableList sender, int positionStart, int itemCount) {
                SpecialEffect specialEffect = characterVM.getSpecialObserver().get(positionStart);
                if (specialEffect != null) {
                    EffectDialogue effectDialogue = new EffectDialogue(specialEffect.getType(), specialEffect.getDuration(), specialEffect.getIcon(), specialEffect.getIsIndefinite());
                    adapter.addNewDialogue(effectDialogue);
                }
            }
            @Override
            public void onItemRangeMoved(ObservableList sender, int fromPosition, int toPosition, int itemCount) {}
            @Override
            public void onItemRangeRemoved(ObservableList sender, int positionStart, int itemCount) {}
        });
    }
    // Dialogue for adding new stats
    private void setTempStatListener() {
        // observed whenever CharacterViewModel observes change in statIncrList
        characterVM.getTempStatIncrObserver().addOnListChangedCallback(new ObservableList.OnListChangedCallback() {
            @Override
            public void onChanged(ObservableList sender) {}
            @Override
            public void onItemRangeChanged(ObservableList sender, int positionStart, int itemCount) {}
            @Override
            public void onItemRangeInserted(ObservableList sender, int positionStart, int itemCount) {
                TempStat tempStat = characterVM.getTempStatIncrObserver().get(positionStart);
                if (tempStat != null) {
                    TempStatDialogue statDialogue = new TempStatDialogue(tempStat.getType(), tempStat.getAmount(), tempStat.getDuration());
                    adapter.addNewDialogue(statDialogue);
                }
            }
            @Override
            public void onItemRangeMoved(ObservableList sender, int fromPosition, int toPosition, int itemCount) {}
            @Override
            public void onItemRangeRemoved(ObservableList sender, int positionStart, int itemCount) {}
        });

        // observed whenever CharacterViewModel observes change in statDecrList
        characterVM.getTempStatDecrObserver().addOnListChangedCallback(new ObservableList.OnListChangedCallback() {
            @Override
            public void onChanged(ObservableList sender) {}
            @Override
            public void onItemRangeChanged(ObservableList sender, int positionStart, int itemCount) {}
            @Override
            public void onItemRangeInserted(ObservableList sender, int positionStart, int itemCount) {
                TempStat tempStat = characterVM.getTempStatDecrObserver().get(positionStart);
                if (tempStat != null) {
                    TempStatDialogue statDialogue = new TempStatDialogue(tempStat.getType(), tempStat.getAmount(), tempStat.getDuration());
                    adapter.addNewDialogue(statDialogue);
                }
            }
            @Override
            public void onItemRangeMoved(ObservableList sender, int fromPosition, int toPosition, int itemCount) {}
            @Override
            public void onItemRangeRemoved(ObservableList sender, int positionStart, int itemCount) {}
        });
    }
    // Dialogue for adding temp Mana/health increases
    private void setBarListener() {
        // observed whenever CharacterViewModel observes change in dotList
        characterVM.getBarObserver().addOnListChangedCallback(new ObservableList.OnListChangedCallback() {
            @Override
            public void onChanged(ObservableList sender) {}
            @Override
            public void onItemRangeChanged(ObservableList sender, int positionStart, int itemCount) {}
            @Override
            public void onItemRangeInserted(ObservableList sender, int positionStart, int itemCount) {
                TempBar tempBar = characterVM.getBarObserver().get(positionStart);
                if (tempBar != null) {
                    TempStatDialogue tempStatDialogue = new TempStatDialogue(tempBar.getType(), tempBar.getAmount(), tempBar.getDuration());
                    adapter.addNewDialogue(tempStatDialogue);
                }
            }
            @Override
            public void onItemRangeMoved(ObservableList sender, int fromPosition, int toPosition, int itemCount) {}
            @Override
            public void onItemRangeRemoved(ObservableList sender, int positionStart, int itemCount) {}
        });
    }
    // Dialogue for changing stat
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
    // Dialogue for gold change
    private void setGoldListener() {
        // observe when gold changed
        Observable.OnPropertyChangedCallback callBackGold = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                Integer goldAmount = characterVM.getGoldObserve().get();
                if (goldAmount != null)
                    adapter.addNewDialogue(new GoldDialogue(goldAmount));
            }
        };
        characterVM.getGoldObserve().addOnPropertyChangedCallback(callBackGold);
    }
    // Dialogue for gold change
    private void setExpListener() {
        // observe when gold changed
        Observable.OnPropertyChangedCallback callBackExp = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                Integer expAmount = characterVM.getExpObserve().get();
                if (expAmount != null)
                    adapter.addNewDialogue(new ExpDialogue(expAmount));
            }
        };
        characterVM.getExpObserve().addOnPropertyChangedCallback(callBackExp);
    }

    public ArrayList<DialogueType> getDialogueList() {
        return adapter.getDialogueList();
    }


    private ArrayList<EnemyViewModel> enemyViewModels;
    public void addEnemyDialogue(ArrayList<EnemyViewModel> enemyViewModels) {
        this.enemyViewModels = enemyViewModels;
        // todo: listeners for enemy changes
            // todo: offset the enemy changes to show on right side of dialogue
    }
}
