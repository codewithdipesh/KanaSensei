package com.codewithdipesh.kanasensei.ui.components.soundPlayer

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.codewithdipesh.kanasensei.ui.components.soundPlayer.Constants.DENIED_SOUND
import com.codewithdipesh.kanasensei.ui.components.soundPlayer.Constants.TAP_SOUND
import com.codewithdipesh.kanasensei.ui.resources.Res
import kotlin.collections.set

@Composable
actual fun rememberAudioManager(): AudioManager {
    val context = LocalContext.current
    val manager = remember {
        AudioManager(context)
    }
    DisposableEffect(Unit) {
        onDispose {
            manager.release()
        }
    }
    return manager
}

object Constants {
    const val TAP_SOUND = "tap"
    const val DENIED_SOUND = "denied"
}

actual class AudioManager(context: Context) {

    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(2)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()
    private val soundMap: MutableMap<String, Int> = mutableMapOf()

    // Separate player for streamed remote audio (kana mp3s). SoundPool is only for short bundled SFX.
    private var mediaPlayer: MediaPlayer? = null

    init {
        loadSound(context, "files/tap_sound.ogg", TAP_SOUND)
        loadSound(context, "files/denied_sound.ogg", DENIED_SOUND)
    }

    private fun loadSound(context: Context, resPath: String, key: String) {
        try {
            /*Imp  Compose resources on Android get packaged as Android assets automatically
            (the build.gradle workaround already handles this). Res.getUri("files/tap_sound.ogg") returns
            file:///android_asset/composeResources/.../files/tap_sound.ogg. We strip the prefix to get the
            asset path and load directly into SoundPool. No temp files, no async loading.
             */
            val assetPath = Res.getUri(resPath).removePrefix("file:///android_asset/")
            val fd = context.assets.openFd(assetPath)
            soundMap[key] = soundPool.load(fd, 1)
            fd.close()
        } catch (_: Exception) {
            println("Sound file not found: $resPath")
        }
    }

    actual fun playTap() {
        soundMap[TAP_SOUND]?.let {
            soundPool.play(it, 1f, 1f, 1, 0, 1f)
        }
    }

    actual fun playLockDenied() {
        soundMap[DENIED_SOUND]?.let {
            soundPool.play(it, 1f, 1f, 1, 0, 1f)
        }
    }

    actual fun playUrl(url: String, speed: Float) {
        try {
            // Release any previous stream so taps don't overlap.
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build()
                )
                setDataSource(url)
                setOnPreparedListener { mp ->
                    if (speed != 1f) {
                        mp.playbackParams = mp.playbackParams.setSpeed(speed)
                    }
                    mp.start()
                }
                setOnCompletionListener { mp ->
                    mp.release()
                    if (mediaPlayer === mp) mediaPlayer = null
                }
                setOnErrorListener { mp, _, _ ->
                    mp.release()
                    if (mediaPlayer === mp) mediaPlayer = null
                    true
                }
                // Network fetch happens off the main thread; playback starts in onPrepared.
                prepareAsync()
            }
        } catch (e: Exception) {
            println("Error playing audio url $url: ${e.message}")
        }
    }

    actual fun release() {
        soundPool.release()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
