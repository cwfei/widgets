package com.widgets.ui.market

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.RemoteViews
import com.widgets.R
import com.widgets.ui.ticker.CoinTickerWidgetProvider

/**
 * Implementation of App Widget functionality.
 */
class MarketListWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            // Instruct the widget manager to update the widget
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent?) {
        super.onReceive(context, intent)

        val appWidgetId = intent?.getIntExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            return
        }

        val appWidgetManager = AppWidgetManager.getInstance(context)
        Log.d("CoinTicker", "onReceived market list: ${appWidgetId}")
        //updateAppWidget(context, appWidgetManager, appWidgetId!!)
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId!!, R.id.listView)
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val intent = Intent(context, MarketListWidgetService::class.java)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME));
        val views = RemoteViews(context.packageName, R.layout.market_list_widget)
        views.setRemoteAdapter(R.id.listView, intent)

        val appWidgetUpdateIntent = Intent(context, MarketListWidgetProvider::class.java)
        appWidgetUpdateIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        Log.d("CoinTicker", "Setting pending intent, ${appWidgetId}")
        appWidgetUpdateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        val appWidgetUpdatePendingIntent = PendingIntent.getBroadcast(
            context,
            appWidgetId,
            appWidgetUpdateIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        views.setOnClickPendingIntent(R.id.refreshButton, appWidgetUpdatePendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}