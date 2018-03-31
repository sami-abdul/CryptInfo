package com.example.tss.cryptinfo.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;


import com.example.tss.cryptinfo.api.CoinPreferences;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import timber.log.Timber;

public class NetworkUtils {

    private static final String BASE_URL = "https://min-api.cryptocompare.com/data/";
    private static final String PARAM_FROM_SYMBOL = "fsym";
    private static final String PARAM_TO_SYMBOL = "tsym";
    private static final String PARAM_FROM_SYMBOLS = "fsyms";
    private static final String PARAM_TO_SYMBOLS = "tsyms";
    private static final String PARAM_LIMIT = "limit";
    private static final String intervalPrefix = "histo";
    private static final String PRICE_MULTI_FULL = "pricemultifull";
    private static final String PLUS_PREFIX = "+";
    private static final String CRYPTO_MUST = "++crypto+";

    private static final String NEWS_BASE_URL = "https://newsapi.org/v2/everything?";
    private static final String PARAM_QUERY = "q";
    private static final String PARAM_PAGE_SIZE = "pageSize";
    private static final String PARAM_SORT_BY = "sortBy";
    private static final String PARAM_FROM = "from";
    private static final String PARAM_API_KEY ="apiKey";
    private static final String VALUE_SORT_BY = "relevancy";

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    private static final long ONE_WEEK = 604800000;
    private static final int HISTO_LIMIT = 60;
    private static final int NEWS_LIMIT = 50;

    public static boolean isNetworkStatusAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null)
        {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if(networkInfo != null)
                if(networkInfo.isConnected())
                    return true;
        }
        return false;
    }

    public static URL getPriceUrl(Context context, String symbols){

        String unitPref = CoinPreferences.getPreferredUnit(context);

        Uri priceUri = Uri.parse(BASE_URL).buildUpon()
                .appendEncodedPath(PRICE_MULTI_FULL)
                .appendQueryParameter(PARAM_FROM_SYMBOLS, symbols)
                .appendQueryParameter(PARAM_TO_SYMBOLS, unitPref)
                .build();
        try {
            URL priceUrl = new URL(priceUri.toString());
            Timber.d("Calling url: " + priceUrl);
            return priceUrl;

        }catch (MalformedURLException e){
            Timber.d(e.getMessage());
            return null;
        }
    }

    public static URL getHistoUrl(Context context, String fromSymbol){

        String intervalPref = CoinPreferences.getPreferredInterval(context);
        String unitPref = CoinPreferences.getPreferredUnit(context);

        Uri histUri = Uri.parse(BASE_URL).buildUpon()
                .appendEncodedPath(intervalPrefix + intervalPref)
                .appendQueryParameter(PARAM_FROM_SYMBOL, fromSymbol)
                .appendQueryParameter(PARAM_TO_SYMBOL, unitPref)
                .appendQueryParameter(PARAM_LIMIT, String.valueOf(HISTO_LIMIT))
                .build();
        try {
            URL histUrl = new URL(histUri.toString());
            //Timber.d("Calling url: " + histUrl);
            return histUrl;

        }catch (MalformedURLException e){
            Timber.d(e.getMessage());
            return null;
        }
    }

    public static URL getNewsUrl(String symbol){

        long weekBefore = System.currentTimeMillis() - ONE_WEEK;

        String from = getDate(weekBefore);

        Uri newsUri = Uri.parse(NEWS_BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_QUERY, PLUS_PREFIX + symbol + CRYPTO_MUST)
                .appendQueryParameter(PARAM_PAGE_SIZE, String.valueOf(NEWS_LIMIT))
                .appendQueryParameter(PARAM_SORT_BY, VALUE_SORT_BY)
                .appendQueryParameter(PARAM_FROM, from)
//                Dangerous
//                .appendQueryParameter(PARAM_API_KEY, BuildConfig.NEWS_API_KEY)
                .build();
        try {
            URL newsUrl = new URL(newsUri.toString());
            //Timber.d("Calling url: " + newsUrl);
            return newsUrl;

        }catch (MalformedURLException e){
            Timber.d(e.getMessage());
            return null;
        }
    }

    public static String getDate(long milliSeconds)
    {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

}
