package com.codewithdipesh.kanasensei.core.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_lessons")
data class CachedLessonEntity(
    @PrimaryKey
    val id: String,
    val chapterId: String,
    val title: String,
    val expandedTitle: String,
    val shortDescription: String,
    val detailedDescription: String,
    val orderNumber: Int,
    val teaserImage: String,
    val teaserText: String,
    val createdAt: Long,
    val updatedAt: Long
)
