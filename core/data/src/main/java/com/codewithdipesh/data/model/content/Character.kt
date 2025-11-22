package com.codewithdipesh.data.model.content

import java.time.LocalDateTime

data class Character(
    val id: String = "",
    val character: String = "",
    val romaji: String = "",
    val kana_type: String = "",
    val svgUrl: String = "",
    val audioUrl: String = "",
    val example_word: String = "",
    val notes: String = "",
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)
