package com.codewithdipesh.kanasensei.core.model.content

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class LessonPage(
    val autoPlay: Boolean = false,
    val badge: String = "",
    val content: String = "",
    val correctOption: Int = 0,
    val createdAt: String = "",
    val hintText: String = "",
    val id: String = "",
    val kanaId: String = "",
    val options: List<String> = emptyList(),
    val order: Int = 0,
    val question: String = "",
    val showGuide: Boolean = false,
    val title: String = "",
    val type: String = "",
    val updatedAt: String = ""
)
