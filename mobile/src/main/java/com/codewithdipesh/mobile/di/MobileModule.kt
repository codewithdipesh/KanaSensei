package com.codewithdipesh.kanasensei.di

import com.codewithdipesh.kanasensei.BuildConfig
import com.codewithdipesh.kanasensei.core.auth.GoogleAuthHelper
import org.koin.dsl.module

val mobileModule = module {
    single { GoogleAuthHelper(BuildConfig.WEB_CLIENT_ID) }
}
