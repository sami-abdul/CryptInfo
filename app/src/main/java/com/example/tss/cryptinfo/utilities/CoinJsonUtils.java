package com.example.tss.cryptinfo.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;

import com.example.tss.cryptinfo.data.CoinDbContract;
import com.example.tss.cryptinfo.data.CoinPreferences;
import com.example.tss.cryptinfo.model.Coin;
import com.example.tss.cryptinfo.model.News;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


import timber.log.Timber;

public class CoinJsonUtils {

    public static String[] popCoins = {"BTC", "ETH", "XRP", "BCH", "ADA", "TRX", "EOS", "LTC",
            "XLM", "NEM", "NEO", "WTC", "VEN", "XMR", "ICX", "ETC", "DASH", "MIOTA", "BTG", "QTUM"};

    public static final String[] PRICE_UNITS ={"USD", "BTC"};
    public static String pref_price_unit = "USD";

    public static String loadCoins(Context context) {
        String json = null;
        AssetManager mngr = context.getAssets();

        try {
            InputStream inputStream = mngr.open("coinlist.json");

            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();

            json = new String(buffer, "UTF-8");
        }catch (IOException e){
            e.printStackTrace();
        }

        return json;
    }

    public static boolean isSymbolValid(Context context, String symbol){
        boolean isValid = false;

        try {
            String coinsStr = loadCoins(context);
            JSONObject coinJsonResponse = new JSONObject(coinsStr);
            JSONObject dataObject = coinJsonResponse.getJSONObject("Data");

            JSONObject symbolObject = dataObject.getJSONObject(symbol);

            if (symbolObject != null ){
                isValid = true;
            }

        }catch (JSONException e){
            e.getStackTrace();
        }

        return isValid;
    }

    public static List<Coin> extractCoinsFromJson(Context context, String coinJsonStr, String priceJsonStr, List<String> symbols){

        String unitPref = CoinPreferences.getPreferredUnit(context);
        //Timber.d("Entering extractCoinsFromJson() method...");
        if (TextUtils.isEmpty(coinJsonStr) || TextUtils.isEmpty(priceJsonStr)){
            return null;
        }

        //Timber.d("size of symbols: " + symbols.size());

        List<Coin> coins = new ArrayList<>();

        try {
            JSONObject coinJsonResponse = new JSONObject(coinJsonStr);
            JSONObject dataObject = coinJsonResponse.getJSONObject("Data");

            //Timber.d("first 500 chars of data object: " + dataObject.toString().substring(0, 500));

            JSONObject priceJsonResponse = new JSONObject(priceJsonStr);
            JSONObject rawPriceObject = priceJsonResponse.getJSONObject("RAW");

            //Timber.d("price object: " + rawPriceObject.toString());

            for (String symbol : symbols){
                //Timber.d("Coin symbol: " + symbol);

                Coin coin = new Coin(symbol);

                JSONObject coinProperties = dataObject.getJSONObject(symbol);

                if (coinProperties != null){
                    //Timber.d(symbol + "'s coinProperties: " + coinProperties.toString());
                    String coinName = coinProperties.getString("CoinName");
                    String url = coinProperties.getString("Url");
                    String imageUrl = coinProperties.getString("ImageUrl");
                    String algorithm = coinProperties.getString("Algorithm");
                    String proofType = coinProperties.getString("ProofType");
                    String tsStr = coinProperties.getString("TotalCoinSupply");
                    int sponsor = 0;
                    boolean sponsored = coinProperties.getBoolean("Sponsored");

                    if (sponsored) {
                        sponsor = 1;
                    }

                    coin.setCoinName(coinName);
                    coin.setUrl(url);
                    coin.setImageUrl(imageUrl);
                    coin.setAlgorithm(algorithm);
                    coin.setProofType(proofType);
                    coin.setSponsor(sponsor);

                    long totalSupply = 0;
                    if (tsStr.length() > 5){
                        totalSupply = Long.valueOf(tsStr);
                    }

                    coin.setTotalSupply(totalSupply);
                }

                JSONObject coinPriceObject = rawPriceObject.getJSONObject(symbol);
                //Timber.d(symbol + "'s RAW price data: " + coinPriceObject.toString());
                JSONObject priceProperties = coinPriceObject.getJSONObject(unitPref);
                //Timber.d(symbol + "'s price data: " + priceProperties.toString());

                if (priceProperties != null){

                    double price, open24h, high24h, low24h, trend, change;

                    Timber.d("symbol: " + symbol + ", unit: " + unitPref);

                    if (symbol.equals("BTC") && unitPref.equals("BTC")) {
                        Timber.d("BTC special cases");
                        //price = Double.parseDouble(priceProperties.getString("PRICE"));
                        price = 1.0000;
                        Timber.d(symbol + " price: " + price);
                        open24h = Double.parseDouble(priceProperties.getString("OPEN24HOUR"));
                        Timber.d(symbol + " open24h: " + open24h);
                        high24h = Double.parseDouble(priceProperties.getString("HIGH24HOUR"));
                        Timber.d(symbol + " high24h: " + high24h);
                        low24h = Double.parseDouble(priceProperties.getString("LOW24HOUR"));
                        Timber.d(symbol + " low24h: " + low24h);
                        trend = 0.00;
                        change = 0.00;
                    }else {
                        price = priceProperties.getDouble("PRICE");
                        open24h = priceProperties.getDouble("OPEN24HOUR");
                        high24h = priceProperties.getDouble("HIGH24HOUR");
                        low24h = priceProperties.getDouble("LOW24HOUR");
                        trend = priceProperties.getDouble("CHANGEPCT24HOUR");
                        change = priceProperties.getDouble("CHANGE24HOUR");
                    }

                    double mktcap = priceProperties.getDouble("MKTCAP");
                    double vol24h = priceProperties.getDouble("VOLUME24HOUR");
                    double vol24h2 = priceProperties.getDouble("VOLUME24HOURTO");


                    double supply = priceProperties.getDouble("SUPPLY");

                    coin.setPrice(price);
                    coin.setMktcap(mktcap);
                    coin.setVol24h(vol24h);
                    coin.setVol24h2(vol24h2);
                    coin.setOpen24h(open24h);
                    coin.setHigh24h(high24h);
                    coin.setLow24h(low24h);
                    coin.setTrend(trend);
                    coin.setChange(change);
                    coin.setSupply(supply);

                    //Timber.d(coin.getSymbol() + "Price: " + coin.getPrice());
                }

                coins.add(coin);
            }

        }catch (JSONException e){
            e.getStackTrace();
        }

        return coins;
    }

    public static ContentValues[] getCoinContentValueFromList(List<Coin> coins){
        //Timber.d("Entering getCoinContentValueFromList() mehtod...");

        Timber.d("number of coins: " + coins.size());
        ContentValues[] contentValues = new ContentValues[coins.size()];

        int i = 0;
        for (Coin coin : coins){
            //Timber.d("Iterate coins: " + coin.getCoinName());
            ContentValues value = getContentValueFromCoin(coin);
            contentValues[i] = value;
            i++;
        }

        //Timber.d("number of content values: " + (i+1));

        return contentValues;
    }

    public static ContentValues getContentValueFromCoin(Coin coin){
        ContentValues value = new ContentValues();

        value.put(CoinDbContract.CoinEntry.COLUMN_SYMBOL, coin.getSymbol());
        value.put(CoinDbContract.CoinEntry.COLUMN_NAME, coin.getCoinName());
        value.put(CoinDbContract.CoinEntry.COLUMN_COIN_URL, coin.getUrl());
        value.put(CoinDbContract.CoinEntry.COLUMN_IMAGE_URL, coin.getImageUrl());
        value.put(CoinDbContract.CoinEntry.COLUMN_ALGORITHM, coin.getAlgorithm());
        value.put(CoinDbContract.CoinEntry.COLUMN_PROOF_TYPE, coin.getProofType());
        value.put(CoinDbContract.CoinEntry.COLUMN_TOTAL_SUPPLY, coin.getTotalSupply());
        value.put(CoinDbContract.CoinEntry.COLUMN_SPONSOR, coin.getSponsor());
        value.put(CoinDbContract.CoinEntry.COLUMN_SUPPLY, coin.getSupply());
        value.put(CoinDbContract.CoinEntry.COLUMN_PRICE, coin.getPrice());
        value.put(CoinDbContract.CoinEntry.COLUMN_MKTCAP, coin.getMktcap());
        value.put(CoinDbContract.CoinEntry.COLUMN_VOL24H, coin.getVol24h());
        value.put(CoinDbContract.CoinEntry.COLUMN_VOL24H2, coin.getVol24h2());
        value.put(CoinDbContract.CoinEntry.COLUMN_OPEN24H, coin.getOpen24h());
        value.put(CoinDbContract.CoinEntry.COLUMN_HIGH24H, coin.getHigh24h());
        value.put(CoinDbContract.CoinEntry.COLUMN_LOW24H, coin.getLow24h());
        value.put(CoinDbContract.CoinEntry.COLUMN_TREND, coin.getTrend());
        value.put(CoinDbContract.CoinEntry.COLUMN_CHANGE, coin.getChange());

        return value;
    }

    public static List<News> extractNewsFromJson(String newsJSON){

        if(TextUtils.isEmpty(newsJSON)){
            return null;
        }

        List<News> newses = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(newsJSON);

            JSONArray newsArray = baseJsonResponse.getJSONArray("articles");

            for(int i=0; i<newsArray.length(); i++){

                JSONObject currentNews = newsArray.getJSONObject(i);

                String imageUrl = currentNews.getString("urlToImage");
                String title = currentNews.getString("title");
                String description = currentNews.getString("description");
                String time = currentNews.getString("publishedAt");
                String url = currentNews.getString("url");

                JSONObject sourceObj = currentNews.getJSONObject("source");
                String source = sourceObj.getString("name");

                if (!source.equals("Python.org")){
                    News news = new News(imageUrl, title, description, time, source, url);
                    newses.add(news);
                }
            }

        }catch (JSONException e){
            e.getStackTrace();
        }

        return newses;
    }


}