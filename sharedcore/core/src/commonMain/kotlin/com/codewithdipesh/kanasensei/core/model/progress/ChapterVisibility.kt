package com.codewithdipesh.kanasensei.core.model.progress

import kotlinx.serialization.Serializable

@Serializable
enum class ChapterVisibility {
    UNLOCKED,      // Completed or current - show full chapter with lessons
    SEMI_VISIBLE,  // Next chapter - show title only, no lessons
    LOCKED         // Future chapters - don't include at all
}
