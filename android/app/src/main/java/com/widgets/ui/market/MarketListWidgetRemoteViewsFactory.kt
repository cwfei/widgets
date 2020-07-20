package com.widgets.ui.market

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.widgets.R
import com.widgets.model.Coin
import okhttp3.OkHttpClient
import okhttp3.Request
import java.text.NumberFormat
import java.util.*

class MarketListWidgetRemoteViewsFactory constructor(
    private val context: Context,
    private val intent: Intent
) : RemoteViewsService.RemoteViewsFactory {

    private val amountFormatter: NumberFormat by lazy {
        val formatter = NumberFormat.getCurrencyInstance(Locale.US)
        formatter.maximumFractionDigits = 1
        formatter.currency = Currency.getInstance("USD")
        formatter
    }

    private val percentageFormatter: NumberFormat by lazy {
        val formatter = NumberFormat.getPercentInstance()
        formatter.maximumFractionDigits = 1
        formatter
    }

    private var items: List<Coin> = listOf()

    override fun onCreate() {
    }

    override fun onDestroy() {
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getViewAt(position: Int): RemoteViews {
        val item = items[position]
        val bitmap = Glide.with(context).asBitmap().load(item.image).submit().get()
        val remoteViews = RemoteViews(context.packageName, R.layout.item_market_list)
        remoteViews.setImageViewBitmap(R.id.imageView, bitmap)
        remoteViews.setTextViewText(R.id.symbolTextView, item.symbol)
        remoteViews.setTextViewText(R.id.priceTextView, amountFormatter.format(item.currentPrice))
        remoteViews.setTextColor(R.id.percentageTextView, if (item.priceChangePercentage>=0) {
            Color.parseColor("#69ce4d")
        } else {
            Color.parseColor("#eb4034")
        })
        remoteViews.setTextViewText(R.id.percentageTextView, percentageFormatter.format(item.priceChangePercentage))
        remoteViews.setTextViewText(R.id.marketCapTextView, amountFormatter.format(item.marketCap))
        return remoteViews
    }

    override fun getLoadingView(): RemoteViews? {
        // You can create a custom loading view (for instance when getViewAt() is slow.) If you
        // return null here, you will get the default loading view.
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun onDataSetChanged() {
        val request = Request.Builder()
            .url("https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&order=market_cap_desc&per_page=10&page=1&sparkline=false&price_change_percentage=24h")
            .build()

        val response = OkHttpClient().newCall(request).execute()
        val type = object : TypeToken<List<Coin>>() {}.type
        val coins = Gson().fromJson<List<Coin>>(response.body()?.charStream(), type)
        items = coins
    }
}
