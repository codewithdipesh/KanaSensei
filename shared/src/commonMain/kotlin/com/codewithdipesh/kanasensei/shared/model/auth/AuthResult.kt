package com.codewithdipesh.kanasensei.shared.model.auth

import com.codewithdipesh.kanasensei.shared.model.user.User

sealed class AuthResult {
    data class Success(val user: User) : AuthResult()
    data class Error(val message: String) : AuthResult()
    object NotStarted : AuthResult()
    object Loading : AuthResult()
}
