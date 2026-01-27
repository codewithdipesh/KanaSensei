package com.codewithdipesh.kanasensei.core.auth

expect class GoogleAuthHelper {
    suspend fun signIn(activityContext: Any): GoogleAuthResult
}
