package com.widgets.ui.ticker

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.widget.RemoteViews
import com.bumptech.glide.Glide
import com.widgets.R
import com.widgets.ui.service.DataStore
import com.widgets.ui.service.FormatterService
import com.widgets.ui.service.store

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [CoinTickerWidgetConfigureActivity]
 */
class CoinTickerWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // When the user deletes the widget, delete the preference associated with it.
        for (appWidgetId in appWidgetIds) {
            DataStore.deleteCoinTickerWidget(context, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created.
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled.
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val coinId = DataStore.loadCoinTickerWidget(context, appWidgetId) ?: return
    DataStore.fetchCoinDetail(coinId) {
        val coin = it ?: return@fetchCoinDetail

        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName, R.layout.coin_ticker_widget)
        val bitmap = Glide.with(context).asBitmap().load(coin.image.large).submit().get()
        views.setImageViewBitmap(R.id.imageView, bitmap)
        views.setTextViewText(R.id.titleTextView, coin.symbol)
        views.setTextViewText(
            R.id.priceTextView,
            FormatterService.amountFormatter.format(coin.marketData.currentPrice.usd)
        )
        views.setTextColor(
            R.id.priceChangePercentage1hTextView,
            if (coin.marketData.priceChangePercentage1h.usd >= 0) {
                Color.parseColor("#69ce4d")
            } else {
                Color.parseColor("#eb4034")
            }
        )
        views.setTextViewText(
            R.id.priceChangePercentage1hTextView,
            FormatterService.percentageFormatter.format(coin.marketData.priceChangePercentage1h.usd)
        )
        views.setTextColor(
            R.id.priceChangePercentage24hTextView,
            if (coin.marketData.priceChangePercentage24h.usd >= 0) {
                Color.parseColor("#69ce4d")
            } else {
                Color.parseColor("#eb4034")
            }
        )
        views.setTextViewText(
            R.id.priceChangePercentage24hTextView,
            FormatterService.percentageFormatter.format(coin.marketData.priceChangePercentage24h.usd)
        )

        // Instruct the widget manager to update the widget.
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}