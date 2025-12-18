package com.codewithdipesh.data.textToSpeech

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class JapaneseTtsManager(context: Context) {

    private var tts: TextToSpeech? = null

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.JAPAN
            }
        }
    }

    fun speak(text: String) {
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
    }
}
