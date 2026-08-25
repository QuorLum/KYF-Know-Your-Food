package com.kyf.knowyourfood.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Emerald500,
    onPrimary = Slate950,
    primaryContainer = Emerald700,
    onPrimaryContainer = Emerald50,
    secondary = Cyan400,
    onSecondary = Slate950,
    secondaryContainer = Cyan600,
    onSecondaryContainer = Slate100,
    background = Slate950,
    onBackground = Slate100,
    surface = Slate900,
    onSurface = Slate100,
    surfaceVariant = Slate800,
    onSurfaceVariant = Slate200,
    error = TrafficRed,
    onError = Slate100
)

private val LightColorScheme = lightColorScheme(
    primary = Emerald600,
    onPrimary = Slate100,
    primaryContainer = Emerald100,
    onPrimaryContainer = Emerald700,
    secondary = Cyan600,
    onSecondary = Slate100,
    secondaryContainer = Cyan400,
    onSecondaryContainer = Slate950,
    background = Slate100,
    onBackground = Slate900,
    surface = Slate100,
    onSurface = Slate900,
    surfaceVariant = Slate200,
    onSurfaceVariant = Slate800,
    error = TrafficRed,
    onError = Slate100
)

@Composable
fun KYFTheme(
    darkTheme: Boolean = true, // Default to sleek Dark Mode
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.let {
                // Use transparent bars for edge-to-edge
                it.statusBarColor = Color.Transparent.toArgb()
                it.navigationBarColor = Color.Transparent.toArgb()
                WindowCompat.getInsetsController(it, view).isAppearanceLightStatusBars = !darkTheme
                WindowCompat.getInsetsController(it, view).isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
