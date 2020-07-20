package com.widgets.model

import com.google.gson.annotations.SerializedName

data class Coin(
    val id: String,
    val symbol: String,
    val image: String,
    @SerializedName("current_price") val currentPrice: Double,
    @SerializedName("market_cap") val marketCap: Double,
    @SerializedName("price_change_percentage_24h") val priceChangePercentage: Double
)
