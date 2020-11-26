package com.habbybolan.textadventure.model;

import com.habbybolan.textadventure.model.inventory.Inventory;

/*
Object for Inventory Objects to sell/buy in shop encounter.
 */
public class GridModel {

    private int imageID; // resource id of the png image
    private int cost; //  price of the thing in gold to buy
    private Inventory inventory; // either a Weapon, Item, or Ability

    public GridModel(Inventory inventory, int cost) {
        this.inventory = inventory;
        this.cost = cost;
        imageID = this.inventory.getPictureResource();
    }

    /**
     * Increase the price of gridModel inventory item that was sold so buying again is more expensive.
     * @return  The new GridModel with updated price.
     */
    public GridModel buySoldInventory() {
        cost *= 2;
        return this;
    }



    public int getImageID() {
        return imageID;
    }
    public void setImageID(int imageID) {
        this.imageID = imageID;
    }

    public int getCost() {
        return cost;
    }
    public void setCost(int cost) {
        this.cost = cost;
    }

    public Inventory getInventory() {
        return inventory;
    }
    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

}
