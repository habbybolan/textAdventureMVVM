package com.habbybolan.textadventure.view.encounter;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.DefaultButtonDetailsBinding;
import com.habbybolan.textadventure.databinding.FragmentShopBinding;
import com.habbybolan.textadventure.model.GridModel;
import com.habbybolan.textadventure.model.inventory.InventoryEntity;
import com.habbybolan.textadventure.view.ButtonInflaters;
import com.habbybolan.textadventure.view.dialogueAdapter.DialogueRecyclerView;
import com.habbybolan.textadventure.view.inventoryinfo.BuyInformationActivity;
import com.habbybolan.textadventure.view.inventoryinfo.InventoryInfoFragment;
import com.habbybolan.textadventure.view.inventoryinfo.SellInformationActivity;
import com.habbybolan.textadventure.view.shopgrid.BuyGridAdapter;
import com.habbybolan.textadventure.view.shopgrid.SellGridAdapter;
import com.habbybolan.textadventure.viewmodel.encounters.ShopViewModel;

import org.json.JSONException;

import java.util.concurrent.ExecutionException;

public class ShopFragment extends EncounterDialogueFragment implements EncounterFragment {


    private FragmentShopBinding shopBinding;
    private ShopViewModel shopVM;
    private DialogueRecyclerView rv;

    private SellGridAdapter sellGridAdapter;
    private BuyGridAdapter buyGridAdapter;

    private static ShopFragment instance = null;

    private ShopFragment() {}

    public static ShopFragment newInstance() {
        instance = new ShopFragment();
        return instance;
    }

    /**
     * Check if an instance exists
     * @return  true if a ShopFragment instance exists
     */
    public static boolean isInstance() {
        return instance != null;
    }

    public static ShopFragment getInstance() {
        if (instance == null) throw new AssertionError("Create instance first");
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        shopBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_shop, container, false);
        shopBinding.setShopVM(shopVM);
        return shopBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        shopVM = new ViewModelProvider(this).get(ShopViewModel.class);
        try {
            rv = setUpEncounterBeginning(shopVM, this, shopBinding.dialogueRV);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void checkState(int state) {
        // remove ability to drop/consume Inventory Objects
        characterVM.setStateInventoryObserver(true);
        switch (state) {
            case ShopViewModel.firstState:
                // dialogue state
                characterVM.setStateInventoryObserver(true);
                dialogueState(shopVM, shopBinding.layoutBtnOptions);
                break;
            case ShopViewModel.secondState:
                // before entering shop state
                characterVM.setStateInventoryObserver(true);
                beforeShopState();
                break;
            case ShopViewModel.thirdState:
                // shop state
                characterVM.setStateInventoryObserver(false);
                shopBinding.shopContainer.setVisibility(View.VISIBLE);
                shopBinding.layoutBtnOptions.setVisibility(View.GONE);
                setShopkeeper();
        }
    }

    /**
     * creates a button to enter the shop after displaying the dialogue, or to leave the encounter entirely
     */
    private void beforeShopState() {
        shopBinding.layoutBtnOptions.removeAllViews();
        setLeaveButton(shopBinding.layoutBtnOptions);
        enterShopButton();
    }

    /**
     * Button for entering the shop.
     */
    private void enterShopButton() {
        String enterTxt = "Enter Shop";
        DefaultButtonDetailsBinding binding = ButtonInflaters.setDefaultButton(shopBinding.layoutBtnOptions, enterTxt, getActivity());
        binding.btnDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if entering the shop, increment state to go to shop state
                shopVM.incrementStateIndex();
            }
        });
    }

    /**
     * set up all shop UI and signal functionality.
     */
    private void setShopkeeper() {
        try {
            shopBinding.dialogueRV.setVisibility(View.GONE);
            setShopkeeperImage();
            setLeaveButtonFunctionality();
            setUpItemToBuy();
            setUpItemsToSell();
            setUpSellBuyBtn();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set up shopKeeper image
     */
    private void setShopkeeperImage() {
        shopBinding.setShopKeeperImage(R.drawable.wizard);
    }

    /**
     *  Create functionality for the static leave button.
     */
    private void setLeaveButtonFunctionality() {
        shopBinding.btnLeaveShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainGameVM.gotoNextRandomEncounter();
            }
        });
    }

    /**
     * create the items to buy and set up gridModel and clicker for list
     */
    private void setUpItemToBuy() throws ExecutionException, InterruptedException {
        // create a buy GridModel list
        shopVM.setUpItemToBuy();
        buyGridAdapter = new BuyGridAdapter(shopVM.getListGridModelBuy());
        shopBinding.gridBuy.setAdapter(buyGridAdapter);
        shopBinding.gridBuy.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), BuyInformationActivity.class);
                GridModel gridModel = shopVM.getListGridModelBuy().get(position);
                InventoryEntity inventoryEntity = gridModel.getInventory();
                try {
                    intent.putExtra(InventoryInfoFragment.INVENTORY_SERIALIZED, inventoryEntity.serializeToJSON().toString());
                    intent.putExtra(InventoryInfoFragment.COST, gridModel.getCost());
                    intent.putExtra(InventoryInfoFragment.POSITION, position);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.fade_out);
            }
        });
    }

    // sets up the gridView of items that can be sold to the shopkeeper
    private void setUpItemsToSell() {
        // create a sell GridModel list
        shopVM.setSellList();
        sellGridAdapter = new SellGridAdapter(shopVM.getListGridModelSell());
        shopBinding.gridSell.setAdapter(sellGridAdapter);
        shopBinding.gridSell.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), SellInformationActivity.class);
                GridModel gridModel = shopVM.getListGridModelSell().get(position);
                InventoryEntity inventoryEntity = gridModel.getInventory();
                try {
                    intent.putExtra(InventoryInfoFragment.INVENTORY_SERIALIZED, inventoryEntity.serializeToJSON().toString());
                    intent.putExtra(InventoryInfoFragment.COST, gridModel.getCost());
                    intent.putExtra(InventoryInfoFragment.POSITION, position);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.fade_out);
            }
        });
    }

    /**
     * Calls shopVM's sellInventory method to sell the Inventory object in listGridModelSell at index position
     * @param position   The position in listGridModelSell holding the Inventory object to sell
     */
    public void sellInventory(int position) {
        shopVM.sellInventory(position);
        sellGridAdapter.updateSellGrid();
        buyGridAdapter.updateBuyGrid();
    }

    /**
     * Calls shopVM's buyInventory method to buy the Inventory object in listGridModelBuy at index position
     * @param position   The position in listGridModelBuy holding the Inventory object to buy
     */
    public boolean buyInventory(int position) {
        if (shopVM.buyInventory(position)) {
            sellGridAdapter.updateSellGrid();
            buyGridAdapter.updateBuyGrid();
            return true;
        }
        return false;
    }



    /**
     *  Sets up the button to swap between the sell and buy gridViews.
     */
    private void setUpSellBuyBtn() {
        shopBinding.btnSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSellBtnClick();
            }
        });

        shopBinding.btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBuyBtnClick();
            }
        });
        onBuyBtnClick();
    }

    /**
     * Set Buy grid as visible and set Buy button as selected so it stays pressed.
     */
    private void onBuyBtnClick() {
        shopBinding.gridBuy.setVisibility(View.VISIBLE);
        shopBinding.btnBuy.setSelected(true);
        shopBinding.gridSell.setVisibility(View.GONE);
        shopBinding.btnSell.setSelected(false);
    }
    /**
     * Set Sell grid as visible and set Sell button as selected so it stays pressed.
     */
    private void onSellBtnClick() {
        shopBinding.gridSell.setVisibility(View.VISIBLE);
        shopBinding.btnSell.setSelected(true);
        shopBinding.gridBuy.setVisibility(View.GONE);
        shopBinding.btnBuy.setSelected(false);
    }

    @Override
    public void endState() {
        // todo: need this for exit dialogue?
    }

    @Override
    public void onStop() {
        super.onStop();
        saveGame(rv, shopVM);
    }
}
