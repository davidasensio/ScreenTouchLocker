package com.handysparksoft.screentouchlocker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Material 3 has no primaryVariant, so Red700 is carried on the primary container instead.
private val DarkColorScheme = darkColorScheme(
    primary = Red200,
    onPrimary = Color.Black,
    primaryContainer = Red700,
    secondary = Blue200,
)

private val LightColorScheme = lightColorScheme(
    primary = Red500,
    onPrimary = Color.White,
    primaryContainer = Red700,
    secondary = Blue200,
)

@Composable
fun ScreenTouchLockerTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content,
    )
}
