package com.codewithdipesh.sharedfeature.learning.lesson.model

import kotlinx.serialization.Serializable

@Serializable
data class LessonCompletionResult(
    val lessonId: String,
    val shortDescription : String = "",
    val chapterCompleted: Boolean = false,
    val advancedToNextChapter: Boolean = false,
    val newChapterOrder: Int = 0,
    val newLessonOrder: Int = 0
)