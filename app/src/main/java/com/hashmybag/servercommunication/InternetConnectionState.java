package com.hashmybag.servercommunication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hashmybag.pusharintegration.PusherChat;

/**
 * This class is used for getting instance for getting internet connection.
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-06-21
 */
public class InternetConnectionState extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectionDetector cd = new ConnectionDetector(context);
        boolean connection = cd.isConnectingToInternet();

        if (connection) {
            PusherChat.getInstance(context);
        }

    }

}
