package com.codewithdipesh.kanasensei.core.repository

import com.codewithdipesh.kanasensei.core.AppConfig
import com.codewithdipesh.kanasensei.core.connectivity.ConnectivityObserver
import com.codewithdipesh.kanasensei.core.firestore.FirestorePaths
import com.codewithdipesh.kanasensei.core.local.dao.ProgressDao
import com.codewithdipesh.kanasensei.core.local.entity.CachedChapterEntity
import com.codewithdipesh.kanasensei.core.local.entity.CachedLessonEntity
import com.codewithdipesh.kanasensei.core.local.entity.CompletedChapterEntity
import com.codewithdipesh.kanasensei.core.local.entity.CompletedLessonEntity
import com.codewithdipesh.kanasensei.core.local.entity.LearnedKanaEntity
import com.codewithdipesh.kanasensei.core.local.entity.UserProgressEntity
import com.codewithdipesh.kanasensei.core.model.content.Chapter
import com.codewithdipesh.kanasensei.core.model.content.Lesson
import com.codewithdipesh.kanasensei.core.model.progress.ChapterVisibility
import com.codewithdipesh.kanasensei.core.model.progress.ChapterWithProgress
import com.codewithdipesh.kanasensei.core.model.progress.LessonWithProgress
import com.codewithdipesh.kanasensei.core.model.progress.ProgressUpdateResult
import com.codewithdipesh.kanasensei.core.model.progress.SyncResult
import com.codewithdipesh.kanasensei.core.model.user.UserProgress
import com.codewithdipesh.kanasensei.core.network.TelegramBotService
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
import com.codewithdipesh.kanasensei.core.util.nowIso

class ProgressRepositoryImpl(
    private val progressDao: ProgressDao,
    private val firestore: FirebaseFirestore,
    private val connectivityObserver: ConnectivityObserver,
    private val telegramBotService: TelegramBotService
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

    override suspend fun isCompleted(
        userId: String,
        lessonId: String
    ): Boolean {
        return progressDao.getCompletionStatus(userId, lessonId)
    }

    override suspend fun completeLesson(
        userId: String,
        lessonId: String,
        chapterId: String
    ): ProgressUpdateResult {
        return try {
            val now = nowIso()

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
                val nextChapter = progressDao.getNextChapter(currentChapter.orderNumber)
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
                val nextLesson = progressDao.getNextLesson(chapterId, currentLesson.orderNumber)
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
                lastSyncedAt = currentProgress?.lastSyncedAt ?: "",
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
                learnedAt = nowIso(),
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
            progressDao.observeAllLessons(),
            progressDao.observeCompletedChapterIds(userId),
            progressDao.observeCompletedLessonIds(userId),
            progressDao.observeUserProgress(userId)
        ) { chapters, allLessons, completedChapterIds, completedLessonIds, progressEntity ->
            Napier.d("Chapters: ${chapters.size}, Progress: $progressEntity", tag = "ProgressRepo")
            val currentChapterOrder = progressEntity?.currentChapterOrder ?: 1
            val currentLessonId = progressEntity?.currentLessonId
            val currentChapterId = progressEntity?.currentChapterId

            // Group lessons by chapter
            val lessonsByChapter = allLessons
                .sortedBy { it.orderNumber }
                .groupBy { it.chapterId }

            chapters.mapNotNull { chapterEntity ->
                val isCompleted = completedChapterIds.contains(chapterEntity.id)
                val isCurrent = chapterEntity.orderNumber == currentChapterOrder

                // Determine visibility
                val visibility = when {
                    isCompleted || isCurrent -> ChapterVisibility.UNLOCKED
                    chapterEntity.orderNumber == currentChapterOrder + 1 -> ChapterVisibility.SEMI_VISIBLE
                    else -> ChapterVisibility.LOCKED
                }

                // Skip locked chapters
                if (visibility == ChapterVisibility.LOCKED) {
                    return@mapNotNull null
                }

                // Build lessons list for unlocked chapters only
                val chapterLessons = lessonsByChapter[chapterEntity.id] ?: emptyList()
                val lessonsWithProgress = if (visibility == ChapterVisibility.UNLOCKED) {
                    chapterLessons.mapIndexed { index, lessonEntity ->
                        val lessonCompleted = completedLessonIds.contains(lessonEntity.id)
                        val lessonIsCurrent = currentChapterId == chapterEntity.id &&
                                lessonEntity.id == currentLessonId

                        Napier.d("chapterId: ${chapterEntity.id}, lessonId: ${lessonEntity.id}", tag = "ProgressRepo")
                        Napier.d("currentChapterId: $currentChapterId, currentlessonId: $currentLessonId", tag = "ProgressRepo")

                        val lessonIsLocked = if (index == 0) false else {
                            val previousLesson = chapterLessons.getOrNull(index - 3)
                            previousLesson != null && !completedLessonIds.contains(previousLesson.id)
                          }

                        LessonWithProgress(
                            lesson = lessonEntity.toLesson(),
                            isCompleted = lessonCompleted,
                            isCurrent = lessonIsCurrent,
                            isLocked = lessonIsLocked
                        )
                    }
                } else {
                    emptyList() // Semi-visible chapters have no lessons
                }

                // Calculate completed lessons count
                val completedLessonsCount = chapterLessons.count { completedLessonIds.contains(it.id) }
                val progressPercentage = if (chapterLessons.isNotEmpty()) {
                    completedLessonsCount.toFloat() / chapterLessons.size
                } else 0f

                ChapterWithProgress(
                    chapter = chapterEntity.toChapter(),
                    lessons = lessonsWithProgress,
                    visibility = visibility,
                    isCompleted = isCompleted,
                    isCurrent = isCurrent,
                    completedLessonsCount = completedLessonsCount,
                    totalLessonsCount = chapterEntity.lessonCount,
                    progressPercentage = progressPercentage
                )
            }
        }
    }

    override suspend fun syncToCloud(userId: String): SyncResult {
        Napier.d("syncToCloud called for user: $userId, isOnline: ${isOnline()}", tag = TAG)
        if (!isOnline()) {
            Napier.d("syncToCloud skipped - no connection", tag = TAG)
            return SyncResult.NoConnection
        }

        val errors = mutableListOf<String>()

        try {
            // Sync user progress
            val unsyncedProgress = progressDao.getUnsyncedProgress()
            Napier.d("Unsynced progress count: ${unsyncedProgress.size}", tag = TAG)
            unsyncedProgress.forEach { progress ->
                try {
                    val progressMap = mapOf(
                        FirestorePaths.ProgressFields.CURRENT_CHAPTER to progress.currentChapterOrder,
                        FirestorePaths.ProgressFields.CURRENT_LESSON to progress.currentLessonOrder,
                        FirestorePaths.ProgressFields.CURRENT_CHAPTER_ID to progress.currentChapterId,
                        FirestorePaths.ProgressFields.CURRENT_LESSON_ID to progress.currentLessonId,
                        FirestorePaths.ProgressFields.LAST_UPDATED to nowIso()
                    )
                    Napier.d("Syncing progress to Firestore: $progressMap", tag = TAG)
                    firestore.collection(FirestorePaths.USERS)
                        .document(userId)
                        .collection(FirestorePaths.UserCollections.PROGRESS)
                        .document(FirestorePaths.Documents.CURRENT_PROGRESS)
                        .set(progressMap)
                        .await()
                    progressDao.markProgressSynced(userId, nowIso())
                    Napier.d("Progress synced successfully", tag = TAG)
                } catch (e: Exception) {
                    Napier.e("Progress sync failed", e, TAG)
                    errors.add("Progress sync failed: ${e.message}")
                }
            }

            // Sync completed lessons
            val unsyncedLessons = progressDao.getUnsyncedCompletedLessons(userId)
            Napier.d("Unsynced lessons count: ${unsyncedLessons.size}", tag = TAG)
            unsyncedLessons.forEach { lesson ->
                try {
                    firestore.collection(FirestorePaths.USERS)
                        .document(userId)
                        .collection(FirestorePaths.UserCollections.COMPLETED_LESSONS)
                        .document(lesson.lessonId)
                        .set(
                            mapOf(
                                FirestorePaths.CompletedLessonFields.LESSON_ID to lesson.lessonId,
                                FirestorePaths.CompletedLessonFields.CHAPTER_ID to lesson.chapterId,
                                FirestorePaths.CompletedLessonFields.COMPLETED_AT to lesson.completedAt
                            )
                        )
                        .await()
                    progressDao.markLessonSynced(userId, lesson.lessonId)
                    Napier.d("Lesson ${lesson.lessonId} synced", tag = TAG)
                } catch (e: Exception) {
                    Napier.e("Lesson ${lesson.lessonId} sync failed", e, TAG)
                    errors.add("Lesson ${lesson.lessonId} sync failed: ${e.message}")
                }
            }

            // Sync completed chapters
            val unsyncedChapters = progressDao.getUnsyncedCompletedChapters(userId)
            Napier.d("Unsynced chapters count: ${unsyncedChapters.size}", tag = TAG)
            unsyncedChapters.forEach { chapter ->
                try {
                    firestore.collection(FirestorePaths.USERS)
                        .document(userId)
                        .collection(FirestorePaths.UserCollections.COMPLETED_CHAPTERS)
                        .document(chapter.chapterId)
                        .set(
                            mapOf(
                                FirestorePaths.CompletedChapterFields.CHAPTER_ID to chapter.chapterId,
                                FirestorePaths.CompletedChapterFields.COMPLETED_AT to chapter.completedAt
                            )
                        )
                        .await()
                    progressDao.markChapterSynced(userId, chapter.chapterId)
                    Napier.d("Chapter ${chapter.chapterId} synced", tag = TAG)
                } catch (e: Exception) {
                    Napier.e("Chapter ${chapter.chapterId} sync failed", e, TAG)
                    errors.add("Chapter ${chapter.chapterId} sync failed: ${e.message}")
                }
            }

            // Sync learned kana
            val unsyncedKana = progressDao.getUnsyncedLearnedKana(userId)
            Napier.d("Unsynced kana count: ${unsyncedKana.size}", tag = TAG)
            unsyncedKana.forEach { kana ->
                try {
                    firestore.collection(FirestorePaths.USERS)
                        .document(userId)
                        .collection(FirestorePaths.UserCollections.LEARNED_KANA)
                        .document(kana.kanaId)
                        .set(
                            mapOf(
                                FirestorePaths.LearnedKanaFields.KANA_ID to kana.kanaId,
                                FirestorePaths.LearnedKanaFields.LEARNED_AT to kana.learnedAt
                            )
                        )
                        .await()
                    progressDao.markKanaSynced(userId, kana.kanaId)
                    Napier.d("Kana ${kana.kanaId} synced", tag = TAG)
                } catch (e: Exception) {
                    Napier.e("Kana ${kana.kanaId} sync failed", e, TAG)
                    errors.add("Kana ${kana.kanaId} sync failed: ${e.message}")
                }
            }

            val result = if (errors.isEmpty()) SyncResult.Success
            else SyncResult.PartialSuccess(errors)
            Napier.d("syncToCloud result: $result", tag = TAG)
            return result

        } catch (e: Exception) {
            Napier.e("Sync to cloud failed", e, TAG)
            return SyncResult.Error(e.message ?: "Sync failed")
        }
    }

    override suspend fun syncFromCloud(userId: String): SyncResult {
        if (!isOnline()) return SyncResult.NoConnection

        try {
            // Pull progress
            val progressDoc = firestore.collection(FirestorePaths.USERS)
                .document(userId)
                .collection(FirestorePaths.UserCollections.PROGRESS)
                .document(FirestorePaths.Documents.CURRENT_PROGRESS)
                .get()
                .await()


            if (progressDoc.exists()) {
                val cloudChapter = progressDoc.getLong(FirestorePaths.ProgressFields.CURRENT_CHAPTER)?.toInt() ?: 1
                val cloudLesson = progressDoc.getLong(FirestorePaths.ProgressFields.CURRENT_LESSON)?.toInt() ?: 1
                val cloudChapterId = progressDoc.getString(FirestorePaths.ProgressFields.CURRENT_CHAPTER_ID) ?: ""
                val cloudLessonId = progressDoc.getString(FirestorePaths.ProgressFields.CURRENT_LESSON_ID) ?: ""

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
                            lastSyncedAt = nowIso(),
                            needsSync = false
                        )
                    )
                }
            }

            // Pull completed lessons
            val completedLessonsSnapshot = firestore.collection(FirestorePaths.USERS)
                .document(userId)
                .collection(FirestorePaths.UserCollections.COMPLETED_LESSONS)
                .get()
                .await()

            completedLessonsSnapshot.documents.forEach { doc ->
                val lessonId = doc.getString(FirestorePaths.CompletedLessonFields.LESSON_ID) ?: return@forEach
                val chapterId = doc.getString(FirestorePaths.CompletedLessonFields.CHAPTER_ID) ?: return@forEach
                val completedAt = doc.getString(FirestorePaths.CompletedLessonFields.COMPLETED_AT) ?: nowIso()

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
            val completedChaptersSnapshot = firestore.collection(FirestorePaths.USERS)
                .document(userId)
                .collection(FirestorePaths.UserCollections.COMPLETED_CHAPTERS)
                .get()
                .await()

            completedChaptersSnapshot.documents.forEach { doc ->
                val chapterId = doc.getString(FirestorePaths.CompletedChapterFields.CHAPTER_ID) ?: return@forEach
                val completedAt = doc.getString(FirestorePaths.CompletedChapterFields.COMPLETED_AT) ?: nowIso()

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
            val learnedKanaSnapshot = firestore.collection(FirestorePaths.USERS)
                .document(userId)
                .collection(FirestorePaths.UserCollections.LEARNED_KANA)
                .get()
                .await()

            learnedKanaSnapshot.documents.forEach { doc ->
                val kanaId = doc.getString(FirestorePaths.LearnedKanaFields.KANA_ID) ?: return@forEach
                val learnedAt = doc.getString(FirestorePaths.LearnedKanaFields.LEARNED_AT) ?: nowIso()

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
                    lastSyncedAt = "",
                    needsSync = true
                )
            )
        }
    }

    override suspend fun clearLocalData(userId: String) {
        progressDao.clearAllUserData(userId)
    }

    override suspend fun postGrievience(
        title: String,
        description: String,
        name: String,
        attachedMedia: List<ByteArray>
    ) {
        val reportMessage = """
            *New Grievance Report*
            
            *Name:* $name
            *Title:* $title
            *Description:* $description
            
            *Device Details:* 
            Name : ${AppConfig.devicename}
            Version : ${AppConfig.version}
            Timestamp: ${AppConfig.timestamp}
        """.trimIndent()

        val success = telegramBotService.sendMessage(reportMessage)
        if (success) {
            attachedMedia.forEachIndexed { index, bytes ->
                telegramBotService.sendPhoto(
                    photoBytes = bytes,
                    caption = if (index == 0) "Attached Media for: $title by $name" else null
                )
            }
        }
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
        createdAt = createdAt
    )

    private fun CachedLessonEntity.toLesson() = Lesson(
        id = id,
        chapterId = chapterId,
        title = title,
        expandedTitle = expandedTitle,
        shortDescription = shortDescription,
        detailedDescription = detailedDescription,
        orderNumber = orderNumber,
        teaserImage = teaserImage,
        teaserText = teaserText,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
