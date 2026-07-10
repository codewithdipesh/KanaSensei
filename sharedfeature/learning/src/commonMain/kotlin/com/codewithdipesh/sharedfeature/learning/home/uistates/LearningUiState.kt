package com.codewithdipesh.sharedfeature.learning.home.uistates

import com.codewithdipesh.kanasensei.core.model.progress.ChapterWithProgress
import com.codewithdipesh.kanasensei.core.model.progress.LessonWithProgress
import com.codewithdipesh.sharedfeature.learning.home.uistates.GrievienceState

data class LearningUiState(
    val isLoading: Boolean = true,
    val chapters: List<ChapterWithProgress> = emptyList(),
    val selectedLesson: LessonWithProgress? = null,
    val syncStatus: SyncStatus = SyncStatus.Idle,
    val error: String? = null,
    val showGrievienceForm: Boolean = false,
    val grievienceState: GrievienceState = GrievienceState()
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
    data class Message(val message: String) : LearningEvent()
    data object SyncCompleted : LearningEvent()
}
