package com.example.topmovers.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme // Make sure this import is present
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = GrowwGreen,
    onPrimary = GrowwWhite,
    background = GrowwDarkBackground,
    onBackground = GrowwLightGray,
    surface = GrowwDarkSurface,
    onSurface = GrowwWhite
)

private val LightColorScheme = lightColorScheme(
    primary = GrowwGreen,
    onPrimary = GrowwWhite,
    background = GrowwBackground,
    onBackground = GrowwDark,
    surface = GrowwWhite,
    onSurface = GrowwDark
)

@Composable
fun TopMoversTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}