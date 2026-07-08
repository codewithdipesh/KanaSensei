package com.codewithdipesh.kanasensei.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
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

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    secondary = LightGray2,
    tertiary = LightGray1,
    background = White,
    surface = LightCard,
    error = LightRed,
    scrim = LightGreen,
    onPrimary = White,
    onSecondary = White,
    onBackground = Black,
    onSurface = LightGray3,
    onError = White
)


@Composable
fun KanaSenseiTheme(
    content: @Composable () -> Unit = {}
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = KanaSenseiTypography,
        content = content
    )
}
