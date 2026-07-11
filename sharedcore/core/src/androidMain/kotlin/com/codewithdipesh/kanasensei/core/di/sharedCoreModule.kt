package com.codewithdipesh.kanasensei.core.di

import androidx.room.Room
import com.codewithdipesh.kanasensei.core.connectivity.ConnectivityObserver
import com.codewithdipesh.kanasensei.core.connectivity.NetworkConnectivityObserver
import com.codewithdipesh.kanasensei.core.local.KanaSenseiDatabase
import com.codewithdipesh.kanasensei.core.repository.FirebaseAuthRepository
import com.codewithdipesh.kanasensei.core.repository.FirebaseAuthRepositoryImpl
import com.codewithdipesh.kanasensei.core.repository.LearningRepository
import com.codewithdipesh.kanasensei.core.repository.LearningRepositoryImpl
import com.codewithdipesh.kanasensei.core.repository.ProgressRepository
import com.codewithdipesh.kanasensei.core.repository.ProgressRepositoryImpl
import com.codewithdipesh.kanasensei.core.repository.TranslateRepository
import com.codewithdipesh.kanasensei.core.sync.ContentSyncManager
import com.codewithdipesh.kanasensei.core.sync.ContentSyncManagerImpl
import com.codewithdipesh.kanasensei.core.testToSpeech.JapaneseTtsManager
import com.codewithdipesh.kanasensei.core.network.TelegramBotService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val sharedCoreModule = module {

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

    single { TelegramBotService(get()) }

    single { TranslateRepository() }

    single<FirebaseAuthRepository> {
        FirebaseAuthRepositoryImpl(get(), get(),get())
    }

    single<LearningRepository> {
        LearningRepositoryImpl(get(), get())
    }

    single { JapaneseTtsManager(androidContext()) }

    single<ConnectivityObserver> {
        NetworkConnectivityObserver(androidContext())
    }

    // Room Database
    single<KanaSenseiDatabase> {
        Room.databaseBuilder(
            androidContext(),
            KanaSenseiDatabase::class.java,
            "kanasensei.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    single { get<KanaSenseiDatabase>().progressDao() }

    // Progress Repository
    single<ProgressRepository> {
        ProgressRepositoryImpl(
            progressDao = get(),
            firestore = get(),
            connectivityObserver = get(),
            telegramBotService = get()
        )
    }

    // Content Sync Manager
    single<ContentSyncManager> {
        ContentSyncManagerImpl(
            progressDao = get(),
            firestore = get()
        )
    }
}