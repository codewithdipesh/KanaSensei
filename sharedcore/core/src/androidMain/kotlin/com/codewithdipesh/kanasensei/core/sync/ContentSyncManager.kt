package com.codewithdipesh.kanasensei.core.sync

import com.codewithdipesh.kanasensei.core.firestore.FirestorePaths
import com.codewithdipesh.kanasensei.core.local.dao.ProgressDao
import com.codewithdipesh.kanasensei.core.local.entity.CachedChapterEntity
import com.codewithdipesh.kanasensei.core.local.entity.CachedLessonEntity
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import io.github.aakira.napier.Napier
import kotlinx.coroutines.tasks.await

class ContentSyncManagerImpl(
    private val progressDao: ProgressDao,
    private val firestore: FirebaseFirestore
) : ContentSyncManager {
    private val TAG = "ContentSyncManager"

    private fun DocumentSnapshot.getDateAsString(field: String): String =
        getString(field) ?: ""

    override suspend fun syncChaptersAndLessons(): Boolean {
        return try {
            // Fetch chapters
            val chaptersSnapshot = firestore.collection(FirestorePaths.CHAPTERS)
                .orderBy(FirestorePaths.ChapterFields.ORDER_NUMBER)
                .get()
                .await()

            val chapters = chaptersSnapshot.documents.mapNotNull { doc ->
                try {
                    CachedChapterEntity(
                        id = doc.id,
                        name = doc.getString(FirestorePaths.ChapterFields.NAME) ?: "",
                        description = doc.getString(FirestorePaths.ChapterFields.DESCRIPTION) ?: "",
                        orderNumber = doc.getLong(FirestorePaths.ChapterFields.ORDER_NUMBER)?.toInt() ?: 0,
                        scriptType = doc.getString(FirestorePaths.ChapterFields.SCRIPT_TYPE) ?: "",
                        createdAt = doc.getDateAsString(FirestorePaths.ChapterFields.CREATED_AT),
                        lessonCount = 0
                    )
                } catch (e: Exception) {
                    Napier.e("Error parsing chapter ${doc.id}", e, TAG)
                    null
                }
            }

            // Fetch all lessons
            val lessonsSnapshot = firestore.collection(FirestorePaths.LESSONS)
                .orderBy(FirestorePaths.LessonFields.ORDER_NUMBER)
                .get()
                .await()

            val lessons = lessonsSnapshot.documents.mapNotNull { doc ->
                try {
                    CachedLessonEntity(
                        id = doc.id,
                        chapterId = doc.getString(FirestorePaths.LessonFields.CHAPTER_ID) ?: "",
                        title = doc.getString(FirestorePaths.LessonFields.TITLE) ?: "",
                        expandedTitle = doc.getString(FirestorePaths.LessonFields.EXPANDED_TITLE) ?: "",
                        shortDescription = doc.getString(FirestorePaths.LessonFields.SHORT_DESCRIPTION) ?: "",
                        detailedDescription = doc.getString(FirestorePaths.LessonFields.DETAILED_DESCRIPTION) ?: "",
                        orderNumber = doc.getLong(FirestorePaths.LessonFields.ORDER_NUMBER)?.toInt() ?: 0,
                        teaserImage = doc.getString(FirestorePaths.LessonFields.TEASER_IMAGE) ?: "",
                        teaserText = doc.getString(FirestorePaths.LessonFields.TEASER_TEXT) ?: "",
                        createdAt = doc.getDateAsString(FirestorePaths.LessonFields.CREATED_AT),
                        updatedAt = doc.getDateAsString(FirestorePaths.LessonFields.UPDATED_AT)
                    )
                } catch (e: Exception) {
                    Napier.e("Error parsing lesson ${doc.id}", e, TAG)
                    null
                }
            }

            // Calculate lesson counts per chapter
            val lessonCountByChapter = lessons.groupBy { it.chapterId }
                .mapValues { it.value.size }

            val chaptersWithCounts = chapters.map { chapter ->
                chapter.copy(lessonCount = lessonCountByChapter[chapter.id] ?: 0)
            }

            // Save to local database
            progressDao.upsertChapters(chaptersWithCounts)
            progressDao.upsertLessons(lessons)

            Napier.d("Synced ${chapters.size} chapters and ${lessons.size} lessons", tag = TAG)
            true
        } catch (e: Exception) {
            Napier.e("Content sync failed", e, TAG)
            false
        }
    }

    override suspend fun hasLocalContent(): Boolean {
        return progressDao.getMaxChapterOrder() != null
    }
}
