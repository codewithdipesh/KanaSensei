package com.codewithdipesh.kanasensei.core.local.entity

import androidx.room.Entity

@Entity(
    tableName = "learned_kana",
    primaryKeys = ["userId", "kanaId"]
)
data class LearnedKanaEntity(
    val userId: String,
    val kanaId: String,
    val learnedAt: Long,
    val needsSync: Boolean = false
)
