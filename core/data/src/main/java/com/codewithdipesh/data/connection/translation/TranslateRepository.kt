package com.codewithdipesh.data.connection.translation

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TranslateRepository(
    private val apiClient : HttpClient
) {

    suspend fun translate(text: String): String? = withContext(Dispatchers.IO) {
       try {
           val response = apiClient.post("https://libretranslate.com/translate") {
               contentType(ContentType.Application.Json)
               setBody(TranslateRequest(q = text))
           }.body<TranslateResponse>()

           response.translatedText
       }catch ( e : Exception){
           e.printStackTrace()
           null
       }
    }
}