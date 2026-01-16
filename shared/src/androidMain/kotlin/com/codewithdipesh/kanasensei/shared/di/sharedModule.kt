package com.codewithdipesh.kanasensei.shared.di

import com.codewithdipesh.kanasensei.shared.connectivity.ConnectivityObserver
import com.codewithdipesh.kanasensei.shared.connectivity.NetworkConnectivityObserver
import com.codewithdipesh.kanasensei.shared.repository.FirebaseAuthRepository
import com.codewithdipesh.kanasensei.shared.repository.TranslateRepository
import com.codewithdipesh.kanasensei.shared.repository.FirebaseAuthRepositoryImpl
import com.codewithdipesh.kanasensei.shared.textToSpeech.JapaneseTtsManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val sharedModule = module {

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

    single<FirebaseFirestore> {
        FirebaseFirestore.getInstance()
    }

    single { TranslateRepository() }

    single<FirebaseAuthRepository> {
        FirebaseAuthRepositoryImpl(get(), get())
    }

    single { JapaneseTtsManager(androidContext()) }

    single<ConnectivityObserver> {
        NetworkConnectivityObserver(androidContext())
    }
}