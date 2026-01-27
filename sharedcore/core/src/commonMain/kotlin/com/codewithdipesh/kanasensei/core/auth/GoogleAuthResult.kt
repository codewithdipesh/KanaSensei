package com.codewithdipesh.kanasensei.core.auth

sealed class GoogleAuthResult {
    data class Success(
        val idToken: String,
        val displayName: String?
    ) : GoogleAuthResult()

    data class Error(val message: String) : GoogleAuthResult()

    data object Cancelled : GoogleAuthResult()
}
