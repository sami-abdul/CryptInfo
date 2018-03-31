package com.example.tss.cryptinfo.adapters

import android.content.Context
import android.database.Cursor
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


import com.example.tss.cryptinfo.R
import com.example.tss.cryptinfo.api.AssetPreferences
import com.example.tss.cryptinfo.utilities.ConstantsUtils
import com.example.tss.cryptinfo.utilities.NumberUtils

import butterknife.BindView
import butterknife.ButterKnife

class AssetAdapter(private val mContext: Context, private val mClickHandler: CoinAdapterOnclickHandler) : RecyclerView.Adapter<AssetAdapter.CoinViewHolder>() {

    interface CoinAdapterOnclickHandler {
        fun onClick(symbol: String, name: String)
    }

    companion object {
        private val BTC = "BTC"
    }

    private var mCursor: Cursor? = null
    private val numberUtils: NumberUtils

    init {
        this.numberUtils = NumberUtils()
    }

    override fun getItemId(position: Int): Long {
        mCursor!!.moveToPosition(position)
        return mCursor!!.getLong(ConstantsUtils.POSITION_ID)
    }

    fun getSymbolAtPosition(position: Int): String {
        mCursor!!.moveToPosition(position)
        return mCursor!!.getString(ConstantsUtils.POSITION_SYMBOL)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_coin_basic, parent, false)

        return CoinViewHolder(view)
    }

    override fun onBindViewHolder(holder: CoinViewHolder, position: Int) {
        if (!mCursor!!.moveToFirst()) {
            return
        }

        mCursor!!.moveToPosition(position)

        val coinSymbol = mCursor!!.getString(ConstantsUtils.POSITION_SYMBOL)
        val name = mCursor!!.getString(ConstantsUtils.POSITION_NAME)

        val price = mCursor!!.getDouble(ConstantsUtils.POSITION_PRICE)
        val trend = mCursor!!.getDouble(ConstantsUtils.POSITION_TREND)

        if (trend > 0.0) {
            holder.mChangeTextView!!.background = mContext.resources.getDrawable(R.drawable.price_increase_green)
        } else if (trend < 0.0) {
            holder.mChangeTextView!!.background = mContext.resources.getDrawable(R.drawable.price_decrease_red)
        } else {
            holder.mChangeTextView!!.background = mContext.resources.getDrawable(R.drawable.price_no_change_orange)
        }

        holder.mSymbolTextView!!.text = coinSymbol
        holder.mNameTextView!!.text = name

        val unitPref = AssetPreferences.getPreferredUnit(mContext)

        if (unitPref == BTC) {
            holder.mPriceTextView!!.text = numberUtils.btcFormatWithSign.format(price)
        } else {
            holder.mPriceTextView!!.text = numberUtils.dollarFormatWithSign.format(price)
        }
        holder.mChangeTextView!!.text = numberUtils.percentageFormat.format(trend / 100.0)
    }

    override fun getItemCount(): Int {
        return if (mCursor != null) {
            mCursor!!.count
        } else 0
    }

    fun swapCursor(cursor: Cursor) {
        mCursor = cursor
        notifyDataSetChanged()
    }

    inner class CoinViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        @BindView(R.id.symbol)
        internal var mSymbolTextView: TextView? = null
        @BindView(R.id.full_name)
        internal var mNameTextView: TextView? = null
        @BindView(R.id.price)
        internal var mPriceTextView: TextView? = null
        @BindView(R.id.change)
        internal var mChangeTextView: TextView? = null

        init {
            ButterKnife.bind(this, itemView)

            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            val adapterPosition = adapterPosition
            mCursor!!.moveToPosition(adapterPosition)

            mClickHandler
                    .onClick(mCursor!!.getString(ConstantsUtils.POSITION_SYMBOL),
                            mCursor!!.getString(ConstantsUtils.POSITION_NAME))
        }

    }
}