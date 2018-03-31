package com.example.tss.cryptinfo.api.sync

import android.app.IntentService
import android.content.Intent
import android.os.Bundle

import com.example.tss.cryptinfo.R
import com.google.android.gms.gcm.TaskParams

class DetailIntentService : IntentService("DetailIntentService") {

    override fun onHandleIntent(intent: Intent?) {
        val detailTaskService = DetailTaskService(this)

        val args = Bundle()

        if (intent!!.getStringExtra(getString(R.string.tag_tag)) == getString(R.string.tag_detail_value)) {
            args.putString(getString(R.string.symbol_tag), intent.getStringExtra(getString(R.string.symbol_tag)))
        }

        try {
            detailTaskService.onRunTask(TaskParams(intent.getStringExtra(getString(R.string.tag_tag)), args))
        } catch (e: Exception) {
            e.stackTrace
        }

    }
}
