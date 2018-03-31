package com.example.tss.cryptinfo.adapters

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

import com.example.tss.cryptinfo.R
import com.example.tss.cryptinfo.fragments.AssetDetailFragment

class SectionPagerAdapter(fm: FragmentManager, private var mSymbol: String, private var mContext: Context) : FragmentPagerAdapter(fm) {
    private var mAssetDetailFragment: AssetDetailFragment = AssetDetailFragment.newInstance(mSymbol)

    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> return mAssetDetailFragment
        }
        return null
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> return mContext.getString(R.string.market_capital)
            1 -> return mContext.getString(R.string.news_capital)
        }
        return null
    }
}
