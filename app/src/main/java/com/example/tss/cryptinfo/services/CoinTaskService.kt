package com.example.tss.cryptinfo.services

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.DatabaseUtils

import com.example.tss.cryptinfo.api.Coin
import com.example.tss.cryptinfo.api.CoinDbContract
import com.example.tss.cryptinfo.utilities.CoinJsonUtils
import com.example.tss.cryptinfo.utilities.ConstantsUtils
import com.example.tss.cryptinfo.utilities.NetworkUtils
import com.google.android.gms.gcm.GcmNetworkManager
import com.google.android.gms.gcm.GcmTaskService
import com.google.android.gms.gcm.TaskParams


import java.io.IOException
import java.net.URL
import java.util.ArrayList
import java.util.Arrays

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class CoinTaskService : GcmTaskService {
    private val client = OkHttpClient()

    private var mContext: Context? = null
    private var isUpdate: Boolean = false

    private val coinSymbols = ArrayList<String>()

    constructor() {}

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

        //Timber.d("Entering onRunTask() method, params tag: " + taskParams.getTag());

        initQueryCursor = mContext!!.contentResolver
                .query(CoinDbContract.CoinEntry.CONTENT_URI, ConstantsUtils.COIN_COLUMNS, null, null, null)

        if (initQueryCursor == null || initQueryCursor.count == 0) {
            //Timber.d("before clear, number of coins: " + coinSymbols.size());
            coinSymbols.clear()
            coinSymbols.addAll(Arrays.asList(*POP_COIN_SYMBOLS))
            //Timber.d("after adding, number of coins: " + coinSymbols.size());
        } else {
            DatabaseUtils.dumpCursor(initQueryCursor)
            initQueryCursor.moveToFirst()
            for (i in 0 until initQueryCursor.count) {
                coinSymbols.add(initQueryCursor.getString(initQueryCursor.getColumnIndex("symbol")))
                initQueryCursor.moveToNext()
            }
        }

        if (taskParams.tag == "add") {
            //Timber.d("To add the symbol to list, current number of coins: " + coinSymbols.size());
            isUpdate = false

            val toAddCoin = taskParams.extras.getString("symbol")

            coinSymbols.add(0, toAddCoin)

            //Timber.d(toAddCoin + "is added: " + "current number of coins: " + coinSymbols.size());

        }

        var result = GcmNetworkManager.RESULT_FAILURE

        try {
            val coinsJsonStr = CoinJsonUtils.loadCoins(mContext!!)

            //Timber.d("First 500 chars of coins json string: " + coinsJsonStr.substring(0, 500));

            var str = ""

            for (symbol in coinSymbols) {

                str += symbol + ","
            }

            val symbolsStr = str.substring(0, str.length - 1)

            val priceUrl = NetworkUtils.getPriceUrl(mContext, symbolsStr)
            val priceJsonStr = fetchData(priceUrl!!.toString())

            println("CyptoInfo:" + priceUrl)

            //Timber.d("First 500 chars of price json string: " + priceJsonStr.substring(0, 500));

            if (priceJsonStr != null && !priceJsonStr.isEmpty()) {
                //Timber.d("result is success!");
                result = GcmNetworkManager.RESULT_SUCCESS
            } else {
                //Timber.d("fetch data failed !");
            }

            val coins = CoinJsonUtils.extractCoinsFromJson(mContext!!, coinsJsonStr!!, priceJsonStr!!, coinSymbols)

            //Timber.d("number of coin objects: " + coins.size());

            val coinValues = CoinJsonUtils.getCoinContentValueFromList(coins!!)

            //ArrayList<ContentProviderOperation> cpo = new ArrayList<ContentProviderOperation>();
            if (coinValues != null && coinValues.size != 0) {
                //Timber.d("coinvalues are not null.");
                //Timber.d("number of contentvalues: " + coinValues.length);

                for (i in coinValues.indices) {
                    //Timber.d("count for loop: " + i);
                    //Timber.d("Inserting coin symbol: " + coinValues[i].getAsString(CoinDbContract.CoinEntry.COLUMN_SYMBOL));

                    val coinContentResolver = mContext!!.contentResolver

                    val toInsert = coinValues[i]!!.getAsString(CoinDbContract.CoinEntry.COLUMN_SYMBOL)

                    val c = mContext!!.contentResolver
                            .query(CoinDbContract.CoinEntry.CONTENT_URI,
                                    //new String[]{CoinDbContract.CoinEntry.COLUMN_SYMBOL},
                                    ConstantsUtils.COIN_COLUMNS,
                                    CoinDbContract.CoinEntry.COLUMN_SYMBOL + "= ?",
                                    arrayOf(toInsert), null)

                    if (c != null && c.moveToFirst()) {
                        coinContentResolver.update(CoinDbContract.CoinEntry.CONTENT_URI,
                                coinValues[i],
                                CoinDbContract.CoinEntry.COLUMN_SYMBOL + "=?",
                                arrayOf(toInsert)
                        )
                    } else {
                        coinContentResolver.insert(CoinDbContract.CoinEntry.CONTENT_URI, coinValues[i])
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
        //Timber.d("calling updateWidget()");
        val updatedDataIntent = Intent(ACTION_DATA_UPDATED)
        updatedDataIntent.`package` = context!!.packageName
        context.sendBroadcast(updatedDataIntent)

    }

    companion object {
        private val POP_COIN_SYMBOLS = arrayOf("BTC", "ETH", "XRP", "BCH", "ADA", "TRX", "EOS", "LTC", "MTL", "CHAT")
        val ACTION_DATA_UPDATED = "ACTION_DATA_UPDATED"
    }

}
