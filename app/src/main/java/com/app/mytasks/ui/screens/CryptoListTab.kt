package com.app.mytasks.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
 */
@Composable
fun CryptoScreen(viewModel: CryptoViewModel = hiltViewModel()) {
    val ticker = viewModel.tickerData.collectAsState().value

    LaunchedEffect(Unit) {
        viewModel.startTicker("SOL_USDT")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "💹 Poloniex Live Ticker",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(20.dp))

        if (ticker != null) {
            val price = ticker.close.toDoubleOrNull() ?: 0.0
            val change = ticker.dailyChange.toDoubleOrNull() ?: 0.0
            val isUp = change >= 0

            Text(
                text = ticker.symbol,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "$${String.format("%.2f", price)}",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = if (isUp)
                        Color(0xFF00C853) // green
                    else
                        Color(0xFFD50000) // red
                )
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "${String.format("%.2f", change * 100)}%",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = if (isUp)
                        Color(0xFF00C853)
                    else
                        Color(0xFFD50000)
                )
            )
        } else {
            CircularProgressIndicator()
        }
    }
}
