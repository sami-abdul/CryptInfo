package com.example.tss.cryptinfo.utilities

import android.content.Context
import android.net.ConnectivityManager
import android.net.Uri


import com.example.tss.cryptinfo.api.data.AssetPreferences

import java.net.MalformedURLException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar

import timber.log.Timber

object NetworkUtils {

    private val BASE_URL = "https://min-api.cryptocompare.com/data/"
    private val PARAM_FROM_SYMBOL = "fsym"
    private val PARAM_TO_SYMBOL = "tsym"
    private val PARAM_FROM_SYMBOLS = "fsyms"
    private val PARAM_TO_SYMBOLS = "tsyms"
    private val PARAM_LIMIT = "limit"
    private val intervalPrefix = "histo"
    private val PRICE_MULTI_FULL = "pricemultifull"
    private val PLUS_PREFIX = "+"
    private val CRYPTO_MUST = "++crypto+"

    private val NEWS_BASE_URL = "https://newsapi.org/v2/everything?"
    private val PARAM_QUERY = "q"
    private val PARAM_PAGE_SIZE = "pageSize"
    private val PARAM_SORT_BY = "sortBy"
    private val PARAM_FROM = "from"
    private val PARAM_API_KEY = "apiKey"
    private val VALUE_SORT_BY = "relevancy"

    private val DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"

    private val ONE_WEEK: Long = 604800000
    private val HISTO_LIMIT = 60
    private val NEWS_LIMIT = 50

    fun isNetworkStatusAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val networkInfo = connectivityManager.activeNetworkInfo
            if (networkInfo != null)
                if (networkInfo.isConnected)
                    return true
        }
        return false
    }

    fun getPriceUrl(context: Context, symbols: String): URL? {

        val unitPref = AssetPreferences.getPreferredUnit(context)

        val priceUri = Uri.parse(BASE_URL).buildUpon()
                .appendEncodedPath(PRICE_MULTI_FULL)
                .appendQueryParameter(PARAM_FROM_SYMBOLS, symbols)
                .appendQueryParameter(PARAM_TO_SYMBOLS, unitPref)
                .build()
        try {
            val priceUrl = URL(priceUri.toString())
            Timber.d("Calling url: " + priceUrl)
            return priceUrl

        } catch (e: MalformedURLException) {
            Timber.d(e.message)
            return null
        }

    }

    fun getHistoUrl(context: Context, fromSymbol: String): URL? {

        val intervalPref = AssetPreferences.getPreferredInterval(context)
        val unitPref = AssetPreferences.getPreferredUnit(context)

        val histUri = Uri.parse(BASE_URL).buildUpon()
                .appendEncodedPath(intervalPrefix + intervalPref)
                .appendQueryParameter(PARAM_FROM_SYMBOL, fromSymbol)
                .appendQueryParameter(PARAM_TO_SYMBOL, unitPref)
                .appendQueryParameter(PARAM_LIMIT, HISTO_LIMIT.toString())
                .build()
        try {
            return URL(histUri.toString())

        } catch (e: MalformedURLException) {
            Timber.d(e.message)
            return null
        }

    }

    fun getNewsUrl(symbol: String): URL? {
        val weekBefore = System.currentTimeMillis() - ONE_WEEK

        val from = getDate(weekBefore)

        val newsUri = Uri.parse(NEWS_BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_QUERY, PLUS_PREFIX + symbol + CRYPTO_MUST)
                .appendQueryParameter(PARAM_PAGE_SIZE, NEWS_LIMIT.toString())
                .appendQueryParameter(PARAM_SORT_BY, VALUE_SORT_BY)
                .appendQueryParameter(PARAM_FROM, from)
                .build()
        try {
            return URL(newsUri.toString())

        } catch (e: MalformedURLException) {
            Timber.d(e.message)
            return null
        }

    }

    fun getDate(milliSeconds: Long): String {
        val formatter = SimpleDateFormat(DATE_FORMAT)
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }
}
