package com.codewithdipesh.kanasensei.core.model.progress

sealed class ProgressUpdateResult {
    data class Success(
        val lessonCompleted: Boolean = false,
        val chapterCompleted: Boolean = false,
        val advancedToNextLesson: Boolean = false,
        val advancedToNextChapter: Boolean = false,
        val newCurrentLesson: Int = 0,
        val newCurrentChapter: Int = 0
    ) : ProgressUpdateResult()

    data class Error(val message: String) : ProgressUpdateResult()
}

sealed class SyncResult {
    data object Success : SyncResult()
    data class PartialSuccess(val errors: List<String>) : SyncResult()
    data class Error(val message: String) : SyncResult()
    data object NoConnection : SyncResult()
}
