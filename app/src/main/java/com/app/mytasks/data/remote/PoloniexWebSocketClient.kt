package com.app.mytasks.data.remote

import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.delay
import okhttp3.*

class PoloniexWebSocketClient {

    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null
    private val endpoint = "wss://ws.poloniex.com/ws/public"

    // Start connection and emit incoming messages
    fun connectAndSubscribe(symbol: String = "BTC_USDT"): Flow<String> = callbackFlow {
        val request = Request.Builder().url(endpoint).build()

        val listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("PoloniexWS", "✅ Connected to Poloniex WebSocket")
                val subscribeMsg = """
                    {
                      "event": "subscribe",
                      "channel": ["ticker"],
                      "symbols": ["$symbol"]
                    }
                """.trimIndent()
                webSocket.send(subscribeMsg)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                trySend(text)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("PoloniexWS", "❌ WebSocket failure: ${t.message}")
                trySendBlocking("""{"error":"${t.message}"}""")  // optional signal
                // Delay closing to ensure no concurrent send crash
                try {
                    webSocket.close(1001, "Unexpected disconnect")
                } catch (_: Exception) {}
                close() // just closes the channel, no exception propagation
            }
            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("PoloniexWS", "🔌 Closed: $reason")
            }
        }

        webSocket = client.newWebSocket(request, listener)

        // Send ping every 25 seconds to keep connection alive
        val pingScope = CoroutineScope(Dispatchers.IO)
        pingScope.launch {
            while (true) {
                delay(25_000)
                webSocket?.send("""{"event":"ping"}""")
            }
        }

        awaitClose {
            webSocket?.close(1000, "Closed by user")
            client.dispatcher.executorService.shutdown()
        }
    }

    fun disconnect() {
        webSocket?.close(1000, "Disconnected manually")
        webSocket = null
    }
}
