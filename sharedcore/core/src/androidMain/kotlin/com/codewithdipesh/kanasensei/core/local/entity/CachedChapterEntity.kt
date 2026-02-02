package com.codewithdipesh.kanasensei.core.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_chapters")
data class CachedChapterEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val orderNumber: Int,
    val scriptType: String,
    val createdAt: Long,
    val lessonCount: Int
)
