package com.example.tss.cryptinfo.actvities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.Loader;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.tss.cryptinfo.R;
import com.example.tss.cryptinfo.adapters.AssetAdapter;
import com.example.tss.cryptinfo.api.data.AssetLoader;
import com.example.tss.cryptinfo.api.data.DBContract;
import com.example.tss.cryptinfo.api.sync.AssetSyncIntentService;
import com.example.tss.cryptinfo.api.sync.AssetTaskService;
import com.example.tss.cryptinfo.utilities.JSONUtils;
import com.example.tss.cryptinfo.utilities.NetworkUtils;
import com.example.tss.cryptinfo.views.AddAssetDialog;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;

public class AssetsActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener,
        AssetAdapter.AssetAdapterOnclickHandler,
        LoaderManager.LoaderCallbacks<Cursor>{

    private static final int LOADER_ID = 0;
    private Intent mServiceIntent;
    private Context mContext;
    private Cursor mCursor;
    private RecyclerView mCoinsRecyclerView;
    private AssetAdapter mCoinAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        setContentView(R.layout.activity_assets);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(true);

        mServiceIntent = new Intent(this, AssetSyncIntentService.class);

        setupSharedPreferences();

        if (savedInstanceState == null){
            if (NetworkUtils.INSTANCE.isNetworkStatusAvailable(mContext)) {
                refresh();
            }else {
                networkError();
            }
        }

        mCoinsRecyclerView = (RecyclerView) findViewById(R.id.rv_list_coin);

        mCoinAdapter = new AssetAdapter(this, this);
        mCoinsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);

        mCoinsRecyclerView.setAdapter(mCoinAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@SuppressWarnings("UnusedParameters") View v) {
                Cursor  cursor = getContentResolver().query(DBContract.CoinEntry.CONTENT_URI,null, null, null, null);
                AddAssetDialog dialg = new AddAssetDialog();
                dialg.setCursor(cursor);
                dialg.show(getSupportFragmentManager(), "CoinDialogFragment");

            }
        });

        if(NetworkUtils.INSTANCE.isNetworkStatusAvailable(mContext)) {
            long period = 3600L;
            long flex = 10L;
            String periodicTag = getString(R.string.tag_periodic);

            PeriodicTask periodicTask = new PeriodicTask.Builder()
                    .setService(AssetTaskService.class)
                    .setPeriod(period)
                    .setFlex(flex)
                    .setTag(periodicTag)
                    .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                    .setRequiresCharging(false)
                    .build();

            GcmNetworkManager.getInstance(this).schedule(periodicTask);
        }
    }

    public void addCoin(String symbol) {
        if (symbol != null && !symbol.isEmpty()) {
            if (NetworkUtils.INSTANCE.isNetworkStatusAvailable(mContext)) {
                String cleanInput = symbol.trim().toUpperCase();
                Cursor c = getContentResolver()
                        .query(DBContract.CoinEntry.CONTENT_URI,
                                new String[]{DBContract.CoinEntry.COLUMN_SYMBOL},
                                DBContract.CoinEntry.COLUMN_SYMBOL + "= ?",
                                new String[]{cleanInput},
                                null);
                if (c.getCount() != 0) {
                    Toast toast =
                            Toast.makeText(AssetsActivity.this,
                                    R.string.error_coin_already_saved,
                                    Toast.LENGTH_LONG);

                    toast.setGravity(Gravity.CENTER, Gravity.CENTER, 0);
                    toast.show();
                    return;
                } else {
                    if (!JSONUtils.INSTANCE.isSymbolValid(mContext, cleanInput)) {
                        Toast toast =
                                Toast.makeText(AssetsActivity.this,
                                        R.string.error_coin_not_found,
                                        Toast.LENGTH_LONG);

                        toast.setGravity(Gravity.CENTER, Gravity.CENTER, 0);
                        toast.show();
                        return;
                    } else {
                        mServiceIntent.putExtra(getString(R.string.tag_tag), getString(R.string.tag_add_value));
                        mServiceIntent.putExtra(getString(R.string.symbol_tag), cleanInput);
                        startService(mServiceIntent);
                        c.close();
                    }
                }
            } else {
                networkError();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_coins, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            if (NetworkUtils.INSTANCE.isNetworkStatusAvailable(mContext)) {
                refresh();
            } else {
                networkError();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void refresh() {
        mServiceIntent.putExtra(getString(R.string.tag_tag), getString(R.string.tag_init));
        startService(mServiceIntent);
    }

    public void networkError(){
        Toast.makeText(mContext, getString(R.string.network_not_connected), Toast.LENGTH_SHORT).show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return AssetLoader.newAllCoinsInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCoinAdapter.swapCursor(cursor);
        mCursor = cursor;

        View view = findViewById(R.id.tv_empty_loading);
        ProgressBar mProgressbar = findViewById(R.id.pb_list_coin);

        if (mCursor == null || mCursor.getCount() == 0){
            view.setVisibility(View.VISIBLE);
            mProgressbar.setVisibility(View.VISIBLE);
        }else{
            view.setVisibility(View.INVISIBLE);
            mProgressbar.setVisibility(View.INVISIBLE);
        }

        final Snackbar snackbar = Snackbar.make(mCoinsRecyclerView, getString(R.string.load_finished), Snackbar.LENGTH_LONG);

        snackbar.setActionTextColor(Color.MAGENTA)
                .setAction(getString(R.string.action_refresh), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

        snackbar.show();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCoinsRecyclerView.setAdapter(null);
    }

    @Override
    public void onClick(String symbol, String name) {
        final Intent detailIntent = new Intent(this, AssetDetailsActivity.class);
        detailIntent.putExtra(getString(R.string.symbol_tag_capital), symbol);
        detailIntent.putExtra(getString(R.string.name_tag_capital), name);
        startActivity(detailIntent);
    }

    private void setupSharedPreferences(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (NetworkUtils.INSTANCE.isNetworkStatusAvailable(mContext)) {
            refresh();
        }else {
            networkError();
        }
    }
}