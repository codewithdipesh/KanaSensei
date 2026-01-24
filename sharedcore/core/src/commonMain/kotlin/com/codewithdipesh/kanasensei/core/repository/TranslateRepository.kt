package com.codewithdipesh.kanasensei.core.repository

class TranslateRepository {

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

    companion object {
        private val PHONEMES = linkedMapOf(
            // ===== Indian & foreign sound handling =====
            "ksha" to "クシャ",
            "bh" to "ビ",
            "dh" to "ド",
            "sh" to "シュ",
            "th" to "ト",
            "kh" to "カ",
            "gh" to "ガ",
            "ph" to "ファ",

            // ===== Extended katakana =====
            "kya" to "キャ", "kyu" to "キュ", "kyo" to "キョ",
            "gya" to "ギャ", "gyu" to "ギュ", "gyo" to "ギョ",
            "sha" to "シャ", "shu" to "シュ", "sho" to "ショ",
            "cha" to "チャ", "chu" to "チュ", "cho" to "チョ",
            "ja" to "ジャ", "ju" to "ジュ", "jo" to "ジョ",
            "she" to "シェ",
            "je" to "ジェ",
            "ti" to "ティ",
            "di" to "ディ",
            "tu" to "トゥ",
            "du" to "ドゥ",
            "fa" to "ファ", "fi" to "フィ", "fe" to "フェ", "fo" to "フォ",
            "va" to "ヴァ", "vi" to "ヴィ", "ve" to "ヴェ", "vo" to "ヴォ",

            // ===== Standard Katakana =====
            "ka" to "カ", "ki" to "キ", "ku" to "ク", "ke" to "ケ", "ko" to "コ",
            "sa" to "サ", "shi" to "シ", "su" to "ス", "se" to "セ", "so" to "ソ",
            "ta" to "タ", "chi" to "チ", "tsu" to "ツ", "te" to "テ", "to" to "ト",
            "na" to "ナ", "ni" to "ニ", "nu" to "ヌ", "ne" to "ネ", "no" to "ノ",
            "ha" to "ハ", "hi" to "ヒ", "fu" to "フ", "he" to "ヘ", "ho" to "ホ",
            "ma" to "マ", "mi" to "ミ", "mu" to "ム", "me" to "メ", "mo" to "モ",
            "ya" to "ヤ", "yu" to "ユ", "yo" to "ヨ",
            "ra" to "ラ", "ri" to "リ", "ru" to "ル", "re" to "レ", "ro" to "ロ",
            "wa" to "ワ",
            "ga" to "ガ", "gi" to "ギ", "gu" to "グ", "ge" to "ゲ", "go" to "ゴ",
            "za" to "ザ", "ji" to "ジ", "zu" to "ズ", "ze" to "ゼ", "zo" to "ゾ",
            "da" to "ダ", "de" to "デ", "do" to "ド",
            "ba" to "バ", "bi" to "ビ", "bu" to "ブ", "be" to "ベ", "bo" to "ボ",
            "pa" to "パ", "pi" to "ピ", "pu" to "プ", "pe" to "ペ", "po" to "ポ",

            // ===== Vowels =====
            "a" to "ア", "i" to "イ", "u" to "ウ", "e" to "エ", "o" to "オ",

            // ===== Ending nasal =====
            "n" to "ン"
        )
    }
}