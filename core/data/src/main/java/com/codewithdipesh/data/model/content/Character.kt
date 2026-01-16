package com.codewithdipesh.data.model.content

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
    val createdAt: Long? = null,  //fix it with db
    val updatedAt: Long? = null
)
