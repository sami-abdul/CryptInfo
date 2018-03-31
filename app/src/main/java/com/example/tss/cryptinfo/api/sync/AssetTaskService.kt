package com.example.tss.cryptinfo.api.sync

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.DatabaseUtils

import com.example.tss.cryptinfo.api.data.DBContract
import com.example.tss.cryptinfo.utilities.JSONUtils
import com.example.tss.cryptinfo.utilities.ConstantsUtils
import com.example.tss.cryptinfo.utilities.NetworkUtils
import com.google.android.gms.gcm.GcmNetworkManager
import com.google.android.gms.gcm.GcmTaskService
import com.google.android.gms.gcm.TaskParams


import java.io.IOException
import java.util.ArrayList
import java.util.Arrays

import okhttp3.OkHttpClient
import okhttp3.Request

class AssetTaskService : GcmTaskService {

    companion object {
        private val POP_COIN_SYMBOLS = arrayOf("BTC")
        val ACTION_DATA_UPDATED = "ACTION_DATA_UPDATED"
    }

    constructor() {

    }

    private val client = OkHttpClient()

    private var mContext: Context? = null
    private var isUpdate: Boolean = false

    private val coinSymbols = ArrayList<String>()

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
        val initQueryCursor: Cursor?

        if (mContext == null) {
            mContext = this
        }

        initQueryCursor = mContext!!.contentResolver
                .query(DBContract.CoinEntry.CONTENT_URI, ConstantsUtils.COIN_COLUMNS, null, null, null)

        if (initQueryCursor == null || initQueryCursor.count == 0) {
            coinSymbols.clear()
            coinSymbols.addAll(Arrays.asList(*POP_COIN_SYMBOLS))
        } else {
            DatabaseUtils.dumpCursor(initQueryCursor)
            initQueryCursor.moveToFirst()
            for (i in 0 until initQueryCursor.count) {
                coinSymbols.add(initQueryCursor.getString(initQueryCursor.getColumnIndex("symbol")))
                initQueryCursor.moveToNext()
            }
        }

        if (taskParams.tag == "add") {
            isUpdate = false
            val toAddCoin = taskParams.extras.getString("symbol")
            coinSymbols.add(0, toAddCoin)
        }

        var result = GcmNetworkManager.RESULT_FAILURE

        try {
            val coinsJsonStr = JSONUtils.loadCoins(mContext!!)

            var str = ""

            for (symbol in coinSymbols) {

                str += symbol + ","
            }

            val symbolsStr = str.substring(0, str.length - 1)

            val priceUrl = NetworkUtils.getPriceUrl(mContext!!, symbolsStr)
            val priceJsonStr = fetchData(priceUrl!!.toString())

            println("CyptoInfo:" + priceUrl)

            if (priceJsonStr != null && !priceJsonStr.isEmpty()) {
                result = GcmNetworkManager.RESULT_SUCCESS
            }

            val coins = JSONUtils.extractCoinsFromJson(mContext!!, coinsJsonStr!!, priceJsonStr!!, coinSymbols)

            val coinValues = JSONUtils.getCoinContentValueFromList(coins!!)

            if (coinValues != null && coinValues.size != 0) {
                for (i in coinValues.indices) {
                    val coinContentResolver = mContext!!.contentResolver

                    val toInsert = coinValues[i]!!.getAsString(DBContract.CoinEntry.COLUMN_SYMBOL)

                    val c = mContext!!.contentResolver
                            .query(DBContract.CoinEntry.CONTENT_URI,
                                    ConstantsUtils.COIN_COLUMNS,
                                    DBContract.CoinEntry.COLUMN_SYMBOL + "= ?",
                                    arrayOf(toInsert), null)

                    if (c != null && c.moveToFirst()) {
                        coinContentResolver.update(DBContract.CoinEntry.CONTENT_URI,
                                coinValues[i],
                                DBContract.CoinEntry.COLUMN_SYMBOL + "=?",
                                arrayOf(toInsert)
                        )
                    } else {
                        coinContentResolver.insert(DBContract.CoinEntry.CONTENT_URI, coinValues[i])
                    }
                }

                updateWidget(mContext)
            }
        } catch (e: Exception) {
            e.stackTrace
        }

        return result
    }

    private fun updateWidget(context: Context?) {
        val updatedDataIntent = Intent(ACTION_DATA_UPDATED)
        updatedDataIntent.`package` = context!!.packageName
        context.sendBroadcast(updatedDataIntent)

    }
}
