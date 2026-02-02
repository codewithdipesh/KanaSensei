package com.codewithdipesh.kanasensei.core.model.content

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Lesson(
    val chapterId : String = "",
    val createdAt : Instant = Clock.System.now(),
    val updatedAt : Instant = Clock.System.now(),
    val detailedDescription : String = "",
    val expandedTitle : String = "",
    val id : String = "",
    val orderNumber : Int = 0,
    val shortDescription : String = "",
    val title : String = "",
    val teaserImage : String = "",
    val teaserText : String = ""
)
