package com.habbybolan.textadventure.view.shopgrid;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.ImageGridBuyBinding;
import com.habbybolan.textadventure.model.GridModel;

import java.util.ArrayList;

/**
Adapter for the Grid image view for the shop encounter for Inventory Objects to buy
 */
public class BuyGridAdapter extends BaseAdapter {

    private ArrayList<GridModel> listGridModel;

    public BuyGridAdapter(ArrayList<GridModel> listGridModel) {
        this.listGridModel = listGridModel;
    }

    @Override
    public int getCount() {
        return listGridModel.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageGridBuyBinding binding;
        ViewHolder holder;
        if (convertView == null) {
            binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.image_grid_buy, parent, false);
            holder = new ViewHolder(binding);
            convertView = binding.getRoot();
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.bind(listGridModel.get(position));
        return convertView;
    }

    public void updateBuyGrid() {
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageGridBuyBinding binding;

        ViewHolder(ImageGridBuyBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(GridModel gridModel) {
            binding.setCost(String.valueOf(gridModel.getCost()));
            binding.setImageID(gridModel.getImageID());
            binding.executePendingBindings();
        }
    }
}
