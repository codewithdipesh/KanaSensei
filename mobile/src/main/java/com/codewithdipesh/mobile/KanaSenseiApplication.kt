package com.codewithdipesh.kanasensei

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
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
    }
}


@Preview(showBackground = true)
@Composable
fun LessonPreview(){
    KanaSenseiTheme(true){
        Box(modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background)) {
            LessonItem(
                lessonWithProgress = LessonWithProgress(
                    lesson = Lesson(
                        id = "1",
                        title = "Lesson 1",
                        shortDescription = "Learn the basics",
                        expandedTitle = "Lesson 1: Learn the basics",
                        detailedDescription = "This is a detailed description of lesson 1. It will help you understand the importance of this lesson.",
                        teaserText = "か"
                    ),
                    isCompleted = false,
                    isLocked = false,
                    isCurrent = true
                ),
                isCurrent = true
            )
        }
    }
}