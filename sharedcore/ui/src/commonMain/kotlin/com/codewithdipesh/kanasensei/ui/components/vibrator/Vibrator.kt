package com.codewithdipesh.kanasensei.ui.components.vibrator

import androidx.compose.runtime.Composable


@Composable
expect fun rememberHapticManager(): HapticManager


expect class HapticManager {
    fun correctHaptic()
    fun wrongHaptic()
}