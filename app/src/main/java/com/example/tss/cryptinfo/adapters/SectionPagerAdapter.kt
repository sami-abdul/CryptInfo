package com.example.tss.cryptinfo.adapters

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

import com.example.tss.cryptinfo.R
import com.example.tss.cryptinfo.fragments.AssetDetailsFragment

class SectionPagerAdapter(fm: FragmentManager, private var mSymbol: String, private var mContext: Context) : FragmentPagerAdapter(fm) {
    private var mAssetDetailsFragment: AssetDetailsFragment = AssetDetailsFragment.newInstance(mSymbol)

    override fun getItem(position: Int): Fragment? {
//        when (position) {
//            0 ->
        return mAssetDetailsFragment
//        }
//        return null
    }

    override fun getCount(): Int {
        return 1
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> return mContext.getString(R.string.market_capital)
        }
        return null
    }
}
