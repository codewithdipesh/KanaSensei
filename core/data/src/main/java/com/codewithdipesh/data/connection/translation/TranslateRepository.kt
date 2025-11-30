package com.codewithdipesh.data.connection.translation

import com.codewithdipesh.data.connection.NetworkModule
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TranslateRepository {

    private val client = NetworkModule.client

    suspend fun translate(text: String): String? = withContext(Dispatchers.IO) {
       try {
           val requestBody = TranslateRequest(
               q = text
           )
           val response = client.post("https://libretranslate.com/translate") {
               contentType(ContentType.Application.Json)
               setBody(requestBody)
           }.body<TranslateResponse>()

           response.translatedText
       }catch ( e : Exception){
           e.printStackTrace()
           null
       }
    }
}