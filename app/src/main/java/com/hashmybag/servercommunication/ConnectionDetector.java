package com.hashmybag.servercommunication;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * This class is used for detecting internet connection in the app.
 *
 * @author CanopusInfoSystems
 * @version 1.0
 * @since 2016-06-07
 */

public class ConnectionDetector {
    private Context _context;

    public ConnectionDetector(Context context) {
        this._context = context;
    }

    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean have = false;
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        have = true;
                    }

        }
        return have;
    }
}
