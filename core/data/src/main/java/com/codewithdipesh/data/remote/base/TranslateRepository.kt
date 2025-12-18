package com.codewithdipesh.data.remote.base

import android.util.Log
import com.codewithdipesh.data.model.user.TranslateRequest
import com.codewithdipesh.data.model.user.TranslateResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TranslateRepository(
    private val apiClient : HttpClient
) {

    suspend fun translate(text: String): String? = withContext(Dispatchers.IO) {
        try {
            Log.d("TranslateRepository", "Translating text: $text")

            val httpResponse = apiClient.post("https://libretranslate.com/translate") {
                contentType(ContentType.Application.Json)
                setBody(TranslateRequest(q = text))
            }

            Log.d("TranslateRepository", "Response status: ${httpResponse.status}")
            val responseBody = httpResponse.bodyAsText()
            Log.d("TranslateRepository", "Response body: $responseBody")

            val response = httpResponse.body<TranslateResponse>()
            Log.d("TranslateRepository", "Parsed response: $response")

            response.translatedText.ifBlank { null }
        } catch (e: Exception) {
            Log.e("TranslateRepository", "Translation error: ${e.message}", e)
            e.printStackTrace()
            null
        }
    }
}