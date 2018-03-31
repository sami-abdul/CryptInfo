package com.example.tss.cryptinfo.views.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.tss.cryptinfo.R;
import com.example.tss.cryptinfo.api.CoinDbContract;
import com.example.tss.cryptinfo.api.CoinPreferences;
import com.example.tss.cryptinfo.utilities.ConstantsUtils;
import com.example.tss.cryptinfo.utilities.NumberUtils;


public class CoinWidgetRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory{
    private Cursor cursor = null;
    private Context mContext = null;

    public CoinWidgetRemoteViewFactory(Context context){
        mContext = context;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        if (cursor != null) {
            cursor.close();
        }
        final long identityToken = Binder.clearCallingIdentity();
        cursor = mContext.getContentResolver()
                .query(CoinDbContract.CoinEntry.CONTENT_URI, ConstantsUtils.COIN_COLUMNS, null, null, null);

        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
    }

    @Override
    public int getCount() {
        return cursor == null ? 0 : cursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position == AdapterView.INVALID_POSITION ||
                cursor == null || !cursor.moveToPosition(position)) {
            return null;
        }

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_item_coin_list);

        String symbol = null;
        NumberUtils numberUtils = new NumberUtils();
        String unitPref = CoinPreferences.getPreferredUnit(mContext);

        if (cursor.moveToPosition(position)) {
            symbol = cursor.getString(ConstantsUtils.POSITION_SYMBOL);
            double price = cursor.getDouble(ConstantsUtils.POSITION_PRICE);
            double trend = cursor.getDouble(ConstantsUtils.POSITION_TREND);

            views.setTextViewText(R.id.wd_symbol, symbol);

            if (unitPref.equals("BTC")){
                views.setTextViewText(R.id.wd_price, numberUtils.btcFormatWithSign.format(price));
            } else {
                views.setTextViewText(R.id.wd_price, numberUtils.dollarFormatWithSign.format(price));
            }

            views.setTextViewText(R.id.wd_trend, numberUtils.percentageFormat.format(trend/100));
            if (trend > 0.0) {
                views.setInt(R.id.wd_trend, mContext.getString(R.string.setBackgroundResource), R.drawable.price_increase_green);
            } else if (trend < 0.0){
                views.setInt(R.id.wd_trend, mContext.getString(R.string.setBackgroundResource), R.drawable.price_decrease_red);
            } else {
                views.setInt(R.id.wd_trend, mContext.getString(R.string.setBackgroundResource), R.drawable.price_no_change_orange);
            }
        }

        final Intent fillInIntent = new Intent();
        fillInIntent.putExtra(mContext.getString(R.string.symbol_tag_capital), symbol);
        views.setOnClickFillInIntent(R.id.wd_coin_basic, fillInIntent);

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return new RemoteViews(mContext.getPackageName(), R.layout.widget_item_coin_list);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        if (cursor.moveToPosition(position))
            return cursor.getLong(ConstantsUtils.POSITION_ID);
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
