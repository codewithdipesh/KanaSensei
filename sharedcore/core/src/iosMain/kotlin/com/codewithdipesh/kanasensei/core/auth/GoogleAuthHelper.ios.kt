package com.codewithdipesh.kanasensei.core.auth

actual class GoogleAuthHelper {
    actual suspend fun signIn(activityContext: Any): GoogleAuthResult {
        // TODO: Implement using Google Sign-In SDK for iOS
        return GoogleAuthResult.Error("Google Sign-In not implemented for iOS yet")
    }
}
