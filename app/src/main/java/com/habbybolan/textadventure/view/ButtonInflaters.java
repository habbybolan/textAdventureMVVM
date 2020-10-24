package com.habbybolan.textadventure.view;

import android.app.Activity;
import android.view.LayoutInflater;
import android.widget.GridLayout;

import androidx.databinding.DataBindingUtil;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.DefaultButtonDetailsBinding;

/**
 * Used to inflate the buttons created and added to gridViews. All programmatically added buttons are added through this class
 */
public class ButtonInflaters {

    public static DefaultButtonDetailsBinding setDefaultButton(GridLayout gridLayout, String text, Activity activity) {
        if (activity == null) throw new IllegalStateException("Activity not attached to fragment");
        DefaultButtonDetailsBinding defaultBinding = DataBindingUtil.inflate(LayoutInflater.from(gridLayout.getContext()),
                R.layout.default_button_details, gridLayout, true);
        defaultBinding.setTitle(text);
        return defaultBinding;
    }
}
