package com.codewithdipesh.kanasensei.core.model.content

import kotlinx.serialization.Serializable

@Serializable
data class Lesson(
    val chapterId : String = "",
    val createdAt : String = "",
    val updatedAt : String = "",
    val detailedDescription : String = "",
    val expandedTitle : String = "",
    val id : String = "",
    val orderNumber : Int = 0,
    val shortDescription : String = "",
    val title : String = "",
    val teaserImage : String = "",
    val teaserText : String = ""
)
