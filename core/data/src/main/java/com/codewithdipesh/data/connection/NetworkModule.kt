package com.codewithdipesh.data.connection

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object NetworkModule {
    val client = HttpClient(OkHttp) {
         install(ContentNegotiation) {
             json(Json {
                 prettyPrint = true
                 ignoreUnknownKeys = true
             })
         }
    }
}