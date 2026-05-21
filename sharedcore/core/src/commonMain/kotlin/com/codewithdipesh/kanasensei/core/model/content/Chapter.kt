package com.codewithdipesh.kanasensei.core.model.content

import kotlinx.serialization.Serializable

@Serializable
data class Chapter(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val orderNumber: Int = 1,
    val scriptType: String = "",
    val createdAt : String = ""
)
