package com.codewithdipesh.sharedfeature.learning.di

import com.codewithdipesh.sharedfeature.learning.home.LearningViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val learningModule = module {
    viewModel { (userId: String) ->
        LearningViewModel(
            progressRepository = get(),
            contentSyncManager = get(),
            connectivityObserver = get(),
            userId = userId
        )
    }
}
