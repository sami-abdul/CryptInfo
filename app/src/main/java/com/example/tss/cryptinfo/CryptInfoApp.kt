package com.example.tss.cryptinfo

import android.app.Application
import android.util.Log


import timber.log.Timber

class CryptInfoApp : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.uprootAll()
            Timber.plant(Timber.DebugTree())
        }

    }
}
