package com.codewithdipesh.kanasensei

import android.app.Application
import com.codewithdipesh.data.di.coreModule
import com.codewithdipesh.di.authModule
import com.google.firebase.FirebaseApp
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class KanaSenseiApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)

        startKoin {
            androidContext(this@KanaSenseiApplication)
            modules(
                coreModule,
                authModule
            )
        }
    }
}