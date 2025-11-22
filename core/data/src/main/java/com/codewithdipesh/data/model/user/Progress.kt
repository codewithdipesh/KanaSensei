package com.codewithdipesh.data.model.user

data class KanaProgress(
    val learnedKanaIds: List<String> = emptyList()
)

data class LessonProgress(
    val completedLessonIds: List<String> = emptyList()
)

data class ChapterProgress(
    val completedChapters: List<String> = emptyList()
)

