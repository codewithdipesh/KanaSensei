package com.codewithdipesh.kanasensei.core.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey
    val userId: String,
    val currentChapterId: String,
    val currentLessonId: String,
    val currentChapterOrder: Int,
    val currentLessonOrder: Int,
    val lastSyncedAt: String,
    val needsSync: Boolean = false
)
