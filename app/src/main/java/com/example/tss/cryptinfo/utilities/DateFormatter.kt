package com.example.tss.cryptinfo.utilities

import android.content.Context

import com.example.tss.cryptinfo.api.AssetPreferences
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter

import java.util.Calendar

class DateFormatter(private val mContext: Context) : IAxisValueFormatter {

    override fun getFormattedValue(value: Float, axis: AxisBase): String {
        val intervalPref = AssetPreferences.getPreferredInterval(mContext)

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = value.toLong() * 1000

        val mMonth = calendar.get(Calendar.MONTH) + 1
        val mDay = calendar.get(Calendar.DAY_OF_MONTH)
        val mHour = calendar.get(Calendar.HOUR_OF_DAY)
        val mMinute = calendar.get(Calendar.MINUTE)
        val mAP = calendar.get(Calendar.AM_PM)

        val minStr = if (mMinute < 10) "0" + mMinute else "" + mMinute
        val apStr = if (mAP == 0) "AM" else "PM"

        var histoTime = mMonth.toString() + "/" + mDay + " " + mHour % 12 + apStr

        if (intervalPref == "day") {
            histoTime = mMonth.toString() + "/" + mDay
        } else if (intervalPref == "minute") {
            histoTime = (mHour % 12).toString() + ":" + minStr + apStr
        }

        return histoTime
    }

}
