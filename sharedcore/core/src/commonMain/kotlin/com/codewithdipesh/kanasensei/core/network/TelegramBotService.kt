package com.codewithdipesh.kanasensei.core.network

import com.codewithdipesh.kanasensei.core.AppConfig
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.github.aakira.napier.Napier

class TelegramBotService(
    private val httpClient: HttpClient
) {

    private val botToken = AppConfig.telegramBotToken
    private val chatId = AppConfig.chatId
    private val baseUrl = "https://api.telegram.org/bot$botToken"

    suspend fun sendMessage(text: String): Boolean {
        return try {
            val response: HttpResponse = httpClient.post("$baseUrl/sendMessage") {
                contentType(ContentType.Application.Json)
                setBody(mapOf(
                    "chat_id" to chatId,
                    "text" to text,
                    "parse_mode" to "Markdown"
                ))
            }
            if (response.status.isSuccess()) {
                true
            } else {
                Napier.e("Failed to send message: ${response.bodyAsText()}", tag = "TelegramBotService")
                false
            }
        } catch (e: Exception) {
            Napier.e("Error sending message", e, tag = "TelegramBotService")
            false
        }
    }

    suspend fun sendPhoto(photoBytes: ByteArray, caption: String? = null): Boolean {
        return try {
            val response: HttpResponse = httpClient.submitFormWithBinaryData(
                url = "$baseUrl/sendPhoto",
                formData = formData {
                    append("chat_id", chatId)
                    if (caption != null) {
                        append("caption", caption)
                    }
                    append("photo", photoBytes, Headers.build {
                        append(HttpHeaders.ContentType, "image/jpeg")
                        append(HttpHeaders.ContentDisposition, "filename=\"grievance_image.jpg\"")
                    })
                }
            )
            if (response.status.isSuccess()) {
                true
            } else {
                Napier.e("Failed to send photo: ${response.bodyAsText()}", tag = "TelegramBotService")
                false
            }
        } catch (e: Exception) {
            Napier.e("Error sending photo", e, tag = "TelegramBotService")
            false
        }
    }
}
