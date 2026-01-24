package com.codewithdipesh.kanasensei.core.testToSpeech

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale


actual fun rememberJapaneseTtsManager(): JapaneseTtsManager {
    val context = LocalContext.current
    return JapaneseTtsManager(context)
}

actual class JapaneseTtsManager(
    private val context: Context
) {
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    init {
        initialize()
    }

    private fun initialize() {
        if (tts == null || !isInitialized) {
            tts = TextToSpeech(context) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    tts?.language = Locale.JAPAN
                    isInitialized = true
                }
            }
        }
    }

    actual fun speak(text: String) {
        // Reinitialize if TTS was shut down
        if (tts == null || !isInitialized) {
            initialize()
        }

        tts?.speak(
            text,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "JA_TTS_ID"
        )
    }

    actual fun release() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}
