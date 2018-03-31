package com.example.tss.cryptinfo.api.data;

import android.content.Context;
import android.support.v4.content.CursorLoader;
import android.net.Uri;

import com.example.tss.cryptinfo.utilities.ConstantsUtils;


public class AssetLoader extends CursorLoader {

    public static AssetLoader newAllCoinsInstance(Context context){
        return new AssetLoader(context, DBContract.CoinEntry.CONTENT_URI);
    }

    public static AssetLoader newInstanceForCoinSymbol(Context context, String symbol){
        return new AssetLoader(context, DBContract.CoinEntry.builUriWithSympol(symbol));
    }

    public AssetLoader(Context context, Uri uri) {
        super(context,
                uri,
                ConstantsUtils.COIN_COLUMNS,
                null,
                null,
                DBContract.CoinEntry.DEFAULT_SORT);
    }
}
