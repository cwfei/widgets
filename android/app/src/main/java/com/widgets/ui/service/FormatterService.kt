package com.widgets.ui.service

import java.text.NumberFormat
import java.util.*

object FormatterService {
    val amountFormatter: NumberFormat by lazy {
        val formatter = NumberFormat.getCurrencyInstance(Locale.US)
        formatter.maximumFractionDigits = 1
        formatter.currency = Currency.getInstance("USD")
        formatter
    }

    val percentageFormatter: NumberFormat by lazy {
        val formatter = NumberFormat.getPercentInstance()
        formatter.maximumFractionDigits = 1
        formatter
    }
}
