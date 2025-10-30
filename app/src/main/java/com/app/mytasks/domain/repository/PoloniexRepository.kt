package com.app.mytasks.domain.repository

import com.app.mytasks.data.remote.PoloniexWebSocketClient
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PoloniexRepository @Inject constructor(
    val socketClient: PoloniexWebSocketClient
) {
    fun getTickerFlow(symbol: String): Flow<String> = socketClient.connectAndSubscribe(symbol)
}
