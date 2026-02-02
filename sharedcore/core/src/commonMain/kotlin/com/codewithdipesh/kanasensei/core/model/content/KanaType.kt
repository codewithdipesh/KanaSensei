package com.codewithdipesh.kanasensei.core.model.content

enum class KanaType(val value: String) {
    HIRAGANA("Hiragana"),
    KATAKANA("Katakana");

    companion object {
        fun fromString(value: String): KanaType? {
            return when (value) {
                "Hiragana" -> HIRAGANA
                "Katakana" -> KATAKANA
                else -> null
            }
        }
    }
}