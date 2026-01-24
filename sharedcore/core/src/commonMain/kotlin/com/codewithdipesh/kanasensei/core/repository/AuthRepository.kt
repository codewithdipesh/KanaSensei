package com.codewithdipesh.kanasensei.core.repository

import com.codewithdipesh.kanasensei.core.model.auth.AuthResult
import com.codewithdipesh.kanasensei.core.model.user.User


interface FirebaseAuthRepository {

    suspend fun login(email: String, password: String): AuthResult
    suspend fun register(email: String, password: String,name: String, motivationSource: String): AuthResult
    suspend fun googleLogin(idToken : String, name: String, motivationSource: String) : AuthResult
    fun logout()
    fun isUserLoggedIn(): Boolean
    suspend fun currentUser() : User?
}