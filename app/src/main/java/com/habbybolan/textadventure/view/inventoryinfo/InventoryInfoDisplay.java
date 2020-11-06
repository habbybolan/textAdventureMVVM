package com.habbybolan.textadventure.view.inventoryinfo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.Observable;

import com.habbybolan.textadventure.viewmodel.InventoryInfoViewModel;

/**
 * Parent of any activity class that wants to display Inventory information using InventoryInfoFragment.
 * Deals with creating the fragment, setting the inventory change listener, and dealing with the back button functionality.
 */
public abstract class InventoryInfoDisplay extends AppCompatActivity {

    InventoryInfoViewModel inventoryInfoViewModel;

    /**
     * Create a new InventoryInfoFragment that displays the inventory info, and place on the back stack.
     */
    void createNewFragment() {
        InventoryInfoFragment fragment = InventoryInfoFragment.newInstance(inventoryInfoViewModel.getInventoryString());
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(getFragmentContainer(), fragment)
                .commit();
    }

    /**
     * Set up the Inventory listeners if an Inventory snippet is clicked inside the info fragment.
     */
    void setInventoryInfoListener() {
        final Observable.OnPropertyChangedCallback callback = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                createNewFragment();
            }
        };
        inventoryInfoViewModel.getInventoryInfoObservable().addOnPropertyChangedCallback(callback);
    }

    /**
     * Functionality for the back button. If only one fragment on the back stack, then kill the activity.
     * Otherwise, normal button functionality.
     */
    public void backButtonFunctionality() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onBackPressed() {
        backButtonFunctionality();
    }

    abstract int getFragmentContainer();
}
