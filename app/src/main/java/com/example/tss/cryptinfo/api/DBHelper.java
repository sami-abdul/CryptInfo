package com.example.tss.cryptinfo.api;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "cryptocoins.db";
    private static final int VERSION = 5;

    DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String builder = "CREATE TABLE " + DBContract.CoinEntry.TABLE_NAME + " ("
                + DBContract.CoinEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DBContract.CoinEntry.COLUMN_SYMBOL + " TEXT NOT NULL, "
                + DBContract.CoinEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + DBContract.CoinEntry.COLUMN_COIN_URL + " TEXT, "
                + DBContract.CoinEntry.COLUMN_IMAGE_URL + " TEXT, "
                + DBContract.CoinEntry.COLUMN_ALGORITHM + " TEXT, "
                + DBContract.CoinEntry.COLUMN_PROOF_TYPE + " TEXT, "
                + DBContract.CoinEntry.COLUMN_TOTAL_SUPPLY + " REAL, "
                + DBContract.CoinEntry.COLUMN_SPONSOR + " INTEGER NOT NULL, "
                + DBContract.CoinEntry.COLUMN_SUPPLY + " REAL, "
                + DBContract.CoinEntry.COLUMN_PRICE + " REAL, "
                + DBContract.CoinEntry.COLUMN_MKTCAP + " REAL, "
                + DBContract.CoinEntry.COLUMN_VOL24H + " REAL, "
                + DBContract.CoinEntry.COLUMN_VOL24H2 + " REAL, "
                + DBContract.CoinEntry.COLUMN_OPEN24H + " REAL, "
                + DBContract.CoinEntry.COLUMN_HIGH24H + " REAL, "
                + DBContract.CoinEntry.COLUMN_LOW24H + " REAL, "
                + DBContract.CoinEntry.COLUMN_TREND + " REAL, "
                + DBContract.CoinEntry.COLUMN_CHANGE + " REAL, "
                + DBContract.CoinEntry.COLUMN_HISTO + " TEXT, "
                + DBContract.CoinEntry.COLUMN_NEWS + " TEXT, "
                + DBContract.CoinEntry.COLUMN_UPDATE + " REAL, "
                + "UNIQUE (" + DBContract.CoinEntry.COLUMN_SYMBOL + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(builder);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(" DROP TABLE IF EXISTS " + DBContract.CoinEntry.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion){
        onUpgrade(sqLiteDatabase, oldVersion, newVersion);
    }
}