package com.codewithdipesh.kanasensei.sharedfeature.di

import com.codewithdipesh.kanasensei.sharedfeature.auth.AuthViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val authModule = module {
    viewModel<AuthViewModel> {
        AuthViewModel(
            firebaseAuthRepository = get(),
            translateRepository = get(),
            connectivityObserver = get(),
            ttsManager = get(),
            googleAuthHelper = get()
        )
    }
}