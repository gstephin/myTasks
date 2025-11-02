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

private val DarkColorScheme = darkColorScheme(
    primary = Black,
    secondary = Grey,
    tertiary = Blue
)

private val LightColorScheme = lightColorScheme(
    primary = White,
    secondary = Grey,
    tertiary = Blue,

    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),

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