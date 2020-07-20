package com.widgets.ui.ticker

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.widgets.R
import com.widgets.model.Coin
import com.widgets.ui.service.DataStore
import com.widgets.ui.service.store
import kotlinx.android.synthetic.main.coin_ticker_widget_configure.*

/**
 * The configuration screen for the [CoinTickerWidgetProvider] AppWidget.
 */
class CoinTickerWidgetConfigureActivity : Activity(), CoinTickerWidgetConfigureAdapter.Listener {
    private val adapter = CoinTickerWidgetConfigureAdapter()
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED)

        setContentView(R.layout.coin_ticker_widget_configure)

        // Configure recycler view and item tap listener.
        adapter.listener = this
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        store.fetchCoins {
            runOnUiThread {
                progressBar.visibility = View.GONE
                adapter.items = it
            }
        }

        // Find the widget id from the intent.
        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            appWidgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }
    }

    override fun onSelectCoin(coin: Coin) {
        DataStore.storeCoinTickerWidget(this, appWidgetId, coin.id)

        // It is the responsibility of the configuration activity to update the app widget.
        val appWidgetManager = AppWidgetManager.getInstance(this)
        updateAppWidget(this, appWidgetManager, appWidgetId)

        // Make sure we pass back the original appWidgetId
        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        setResult(RESULT_OK, resultValue)
        finish()
    }
}
