package com.codewithdipesh.kanasensei.core.firestore


object FirestorePaths {

    // Content collections (global)
    const val CHAPTERS = "chapters"
    const val LESSONS = "lessons"
    const val CHARACTERS = "characters"

    // Users collection
    const val USERS = "users"

    // User sub-collections
    object UserCollections {
        const val PROGRESS = "progress"
        const val COMPLETED_LESSONS = "completedLessons"
        const val COMPLETED_CHAPTERS = "completedChapters"
        const val LEARNED_KANA = "learnedKana"
    }

    // Document IDs
    object Documents {
        const val CURRENT_PROGRESS = "current"
    }

    // Field names for chapters
    object ChapterFields {
        const val NAME = "name"
        const val DESCRIPTION = "description"
        const val ORDER_NUMBER = "orderNumber"
        const val SCRIPT_TYPE = "scriptType"
        const val CREATED_AT = "createdAt"
    }

    // Field names for lessons
    object LessonFields {
        const val CHAPTER_ID = "chapterId"
        const val TITLE = "title"
        const val EXPANDED_TITLE = "expandedTitle"
        const val SHORT_DESCRIPTION = "shortDescription"
        const val DETAILED_DESCRIPTION = "detailedDescription"
        const val ORDER_NUMBER = "orderNumber"
        const val TEASER_IMAGE = "teaserImage"
        const val TEASER_TEXT = "teaserText"
        const val CREATED_AT = "createdAt"
        const val UPDATED_AT = "updatedAt"
    }

    // Field names for characters
    object CharacterFields {
        const val CHARACTER = "character"
        const val ROMAJI = "romaji"
        const val KANA_TYPE = "kana_type"
        const val SVG_URL = "svgUrl"
        const val AUDIO_URL = "audioUrl"
        const val EXAMPLE_WORD = "example_word"
        const val NOTES = "notes"
        const val CREATED_AT = "createdAt"
        const val UPDATED_AT = "updatedAt"
    }

    // Field names for user progress
    object ProgressFields {
        const val CURRENT_CHAPTER = "currentChapter"
        const val CURRENT_LESSON = "currentLesson"
        const val CURRENT_CHAPTER_ID = "currentChapterId"
        const val CURRENT_LESSON_ID = "currentLessonId"
        const val LAST_UPDATED = "lastUpdated"
    }

    // Field names for completed lessons
    object CompletedLessonFields {
        const val LESSON_ID = "lessonId"
        const val CHAPTER_ID = "chapterId"
        const val COMPLETED_AT = "completedAt"
    }

    // Field names for completed chapters
    object CompletedChapterFields {
        const val CHAPTER_ID = "chapterId"
        const val COMPLETED_AT = "completedAt"
    }

    // Field names for learned kana
    object LearnedKanaFields {
        const val KANA_ID = "kanaId"
        const val LEARNED_AT = "learnedAt"
    }
}
