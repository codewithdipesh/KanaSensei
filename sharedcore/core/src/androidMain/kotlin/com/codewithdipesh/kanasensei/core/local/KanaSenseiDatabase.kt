package com.codewithdipesh.kanasensei.core.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.codewithdipesh.kanasensei.core.local.dao.ProgressDao
import com.codewithdipesh.kanasensei.core.local.entity.CachedChapterEntity
import com.codewithdipesh.kanasensei.core.local.entity.CachedLessonEntity
import com.codewithdipesh.kanasensei.core.local.entity.CompletedChapterEntity
import com.codewithdipesh.kanasensei.core.local.entity.CompletedLessonEntity
import com.codewithdipesh.kanasensei.core.local.entity.LearnedKanaEntity
import com.codewithdipesh.kanasensei.core.local.entity.UserProgressEntity

@Database(
    entities = [
        UserProgressEntity::class,
        CompletedLessonEntity::class,
        CompletedChapterEntity::class,
        LearnedKanaEntity::class,
        CachedChapterEntity::class,
        CachedLessonEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class KanaSenseiDatabase : RoomDatabase() {
    abstract fun progressDao(): ProgressDao
}
