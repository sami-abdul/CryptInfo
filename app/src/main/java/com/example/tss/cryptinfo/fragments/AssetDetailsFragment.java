package com.example.tss.cryptinfo.fragments;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tss.cryptinfo.R;
import com.example.tss.cryptinfo.api.data.AssetLoader;
import com.example.tss.cryptinfo.api.data.AssetPreferences;
import com.example.tss.cryptinfo.api.History;
import com.example.tss.cryptinfo.utilities.ConstantsUtils;
import com.example.tss.cryptinfo.utilities.DateFormatter;
import com.example.tss.cryptinfo.utilities.NumberUtils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AssetDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String SYMBOL_LABEL = "SYMBOL";

    private String mSymbol;
    private Cursor mCursor;

    @BindView(R.id.iv_coin_logo)
    ImageView mLogoImageView;
    @BindView(R.id.current_price)
    TextView mPriceTextView;
    @BindView(R.id.change_value)
    TextView mChangeValueTextView;
    @BindView(R.id.change_percent)
    TextView mChangePerTextView;
    @BindView(R.id.current_unit)
    TextView mUnit;

    @BindView(R.id.tv_open_24h)
    TextView mOpenTextView;
    @BindView(R.id.tv_low_24h)
    TextView mLowTextView;
    @BindView(R.id.tv_high_24h)
    TextView mHighTextView;
    @BindView(R.id.tv_volume_24h)
    TextView mVolumeTextView;
    @BindView(R.id.tv_volume_24h_to)
    TextView mVolumeToTextView;
    @BindView(R.id.tv_mkt_cap)
    TextView mMktCapTextView;
    @BindView(R.id.tv_curr_spply)
    TextView mCurrSupplyTextView;
    @BindView(R.id.tv_max_spply)
    TextView mMaxSupplyTextView;
    @BindView(R.id.tv_algorithm)
    TextView mAlgorithmTextView;
    @BindView(R.id.tv_proof_type)
    TextView mProofTypeTextView;
    @BindView(R.id.tv_sponsor)
    TextView mSponsorTextView;

    @BindView(R.id.chart_historical)
    LineChart mChartView;

    private Unbinder unbinder;

    public AssetDetailsFragment() {
        // Required empty public constructor
    }

    public static AssetDetailsFragment newInstance(String symbol) {
        AssetDetailsFragment fragment = new AssetDetailsFragment();
        Bundle args = new Bundle();
        args.putString(SYMBOL_LABEL, symbol);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSymbol = getArguments().getString(getString(R.string.symbol_tag_capital));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_asset_details, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        addBannerAd(rootView);

        return rootView;
    }

    private void bindViews(){
        NumberUtils numberUtils = new NumberUtils();

        if (mCursor != null){

            String histoStr = mCursor.getString(ConstantsUtils.POSITION_HISTO);

            boolean landPhone = getResources().getBoolean(R.bool.isLandscape)
                    && !getResources().getBoolean(R.bool.isTablet);

            if (histoStr == null || histoStr.length() < 500 || landPhone){
                mChartView.setVisibility(View.GONE);
            }else {
                mChartView.setVisibility(View.VISIBLE);

                LineDataSet dataSet = getLineDataSet(histoStr);
                LineData lineData = new LineData(dataSet);
                mChartView.setData(lineData);
                formatChart(mChartView, dataSet);
                mChartView.invalidate();
            }

            String unitPref = AssetPreferences.getPreferredUnit(getContext());

            double price = mCursor.getDouble(ConstantsUtils.POSITION_PRICE);
            if (unitPref.equals(getString(R.string.pref_unit_btc_value))){
                mPriceTextView.setText(numberUtils.getBtcFormat().format(price));
                mUnit.setText(getString(R.string.pref_unit_btc_value_bref));
            }else{
                mPriceTextView.setText(numberUtils.getDollarFormat().format(price));
            }

            double trend = mCursor.getDouble(ConstantsUtils.POSITION_TREND);
            mChangePerTextView.setText(numberUtils.getPercentageFormat().format(trend/100));

            String change = mCursor.getString(ConstantsUtils.POSITION_CHANGE);
            double value = Double.parseDouble(change);

            String bpChange = getString(R.string.left_bracket_plus) + change + getString(R.string.right_bracket);
            String bChange = getString(R.string.left_bracket) + change + getString(R.string.right_bracket);

            if (change != null && !change.isEmpty()){

                if (value > 0){
                    mChangeValueTextView.setText(bpChange);
                    mChangePerTextView.setTextColor(getResources().getColor(R.color.colorGreen700));
                    mChangeValueTextView.setTextColor(getResources().getColor(R.color.colorGreen700));
                }else {
                    mChangeValueTextView.setText(bChange);
                    if (value < 0) {
                        mChangePerTextView.setTextColor(getResources().getColor(R.color.colorRed700));
                        mChangeValueTextView.setTextColor(getResources().getColor(R.color.colorRed700));
                    }else{
                        mChangePerTextView.setTextColor(getResources().getColor(R.color.colorOrange700));
                        mChangeValueTextView.setTextColor(getResources().getColor(R.color.colorOrange700));
                    }

                }
            }

            String logo = mCursor.getString(ConstantsUtils.POSITION_IMAGE_URL);
            if (logo != null){
                String logoUrl = getString(R.string.base_logo_url) + logo;
                Glide.with(getContext())
                        .load(logoUrl)
                        .into(mLogoImageView);

                mLogoImageView.setVisibility(View.VISIBLE);
            }else {
                mLogoImageView.setVisibility(View.INVISIBLE);
            }

            mOpenTextView.setText(mCursor.getString(ConstantsUtils.POSITION_OPEN24H));
            mLowTextView.setText(mCursor.getString(ConstantsUtils.POSITION_LOW24H));
            mHighTextView.setText(mCursor.getString(ConstantsUtils.POSITION_HIGH24H));
            mVolumeTextView.setText(mCursor.getString(ConstantsUtils.POSITION_VOL24H));
            mVolumeToTextView.setText(mCursor.getString(ConstantsUtils.POSITION_VOL24H2));
            mMktCapTextView.setText(mCursor.getString(ConstantsUtils.POSITION_MKTCAP));
            mAlgorithmTextView.setText(mCursor.getString(ConstantsUtils.POSITION_ALGORITHM));
            mProofTypeTextView.setText(mCursor.getString(ConstantsUtils.POSITION_PROOF_TYPE));

            int sponsor = Integer.valueOf(mCursor.getString(ConstantsUtils.POSITION_SPONSOR));
            if (sponsor == 1){
                mSponsorTextView.setText(getString(R.string.tag_yes));
            }else {
                mSponsorTextView.setText(getString(R.string.tag_no));
            }

            String currSup = mCursor.getString(ConstantsUtils.POSITION_SUPPLY);
            if (currSup != null && !currSup.isEmpty() && !currSup.equals("0")){
                mCurrSupplyTextView.setText(currSup);
            }else {
                mCurrSupplyTextView.setText(getString(R.string.tag_na));
            }

            String maxSup = mCursor.getString(ConstantsUtils.POSITION_TOTAL_SUPPLY);

            if (maxSup != null && !maxSup.isEmpty() && !maxSup.equals("0")){
                mMaxSupplyTextView.setText(maxSup);
            }else {
                mMaxSupplyTextView.setText(getString(R.string.tag_na));
            }

        }
    }

    private LineDataSet getLineDataSet(String histoJsonStr){
        ArrayList<Entry> histoEntries = new ArrayList<Entry>();
        try {
            JSONObject baseJsonObject = new JSONObject(histoJsonStr);
            JSONArray histoArray = baseJsonObject.getJSONArray(getString(R.string.data_key));

            for (int i = 0; i < histoArray.length(); i++){
                JSONObject histoObject = histoArray.getJSONObject(i);

                History history = new History();

                float low = (float) histoObject.getDouble(getString(R.string.low_key));
                float high = (float) histoObject.getDouble(getString(R.string.high_key));


                Entry histoEntry = new Entry(
                        (float) histoObject.getDouble(getString(R.string.time_key)),
                        (low + high)/2.0f
                );

                histoEntries.add(histoEntry);
            }
        }catch (JSONException e){
            e.getStackTrace();
        }

        return new LineDataSet(histoEntries, getString(R.string.history_label));
    }

    private void formatChart(LineChart chart, LineDataSet dataSet) {

        int backgroundColor = getResources().getColor(R.color.colorBlueGrey700);

        XAxis xAxis = chart.getXAxis();
        xAxis.setTextColor(Color.WHITE);
        //xAxis.setValueFormatter(new DateFormatter(chart));
        xAxis.setValueFormatter(new DateFormatter(getContext()));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        xAxis.setAxisLineColor(backgroundColor);
        xAxis.setAxisLineWidth(1.5f);
        //xAxis.enableGridDashedLine(20, 10, 0);

        YAxis yAxisLeft = chart.getAxisLeft();
        yAxisLeft.setAxisLineColor(backgroundColor);
        yAxisLeft.setTextColor(Color.WHITE);
        yAxisLeft.setAxisLineWidth(1.5f);
        //yAxisLeft.enableGridDashedLine(20,40,0);


        YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setTextColor(Color.WHITE);
        yAxisRight.setAxisLineColor(backgroundColor);
        //yAxisRight.setAxisLineWidth(2);
        //yAxisRight.enableGridDashedLine(20, 40, 0);

        chart.setDrawGridBackground(false);

        chart.setBackgroundColor(backgroundColor);

        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(false);
        chart.getDescription().setEnabled(false);

        Legend legend = chart.getLegend();
        legend.setEnabled(false);

        dataSet.setDrawCircles(false);
        dataSet.setDrawFilled(false);
        dataSet.setDrawValues(false);
        //dataSet.setFillColor(backgroundColor);
        dataSet.setColors(Color.WHITE);
        dataSet.setLineWidth(2);

    }

    public void addBannerAd(View view){
        AdView mAdView = (AdView) view.findViewById(R.id.adView);
        // Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return AssetLoader.newInstanceForCoinSymbol(getActivity(), mSymbol);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursor = cursor;
        if (mCursor != null && mCursor.moveToFirst()){
            //Timber.d("Successfully loaded data for: " + mSymbol);
            bindViews();
        }else {
            mCursor.close();
            mCursor = null;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        unbinder.unbind();
    }
}