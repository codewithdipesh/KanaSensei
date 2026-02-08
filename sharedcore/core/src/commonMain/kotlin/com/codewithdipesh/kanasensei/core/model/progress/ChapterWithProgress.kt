package com.codewithdipesh.kanasensei.core.model.progress

import com.codewithdipesh.kanasensei.core.model.content.Chapter

data class ChapterWithProgress(
    val chapter: Chapter,
    val lessons: List<LessonWithProgress>,
    val visibility: ChapterVisibility,
    val isCompleted: Boolean,
    val isCurrent: Boolean,
    val completedLessonsCount: Int,
    val totalLessonsCount: Int,
    val progressPercentage: Float
)
