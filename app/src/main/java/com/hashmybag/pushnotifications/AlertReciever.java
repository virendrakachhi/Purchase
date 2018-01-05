package com.hashmybag.pushnotifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.WindowManager;

import com.hashmybag.R;

/**
 * Created by cp-android on 8/8/16.
 */
public class AlertReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("", "");
        showSettingsAlert(context);
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     */
    public void showSettingsAlert(final Context context) {
        try {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.AppTheme);

            // Setting Dialog Title
            alertDialog.setTitle("GPS is settings");
            alertDialog.setIcon(R.mipmap.notification_bag);
            // Setting Dialog Message
            alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
            // On pressing Settings button
            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    dialog.cancel();
                    dialog.dismiss();
                }
            });

            // on pressing cancel button
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    dialog.dismiss();

                }
            });

            AlertDialog alert = alertDialog.create();
            alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            alert.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
