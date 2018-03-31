package com.example.tss.cryptinfo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class CoinDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "cryptocoins.db";
    private static final int VERSION = 5;

    CoinDbHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String builder = "CREATE TABLE " + CoinDbContract.CoinEntry.TABLE_NAME + " ("
                + CoinDbContract.CoinEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + CoinDbContract.CoinEntry.COLUMN_SYMBOL + " TEXT NOT NULL, "
                + CoinDbContract.CoinEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + CoinDbContract.CoinEntry.COLUMN_COIN_URL + " TEXT, "
                + CoinDbContract.CoinEntry.COLUMN_IMAGE_URL + " TEXT, "
                + CoinDbContract.CoinEntry.COLUMN_ALGORITHM + " TEXT, "
                + CoinDbContract.CoinEntry.COLUMN_PROOF_TYPE + " TEXT, "
                + CoinDbContract.CoinEntry.COLUMN_TOTAL_SUPPLY + " REAL, "
                + CoinDbContract.CoinEntry.COLUMN_SPONSOR + " INTEGER NOT NULL, "
                + CoinDbContract.CoinEntry.COLUMN_SUPPLY + " REAL, "
                + CoinDbContract.CoinEntry.COLUMN_PRICE + " REAL, "
                + CoinDbContract.CoinEntry.COLUMN_MKTCAP + " REAL, "
                + CoinDbContract.CoinEntry.COLUMN_VOL24H + " REAL, "
                + CoinDbContract.CoinEntry.COLUMN_VOL24H2 + " REAL, "
                + CoinDbContract.CoinEntry.COLUMN_OPEN24H + " REAL, "
                + CoinDbContract.CoinEntry.COLUMN_HIGH24H + " REAL, "
                + CoinDbContract.CoinEntry.COLUMN_LOW24H + " REAL, "
                + CoinDbContract.CoinEntry.COLUMN_TREND + " REAL, "
                + CoinDbContract.CoinEntry.COLUMN_CHANGE + " REAL, "
                + CoinDbContract.CoinEntry.COLUMN_HISTO + " TEXT, "
                + CoinDbContract.CoinEntry.COLUMN_NEWS + " TEXT, "
                + CoinDbContract.CoinEntry.COLUMN_UPDATE + " REAL, "
                + "UNIQUE (" + CoinDbContract.CoinEntry.COLUMN_SYMBOL + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(builder);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(" DROP TABLE IF EXISTS " + CoinDbContract.CoinEntry.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion){
        onUpgrade(sqLiteDatabase, oldVersion, newVersion);
    }
}