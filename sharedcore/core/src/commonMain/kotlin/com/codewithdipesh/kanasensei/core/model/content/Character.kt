package com.codewithdipesh.kanasensei.core.model.content

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
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
    val createdAt : Instant = Clock.System.now(),
    val updatedAt : Instant = Clock.System.now()
)
