package com.app.mytasks.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.mytasks.viemodel.CryptoViewModel

/**
 * CryptoListTab
 *
 * @author stephingeorge
 * @date 30/10/2025
 */@Composable
fun CryptoScreen(viewModel: CryptoViewModel = hiltViewModel()) {
    val ticker by viewModel.ticker.collectAsState()
    val selectedCoin by viewModel.selectedCoin.collectAsState()

    val majorCoins = listOf(
        "BTC_USDT",
        "ETH_USDT",
        "BNB_USDT",
        "SOL_USDT",
        "XRP_USDT",
        "ADA_USDT",
        "DOGE_USDT",
        "DOT_USDT",
        "MATIC_USDT",
        "LTC_USDT"
    )

    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // 🔹 Dropdown for coin selection
        Box {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text(selectedCoin)
                Icon(Icons.Filled.ArrowDropDown, contentDescription = "Dropdown")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                majorCoins.forEach { coin ->
                    DropdownMenuItem(
                        text = { Text(coin) },
                        onClick = {
                            expanded = false
                            viewModel.changeCoin(coin)
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        if (ticker != null) {
            val change = ticker!!.dailyChange.toDoubleOrNull() ?: 0.0
            val close = ticker!!.close.toDoubleOrNull() ?: 0.0
            val color = if (change >= 0) Color(0xFF00C853) else Color(0xFFD50000)
            val sign = if (change >= 0) "▲" else "▼"

            Text(
                text = ticker!!.symbol,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "$${String.format("%,.2f", close)}",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "$sign ${(change * 100).format(2)}%",
                color = color,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

// helper
fun Double.format(digits: Int) = "%.${digits}f".format(this)
