package com.widgets.ui.service

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.widgets.BuildConfig
import com.widgets.model.Coin
import com.widgets.model.CoinDetail
import okhttp3.*
import java.io.IOException

object DataStore {

    private const val BASE_URL = "https://api.coingecko.com/api/v3"

    private val client = OkHttpClient()

    private const val PREF_COIN_TICKER_WIDGET_KEY = "appwidget_coin_ticker"

    // Store the coin ID associated with the app widget.
    fun storeCoinTickerWidget(context: Context, appWidgetId: Int, coinId: String) {
        val sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, 0).edit()
        sharedPreferences.putString(PREF_COIN_TICKER_WIDGET_KEY + appWidgetId, coinId)
        sharedPreferences.apply()
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, the widget does not exist.
    fun loadCoinTickerWidget(context: Context, appWidgetId: Int): String? {
        val sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, 0)
        return sharedPreferences.getString(PREF_COIN_TICKER_WIDGET_KEY + appWidgetId, null)
    }

    // Delete the app widget's preference.
    fun deleteCoinTickerWidget(context: Context, appWidgetId: Int) {
        val sharedPreferences = context.getSharedPreferences(BuildConfig.APPLICATION_ID, 0).edit()
        sharedPreferences.remove(PREF_COIN_TICKER_WIDGET_KEY + appWidgetId)
        sharedPreferences.apply()
    }

    fun fetchCoins(): List<Coin> {
        val urlBuilder = requireNotNull(HttpUrl.parse(BASE_URL)).newBuilder()
            .addPathSegment("coins")
            .addPathSegment("markets")
            .addQueryParameter("vs_currency", "usd")
            .addQueryParameter("order", "market_cap_desc")
            .addQueryParameter("per_page", "10")
            .addQueryParameter("page", "1")
            .addQueryParameter("sparkline", "false")
            .addQueryParameter("price_change_percentage", "24h")
        val request = Request.Builder()
            .url(urlBuilder.build().toString())
            .build()

        val response = client.newCall(request).execute()
        val type = object : TypeToken<List<Coin>>() {}.type
        return Gson().fromJson(response.body()?.charStream(), type)
    }

    fun fetchCoins(callback: (coins: List<Coin>) -> Unit) {
        val urlBuilder = requireNotNull(HttpUrl.parse(BASE_URL)).newBuilder()
            .addPathSegment("coins")
            .addPathSegment("markets")
            .addQueryParameter("vs_currency", "usd")
            .addQueryParameter("order", "market_cap_desc")
            .addQueryParameter("per_page", "20")
            .addQueryParameter("page", "1")
            .addQueryParameter("sparkline", "false")
            .addQueryParameter("price_change_percentage", "24h")
        val request = Request.Builder()
            .url(urlBuilder.build().toString())
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.invoke(listOf())
            }

            override fun onResponse(call: Call, response: Response) {
                val type = object : TypeToken<List<Coin>>() {}.type
                callback.invoke(Gson().fromJson(response.body()?.charStream(), type))
            }
        })
    }

    fun fetchCoinDetail(coinId: String, callback: (coinDetail: CoinDetail?) -> Unit) {
        val urlBuilder = requireNotNull(HttpUrl.parse(BASE_URL)).newBuilder()
            .addPathSegment("coins")
            .addPathSegment(coinId)
            .addQueryParameter("localization", "true")
            .addQueryParameter("tickers", "false")
            .addQueryParameter("market_data", "true")
            .addQueryParameter("community_data", "false")
            .addQueryParameter("developer_data", "false")
            .addQueryParameter("sparkline", "false")
        val request = Request.Builder()
            .url(urlBuilder.build().toString())
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.invoke(null)
            }

            override fun onResponse(call: Call, response: Response) {
                callback.invoke(
                    Gson().fromJson(
                        response.body()?.charStream(),
                        CoinDetail::class.java
                    )
                )
            }
        })
    }
}

// Extensions

internal val Any.store get() = DataStore
