package com.codewithdipesh.kanasensei.core.local.entity

import androidx.room.Entity

@Entity(
    tableName = "completed_lessons",
    primaryKeys = ["userId", "lessonId"]
)
data class CompletedLessonEntity(
    val userId: String,
    val lessonId: String,
    val chapterId: String,
    val completedAt: String,
    val needsSync: Boolean = false
)
