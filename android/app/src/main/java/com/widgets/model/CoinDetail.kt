package com.widgets.model

import com.google.gson.annotations.SerializedName

data class CoinDetail(
    val id: String,
    val symbol: String,
    val image: Image,
    @SerializedName("market_data") val marketData: MarketData
)

data class Image(
    val thumb: String,
    val small: String,
    val large: String
)

data class MarketData(
    @SerializedName("current_price") val currentPrice: Price,
    @SerializedName("market_cap") val marketCap: Price,
    @SerializedName("price_change_percentage_1h_in_currency") val priceChangePercentage1h: Price,
    @SerializedName("price_change_percentage_24h_in_currency") val priceChangePercentage24h: Price
)

data class Price(
    val usd: Double,
    val myr: Double
)
