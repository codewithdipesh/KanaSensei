package com.codewithdipesh.data.model.user

import kotlinx.serialization.Serializable

@Serializable
data class TranslateRequest(
    val q: String,
    val source: String = "en",
    val target: String = "ja",
    val format: String = "text",
    val alternatives: Int = 3,
    val api_key: String = ""
)

@Serializable
data class TranslateResponse(
    val translatedText: String,
    val alternatives: List<String>
)