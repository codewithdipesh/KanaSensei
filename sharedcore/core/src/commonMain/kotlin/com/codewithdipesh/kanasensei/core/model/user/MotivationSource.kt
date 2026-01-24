package com.codewithdipesh.kanasensei.core.model.user

enum class MotivationSource{
    ANIME_MANGA,
    WORK_STUDY,
    CULTURE,
    OTHERS;

    fun displayName() : String {
        return when(this){
            ANIME_MANGA -> "Anime / Manga"
            WORK_STUDY -> "Work / Study"
            CULTURE -> "Japanese Culture"
            OTHERS -> "Others"
        }
    }
    companion object {
        fun getAll() : List<MotivationSource>{
            return listOf(ANIME_MANGA, WORK_STUDY, CULTURE, OTHERS)
        }
    }
}
