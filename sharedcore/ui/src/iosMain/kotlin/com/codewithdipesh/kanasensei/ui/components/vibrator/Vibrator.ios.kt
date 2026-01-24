package com.codewithdipesh.kanasensei.ui.components.vibrator

import androidx.compose.runtime.Composable

actual class HapticManager {
    actual fun correctHaptic() {
    }

    actual fun wrongHaptic() {
    }
}

@Composable
actual fun rememberHapticManager(): HapticManager {
    TODO("Not yet implemented")
}