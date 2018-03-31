package com.example.tss.cryptinfo.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.tss.cryptinfo.R;
import com.example.tss.cryptinfo.fragments.CoinDetailFragment;
import com.example.tss.cryptinfo.fragments.NewsFragment;


public class SectionPagerAdapter extends FragmentPagerAdapter{

    String mSymbol;
    CoinDetailFragment mCoinDetailFragment;
    NewsFragment mNewsFragment;
    Context mContext;

    public SectionPagerAdapter(FragmentManager fm, String symbol, Context context){
        super(fm);
        this.mSymbol = symbol;
        this.mContext = context;
        mCoinDetailFragment = CoinDetailFragment.Companion.newInstance(symbol);
        mNewsFragment = NewsFragment.newInstance(symbol);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return mCoinDetailFragment;
            case 1:
                return mNewsFragment;
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
                return mContext.getString(R.string.market_capital);
            case 1:
                return mContext.getString(R.string.news_capital);
        }
        return null;
    }
}
