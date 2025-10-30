package com.app.mytasks.data.entities

import kotlinx.serialization.Serializable

@Serializable
data class TickerMessage(
    val channel: String,
    val data: List<TickerData>
)

@Serializable
data class TickerData(
    val symbol: String,
    val close: String,
    val dailyChange: String
)
