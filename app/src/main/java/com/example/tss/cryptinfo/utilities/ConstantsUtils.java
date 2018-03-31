package com.example.tss.cryptinfo.utilities;

import com.example.tss.cryptinfo.api.data.DBContract;

public class ConstantsUtils {

    public static final String[] COIN_COLUMNS = {
            DBContract.CoinEntry.TABLE_NAME + "." + DBContract.CoinEntry._ID,
            DBContract.CoinEntry.COLUMN_SYMBOL,
            DBContract.CoinEntry.COLUMN_NAME,
            DBContract.CoinEntry.COLUMN_COIN_URL,
            DBContract.CoinEntry.COLUMN_IMAGE_URL,
            DBContract.CoinEntry.COLUMN_ALGORITHM,
            DBContract.CoinEntry.COLUMN_PROOF_TYPE,
            DBContract.CoinEntry.COLUMN_TOTAL_SUPPLY,
            DBContract.CoinEntry.COLUMN_SPONSOR,
            DBContract.CoinEntry.COLUMN_SUPPLY,
            DBContract.CoinEntry.COLUMN_PRICE,
            DBContract.CoinEntry.COLUMN_MKTCAP,
            DBContract.CoinEntry.COLUMN_VOL24H,
            DBContract.CoinEntry.COLUMN_VOL24H2,
            DBContract.CoinEntry.COLUMN_OPEN24H,
            DBContract.CoinEntry.COLUMN_HIGH24H,
            DBContract.CoinEntry.COLUMN_LOW24H,
            DBContract.CoinEntry.COLUMN_TREND,
            DBContract.CoinEntry.COLUMN_CHANGE,
            DBContract.CoinEntry.COLUMN_HISTO,
            DBContract.CoinEntry.COLUMN_NEWS,
            DBContract.CoinEntry.COLUMN_UPDATE
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
