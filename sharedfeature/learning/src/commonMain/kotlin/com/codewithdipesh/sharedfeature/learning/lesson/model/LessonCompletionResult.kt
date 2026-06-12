package com.codewithdipesh.sharedfeature.learning.lesson.model

import kotlinx.serialization.Serializable

@Serializable
data class LessonCompletionResult(
    val lessonId: String,
    val shortDescription: String,
    val chapterCompleted: Boolean,
    val advancedToNextChapter: Boolean,
    val newChapterOrder: Int,
    val newLessonOrder: Int
)