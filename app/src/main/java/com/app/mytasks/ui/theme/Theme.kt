package com.app.mytasks.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.app.mytasks.util.ColorPreferences
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6200EE), // Default will be overridden
    secondary = Color(0xFF03DAC6),
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFF5F5F5),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black
)
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBB86FC), // Default will be overridden
    secondary = Color(0xFF03DAC6),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun TaskManagerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    colorPreferences: ColorPreferences,
    content: @Composable () -> Unit
) {
    val primaryColorFlow = colorPreferences.primaryColor // Flow<Int>
    val primaryColor by primaryColorFlow.collectAsState(initial = ColorPreferences.DEFAULT_COLOR)

    val colorScheme = if (darkTheme) {
        DarkColorScheme.copy(primary = Color(primaryColor)) // Convert to Int
    } else {
        LightColorScheme.copy(primary = Color(primaryColor)) // Convert to Int
    }

    val systemUiController = rememberSystemUiController()
    val statusBarColor = colorScheme.primary
    SideEffect {
        systemUiController.setStatusBarColor(
            color = statusBarColor,
            darkIcons = !darkTheme
        )
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}