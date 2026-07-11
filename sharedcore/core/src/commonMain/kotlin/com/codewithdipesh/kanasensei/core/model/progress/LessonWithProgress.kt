package com.codewithdipesh.kanasensei.core.model.progress

import com.codewithdipesh.kanasensei.core.model.content.Lesson

data class LessonWithProgress(
    val lesson: Lesson,
    val isCompleted: Boolean,
    val isCurrent: Boolean,
    val isLocked: Boolean
)
