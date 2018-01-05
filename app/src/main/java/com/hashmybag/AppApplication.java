package com.hashmybag;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by cp-android on 11/8/16.
 */
public class AppApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
