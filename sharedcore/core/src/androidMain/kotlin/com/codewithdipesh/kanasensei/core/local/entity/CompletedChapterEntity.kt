package com.codewithdipesh.kanasensei.core.local.entity

import androidx.room.Entity

@Entity(
    tableName = "completed_chapters",
    primaryKeys = ["userId", "chapterId"]
)
data class CompletedChapterEntity(
    val userId: String,
    val chapterId: String,
    val completedAt: String,
    val needsSync: Boolean = false
)
