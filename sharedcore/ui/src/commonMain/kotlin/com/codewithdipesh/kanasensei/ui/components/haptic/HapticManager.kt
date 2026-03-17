package com.codewithdipesh.kanasensei.ui.components.haptic

import androidx.compose.runtime.Composable


@Composable
expect fun rememberHapticManager(): HapticManager


expect class HapticManager {
    fun correctHaptic()
    fun wrongHaptic()
    fun softBounce()
}