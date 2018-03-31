package com.example.tss.cryptinfo.actvities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.content.Loader
import android.support.v4.app.LoaderManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.preference.PreferenceManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.View
import android.view.Menu
import android.view.MenuItem
import android.widget.ProgressBar
import android.widget.Toast

import com.example.tss.cryptinfo.R
import com.example.tss.cryptinfo.adapters.AssetAdapter
import com.example.tss.cryptinfo.views.AddCoinDialog
import com.example.tss.cryptinfo.api.DBContract
import com.example.tss.cryptinfo.api.AssetLoader
import com.example.tss.cryptinfo.services.AssetSyncIntentService
import com.example.tss.cryptinfo.services.AssetTaskService
import com.example.tss.cryptinfo.utilities.JSONUtils
import com.example.tss.cryptinfo.utilities.NetworkUtils
import com.google.android.gms.gcm.GcmNetworkManager
import com.google.android.gms.gcm.PeriodicTask
import com.google.android.gms.gcm.Task

class AssetActivity : AppCompatActivity(),
        SharedPreferences.OnSharedPreferenceChangeListener, AssetAdapter.CoinAdapterOnclickHandler, LoaderManager.LoaderCallbacks<Cursor> {

    companion object {
        private val LOADER_ID = 0
    }

    private var mServiceIntent: Intent? = null
    private var mContext: Context? = null
    private var mCursor: Cursor? = null
    private var mCoinsRecyclerView: RecyclerView? = null
    private var mAssetAdapter: AssetAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mContext = this

        setContentView(R.layout.activity_coins)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(true)

        mServiceIntent = Intent(this, AssetSyncIntentService::class.java)

        setupSharedPreferences()

        if (savedInstanceState == null) {
            if (NetworkUtils.isNetworkStatusAvailable(mContext!!)) {
                refresh()
            } else {
                networkError()
            }
        }

        mCoinsRecyclerView = findViewById<View>(R.id.rv_list_coin) as RecyclerView

        mAssetAdapter = AssetAdapter(this, this)
        mCoinsRecyclerView!!.layoutManager = LinearLayoutManager(this)

        supportLoaderManager.initLoader(LOADER_ID, null, this)

        mCoinsRecyclerView!!.adapter = mAssetAdapter

        val fab = findViewById<View>(R.id.fab) as FloatingActionButton

        fab.setOnClickListener { AddCoinDialog().show(supportFragmentManager, "CoinDialogFragment") }

        if (NetworkUtils.isNetworkStatusAvailable(mContext!!)) {
            val period = 3600L
            val flex = 10L
            val periodicTag = getString(R.string.tag_periodic)

            val periodicTask = PeriodicTask.Builder()
                    .setService(AssetTaskService::class.java)
                    .setPeriod(period)
                    .setFlex(flex)
                    .setTag(periodicTag)
                    .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                    .setRequiresCharging(false)
                    .build()

            GcmNetworkManager.getInstance(this).schedule(periodicTask)
        }
    }

    fun addCoin(symbol: String?) {
        if (symbol != null && !symbol.isEmpty()) {

            if (NetworkUtils.isNetworkStatusAvailable(mContext!!)) {
                val cleanInput = symbol.trim { it <= ' ' }.toUpperCase()

                val c = contentResolver
                        .query(DBContract.CoinEntry.CONTENT_URI,
                                arrayOf(DBContract.CoinEntry.COLUMN_SYMBOL),
                                DBContract.CoinEntry.COLUMN_SYMBOL + "= ?",
                                arrayOf(cleanInput), null)

                if (c!!.count != 0) {
                    val toast = Toast.makeText(this@AssetActivity,
                            R.string.error_coin_already_saved,
                            Toast.LENGTH_LONG)

                    toast.setGravity(Gravity.CENTER, Gravity.CENTER, 0)
                    toast.show()
                    return
                } else {
                    if (!JSONUtils.isSymbolValid(mContext!!, cleanInput)) {
                        val toast = Toast.makeText(this@AssetActivity,
                                R.string.error_coin_not_found,
                                Toast.LENGTH_LONG)

                        toast.setGravity(Gravity.CENTER, Gravity.CENTER, 0)
                        toast.show()
                        return
                    } else {
                        mServiceIntent!!.putExtra(getString(R.string.tag_tag), getString(R.string.tag_add_value))
                        mServiceIntent!!.putExtra(getString(R.string.symbol_tag), cleanInput)
                        startService(mServiceIntent)
                        c.close()
                    }
                }
            } else {
                networkError()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_coins, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_refresh) {
            if (NetworkUtils.isNetworkStatusAvailable(mContext!!)) {
                refresh()
            } else {
                networkError()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun refresh() {
        mServiceIntent!!.putExtra(getString(R.string.tag_tag), getString(R.string.tag_init))
        startService(mServiceIntent)
    }

    fun networkError() {
        Toast.makeText(mContext, getString(R.string.network_not_connected), Toast.LENGTH_SHORT).show()
    }

    override fun onCreateLoader(i: Int, bundle: Bundle): Loader<Cursor> {
        return AssetLoader.newAllCoinsInstance(this)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor) {
        mAssetAdapter!!.swapCursor(cursor)
        mCursor = cursor

        val view = findViewById<View>(R.id.tv_empty_loading)
        val mProgressbar = findViewById<ProgressBar>(R.id.pb_list_coin)

        if (mCursor == null || mCursor!!.count == 0) {
            view.visibility = View.VISIBLE
            mProgressbar.visibility = View.VISIBLE
        } else {
            view.visibility = View.INVISIBLE
            mProgressbar.visibility = View.INVISIBLE
        }

        val snackbar = Snackbar.make(mCoinsRecyclerView!!, getString(R.string.load_finished), Snackbar.LENGTH_LONG)

        snackbar.setActionTextColor(Color.MAGENTA)
                .setAction(getString(R.string.action_refresh)) { }

        snackbar.show()
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        mCoinsRecyclerView!!.adapter = null
    }

    override fun onClick(symbol: String, name: String) {
        val detailIntent = Intent(this, AssetDetailsActivity::class.java)
        detailIntent.putExtra(getString(R.string.symbol_tag_capital), symbol)
        detailIntent.putExtra(getString(R.string.name_tag_capital), name)
        startActivity(detailIntent)
    }

    private fun setupSharedPreferences() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, s: String) {
        if (NetworkUtils.isNetworkStatusAvailable(mContext!!)) {
            refresh()
        } else {
            networkError()
        }
    }
}
