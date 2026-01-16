package com.codewithdipesh.kanasensei.shared.textToSpeech

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class JapaneseTtsManager(private val context: Context) {

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

    fun speak(text: String) {
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

    fun release() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}
