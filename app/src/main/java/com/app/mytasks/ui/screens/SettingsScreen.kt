package com.app.mytasks.ui.screens

import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.app.mytasks.util.ColorPreferences
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    colorPreferences: ColorPreferences,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val currentColor by colorPreferences.primaryColor.collectAsState(initial = ColorPreferences.DEFAULT_COLOR)
    val colorOptions = listOf(
        "Purple" to Color(0xFF6200EE),
        "Blue" to Color(0xFF1976D2),
        "Green" to Color(0xFF2E7D32),
        "Red" to Color(0xFFD32F2F),
        "Orange" to Color(0xFFF57C00)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            item {
                Text(
                    text = "Primary Color",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(16.dp)
                )
            }
            items(colorOptions) { (name, color) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            scope.launch {
                                val colorInt = color.toArgb()
                                colorPreferences.savePrimaryColor(colorInt)
                            }
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(color)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = name)
                    Spacer(modifier = Modifier.weight(1f))
                    if (color.toArgb() == currentColor) {
                        Icon(Icons.Default.Check, contentDescription = "Selected")
                    }
                }
            }
        }
    }
}