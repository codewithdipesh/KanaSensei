package com.codewithdipesh.data.remote.base

import com.codewithdipesh.data.model.auth.AuthResult
import com.codewithdipesh.data.model.user.User

interface FirebaseAuthRepository {

    suspend fun login(email: String, password: String): AuthResult
    suspend fun register(email: String, password: String,name: String, motivationSource: String): AuthResult
    suspend fun googleLogin(idToken : String, name: String, motivationSource: String) : AuthResult
    fun logout()
    suspend fun currentUser() : User?
}