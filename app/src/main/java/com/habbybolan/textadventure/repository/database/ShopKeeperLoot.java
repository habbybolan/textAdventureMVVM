package com.habbybolan.textadventure.repository.database;

import android.content.Context;

import com.habbybolan.textadventure.model.GridModel;
import com.habbybolan.textadventure.model.inventory.Inventory;
import com.habbybolan.textadventure.model.inventory.InventoryEntity;

import java.util.ArrayList;
import java.util.Random;

/**
 * Loot class for getting shopkeeper loot since shopkeeper loot is scaled towards specific types of loot.
 */
public class ShopKeeperLoot extends LootInventory {

    // Items that are more likely to appear inside a shop.
    private final int[] shopInventoryItems = {1, 2};

    // todo: shop keeper loot
    public ShopKeeperLoot(Context context) {
        super(context);
    }


    /**
     * Create item to buy from a shop keeper, scaled to tierScaled
     * @param tierScaled    The loot to get scaled towards tierScaled
     * @return              An array of scaled gridModels that holds the inventory objects to buy
     */
    public ArrayList<GridModel> inventoryToBuy(int tierScaled) {
        /* chances for different types of shops
        ChangeForAbilities is remaining percentage */
        final double chanceForItems = 0.4;
        final double chanceForAll = 0.3;
        final double chanceForWeapons = 0.2;

        // The Inventory Objects to convert to GridModels
        ArrayList<Inventory> inventories = new ArrayList<>();

        int fullPercent = 100;
        Random rand = new Random();
        double shopType = rand.nextInt(fullPercent);
        // Get the type of shop
        if (shopType < fullPercent * chanceForItems) {
            // create shop of random items (5 items)
            inventories.addAll(getRandomItems(tierScaled, 5));
        } else if (shopType < (fullPercent * chanceForItems) + (fullPercent * chanceForAll)) {
            // create a shop full of all random (3 items, 2 weapons, 1 abilities)
            inventories.addAll(getRandomItems(tierScaled,3));
            inventories.addAll(getRandomWeapons(tierScaled,2));
            inventories.addAll(getRandomAbilities(tierScaled,1));
        } else if (shopType < (fullPercent * chanceForItems) + (fullPercent * chanceForAll) + (fullPercent * chanceForWeapons)) {
            // create a shop full of random weapons (5)
            inventories.addAll(getRandomWeapons(tierScaled,5));
        } else {
            // create a shop full of random abilities (4)
            inventories.addAll(getRandomAbilities(tierScaled,4));
        }
        return convertArrayToGridModel(inventories);
    }

    /**
     * Converts a list of Inventory objects to grid models.
     * @param inventories   The list of Inventories to convert to gridModels
     * @return              The list of gridModels converted from inventories
     */
    private ArrayList<GridModel> convertArrayToGridModel(ArrayList<Inventory> inventories) {
        ArrayList<GridModel> gridModels = new ArrayList<>();
        for (Inventory inventory : inventories) {
            gridModels.add(convertToGridModel(inventory));
        }
        return gridModels;
    }

    /**
     * Convert a single Inventory object into a GridModel
     * @param inventory     The Inventory object to convert to GridModel
     * @return              The GridModel converted from inventory
     */
    private GridModel convertToGridModel(Inventory inventory) {
        return new GridModel(inventory, priceBase(inventory.getType(), inventory.getTier()));
    }

    /**
     * Get the base price of the inventory object, based on its type and tier.
     * @param inventoryType     The string inventory type
     * @return                  The base price of the Inventory object
     */
    private int priceBase(String inventoryType, int tier) {
        switch (inventoryType) {
            case InventoryEntity.TYPE_ABILITY:
                switch (tier) {
                    case 1:
                        return 2;
                    case 2:
                        return 4;
                    case 3:
                        return 8;
                    default:
                        throw new IllegalArgumentException(tier + " is not a valid tier");
                }
            case InventoryEntity.TYPE_WEAPON:
                switch (tier) {
                    case 1:
                        return 5;
                    case 2:
                        return 10;
                    case 3:
                        return 15;
                    default:
                        throw new IllegalArgumentException(tier + " is not a valid tier");
                }
            case InventoryEntity.TYPE_ITEM:
                switch (tier) {
                    case 1:
                        return 7;
                    case 2:
                        return 13;
                    case 3:
                        return 20;
                    default:
                        throw new IllegalArgumentException(tier + " is not a valid tier");
                }
            default:
                throw new IllegalArgumentException(inventoryType + " is not a valid inventory type");
        }
    }
}
