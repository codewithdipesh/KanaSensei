package com.codewithdipesh.kanasensei.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = DarkBrown,
    secondary = DarkGray2,
    tertiary = DarkGray2,
    background = DarkGray3,
    surface = DarkCard,
    error = DarkRed,
    scrim = DarkGreen,
    onPrimary = White,
    onSecondary = DarkGray3,
    onBackground = White,
    onSurface = DarkGray1,
    onError = White
)

@Composable
fun KanaSenseiTheme(
    content: @Composable () -> Unit = {}
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = KanaSenseiTypography,
        content = content
    )
}
