package com.codewithdipesh.kanasensei.core.repository

import com.codewithdipesh.kanasensei.core.connectivity.ConnectivityObserver
import com.codewithdipesh.kanasensei.core.local.dao.ProgressDao
import com.codewithdipesh.kanasensei.core.local.entity.CachedChapterEntity
import com.codewithdipesh.kanasensei.core.local.entity.CachedLessonEntity
import com.codewithdipesh.kanasensei.core.local.entity.CompletedChapterEntity
import com.codewithdipesh.kanasensei.core.local.entity.CompletedLessonEntity
import com.codewithdipesh.kanasensei.core.local.entity.LearnedKanaEntity
import com.codewithdipesh.kanasensei.core.local.entity.UserProgressEntity
import com.codewithdipesh.kanasensei.core.model.content.Chapter
import com.codewithdipesh.kanasensei.core.model.content.Lesson
import com.codewithdipesh.kanasensei.core.model.progress.ChapterWithProgress
import com.codewithdipesh.kanasensei.core.model.progress.LessonWithProgress
import com.codewithdipesh.kanasensei.core.model.progress.ProgressUpdateResult
import com.codewithdipesh.kanasensei.core.model.progress.SyncResult
import com.codewithdipesh.kanasensei.core.model.user.UserProgress
import com.google.firebase.firestore.FirebaseFirestore
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class ProgressRepositoryImpl(
    private val progressDao: ProgressDao,
    private val firestore: FirebaseFirestore,
    private val connectivityObserver: ConnectivityObserver
) : ProgressRepository {

    private val TAG = "ProgressRepository"
    private val scope = CoroutineScope(Dispatchers.IO)
    private val networkStatus = MutableStateFlow(ConnectivityObserver.Status.Unavailable)

    init {
        scope.launch {
            connectivityObserver.observe().collect { status ->
                networkStatus.value = status
            }
        }
    }

    override fun observeProgress(userId: String): Flow<UserProgress> {
        return combine(
            progressDao.observeUserProgress(userId),
            progressDao.observeCompletedLessonIds(userId),
            progressDao.observeCompletedChapterIds(userId),
            progressDao.observeLearnedKanaIds(userId)
        ) { progressEntity, completedLessons, completedChapters, learnedKana ->
            UserProgress(
                learnedKanaIds = learnedKana,
                completedLessonIds = completedLessons,
                completedChapters = completedChapters,
                currentChapter = progressEntity?.currentChapterOrder ?: 1,
                currentLesson = progressEntity?.currentLessonOrder ?: 1
            )
        }
    }

    override suspend fun getProgress(userId: String): UserProgress {
        val progressEntity = progressDao.getUserProgress(userId)
        val completedLessons = progressDao.observeCompletedLessonIds(userId).first()
        val completedChapters = progressDao.observeCompletedChapterIds(userId).first()
        val learnedKana = progressDao.observeLearnedKanaIds(userId).first()

        return UserProgress(
            learnedKanaIds = learnedKana,
            completedLessonIds = completedLessons,
            completedChapters = completedChapters,
            currentChapter = progressEntity?.currentChapterOrder ?: 1,
            currentLesson = progressEntity?.currentLessonOrder ?: 1
        )
    }

    override suspend fun completeLesson(
        userId: String,
        lessonId: String,
        chapterId: String
    ): ProgressUpdateResult {
        return try {
            val now = Clock.System.now().toEpochMilliseconds()

            // 1. Mark lesson as completed
            progressDao.insertCompletedLesson(
                CompletedLessonEntity(
                    userId = userId,
                    lessonId = lessonId,
                    chapterId = chapterId,
                    completedAt = now,
                    needsSync = true
                )
            )

            // 2. Get current progress and chapter info
            val currentProgress = progressDao.getUserProgress(userId)
            val currentChapter = progressDao.getChapterById(chapterId)
            val currentLesson = progressDao.getLessonById(lessonId)

            if (currentChapter == null || currentLesson == null) {
                return ProgressUpdateResult.Error("Chapter or lesson not found in cache")
            }

            // 3. Check if this completes the chapter
            val completedCount = progressDao.getCompletedLessonCountForChapter(userId, chapterId)
            val totalLessons = progressDao.getLessonCountForChapter(chapterId)
            val chapterNowComplete = completedCount >= totalLessons

            var advancedToNextLesson = false
            var advancedToNextChapter = false
            var newLessonOrder = currentProgress?.currentLessonOrder ?: 1
            var newChapterOrder = currentProgress?.currentChapterOrder ?: 1
            var newChapterId = chapterId
            var newLessonId = lessonId

            if (chapterNowComplete) {
                // 4a. Mark chapter as completed
                progressDao.insertCompletedChapter(
                    CompletedChapterEntity(
                        userId = userId,
                        chapterId = chapterId,
                        completedAt = now,
                        needsSync = true
                    )
                )

                // 4b. Try to advance to next chapter
                val nextChapter = progressDao.getChapterByOrder(currentChapter.orderNumber + 1)
                if (nextChapter != null) {
                    newChapterOrder = nextChapter.orderNumber
                    newChapterId = nextChapter.id
                    newLessonOrder = 1
                    advancedToNextChapter = true
                    advancedToNextLesson = true

                    // Get the first lesson of the new chapter
                    val firstLesson = progressDao.getLessonByOrder(nextChapter.id, 1)
                    newLessonId = firstLesson?.id ?: ""
                }
            } else {
                // 4c. Advance to next lesson in same chapter
                val nextLesson = progressDao.getLessonByOrder(chapterId, currentLesson.orderNumber + 1)
                if (nextLesson != null) {
                    newLessonOrder = nextLesson.orderNumber
                    newLessonId = nextLesson.id
                    advancedToNextLesson = true
                }
            }

            // 5. Update user progress
            val newProgressEntity = UserProgressEntity(
                userId = userId,
                currentChapterId = newChapterId,
                currentLessonId = newLessonId,
                currentChapterOrder = newChapterOrder,
                currentLessonOrder = newLessonOrder,
                lastSyncedAt = currentProgress?.lastSyncedAt ?: 0,
                needsSync = true
            )
            progressDao.upsertUserProgress(newProgressEntity)

            // 6. Trigger background sync if online
            triggerBackgroundSync(userId)

            ProgressUpdateResult.Success(
                lessonCompleted = true,
                chapterCompleted = chapterNowComplete,
                advancedToNextLesson = advancedToNextLesson,
                advancedToNextChapter = advancedToNextChapter,
                newCurrentLesson = newLessonOrder,
                newCurrentChapter = newChapterOrder
            )
        } catch (e: Exception) {
            Napier.e("Error completing lesson", e, TAG)
            ProgressUpdateResult.Error(e.message ?: "Unknown error")
        }
    }

    override suspend fun markKanaLearned(userId: String, kanaId: String) {
        progressDao.insertLearnedKana(
            LearnedKanaEntity(
                userId = userId,
                kanaId = kanaId,
                learnedAt = Clock.System.now().toEpochMilliseconds(),
                needsSync = true
            )
        )
        triggerBackgroundSync(userId)
    }

    override fun observeLearnedKanaIds(userId: String): Flow<List<String>> {
        return progressDao.observeLearnedKanaIds(userId)
    }

    override fun observeChaptersWithProgress(userId: String): Flow<List<ChapterWithProgress>> {
        return combine(
            progressDao.observeAllChapters(),
            progressDao.observeCompletedChapterIds(userId),
            progressDao.observeCompletedLessonIds(userId),
            progressDao.observeUserProgress(userId)
        ) { chapters, completedChapterIds, completedLessonIds, progressEntity ->
            val currentChapterOrder = progressEntity?.currentChapterOrder ?: 1

            chapters.mapIndexed { index, chapterEntity ->
                val isCompleted = completedChapterIds.contains(chapterEntity.id)
                val isCurrent = chapterEntity.orderNumber == currentChapterOrder

                val isLocked = if (index == 0) false else {
                    val previousChapter = chapters.getOrNull(index - 1)
                    previousChapter != null && !completedChapterIds.contains(previousChapter.id)
                }

                ChapterWithProgress(
                    chapter = chapterEntity.toChapter(),
                    isCompleted = isCompleted,
                    isCurrent = isCurrent,
                    isLocked = isLocked,
                    completedLessonsCount = 0, // Will be calculated separately if needed
                    totalLessonsCount = chapterEntity.lessonCount,
                    progressPercentage = if (isCompleted) 1f else 0f
                )
            }
        }
    }

    override fun observeLessonsWithProgress(
        userId: String,
        chapterId: String
    ): Flow<List<LessonWithProgress>> {
        return combine(
            progressDao.observeLessonsForChapter(chapterId),
            progressDao.observeCompletedLessonIds(userId),
            progressDao.observeUserProgress(userId)
        ) { lessons, completedLessonIds, progressEntity ->
            val currentLessonOrder = progressEntity?.currentLessonOrder ?: 1
            val currentChapterId = progressEntity?.currentChapterId

            lessons.mapIndexed { index, lessonEntity ->
                val isCompleted = completedLessonIds.contains(lessonEntity.id)
                val isCurrent = currentChapterId == chapterId &&
                        lessonEntity.orderNumber == currentLessonOrder

                val isLocked = if (index == 0) false else {
                    val previousLesson = lessons.getOrNull(index - 1)
                    previousLesson != null && !completedLessonIds.contains(previousLesson.id)
                }

                LessonWithProgress(
                    lesson = lessonEntity.toLesson(),
                    isCompleted = isCompleted,
                    isCurrent = isCurrent,
                    isLocked = isLocked
                )
            }
        }
    }

    override suspend fun syncToCloud(userId: String): SyncResult {
        if (!isOnline()) return SyncResult.NoConnection

        val errors = mutableListOf<String>()

        try {
            // Sync user progress
            val unsyncedProgress = progressDao.getUnsyncedProgress()
            unsyncedProgress.forEach { progress ->
                try {
                    val progressMap = mapOf(
                        "currentChapter" to progress.currentChapterOrder,
                        "currentLesson" to progress.currentLessonOrder,
                        "currentChapterId" to progress.currentChapterId,
                        "currentLessonId" to progress.currentLessonId,
                        "lastUpdated" to Clock.System.now().toEpochMilliseconds()
                    )
                    firestore.collection("users")
                        .document(userId)
                        .collection("progress")
                        .document("current")
                        .set(progressMap)
                        .await()
                    progressDao.markProgressSynced(userId, Clock.System.now().toEpochMilliseconds())
                } catch (e: Exception) {
                    errors.add("Progress sync failed: ${e.message}")
                }
            }

            // Sync completed lessons
            val unsyncedLessons = progressDao.getUnsyncedCompletedLessons(userId)
            unsyncedLessons.forEach { lesson ->
                try {
                    firestore.collection("users")
                        .document(userId)
                        .collection("completedLessons")
                        .document(lesson.lessonId)
                        .set(
                            mapOf(
                                "lessonId" to lesson.lessonId,
                                "chapterId" to lesson.chapterId,
                                "completedAt" to lesson.completedAt
                            )
                        )
                        .await()
                    progressDao.markLessonSynced(userId, lesson.lessonId)
                } catch (e: Exception) {
                    errors.add("Lesson ${lesson.lessonId} sync failed")
                }
            }

            // Sync completed chapters
            val unsyncedChapters = progressDao.getUnsyncedCompletedChapters(userId)
            unsyncedChapters.forEach { chapter ->
                try {
                    firestore.collection("users")
                        .document(userId)
                        .collection("completedChapters")
                        .document(chapter.chapterId)
                        .set(
                            mapOf(
                                "chapterId" to chapter.chapterId,
                                "completedAt" to chapter.completedAt
                            )
                        )
                        .await()
                    progressDao.markChapterSynced(userId, chapter.chapterId)
                } catch (e: Exception) {
                    errors.add("Chapter ${chapter.chapterId} sync failed")
                }
            }

            // Sync learned kana
            val unsyncedKana = progressDao.getUnsyncedLearnedKana(userId)
            unsyncedKana.forEach { kana ->
                try {
                    firestore.collection("users")
                        .document(userId)
                        .collection("learnedKana")
                        .document(kana.kanaId)
                        .set(
                            mapOf(
                                "kanaId" to kana.kanaId,
                                "learnedAt" to kana.learnedAt
                            )
                        )
                        .await()
                    progressDao.markKanaSynced(userId, kana.kanaId)
                } catch (e: Exception) {
                    errors.add("Kana ${kana.kanaId} sync failed")
                }
            }

            return if (errors.isEmpty()) SyncResult.Success
            else SyncResult.PartialSuccess(errors)

        } catch (e: Exception) {
            Napier.e("Sync to cloud failed", e, TAG)
            return SyncResult.Error(e.message ?: "Sync failed")
        }
    }

    override suspend fun syncFromCloud(userId: String): SyncResult {
        if (!isOnline()) return SyncResult.NoConnection

        try {
            // Pull progress
            val progressDoc = firestore.collection("users")
                .document(userId)
                .collection("progress")
                .document("current")
                .get()
                .await()

            if (progressDoc.exists()) {
                val cloudChapter = progressDoc.getLong("currentChapter")?.toInt() ?: 1
                val cloudLesson = progressDoc.getLong("currentLesson")?.toInt() ?: 1
                val cloudChapterId = progressDoc.getString("currentChapterId") ?: ""
                val cloudLessonId = progressDoc.getString("currentLessonId") ?: ""

                val localProgress = progressDao.getUserProgress(userId)
                val shouldUseCloud = localProgress == null ||
                        cloudChapter > localProgress.currentChapterOrder ||
                        (cloudChapter == localProgress.currentChapterOrder &&
                                cloudLesson > localProgress.currentLessonOrder)

                if (shouldUseCloud) {
                    progressDao.upsertUserProgress(
                        UserProgressEntity(
                            userId = userId,
                            currentChapterId = cloudChapterId,
                            currentLessonId = cloudLessonId,
                            currentChapterOrder = cloudChapter,
                            currentLessonOrder = cloudLesson,
                            lastSyncedAt = Clock.System.now().toEpochMilliseconds(),
                            needsSync = false
                        )
                    )
                }
            }

            // Pull completed lessons
            val completedLessonsSnapshot = firestore.collection("users")
                .document(userId)
                .collection("completedLessons")
                .get()
                .await()

            completedLessonsSnapshot.documents.forEach { doc ->
                val lessonId = doc.getString("lessonId") ?: return@forEach
                val chapterId = doc.getString("chapterId") ?: return@forEach
                val completedAt = doc.getLong("completedAt") ?: Clock.System.now().toEpochMilliseconds()

                progressDao.insertCompletedLesson(
                    CompletedLessonEntity(
                        userId = userId,
                        lessonId = lessonId,
                        chapterId = chapterId,
                        completedAt = completedAt,
                        needsSync = false
                    )
                )
            }

            // Pull completed chapters
            val completedChaptersSnapshot = firestore.collection("users")
                .document(userId)
                .collection("completedChapters")
                .get()
                .await()

            completedChaptersSnapshot.documents.forEach { doc ->
                val chapterId = doc.getString("chapterId") ?: return@forEach
                val completedAt = doc.getLong("completedAt") ?: Clock.System.now().toEpochMilliseconds()

                progressDao.insertCompletedChapter(
                    CompletedChapterEntity(
                        userId = userId,
                        chapterId = chapterId,
                        completedAt = completedAt,
                        needsSync = false
                    )
                )
            }

            // Pull learned kana
            val learnedKanaSnapshot = firestore.collection("users")
                .document(userId)
                .collection("learnedKana")
                .get()
                .await()

            learnedKanaSnapshot.documents.forEach { doc ->
                val kanaId = doc.getString("kanaId") ?: return@forEach
                val learnedAt = doc.getLong("learnedAt") ?: Clock.System.now().toEpochMilliseconds()

                progressDao.insertLearnedKana(
                    LearnedKanaEntity(
                        userId = userId,
                        kanaId = kanaId,
                        learnedAt = learnedAt,
                        needsSync = false
                    )
                )
            }

            return SyncResult.Success
        } catch (e: Exception) {
            Napier.e("Sync from cloud failed", e, TAG)
            return SyncResult.Error(e.message ?: "Sync failed")
        }
    }

    override suspend fun initializeProgress(userId: String) {
        val existingProgress = progressDao.getUserProgress(userId)
        if (existingProgress == null) {
            val firstChapter = progressDao.getChapterByOrder(1)
            val firstLesson = firstChapter?.let {
                progressDao.getLessonByOrder(it.id, 1)
            }

            progressDao.upsertUserProgress(
                UserProgressEntity(
                    userId = userId,
                    currentChapterId = firstChapter?.id ?: "",
                    currentLessonId = firstLesson?.id ?: "",
                    currentChapterOrder = 1,
                    currentLessonOrder = 1,
                    lastSyncedAt = 0,
                    needsSync = true
                )
            )
        }
    }

    override suspend fun clearLocalData(userId: String) {
        progressDao.clearAllUserData(userId)
    }

    private fun isOnline(): Boolean {
        return networkStatus.value == ConnectivityObserver.Status.Available
    }

    private fun triggerBackgroundSync(userId: String) {
        if (isOnline()) {
            scope.launch {
                syncToCloud(userId)
            }
        }
    }

    // Extension functions for entity conversion
    private fun CachedChapterEntity.toChapter() = Chapter(
        id = id,
        name = name,
        description = description,
        orderNumber = orderNumber,
        scriptType = scriptType,
        createdAt = Instant.fromEpochMilliseconds(createdAt)
    )

    private fun CachedLessonEntity.toLesson() = Lesson(
        id = id,
        chapterId = chapterId,
        title = title,
        expandedTitle = expandedTitle,
        shortDescription = shortDescription,
        detailedDescription = detailedDescription,
        orderNumber = orderNumber,
        createdAt = Instant.fromEpochMilliseconds(createdAt),
        updatedAt = Instant.fromEpochMilliseconds(updatedAt)
    )
}
