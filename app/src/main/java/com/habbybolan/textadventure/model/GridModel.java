package com.habbybolan.textadventure.model;

import com.habbybolan.textadventure.model.inventory.Inventory;

/*
Object for the ID of an image view of one of the grid cells
 */
public class GridModel {

    private int imageID; // resource id of the png image
    private int cost; //  price of the thing in gold to buy
    private Inventory inventory; // either a Weapon, Item, or Ability

    public GridModel(Inventory inventory, int cost) {
        this.inventory = inventory;
        this.cost = cost;
        imageID = inventory.getPictureResource();
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
