package com.codewithdipesh.auth.model

import com.codewithdipesh.data.model.auth.AuthResult

data class AuthUI(
    val email : String = "",
    val password : String = "",
    val status : AuthResult = AuthResult.NotStarted
)
