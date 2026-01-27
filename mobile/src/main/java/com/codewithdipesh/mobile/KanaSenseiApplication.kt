package com.codewithdipesh.kanasensei

import android.app.Application
import com.codewithdipesh.kanasensei.core.di.sharedCoreModule
import com.codewithdipesh.kanasensei.di.mobileModule
import com.codewithdipesh.kanasensei.sharedfeature.di.authModule
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
                sharedCoreModule,
                mobileModule,
                authModule
            )
        }
    }
}