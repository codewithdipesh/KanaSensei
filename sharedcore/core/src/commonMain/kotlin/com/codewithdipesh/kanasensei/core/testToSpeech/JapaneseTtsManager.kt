package com.codewithdipesh.kanasensei.core.testToSpeech

expect class JapaneseTtsManager {

    fun speak(text: String)
    fun release()
}
