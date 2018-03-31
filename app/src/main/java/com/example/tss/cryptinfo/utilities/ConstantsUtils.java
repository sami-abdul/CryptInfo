package com.example.tss.cryptinfo.utilities;

import com.example.tss.cryptinfo.data.CoinDbContract;

public class ConstantsUtils {

    public static final String[] COIN_COLUMNS = {
            CoinDbContract.CoinEntry.TABLE_NAME + "." + CoinDbContract.CoinEntry._ID,
            CoinDbContract.CoinEntry.COLUMN_SYMBOL,
            CoinDbContract.CoinEntry.COLUMN_NAME,
            CoinDbContract.CoinEntry.COLUMN_COIN_URL,
            CoinDbContract.CoinEntry.COLUMN_IMAGE_URL,
            CoinDbContract.CoinEntry.COLUMN_ALGORITHM,
            CoinDbContract.CoinEntry.COLUMN_PROOF_TYPE,
            CoinDbContract.CoinEntry.COLUMN_TOTAL_SUPPLY,
            CoinDbContract.CoinEntry.COLUMN_SPONSOR,
            CoinDbContract.CoinEntry.COLUMN_SUPPLY,
            CoinDbContract.CoinEntry.COLUMN_PRICE,
            CoinDbContract.CoinEntry.COLUMN_MKTCAP,
            CoinDbContract.CoinEntry.COLUMN_VOL24H,
            CoinDbContract.CoinEntry.COLUMN_VOL24H2,
            CoinDbContract.CoinEntry.COLUMN_OPEN24H,
            CoinDbContract.CoinEntry.COLUMN_HIGH24H,
            CoinDbContract.CoinEntry.COLUMN_LOW24H,
            CoinDbContract.CoinEntry.COLUMN_TREND,
            CoinDbContract.CoinEntry.COLUMN_CHANGE,
            CoinDbContract.CoinEntry.COLUMN_HISTO,
            CoinDbContract.CoinEntry.COLUMN_NEWS,
            CoinDbContract.CoinEntry.COLUMN_UPDATE
    };

    public static final int POSITION_ID = 0;
    public static final int POSITION_SYMBOL = 1;
    public static final int POSITION_NAME = 2;
    public static final int POSITION_COIN_URL = 3;
    public static final int POSITION_IMAGE_URL = 4;
    public static final int POSITION_ALGORITHM = 5;
    public static final int POSITION_PROOF_TYPE = 6;
    public static final int POSITION_TOTAL_SUPPLY = 7;
    public static final int POSITION_SPONSOR = 8;
    public static final int POSITION_SUPPLY = 9;
    public static final int POSITION_PRICE = 10;
    public static final int POSITION_MKTCAP = 11;
    public static final int POSITION_VOL24H = 12;
    public static final int POSITION_VOL24H2 = 13;
    public static final int POSITION_OPEN24H = 14;
    public static final int POSITION_HIGH24H = 15;
    public static final int POSITION_LOW24H = 16;
    public static final int POSITION_TREND = 17;
    public static final int POSITION_CHANGE = 18;
    public static final int POSITION_HISTO = 19;
    public static final int POSITION_NEWS = 20;
    public static final int POSITION_UPDATE = 21;
}
