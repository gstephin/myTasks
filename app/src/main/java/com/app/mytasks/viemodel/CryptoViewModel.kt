package com.app.mytasks.viemodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.mytasks.data.entities.TickerData
import com.app.mytasks.data.entities.TickerMessage
import com.app.mytasks.domain.repository.PoloniexRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CryptoViewModel @Inject constructor(
    private val repository: PoloniexRepository
) : ViewModel() {
    private val _tickerData = MutableStateFlow<TickerData?>(null)
    val tickerData: StateFlow<TickerData?> = _tickerData

    private val json = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }

    fun startTicker(symbol: String = "SOL") {
        viewModelScope.launch {
            repository.getTickerFlow(symbol).collect { message ->
                try {
                    Log.d("CryptoViewModel", "Received message: $message")

                    val parsed = json.decodeFromString<TickerMessage>(message)
                    if (parsed.channel == "ticker" && parsed.data.isNotEmpty()) {
                        _tickerData.value = parsed.data.first()
                    }
                } catch (e: Exception) {
                    Log.e("CryptoViewModel", "JSON parse error: ${e.message}")
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.socketClient.disconnect()
    }
}
