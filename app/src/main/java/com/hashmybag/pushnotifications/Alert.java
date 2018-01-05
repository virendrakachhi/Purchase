package com.hashmybag.pushnotifications;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import com.hashmybag.R;

/**
 * This class is handling all the chat activities.
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-07-28
 */

public class Alert extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.alertlayout);
    }

    /**
     * Location source setting(GCM)
     *
     * @param view
     */

    public void gpsSetting(View view) {
        try {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void gpsCancel(View view) {
        onBackPressed();
        finish();
    }

}
