package com.codewithdipesh.sharedfeature.learning.di

import com.codewithdipesh.sharedfeature.learning.home.LearningViewModel
import com.codewithdipesh.sharedfeature.learning.lesson.LessonViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val learningModule = module {
    viewModel {
        LearningViewModel(
            progressRepository = get(),
            contentSyncManager = get(),
            connectivityObserver = get(),
            firebaseAuthRepository = get()
        )
    }

    viewModel {
        LessonViewModel(
            repo = get()
        )
    }
}
