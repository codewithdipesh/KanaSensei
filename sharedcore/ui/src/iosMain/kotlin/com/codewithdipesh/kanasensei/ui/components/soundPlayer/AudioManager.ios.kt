package com.codewithdipesh.kanasensei.ui.components.soundPlayer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.play
import platform.AVFoundation.pause
import platform.AVFoundation.setRate
import platform.Foundation.NSURL

actual class AudioManager {

    private var player: AVPlayer? = null

    actual fun playTap() {
    }

    actual fun playLockDenied() {
    }

    actual fun playUrl(url: String, speed: Float) {
        val nsUrl = NSURL.URLWithString(url) ?: return
        val item = AVPlayerItem(uRL = nsUrl)
        player = AVPlayer(playerItem = item)
        // play() resets rate to 1f, so for the slow "turtle" reading override it afterwards.
        player?.play()
        if (speed != 1f) {
            player?.setRate(speed)
        }
    }

    actual fun release() {
        player?.pause()
        player = null
    }
}

@Composable
actual fun rememberAudioManager(): AudioManager {
    val manager = remember { AudioManager() }
    DisposableEffect(Unit) {
        onDispose { manager.release() }
    }
    return manager
}