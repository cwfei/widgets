package com.widgets.ui.ticker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.widgets.R
import com.widgets.model.Coin
import kotlinx.android.synthetic.main.item_coin.view.*

class CoinTickerWidgetConfigureAdapter :
    RecyclerView.Adapter<CoinTickerWidgetConfigureAdapter.ViewHolder>() {

    interface Listener {
        fun onSelectCoin(coin: Coin)
    }

    var listener: Listener? = null

    var items: List<Coin> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_coin, parent, false)
        return ViewHolder(layout)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Coin) {
            Glide.with(itemView.imageView).load(item.image).into(itemView.imageView)
            itemView.symbolTextView.text = item.symbol
            itemView.setOnClickListener {
                listener?.onSelectCoin(item)
            }
        }
    }
}