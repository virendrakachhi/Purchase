package com.hashmybag.servercommunication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hashmybag.fragments.TabScreen;

/**
 * This class is used as a receiver.
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-07-05
 */
public class MessageReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        new TabScreen().setCount(context);
        //new ChatsFragment().refresh(context);
    }
}
