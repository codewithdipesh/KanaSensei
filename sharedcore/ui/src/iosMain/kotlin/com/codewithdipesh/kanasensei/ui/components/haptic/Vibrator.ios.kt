package com.codewithdipesh.kanasensei.ui.components.haptic

import androidx.compose.runtime.Composable

actual class HapticManager {
    actual fun correctHaptic() {
    }

    actual fun wrongHaptic() {
    }

    actual fun softBounce() {
    }
}

@Composable
actual fun rememberHapticManager(): HapticManager {
    TODO("Not yet implemented")
}