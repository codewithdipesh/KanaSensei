package com.codewithdipesh.kanasensei.ui.components.soundPlayer

import androidx.compose.runtime.Composable

@Composable
expect fun rememberAudioManager(): AudioManager

expect class AudioManager {
    fun playTap()
    fun playLockDenied()

    /**
     * Streams and plays an audio file from a remote URL (e.g. a kana's [audioUrl]).
     * @param speed playback rate; use 1f for normal and e.g. 0.6f for the slow "turtle" reading.
     */
    fun playUrl(url: String, speed: Float = 1f)
    fun release()

}

