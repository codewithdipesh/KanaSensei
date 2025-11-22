package com.codewithdipesh.data.model.auth

import com.codewithdipesh.data.model.user.User

sealed class AuthResult {
    data class Success(val user: User) : AuthResult()
    data class Error(val message: String) : AuthResult()
    object Loading : AuthResult()
}
