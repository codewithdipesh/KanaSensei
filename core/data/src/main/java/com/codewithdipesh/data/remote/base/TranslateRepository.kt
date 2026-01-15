package com.codewithdipesh.data.remote.base

class TranslateRepository() {

    fun translate(input: String): String {
        val text = input
            .lowercase()
            .replace(Regex("[^a-z]"), "")

        val result = StringBuilder()
        var i = 0

        while (i < text.length) {
            var matched = false

            // Try longest match first (max 4 chars)
            for (len in 4 downTo 1) {
                if (i + len <= text.length) {
                    val chunk = text.substring(i, i + len)
                    val kana = PHONEMES[chunk]
                    if (kana != null) {
                        result.append(kana)
                        i += len
                        matched = true
                        break
                    }
                }
            }

            if (!matched) {
                i++ // skip unknown safely
            }
        }
        return result.toString()
    }

}