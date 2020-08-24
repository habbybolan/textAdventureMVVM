package com.habbybolan.textadventure.view;

import android.widget.TextView;

import androidx.databinding.BindingAdapter;

public class BindingAdapters {

    // takes two stat inputs and outputs a formatted string for displaying the stat numbers
    @BindingAdapter(value = {"stat", "base"})
    public static void statText(TextView view, int stat, int base) {
        int statChanged = stat - base;
        String statFormat = stat + "(" + statChanged + ")";
        view.setText(statFormat);
    }
}
