package com.example.tss.cryptinfo.services

import android.content.ContentValues
import android.content.Context

import com.example.tss.cryptinfo.api.DBContract
import com.example.tss.cryptinfo.utilities.ConstantsUtils
import com.example.tss.cryptinfo.utilities.NetworkUtils
import com.google.android.gms.gcm.GcmNetworkManager
import com.google.android.gms.gcm.GcmTaskService
import com.google.android.gms.gcm.TaskParams

import java.io.IOException

import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber

class DetailTaskService : GcmTaskService {

    constructor() {

    }

    private val client = OkHttpClient()

    private var mContext: Context? = null

    constructor(context: Context) {
        mContext = context
    }

    @Throws(IOException::class)
    fun fetchData(url: String): String? {
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        return response.body()!!.string()
    }

    override fun onRunTask(taskParams: TaskParams): Int {
        if (mContext == null) {
            mContext = this
        }

        val symbol = taskParams.extras.getString("symbol")

        var result = GcmNetworkManager.RESULT_FAILURE

        if (symbol != null && !symbol.isEmpty()) {

            try {
                val histoUrl = NetworkUtils.getHistoUrl(mContext!!, symbol)
                val histoJsonStr = fetchData(histoUrl!!.toString())

                var toUpdate = false
                val now = System.currentTimeMillis()

                val c = mContext!!.contentResolver
                        .query(DBContract.CoinEntry.CONTENT_URI,
                                ConstantsUtils.COIN_COLUMNS,
                                DBContract.CoinEntry.COLUMN_SYMBOL + "= ?",
                                arrayOf(symbol), null)

                var update: Long = 0

                if (c != null && c.moveToFirst()) {
                    update = c.getLong(ConstantsUtils.POSITION_UPDATE)
                }

                if (update == 0L) {
                    toUpdate = true
                } else {
                    if (now - update > 86400000) {
                        toUpdate = true
                    }
                }

                var newsJsonStr: String? = ""
                if (toUpdate) {
                    val newsUrl = NetworkUtils.getNewsUrl(symbol)
                    newsJsonStr = fetchData(newsUrl!!.toString())
                }

                if (histoJsonStr != null && !histoJsonStr.isEmpty()) {
                    result = GcmNetworkManager.RESULT_SUCCESS

                    val value = ContentValues()

                    value.put(DBContract.CoinEntry.COLUMN_HISTO, histoJsonStr)

                    if (toUpdate && newsJsonStr!!.length > 0) {
                        value.put(DBContract.CoinEntry.COLUMN_NEWS, newsJsonStr)
                        value.put(DBContract.CoinEntry.COLUMN_UPDATE, now)
                    }

                    val coinContentResolver = mContext!!.contentResolver

                    coinContentResolver.update(DBContract.CoinEntry.CONTENT_URI,
                            value,
                            DBContract.CoinEntry.COLUMN_SYMBOL + "=?",
                            arrayOf(symbol)
                    )

                } else {
                    Timber.d("fetch historical data failed !")
                }
            } catch (e: Exception) {
                Timber.d(e.message)
                e.stackTrace
            }

        }

        return result
    }
}
