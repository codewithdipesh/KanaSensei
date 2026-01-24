package com.codewithdipesh.kanasensei.core.testToSpeech

expect fun rememberJapaneseTtsManager() : JapaneseTtsManager

expect class JapaneseTtsManager {

    fun speak(text: String)
    fun release()
}
