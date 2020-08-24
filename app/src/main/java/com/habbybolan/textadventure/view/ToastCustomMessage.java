package com.habbybolan.textadventure.view;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.habbybolan.textadventure.R;

/**
 * creates a custom toast message
 */

public class ToastCustomMessage {
    // todo: create a proper custom toast message
    public static void showToast(Activity activity, String message, Toast toast) {
        try {
            if (!toast.getView().isShown()) toast.show();
        } catch (Exception e) {
            // throws exception if no view is shown - create toast
            createUpToast(activity, message, toast);
        }
    }

    // helper for toastCustomMessage - creates the toast object
    private static void createUpToast(Activity activity, String message, Toast toast) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) activity.findViewById(R.id.toast_container));
        TextView text = layout.findViewById(R.id.txt_full);
        text.setText(message);
        text.setTextSize(20);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 100);
        toast.setView(layout);
        toast.show();
    }
}
