package com.codewithdipesh.kanasensei.core.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.codewithdipesh.kanasensei.core.local.entity.CachedChapterEntity
import com.codewithdipesh.kanasensei.core.local.entity.CachedLessonEntity
import com.codewithdipesh.kanasensei.core.local.entity.CompletedChapterEntity
import com.codewithdipesh.kanasensei.core.local.entity.CompletedLessonEntity
import com.codewithdipesh.kanasensei.core.local.entity.LearnedKanaEntity
import com.codewithdipesh.kanasensei.core.local.entity.UserProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressDao {

    //user progress
    @Query("SELECT * FROM user_progress WHERE userId = :userId")
    fun observeUserProgress(userId: String): Flow<UserProgressEntity?>

    @Query("SELECT * FROM user_progress WHERE userId = :userId")
    suspend fun getUserProgress(userId: String): UserProgressEntity?

    @Upsert
    suspend fun upsertUserProgress(progress: UserProgressEntity)

    @Query("UPDATE user_progress SET needsSync = 1 WHERE userId = :userId")
    suspend fun markProgressDirty(userId: String)

    @Query("SELECT * FROM user_progress WHERE needsSync = 1")
    suspend fun getUnsyncedProgress(): List<UserProgressEntity>

    @Query("UPDATE user_progress SET needsSync = 0, lastSyncedAt = :syncTime WHERE userId = :userId")
    suspend fun markProgressSynced(userId: String, syncTime: Long)

    //completed lessons
    @Query("SELECT lessonId FROM completed_lessons WHERE userId = :userId")
    fun observeCompletedLessonIds(userId: String): Flow<List<String>>

    @Query("SELECT * FROM completed_lessons WHERE userId = :userId AND chapterId = :chapterId")
    suspend fun getCompletedLessonsForChapter(userId: String, chapterId: String): List<CompletedLessonEntity>

    @Query("SELECT COUNT(*) FROM completed_lessons WHERE userId = :userId AND chapterId = :chapterId")
    suspend fun getCompletedLessonCountForChapter(userId: String, chapterId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletedLesson(lesson: CompletedLessonEntity)

    @Query("SELECT * FROM completed_lessons WHERE needsSync = 1 AND userId = :userId")
    suspend fun getUnsyncedCompletedLessons(userId: String): List<CompletedLessonEntity>

    @Query("UPDATE completed_lessons SET needsSync = 0 WHERE userId = :userId AND lessonId = :lessonId")
    suspend fun markLessonSynced(userId: String, lessonId: String)

    //chapter
    @Query("SELECT chapterId FROM completed_chapters WHERE userId = :userId")
    fun observeCompletedChapterIds(userId: String): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletedChapter(chapter: CompletedChapterEntity)

    @Query("SELECT * FROM completed_chapters WHERE needsSync = 1 AND userId = :userId")
    suspend fun getUnsyncedCompletedChapters(userId: String): List<CompletedChapterEntity>

    @Query("UPDATE completed_chapters SET needsSync = 0 WHERE userId = :userId AND chapterId = :chapterId")
    suspend fun markChapterSynced(userId: String, chapterId: String)

    //learned kana
    @Query("SELECT kanaId FROM learned_kana WHERE userId = :userId")
    fun observeLearnedKanaIds(userId: String): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLearnedKana(kana: LearnedKanaEntity)

    @Query("SELECT * FROM learned_kana WHERE needsSync = 1 AND userId = :userId")
    suspend fun getUnsyncedLearnedKana(userId: String): List<LearnedKanaEntity>

    @Query("UPDATE learned_kana SET needsSync = 0 WHERE userId = :userId AND kanaId = :kanaId")
    suspend fun markKanaSynced(userId: String, kanaId: String)

    //cached content
    @Query("SELECT * FROM cached_chapters ORDER BY orderNumber ASC")
    fun observeAllChapters(): Flow<List<CachedChapterEntity>>

    @Query("SELECT * FROM cached_chapters WHERE id = :chapterId")
    suspend fun getChapterById(chapterId: String): CachedChapterEntity?

    @Query("SELECT * FROM cached_chapters WHERE orderNumber = :order")
    suspend fun getChapterByOrder(order: Int): CachedChapterEntity?

    @Query("SELECT * FROM cached_lessons WHERE chapterId = :chapterId ORDER BY orderNumber ASC")
    fun observeLessonsForChapter(chapterId: String): Flow<List<CachedLessonEntity>>

    @Query("SELECT * FROM cached_lessons ORDER BY chapterId ASC, orderNumber ASC")
    fun observeAllLessons(): Flow<List<CachedLessonEntity>>

    @Query("SELECT * FROM cached_lessons WHERE id = :lessonId")
    suspend fun getLessonById(lessonId: String): CachedLessonEntity?

    @Query("SELECT * FROM cached_lessons WHERE chapterId = :chapterId AND orderNumber = :order")
    suspend fun getLessonByOrder(chapterId: String, order: Int): CachedLessonEntity?

    @Query("SELECT COUNT(*) FROM cached_lessons WHERE chapterId = :chapterId")
    suspend fun getLessonCountForChapter(chapterId: String): Int

    @Query("SELECT MAX(orderNumber) FROM cached_chapters")
    suspend fun getMaxChapterOrder(): Int?

    @Upsert
    suspend fun upsertChapters(chapters: List<CachedChapterEntity>)

    @Upsert
    suspend fun upsertLessons(lessons: List<CachedLessonEntity>)

    //clear data
    @Query("DELETE FROM user_progress WHERE userId = :userId")
    suspend fun clearUserProgress(userId: String)

    @Query("DELETE FROM completed_lessons WHERE userId = :userId")
    suspend fun clearCompletedLessons(userId: String)

    @Query("DELETE FROM completed_chapters WHERE userId = :userId")
    suspend fun clearCompletedChapters(userId: String)

    @Query("DELETE FROM learned_kana WHERE userId = :userId")
    suspend fun clearLearnedKana(userId: String)

    @Transaction
    suspend fun clearAllUserData(userId: String) {
        clearUserProgress(userId)
        clearCompletedLessons(userId)
        clearCompletedChapters(userId)
        clearLearnedKana(userId)
    }
}
