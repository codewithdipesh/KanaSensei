package com.codewithdipesh.kanasensei.ui.components.soundPlayer

import androidx.compose.runtime.Composable

@Composable
expect fun rememberAudioManager(): AudioManager

expect class AudioManager {
    fun playTap()
    fun playLockDenied()
    fun release()

}

