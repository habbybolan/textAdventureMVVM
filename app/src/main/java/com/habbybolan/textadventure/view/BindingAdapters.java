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

    // creates the title of a inventory snippet
    @BindingAdapter(value = {"name"})
    public static void inventorySnippetTitle(TextView view, String name) {
        String text = "You found a " + name;
        view.setText(text);
    }

    @BindingAdapter(value = {"attacker", "target", "action"})
    public static void combatDialogue(TextView view,  String attacker, String target, String action) {
        String builder = attacker +
                " uses " +
                action +
                " on " +
                target;
        view.setText(builder);
    }

    @BindingAdapter(value = {"current", "max"})
    public static void currMaxDialogue(TextView view, int current, int max) {
        String text = current +  "/" + max;
        view.setText(text);
    }

    // turns a int into a String
    @BindingAdapter(value = {"val"})
    public static void intToString(TextView view, int val) {
        String text = String.valueOf(val);
        view.setText(text);
    }


}
