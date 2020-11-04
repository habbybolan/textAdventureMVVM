package com.habbybolan.textadventure.view;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import androidx.databinding.DataBindingUtil;

import com.habbybolan.textadventure.R;
import com.habbybolan.textadventure.databinding.DungeonPopupInfoBinding;
import com.habbybolan.textadventure.databinding.SimpleTempPopupMessageBinding;

public class CustomPopupWindow {

    /**
     * Sets up the popup window for the info about the dungeon.
     * @param info  The String info to display in a textView about the dungeon
     * @param context       Context of the activity/fragment popup window is being displayed in
     * @param popupWindow   The created popup Window to set up and display
     * @param container     The container to add the popup window to
     */
    public static void setDungeonInfo(String info, Context context, final PopupWindow popupWindow, ViewGroup container) {
        // if the popup is not already showing, then display it
        if (!popupWindow.isShowing()) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            DungeonPopupInfoBinding binding = DataBindingUtil.inflate(inflater, R.layout.dungeon_popup_info, null, false);
            // parameters
            popupWindow.setContentView(binding.getRoot());
            popupWindow.setWidth(RelativeLayout.LayoutParams.WRAP_CONTENT);
            popupWindow.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
            // animation
            popupWindow.setAnimationStyle(R.style.DungeonPopupAnimation);
            binding.setTxtInfo(info);
            // set clicker to kill the popup
            binding.btnClosePopup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });
            popupWindow.showAtLocation(container, Gravity.CENTER, 0, 0);
        }
    }

    /**
     * Sets up the temporary popup window that displays a message for a short time.
     * @param message       The String message to display on the popup window
     * @param context       Context of the activity/fragment popup window is being displayed in
     * @param popupWindow   The created popup Window to set up and display
     * @param container     The container to add the popup window to
     */
    public static void setTempMessage(String message, Context context, final PopupWindow popupWindow, ViewGroup container) {
        // if the popup is not already showing, then display it
        if (!popupWindow.isShowing()) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            SimpleTempPopupMessageBinding binding = DataBindingUtil.inflate(inflater, R.layout.simple_temp_popup_message, null, false);
            // parameters
            popupWindow.setContentView(binding.getRoot());
            popupWindow.setWidth(RelativeLayout.LayoutParams.WRAP_CONTENT);
            popupWindow.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
            // animation
            popupWindow.setAnimationStyle(R.style.DungeonPopupAnimation);
            binding.setMessage(message);
            popupWindow.showAtLocation(container, Gravity.BOTTOM, 0, 0);

            new Handler().postDelayed(new Runnable(){
                public void run() {
                    popupWindow.dismiss();
                }
            }, 2 * 1000);
        }
    }
}
