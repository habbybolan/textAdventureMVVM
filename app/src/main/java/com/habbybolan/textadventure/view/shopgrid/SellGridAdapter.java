package com.habbybolan.textadventure.view.shopgrid;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.ImageGridSellBinding;
import com.habbybolan.textadventure.model.GridModel;

import java.util.ArrayList;

public class SellGridAdapter extends BaseAdapter {

    private ArrayList<GridModel> listGridModel;

    public SellGridAdapter(ArrayList<GridModel> listGridModel) {
        this.listGridModel = listGridModel;
    }


    @Override
    public int getCount() {
        return listGridModel.size();
    }

    @Override
    public Object getItem(int position) {
        return 0;
    }

    public ArrayList<GridModel> getListGridModel() {
        return listGridModel;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageGridSellBinding binding;
        SellGridAdapter.ViewHolder holder;
        if (convertView == null) {
            binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.image_grid_sell, parent, false);
            convertView = binding.getRoot();
            convertView.setTag(binding);
        } else {
            binding = (ImageGridSellBinding) convertView.getTag();
        }
        holder = new SellGridAdapter.ViewHolder(binding);
        holder.bind(listGridModel.get(position));
        return convertView;
    }


    public void updateSellGrid() {
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder  {
        ImageGridSellBinding binding;
        View view;

        ViewHolder(ImageGridSellBinding binding) {
            super(binding.getRoot());
            this.view = binding.getRoot();
            this.binding = binding;
        }

        void bind(GridModel gridModel) {
            binding.setCost(String.valueOf(gridModel.getCost()));
            binding.setImageID(gridModel.getImageID());
            binding.executePendingBindings();
        }
    }
}
