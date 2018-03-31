package com.example.tss.cryptinfo.actvities

import android.content.Intent
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.os.Bundle
import android.view.View
import android.widget.Toast

import com.example.tss.cryptinfo.R
import com.example.tss.cryptinfo.adapters.SectionPagerAdapter
import com.example.tss.cryptinfo.services.DetailIntentService
import com.example.tss.cryptinfo.utilities.NetworkUtils

class AssetDetailsActivity : AppCompatActivity() {

    private var mToolbar: Toolbar? = null
    private var mTabLayout: TabLayout? = null
    private var mViewPager: ViewPager? = null

    private var mSymbol: String? = null

    private var mDetailServiceIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coin_detail)

        mToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mSymbol = intent.extras!!.getString(getString(R.string.symbol_tag_capital))
        val name = intent.extras!!.getString(getString(R.string.name_tag_capital))

        mDetailServiceIntent = Intent(this, DetailIntentService::class.java)

        if (NetworkUtils.isNetworkStatusAvailable(this)) {
            refreshDetail(mSymbol)
        } else {
            Toast.makeText(this, getString(R.string.network_not_connected), Toast.LENGTH_SHORT).show()
        }

        mViewPager = findViewById(R.id.viewpager)

        val sectionPagerAdapter = SectionPagerAdapter(supportFragmentManager, mSymbol!!, this)

        mViewPager!!.adapter = sectionPagerAdapter

        mTabLayout = findViewById(R.id.tabs)
        mTabLayout!!.setupWithViewPager(mViewPager)

        supportActionBar!!.setTitle(name)

    }

    private fun refreshDetail(symbol: String?) {
        mDetailServiceIntent!!.putExtra(getString(R.string.tag_tag), getString(R.string.tag_detail_value))
        mDetailServiceIntent!!.putExtra(getString(R.string.symbol_tag), symbol)
        startService(mDetailServiceIntent)
    }
}
