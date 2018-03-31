package com.example.tss.cryptinfo.services

import android.app.IntentService
import android.content.Intent
import android.os.Bundle

import com.example.tss.cryptinfo.R
import com.google.android.gms.gcm.TaskParams

class AssetSyncIntentService : IntentService("AssetSyncIntentService") {

    override fun onHandleIntent(intent: Intent?) {
        val coinTaskService = AssetTaskService(this)

        val args = Bundle()

        if (intent!!.getStringExtra(getString(R.string.tag_tag)) == getString(R.string.tag_add_value)) {
            args.putString(getString(R.string.symbol_tag), intent.getStringExtra(getString(R.string.symbol_tag)))
        }

        try {
            coinTaskService.onRunTask(TaskParams(intent.getStringExtra(getString(R.string.tag_tag)), args))
        } catch (e: Exception) {
            e.stackTrace
        }
    }
}
