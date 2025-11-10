package com.app.mytasks.viemodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.mytasks.data.entities.TickerData
import com.app.mytasks.data.entities.TickerMessage
import com.app.mytasks.domain.repository.PoloniexRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class CryptoViewModel @Inject constructor(
    private val repo: PoloniexRepository
) : ViewModel() {

    private val _selectedCoin = MutableStateFlow("BTC_USDT")
    val selectedCoin: StateFlow<String> = _selectedCoin

    private val _ticker = MutableStateFlow<TickerData?>(null)
    val ticker: StateFlow<TickerData?> = _ticker

    init {
        observeSelectedCoin()
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private fun observeSelectedCoin() {
        viewModelScope.launch {
            _selectedCoin.debounce(400)
                .flatMapLatest { symbol ->
                    repo.getTickerFlow(symbol) // cancels old socket automatically
                }
                .collect { json ->
                    parseTickerMessage(json)?.let {
                        _ticker.value = it
                    }
                }
        }
    }


    val jsonParser = Json {
        ignoreUnknownKeys = true  // ✅ Ignore extra fields like volume, open, etc.
    }

    fun parseTickerMessage(json: String): TickerData? {
        return try {
            val tickerMessage = jsonParser.decodeFromString<TickerMessage>(json)
            tickerMessage.data.firstOrNull()  // we only need the first ticker update
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun changeCoin(symbol: String) {
        _selectedCoin.value = symbol
    }


    override fun onCleared() {
        super.onCleared()
        repo.socketClient.disconnect()
    }
}
