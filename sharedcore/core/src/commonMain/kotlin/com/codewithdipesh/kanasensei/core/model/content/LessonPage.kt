package com.codewithdipesh.kanasensei.core.model.content

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
    val type: LessonPageType = LessonPageType.LISTEN, //firebase auto convert to enum if the name is in bold letter
    val updatedAt: String = ""
)


enum class LessonPageType(val value: String) {
    LISTEN("LISTEN"),
    STROKE("STROKE"),
    WRITE("WRITE"),
    QUIZ("QUIZ")
}
