package com.example.tss.cryptinfo.fragments

import android.graphics.Color
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.example.tss.cryptinfo.R
import com.example.tss.cryptinfo.api.AssetLoader
import com.example.tss.cryptinfo.api.AssetPreferences
import com.example.tss.cryptinfo.api.History
import com.example.tss.cryptinfo.utilities.ConstantsUtils
import com.example.tss.cryptinfo.utilities.DateFormatter
import com.example.tss.cryptinfo.utilities.NumberUtils
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView

import org.json.JSONException
import org.json.JSONObject

import java.util.ArrayList

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder

class AssetDetailFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {

    private var mSymbol: String? = null
    private var mCursor: Cursor? = null

    @BindView(R.id.iv_coin_logo)
    internal var mLogoImageView: ImageView? = null
    @BindView(R.id.current_price)
    internal var mPriceTextView: TextView? = null
    @BindView(R.id.change_value)
    internal var mChangeValueTextView: TextView? = null
    @BindView(R.id.change_percent)
    internal var mChangePerTextView: TextView? = null
    @BindView(R.id.current_unit)
    internal var mUnit: TextView? = null

    @BindView(R.id.tv_open_24h)
    internal var mOpenTextView: TextView? = null
    @BindView(R.id.tv_low_24h)
    internal var mLowTextView: TextView? = null
    @BindView(R.id.tv_high_24h)
    internal var mHighTextView: TextView? = null
    @BindView(R.id.tv_volume_24h)
    internal var mVolumeTextView: TextView? = null
    @BindView(R.id.tv_volume_24h_to)
    internal var mVolumeToTextView: TextView? = null
    @BindView(R.id.tv_mkt_cap)
    internal var mMktCapTextView: TextView? = null
    @BindView(R.id.tv_curr_spply)
    internal var mCurrSupplyTextView: TextView? = null
    @BindView(R.id.tv_max_spply)
    internal var mMaxSupplyTextView: TextView? = null
    @BindView(R.id.tv_algorithm)
    internal var mAlgorithmTextView: TextView? = null
    @BindView(R.id.tv_proof_type)
    internal var mProofTypeTextView: TextView? = null
    @BindView(R.id.tv_sponsor)
    internal var mSponsorTextView: TextView? = null

    @BindView(R.id.chart_historical)
    internal var mChartView: LineChart? = null

    private var unbinder: Unbinder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mSymbol = arguments!!.getString(getString(R.string.symbol_tag_capital))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_coin_detail, container, false)
        unbinder = ButterKnife.bind(this, rootView)

        addBannerAd(rootView)

        return rootView
    }

    private fun bindViews() {
        val numberUtils = NumberUtils()

        if (mCursor != null) {

            val histoStr = mCursor!!.getString(ConstantsUtils.POSITION_HISTO)

            val landPhone = resources.getBoolean(R.bool.isLandscape) && !resources.getBoolean(R.bool.isTablet)

            if (histoStr == null || histoStr.length < 500 || landPhone) {
                mChartView!!.visibility = View.GONE
            } else {
                mChartView!!.visibility = View.VISIBLE

                val dataSet = getLineDataSet(histoStr)
                val lineData = LineData(dataSet)
                mChartView!!.data = lineData
                formatChart(mChartView!!, dataSet)
                mChartView!!.invalidate()
            }

            val unitPref = AssetPreferences.getPreferredUnit(context)

            val price = mCursor!!.getDouble(ConstantsUtils.POSITION_PRICE)
            if (unitPref == getString(R.string.pref_unit_btc_value)) {
                mPriceTextView!!.text = numberUtils.btcFormat.format(price)
                mUnit!!.text = getString(R.string.pref_unit_btc_value_bref)
            } else {
                mPriceTextView!!.text = numberUtils.dollarFormat.format(price)
            }

            val trend = mCursor!!.getDouble(ConstantsUtils.POSITION_TREND)
            mChangePerTextView!!.text = numberUtils.percentageFormat.format(trend / 100)

            val change: String? = mCursor!!.getString(ConstantsUtils.POSITION_CHANGE)
            val value = java.lang.Double.parseDouble(change)

            val bpChange = getString(R.string.left_bracket_plus) + change + getString(R.string.right_bracket)
            val bChange = getString(R.string.left_bracket) + change + getString(R.string.right_bracket)

            if (change != null && change.length != 0) {

                if (value > 0) {
                    mChangeValueTextView!!.text = bpChange
                    mChangePerTextView!!.setTextColor(resources.getColor(R.color.colorGreen700))
                    mChangeValueTextView!!.setTextColor(resources.getColor(R.color.colorGreen700))
                } else {
                    mChangeValueTextView!!.text = bChange
                    if (value < 0) {
                        mChangePerTextView!!.setTextColor(resources.getColor(R.color.colorRed700))
                        mChangeValueTextView!!.setTextColor(resources.getColor(R.color.colorRed700))
                    } else {
                        mChangePerTextView!!.setTextColor(resources.getColor(R.color.colorOrange700))
                        mChangeValueTextView!!.setTextColor(resources.getColor(R.color.colorOrange700))
                    }

                }
            }

            val logo = mCursor!!.getString(ConstantsUtils.POSITION_IMAGE_URL)
            if (logo != null) {
                val logoUrl = getString(R.string.base_logo_url) + logo
                Glide.with(context!!)
                        .load(logoUrl)
                        .into(mLogoImageView!!)

                mLogoImageView!!.visibility = View.VISIBLE
            } else {
                mLogoImageView!!.visibility = View.INVISIBLE
            }

            //image here
            mOpenTextView!!.text = mCursor!!.getString(ConstantsUtils.POSITION_OPEN24H)
            mLowTextView!!.text = mCursor!!.getString(ConstantsUtils.POSITION_LOW24H)
            mHighTextView!!.text = mCursor!!.getString(ConstantsUtils.POSITION_HIGH24H)
            mVolumeTextView!!.text = mCursor!!.getString(ConstantsUtils.POSITION_VOL24H)
            mVolumeToTextView!!.text = mCursor!!.getString(ConstantsUtils.POSITION_VOL24H2)
            mMktCapTextView!!.text = mCursor!!.getString(ConstantsUtils.POSITION_MKTCAP)
            mAlgorithmTextView!!.text = mCursor!!.getString(ConstantsUtils.POSITION_ALGORITHM)
            mProofTypeTextView!!.text = mCursor!!.getString(ConstantsUtils.POSITION_PROOF_TYPE)

            val sponsor = Integer.valueOf(mCursor!!.getString(ConstantsUtils.POSITION_SPONSOR))!!
            if (sponsor == 1) {
                mSponsorTextView!!.text = getString(R.string.tag_yes)
            } else {
                mSponsorTextView!!.text = getString(R.string.tag_no)
            }

            val currSup = mCursor!!.getString(ConstantsUtils.POSITION_SUPPLY)
            if (currSup != null && !currSup.isEmpty() && currSup != "0") {
                mCurrSupplyTextView!!.text = currSup
            } else {
                mCurrSupplyTextView!!.text = getString(R.string.tag_na)
            }

            val maxSup = mCursor!!.getString(ConstantsUtils.POSITION_TOTAL_SUPPLY)

            if (maxSup != null && !maxSup.isEmpty() && maxSup != "0") {
                mMaxSupplyTextView!!.text = maxSup
            } else {
                mMaxSupplyTextView!!.text = getString(R.string.tag_na)
            }

        }
    }

    private fun getLineDataSet(histoJsonStr: String): LineDataSet {
        val histoEntries = ArrayList<Entry>()
        try {
            val baseJsonObject = JSONObject(histoJsonStr)
            val histoArray = baseJsonObject.getJSONArray(getString(R.string.data_key))

            for (i in 0 until histoArray.length()) {
                val histoObject = histoArray.getJSONObject(i)

                val history = History()

                val low = histoObject.getDouble(getString(R.string.low_key)).toFloat()
                val high = histoObject.getDouble(getString(R.string.high_key)).toFloat()


                val histoEntry = Entry(
                        histoObject.getDouble(getString(R.string.time_key)).toFloat(),
                        (low + high) / 2.0f
                )

                histoEntries.add(histoEntry)
            }
        } catch (e: JSONException) {
            e.stackTrace
        }

        return LineDataSet(histoEntries, getString(R.string.history_label))
    }

    private fun formatChart(chart: LineChart, dataSet: LineDataSet) {

        val backgroundColor = resources.getColor(R.color.colorBlueGrey700)

        val xAxis = chart.xAxis
        xAxis.textColor = Color.WHITE
        //xAxis.setValueFormatter(new DateFormatter(chart));
        xAxis.valueFormatter = DateFormatter(context!!)
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        xAxis.axisLineColor = backgroundColor
        xAxis.axisLineWidth = 1.5f
        //xAxis.enableGridDashedLine(20, 10, 0);

        val yAxisLeft = chart.axisLeft
        yAxisLeft.axisLineColor = backgroundColor
        yAxisLeft.textColor = Color.WHITE
        yAxisLeft.axisLineWidth = 1.5f
        //yAxisLeft.enableGridDashedLine(20,40,0);


        val yAxisRight = chart.axisRight
        yAxisRight.textColor = Color.WHITE
        yAxisRight.axisLineColor = backgroundColor
        //yAxisRight.setAxisLineWidth(2);
        //yAxisRight.enableGridDashedLine(20, 40, 0);

        chart.setDrawGridBackground(false)

        chart.setBackgroundColor(backgroundColor)

        chart.isDragEnabled = true
        chart.setScaleEnabled(true)
        chart.setPinchZoom(false)
        chart.description.isEnabled = false

        val legend = chart.legend
        legend.isEnabled = false

        dataSet.setDrawCircles(false)
        dataSet.setDrawFilled(false)
        dataSet.setDrawValues(false)
        //dataSet.setFillColor(backgroundColor);
        dataSet.setColors(Color.WHITE)
        dataSet.lineWidth = 2f

    }

    fun addBannerAd(view: View) {
        val mAdView = view.findViewById<View>(R.id.adView) as AdView
        // Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        val adRequest = AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build()
        mAdView.loadAd(adRequest)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        loaderManager.initLoader(0, null, this)
    }

    override fun onCreateLoader(i: Int, bundle: Bundle): Loader<Cursor> {
        return AssetLoader.newInstanceForCoinSymbol(activity, mSymbol)
    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor) {
        mCursor = cursor
        if (mCursor != null && mCursor!!.moveToFirst()) {
            //Timber.d("Successfully loaded data for: " + mSymbol);
            bindViews()
        } else {
            mCursor!!.close()
            mCursor = null
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        unbinder!!.unbind()
    }

    companion object {

        private val SYMBOL_LABEL = "SYMBOL"

        fun newInstance(symbol: String): AssetDetailFragment {
            val fragment = AssetDetailFragment()
            val args = Bundle()
            args.putString(SYMBOL_LABEL, symbol)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
