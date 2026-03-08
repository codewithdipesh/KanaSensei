package com.codewithdipesh.kanasensei

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.codewithdipesh.kanasensei.core.di.sharedCoreModule
import com.codewithdipesh.kanasensei.core.model.content.Lesson
import com.codewithdipesh.kanasensei.core.model.progress.LessonWithProgress
import com.codewithdipesh.kanasensei.di.mobileModule
import com.codewithdipesh.kanasensei.sharedfeature.di.authModule
import com.codewithdipesh.kanasensei.ui.theme.KanaSenseiTheme
import com.codewithdipesh.sharedfeature.learning.di.learningModule
import com.codewithdipesh.sharedfeature.learning.home.components.LessonItem
import com.google.firebase.FirebaseApp
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
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
                authModule,
                learningModule
            )
        }
        Napier.base(DebugAntilog())
    }
}

