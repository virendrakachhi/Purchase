package com.hashmybag.pushnotifications;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * This class is used for implementing pusher in to the app for chat feature to the app.
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-07-25
 */

public class GCMTokenRefreshListenerService extends InstanceIDListenerService {
    //If the token is changed registering the device again
    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, GCMRegistrationIntentService.class);
        startService(intent);
    }
}
