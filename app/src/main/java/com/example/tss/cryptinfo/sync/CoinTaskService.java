package com.example.tss.cryptinfo.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;

import com.example.tss.cryptinfo.data.CoinDbContract;
import com.example.tss.cryptinfo.model.Coin;
import com.example.tss.cryptinfo.utilities.CoinJsonUtils;
import com.example.tss.cryptinfo.utilities.ConstantsUtils;
import com.example.tss.cryptinfo.utilities.NetworkUtils;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

public class CoinTaskService extends GcmTaskService {
    private static final String[] POP_COIN_SYMBOLS = {"BTC", "ETH", "XRP", "BCH", "ADA", "TRX", "EOS", "LTC", "MTL", "CHAT"};
    public static final String ACTION_DATA_UPDATED = "ACTION_DATA_UPDATED";
    private OkHttpClient client = new OkHttpClient();

    private Context mContext;
    private boolean isUpdate;

    private List<String> coinSymbols = new ArrayList<>();

    public CoinTaskService() {}

    public CoinTaskService(Context context) {
        mContext = context;
    }

    public String fetchData(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    @Override
    public int onRunTask(TaskParams taskParams) {
        Cursor initQueryCursor;

        if (mContext == null){
            mContext = this;
        }

        //Timber.d("Entering onRunTask() method, params tag: " + taskParams.getTag());

        initQueryCursor = mContext.getContentResolver()
                .query(CoinDbContract.CoinEntry.CONTENT_URI, ConstantsUtils.COIN_COLUMNS, null, null, null);

        if (initQueryCursor == null || initQueryCursor.getCount() == 0){
            //Timber.d("before clear, number of coins: " + coinSymbols.size());
            coinSymbols.clear();
            coinSymbols.addAll(Arrays.asList(POP_COIN_SYMBOLS));
            //Timber.d("after adding, number of coins: " + coinSymbols.size());
        }else{
            DatabaseUtils.dumpCursor(initQueryCursor);
            initQueryCursor.moveToFirst();
            for (int i = 0; i < initQueryCursor.getCount(); i++){
                coinSymbols.add(initQueryCursor.getString(initQueryCursor.getColumnIndex("symbol")));
                initQueryCursor.moveToNext();
            }
        }

        if (taskParams.getTag().equals("add")){
            //Timber.d("To add the symbol to list, current number of coins: " + coinSymbols.size());
            isUpdate = false;

            String toAddCoin = taskParams.getExtras().getString("symbol");

            coinSymbols.add(0, toAddCoin);

            //Timber.d(toAddCoin + "is added: " + "current number of coins: " + coinSymbols.size());

        }

        int result = GcmNetworkManager.RESULT_FAILURE;

        try {
            String coinsJsonStr = CoinJsonUtils.loadCoins(mContext);

            //Timber.d("First 500 chars of coins json string: " + coinsJsonStr.substring(0, 500));

            String str = "";

            for (String symbol : coinSymbols) {

                str += symbol + ",";
            }

            String symbolsStr = str.substring(0, str.length()-1);

            URL priceUrl = NetworkUtils.getPriceUrl(mContext, symbolsStr);
            String priceJsonStr = fetchData(priceUrl.toString());

            //Timber.d("First 500 chars of price json string: " + priceJsonStr.substring(0, 500));

            if (priceJsonStr != null && !priceJsonStr.isEmpty()){
                //Timber.d("result is success!");
                result = GcmNetworkManager.RESULT_SUCCESS;
            }else {
                //Timber.d("fetch data failed !");
            }

            List<Coin> coins = CoinJsonUtils.extractCoinsFromJson(mContext, coinsJsonStr, priceJsonStr, coinSymbols);

            //Timber.d("number of coin objects: " + coins.size());

            ContentValues[] coinValues = CoinJsonUtils.getCoinContentValueFromList(coins);

            //ArrayList<ContentProviderOperation> cpo = new ArrayList<ContentProviderOperation>();
            if (coinValues != null && coinValues.length != 0){
                //Timber.d("coinvalues are not null.");
                //Timber.d("number of contentvalues: " + coinValues.length);

                for (int i = 0; i < coinValues.length; i++){
                    //Timber.d("count for loop: " + i);
                    //Timber.d("Inserting coin symbol: " + coinValues[i].getAsString(CoinDbContract.CoinEntry.COLUMN_SYMBOL));

                    ContentResolver coinContentResolver = mContext.getContentResolver();

                    String toInsert = coinValues[i].getAsString(CoinDbContract.CoinEntry.COLUMN_SYMBOL);

                    Cursor c = mContext.getContentResolver()
                            .query(CoinDbContract.CoinEntry.CONTENT_URI,
                                    //new String[]{CoinDbContract.CoinEntry.COLUMN_SYMBOL},
                                    ConstantsUtils.COIN_COLUMNS,
                                    CoinDbContract.CoinEntry.COLUMN_SYMBOL + "= ?",
                                    new String[]{toInsert},
                                    null);

                    if (c != null && c.moveToFirst()){
                        coinContentResolver.update(CoinDbContract.CoinEntry.CONTENT_URI,
                                coinValues[i],
                                CoinDbContract.CoinEntry.COLUMN_SYMBOL + "=?",
                                new String[]{toInsert}
                        );
                    }else {
                        coinContentResolver.insert(CoinDbContract.CoinEntry.CONTENT_URI, coinValues[i]);
                    }
                }

                updateWidget(mContext);
            }
        }catch (Exception e){
            e.getStackTrace();
        }

        return result;
    }

    private void updateWidget(Context context) {
        //Timber.d("calling updateWidget()");
        Intent updatedDataIntent = new Intent(ACTION_DATA_UPDATED);
        updatedDataIntent.setPackage(context.getPackageName());
        context.sendBroadcast(updatedDataIntent);

    }

}
