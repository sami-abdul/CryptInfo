package com.example.tss.cryptinfo.data;

import android.content.Context;
import android.support.v4.content.CursorLoader;
import android.net.Uri;

import com.example.tss.cryptinfo.utilities.ConstantsUtils;


public class CoinLoader extends CursorLoader {

    public static CoinLoader newAllCoinsInstance(Context context){
        return new CoinLoader(context, CoinDbContract.CoinEntry.CONTENT_URI);
    }

    public static CoinLoader newInstanceForCoinSymbol(Context context, String symbol){
        return new CoinLoader(context, CoinDbContract.CoinEntry.builUriWithSympol(symbol));
    }

    public CoinLoader(Context context, Uri uri) {
        super(context,
                uri,
                ConstantsUtils.COIN_COLUMNS,
                null,
                null,
                CoinDbContract.CoinEntry.DEFAULT_SORT);
    }
}
