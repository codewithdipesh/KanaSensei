package com.codewithdipesh.kanasensei.core.model.user

import kotlinx.serialization.Serializable

@Serializable
data class UserProgress(
    val learnedKanaIds: List<String> = emptyList(),
    val completedLessonIds: List<String> = emptyList(),
    val completedChapters: List<String> = emptyList(),
    val currentChapter: Int = 0,
    val currentLesson: Int = 0 //saving the number from the chapter
)
