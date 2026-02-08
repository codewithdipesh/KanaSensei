package com.codewithdipesh.kanasensei.core.repository

import com.codewithdipesh.kanasensei.core.model.progress.ChapterWithProgress
import com.codewithdipesh.kanasensei.core.model.progress.ProgressUpdateResult
import com.codewithdipesh.kanasensei.core.model.progress.SyncResult
import com.codewithdipesh.kanasensei.core.model.user.UserProgress
import kotlinx.coroutines.flow.Flow

interface ProgressRepository {

    //Progress Observation

    fun observeProgress(userId: String): Flow<UserProgress>

    suspend fun getProgress(userId: String): UserProgress

    //Lesson Completion

    suspend fun completeLesson(
        userId: String,
        lessonId: String,
        chapterId: String
    ): ProgressUpdateResult

    //Kana Learning

    suspend fun markKanaLearned(userId: String, kanaId: String)

    fun observeLearnedKanaIds(userId: String): Flow<List<String>>

    //Content with Progress

    fun observeChaptersWithProgress(userId: String): Flow<List<ChapterWithProgress>>

    //Sync Operations
    suspend fun syncToCloud(userId: String): SyncResult

    suspend fun syncFromCloud(userId: String): SyncResult

    suspend fun initializeProgress(userId: String)

    suspend fun clearLocalData(userId: String)
}
