package com.example.tss.cryptinfo;

import android.app.Application;
import android.util.Log;


import timber.log.Timber;


public class CryptoCoinApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.uprootAll();
            Timber.plant(new Timber.DebugTree());
        }

    }
}
