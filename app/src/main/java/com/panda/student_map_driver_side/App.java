package com.panda.student_map_driver_side;

import android.app.Application;
import android.support.multidex.MultiDex;
import timber.log.Timber;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        Timber.plant(new Timber.DebugTree());
    }
}
