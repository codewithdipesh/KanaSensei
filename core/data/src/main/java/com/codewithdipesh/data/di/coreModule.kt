package com.codewithdipesh.data.di

import com.codewithdipesh.data.remote.base.FirebaseAuthRepository
import com.codewithdipesh.data.remote.base.TranslateRepository
import com.codewithdipesh.data.remote.implementation.FirebaseAuthRepositoryImpl
import com.codewithdipesh.data.textToSpeech.JapaneseTtsManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val coreModule = module {

    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }

            expectSuccess = false
        }
    }

    single<FirebaseAuth> {
        FirebaseAuth.getInstance()
    }
    single<FirebaseFirestore> { FirebaseFirestore.getInstance() }

    single { TranslateRepository(get()) }
    single<FirebaseAuthRepository> { FirebaseAuthRepositoryImpl(get(),get()) }
    single { JapaneseTtsManager(androidContext()) }
}
