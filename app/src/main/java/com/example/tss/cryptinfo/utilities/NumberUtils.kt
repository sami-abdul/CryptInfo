package com.example.tss.cryptinfo.utilities

import java.text.DecimalFormat
import java.text.Format
import java.text.NumberFormat
import java.util.Locale

class NumberUtils {
    val dollarFormatWithPlus: DecimalFormat
    val dollarFormat: DecimalFormat
    val dollarFormatWithSign: DecimalFormat
    val percentageFormat: DecimalFormat

    val btcFormat: DecimalFormat
    val btcFormatWithPlus: DecimalFormat
    val btcFormatWithSign: DecimalFormat

    init {
        dollarFormatWithSign = NumberFormat.getCurrencyInstance(Locale.US) as DecimalFormat
        dollarFormat = NumberFormat.getNumberInstance(Locale.getDefault()) as DecimalFormat
        dollarFormat.maximumFractionDigits = 2
        dollarFormat.minimumFractionDigits = 2

        dollarFormatWithPlus = NumberFormat.getCurrencyInstance(Locale.US) as DecimalFormat
        dollarFormatWithPlus.positivePrefix = "+$"
        percentageFormat = NumberFormat.getPercentInstance(Locale.getDefault()) as DecimalFormat
        percentageFormat.maximumFractionDigits = 2
        percentageFormat.minimumFractionDigits = 2
        percentageFormat.positivePrefix = "+"

        btcFormat = NumberFormat.getNumberInstance(Locale.getDefault()) as DecimalFormat
        btcFormat.maximumFractionDigits = 6
        btcFormat.minimumFractionDigits = 6

        btcFormatWithSign = NumberFormat.getNumberInstance(Locale.getDefault()) as DecimalFormat
        btcFormatWithSign.maximumFractionDigits = 6
        btcFormatWithSign.minimumFractionDigits = 6
        btcFormatWithSign.positivePrefix = "B"

        btcFormatWithPlus = NumberFormat.getNumberInstance(Locale.getDefault()) as DecimalFormat
        btcFormatWithPlus.positivePrefix = "+B"
    }
}
