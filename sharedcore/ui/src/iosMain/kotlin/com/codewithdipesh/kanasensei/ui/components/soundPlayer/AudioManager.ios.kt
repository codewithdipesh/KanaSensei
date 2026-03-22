package com.codewithdipesh.kanasensei.ui.components.soundPlayer

import androidx.compose.runtime.Composable

actual class AudioManager {
    actual fun playTap() {
    }

    actual fun playLockDenied() {
    }

    actual fun release() {
    }
}

@Composable
actual fun rememberAudioManager(): AudioManager {
    TODO("Not yet implemented")
}
