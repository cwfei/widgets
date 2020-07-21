package com.widgets.ui.market

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.bumptech.glide.Glide
import com.widgets.R
import com.widgets.model.Coin
import com.widgets.ui.service.DataStore
import com.widgets.ui.service.FormatterService
import java.text.NumberFormat
import java.util.*

class MarketListWidgetRemoteViewsFactory constructor(
    private val context: Context,
    private val intent: Intent
) : RemoteViewsService.RemoteViewsFactory {


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
        remoteViews.setTextViewText(
            R.id.priceTextView,
            FormatterService.amountFormatter.format(item.currentPrice)
        )
        remoteViews.setTextColor(
            R.id.percentageTextView, if (item.priceChangePercentage >= 0) {
                Color.parseColor("#69ce4d")
            } else {
                Color.parseColor("#eb4034")
            }
        )
        remoteViews.setTextViewText(
            R.id.percentageTextView,
            FormatterService.percentageFormatter.format(item.priceChangePercentage)
        )
        remoteViews.setTextViewText(
            R.id.marketCapTextView,
            FormatterService.amountFormatter.format(item.marketCap)
        )
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
        items = DataStore.fetchCoins()
        Log.d("CoinTicker", "onDataSetChanged")
    }
}
