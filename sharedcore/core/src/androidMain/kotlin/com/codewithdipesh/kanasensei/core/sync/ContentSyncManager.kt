package com.codewithdipesh.kanasensei.core.sync

import com.codewithdipesh.kanasensei.core.local.dao.ProgressDao
import com.codewithdipesh.kanasensei.core.local.entity.CachedChapterEntity
import com.codewithdipesh.kanasensei.core.local.entity.CachedLessonEntity
import com.google.firebase.firestore.FirebaseFirestore
import io.github.aakira.napier.Napier
import kotlinx.coroutines.tasks.await

class ContentSyncManagerImpl(
    private val progressDao: ProgressDao,
    private val firestore: FirebaseFirestore
) : ContentSyncManager {
    private val TAG = "ContentSyncManager"

    override suspend fun syncChaptersAndLessons(): Boolean {
        return try {
            // Fetch chapters
            val chaptersSnapshot = firestore.collection("chapters")
                .orderBy("orderNumber")
                .get()
                .await()

            val chapters = chaptersSnapshot.documents.mapNotNull { doc ->
                try {
                    CachedChapterEntity(
                        id = doc.id,
                        name = doc.getString("name") ?: "",
                        description = doc.getString("description") ?: "",
                        orderNumber = doc.getLong("orderNumber")?.toInt() ?: 0,
                        scriptType = doc.getString("scriptType") ?: "",
                        createdAt = doc.getLong("createdAt") ?: 0,
                        lessonCount = 0
                    )
                } catch (e: Exception) {
                    Napier.e("Error parsing chapter ${doc.id}", e, TAG)
                    null
                }
            }

            // Fetch all lessons
            val lessonsSnapshot = firestore.collection("lessons")
                .orderBy("orderNumber")
                .get()
                .await()

            val lessons = lessonsSnapshot.documents.mapNotNull { doc ->
                try {
                    CachedLessonEntity(
                        id = doc.id,
                        chapterId = doc.getString("chapterId") ?: "",
                        title = doc.getString("title") ?: "",
                        expandedTitle = doc.getString("expandedTitle") ?: "",
                        shortDescription = doc.getString("shortDescription") ?: "",
                        detailedDescription = doc.getString("detailedDescription") ?: "",
                        orderNumber = doc.getLong("orderNumber")?.toInt() ?: 0,
                        createdAt = doc.getLong("createdAt") ?: 0,
                        updatedAt = doc.getLong("updatedAt") ?: 0
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
