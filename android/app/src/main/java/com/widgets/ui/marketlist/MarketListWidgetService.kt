package com.widgets.ui.marketlist

import android.content.Intent
import android.widget.RemoteViewsService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.widgets.model.Coin
import okhttp3.OkHttpClient
import okhttp3.Request

class MarketListWidgetService : RemoteViewsService() {

    private val client = OkHttpClient()

    fun fetchCoins(): List<Coin> {
        val request = Request.Builder()
            .url("https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&order=market_cap_desc&per_page=10&page=1&sparkline=false&price_change_percentage=24h")
            .build()

        val response = client.newCall(request).execute()
        val type = object : TypeToken<List<Coin>>() {}.type
        return Gson().fromJson(response.body()?.charStream(), type)
    }

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return MarketListWidgetRemoteViewsFactory(
            this.applicationContext,
            intent
        )
    }
}
