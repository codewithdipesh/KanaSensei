package com.codewithdipesh.di

import com.codewithdipesh.auth.AuthViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val authModule  = module {
    viewModel { AuthViewModel(get(), get()) }

}