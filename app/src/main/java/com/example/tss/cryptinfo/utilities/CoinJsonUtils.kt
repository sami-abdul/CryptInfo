package com.example.tss.cryptinfo.utilities

import android.content.ContentValues
import android.content.Context
import android.content.res.AssetManager
import android.text.TextUtils

import com.example.tss.cryptinfo.api.Coin
import com.example.tss.cryptinfo.api.CoinDbContract
import com.example.tss.cryptinfo.api.CoinPreferences
import com.example.tss.cryptinfo.api.News

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.io.IOException
import java.io.InputStream
import java.util.ArrayList


import timber.log.Timber

object CoinJsonUtils {

    var popCoins = arrayOf("BTC", "ETH", "XRP", "BCH", "ADA", "TRX", "EOS", "LTC", "XLM", "NEM", "NEO", "WTC", "VEN", "XMR", "ICX", "ETC", "DASH", "MIOTA", "BTG", "QTUM")

    val PRICE_UNITS = arrayOf("USD", "BTC")
    var pref_price_unit = "USD"

    fun loadCoins(context: Context): String? {
        var json: String? = null
        val mngr = context.assets

        try {
            val inputStream = mngr.open("coinlist.json")

            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()

            json = String(buffer)
            // dangerous
//            json = String(buffer, "UTF-8")
            String()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return json
    }

    fun isSymbolValid(context: Context, symbol: String): Boolean {
        var isValid = false

        try {
            val coinsStr = loadCoins(context)
            val coinJsonResponse = JSONObject(coinsStr)
            val dataObject = coinJsonResponse.getJSONObject("Data")

            val symbolObject = dataObject.getJSONObject(symbol)

            if (symbolObject != null) {
                isValid = true
            }

        } catch (e: JSONException) {
            e.stackTrace
        }

        return isValid
    }

    fun extractCoinsFromJson(context: Context, coinJsonStr: String, priceJsonStr: String, symbols: List<String>): List<Coin>? {

        val unitPref = CoinPreferences.getPreferredUnit(context)
        //Timber.d("Entering extractCoinsFromJson() method...");
        if (TextUtils.isEmpty(coinJsonStr) || TextUtils.isEmpty(priceJsonStr)) {
            return null
        }

        //Timber.d("size of symbols: " + symbols.size());

        val coins = ArrayList<Coin>()

        try {
            val coinJsonResponse = JSONObject(coinJsonStr)
            val dataObject = coinJsonResponse.getJSONObject("Data")

            //Timber.d("first 500 chars of data object: " + dataObject.toString().substring(0, 500));

            val priceJsonResponse = JSONObject(priceJsonStr)
            val rawPriceObject = priceJsonResponse.getJSONObject("RAW")

            //Timber.d("price object: " + rawPriceObject.toString());

            for (symbol in symbols) {
                //Timber.d("Coin symbol: " + symbol);

                val coin = Coin(symbol)

                val coinProperties = dataObject.getJSONObject(symbol)

                if (coinProperties != null) {
                    //Timber.d(symbol + "'s coinProperties: " + coinProperties.toString());
                    val coinName = coinProperties.getString("CoinName")
                    val url = coinProperties.getString("Url")
                    val imageUrl = coinProperties.getString("ImageUrl")
                    val algorithm = coinProperties.getString("Algorithm")
                    val proofType = coinProperties.getString("ProofType")
                    val tsStr = coinProperties.getString("TotalCoinSupply")
                    var sponsor = 0
                    val sponsored = coinProperties.getBoolean("Sponsored")

                    if (sponsored) {
                        sponsor = 1
                    }

                    coin.coinName = coinName
                    coin.url = url
                    coin.imageUrl = imageUrl
                    coin.algorithm = algorithm
                    coin.proofType = proofType
                    coin.sponsor = sponsor

                    var totalSupply: Long = 0
                    if (tsStr.length > 5) {
                        totalSupply = java.lang.Long.valueOf(tsStr)!!
                    }

                    coin.totalSupply = totalSupply
                }

                val coinPriceObject = rawPriceObject.getJSONObject(symbol)
                //Timber.d(symbol + "'s RAW price data: " + coinPriceObject.toString());
                val priceProperties = coinPriceObject.getJSONObject(unitPref)
                //Timber.d(symbol + "'s price data: " + priceProperties.toString());

                if (priceProperties != null) {

                    val price: Double
                    val open24h: Double
                    val high24h: Double
                    val low24h: Double
                    val trend: Double
                    val change: Double

                    Timber.d("symbol: $symbol, unit: $unitPref")

                    if (symbol == "BTC" && unitPref == "BTC") {
                        Timber.d("BTC special cases")
                        //price = Double.parseDouble(priceProperties.getString("PRICE"));
                        price = 1.0000
                        Timber.d(symbol + " price: " + price)
                        open24h = java.lang.Double.parseDouble(priceProperties.getString("OPEN24HOUR"))
                        Timber.d(symbol + " open24h: " + open24h)
                        high24h = java.lang.Double.parseDouble(priceProperties.getString("HIGH24HOUR"))
                        Timber.d(symbol + " high24h: " + high24h)
                        low24h = java.lang.Double.parseDouble(priceProperties.getString("LOW24HOUR"))
                        Timber.d(symbol + " low24h: " + low24h)
                        trend = 0.00
                        change = 0.00
                    } else {
                        price = priceProperties.getDouble("PRICE")
                        open24h = priceProperties.getDouble("OPEN24HOUR")
                        high24h = priceProperties.getDouble("HIGH24HOUR")
                        low24h = priceProperties.getDouble("LOW24HOUR")
                        trend = priceProperties.getDouble("CHANGEPCT24HOUR")
                        change = priceProperties.getDouble("CHANGE24HOUR")
                    }

                    val mktcap = priceProperties.getDouble("MKTCAP")
                    val vol24h = priceProperties.getDouble("VOLUME24HOUR")
                    val vol24h2 = priceProperties.getDouble("VOLUME24HOURTO")


                    val supply = priceProperties.getDouble("SUPPLY")

                    coin.price = price
                    coin.mktcap = mktcap
                    coin.vol24h = vol24h
                    coin.vol24h2 = vol24h2
                    coin.open24h = open24h
                    coin.high24h = high24h
                    coin.low24h = low24h
                    coin.trend = trend
                    coin.change = change
                    coin.supply = supply

                    //Timber.d(coin.getSymbol() + "Price: " + coin.getPrice());
                }

                coins.add(coin)
            }

        } catch (e: JSONException) {
            e.stackTrace
        }

        return coins
    }

    fun getCoinContentValueFromList(coins: List<Coin>): Array<ContentValues?> {
        //Timber.d("Entering getCoinContentValueFromList() mehtod...");

        Timber.d("number of coins: " + coins.size)
        val contentValues = arrayOfNulls<ContentValues>(coins.size)

        var i = 0
        for (coin in coins) {
            //Timber.d("Iterate coins: " + coin.getCoinName());
            val value = getContentValueFromCoin(coin)
            contentValues[i] = value
            i++
        }

        //Timber.d("number of content values: " + (i+1));

        return contentValues
    }

    fun getContentValueFromCoin(coin: Coin): ContentValues {
        val value = ContentValues()

        value.put(CoinDbContract.CoinEntry.COLUMN_SYMBOL, coin.symbol)
        value.put(CoinDbContract.CoinEntry.COLUMN_NAME, coin.coinName)
        value.put(CoinDbContract.CoinEntry.COLUMN_COIN_URL, coin.url)
        value.put(CoinDbContract.CoinEntry.COLUMN_IMAGE_URL, coin.imageUrl)
        value.put(CoinDbContract.CoinEntry.COLUMN_ALGORITHM, coin.algorithm)
        value.put(CoinDbContract.CoinEntry.COLUMN_PROOF_TYPE, coin.proofType)
        value.put(CoinDbContract.CoinEntry.COLUMN_TOTAL_SUPPLY, coin.totalSupply)
        value.put(CoinDbContract.CoinEntry.COLUMN_SPONSOR, coin.sponsor)
        value.put(CoinDbContract.CoinEntry.COLUMN_SUPPLY, coin.supply)
        value.put(CoinDbContract.CoinEntry.COLUMN_PRICE, coin.price)
        value.put(CoinDbContract.CoinEntry.COLUMN_MKTCAP, coin.mktcap)
        value.put(CoinDbContract.CoinEntry.COLUMN_VOL24H, coin.vol24h)
        value.put(CoinDbContract.CoinEntry.COLUMN_VOL24H2, coin.vol24h2)
        value.put(CoinDbContract.CoinEntry.COLUMN_OPEN24H, coin.open24h)
        value.put(CoinDbContract.CoinEntry.COLUMN_HIGH24H, coin.high24h)
        value.put(CoinDbContract.CoinEntry.COLUMN_LOW24H, coin.low24h)
        value.put(CoinDbContract.CoinEntry.COLUMN_TREND, coin.trend)
        value.put(CoinDbContract.CoinEntry.COLUMN_CHANGE, coin.change)

        return value
    }

    fun extractNewsFromJson(newsJSON: String): List<News>? {

        if (TextUtils.isEmpty(newsJSON)) {
            return null
        }

        val newses = ArrayList<News>()

        try {
            val baseJsonResponse = JSONObject(newsJSON)

            val newsArray = baseJsonResponse.getJSONArray("articles")

            for (i in 0 until newsArray.length()) {

                val currentNews = newsArray.getJSONObject(i)

                val imageUrl = currentNews.getString("urlToImage")
                val title = currentNews.getString("title")
                val description = currentNews.getString("description")
                val time = currentNews.getString("publishedAt")
                val url = currentNews.getString("url")

                val sourceObj = currentNews.getJSONObject("source")
                val source = sourceObj.getString("name")

                if (source != "Python.org") {
                    val news = News(imageUrl, title, description, time, source, url)
                    newses.add(news)
                }
            }

        } catch (e: JSONException) {
            e.stackTrace
        }

        return newses
    }


}