package com.codewithdipesh.sharedfeature.learning.home

import com.codewithdipesh.kanasensei.core.model.progress.ChapterWithProgress
import com.codewithdipesh.kanasensei.core.model.progress.LessonWithProgress

data class LearningUiState(
    val isLoading: Boolean = true,
    val chapters: List<ChapterWithProgress> = emptyList(),
    val selectedLesson: LessonWithProgress? = null,
    val syncStatus: SyncStatus = SyncStatus.Idle,
    val error: String? = null
)

enum class SyncStatus {
    Idle, Syncing, Success, Error
}

sealed class LearningEvent {
    data class LessonCompleted(
        val chapterCompleted: Boolean,
        val advancedToNextChapter: Boolean,
        val newChapterOrder: Int,
        val newLessonOrder: Int
    ) : LearningEvent()

    data class Error(val message: String) : LearningEvent()
    data object SyncCompleted : LearningEvent()
}
