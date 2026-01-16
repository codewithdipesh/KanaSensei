package com.codewithdipesh.kanasensei.shared.model.content

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

@Serializable
data class Character(
    val id: String = "",
    val character: String = "",
    val romaji: String = "",
    val kana_type: String = "",
    val svgUrl: String = "",
    val audioUrl: String = "",
    val example_word: String = "",
    val notes: String = "",
    val createdAt: Long? = Clock.System.now().toEpochMilliseconds(),  //todo fix it with db
    val updatedAt: Long? = Clock.System.now().toEpochMilliseconds()
)
