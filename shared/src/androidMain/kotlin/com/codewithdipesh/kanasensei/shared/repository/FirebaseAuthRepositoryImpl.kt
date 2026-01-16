package com.codewithdipesh.kanasensei.shared.repository

import com.codewithdipesh.kanasensei.shared.model.auth.AuthResult
import com.codewithdipesh.kanasensei.shared.model.user.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : FirebaseAuthRepository {

    override suspend fun login(
        email: String,
        password: String
    ): AuthResult {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = auth.currentUser ?: throw Exception("User not found")
            val uid = firebaseUser.uid

            val userDoc = db.collection("users").document(uid).get().await()

            if (!userDoc.exists()) {
                return AuthResult.Error("User profile not found")
            }

            val userProfile = userDoc.toObject(User::class.java)
                ?: return AuthResult.Error("User profile corrupted")

            // Update lastLogin
            val updatedProfile = userProfile.copy(
                lastLogin = System.currentTimeMillis()
            )

            db.collection("users").document(uid).update(
                "lastLogin", updatedProfile.lastLogin
            ).await()

            AuthResult.Success(updatedProfile)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Login failed")
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        name: String,
        motivationSource: String
    ): AuthResult {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = auth.currentUser ?: throw Exception("User not found")

            val user = User(
                uid = firebaseUser.uid,
                name = name,
                motivationSource = motivationSource,
                createdAt = System.currentTimeMillis(),
                lastLogin = System.currentTimeMillis()
            )

            db.collection("users")
                .document(firebaseUser.uid)
                .set(user)
                .await()

            AuthResult.Success(user)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Registration failed")
        }
    }

    override suspend fun googleLogin(
        idToken: String,
        name: String,
        motivationSource: String
    ): AuthResult {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential).await()

            val firebaseUser = auth.currentUser
                ?: return AuthResult.Error("Google login failed")
            val uid = firebaseUser.uid
            val userDoc = db.collection("users").document(uid).get().await()

            // Check if user profile exists, if not create one
            val userProfile = if (userDoc.exists()) {
                // Existing user - update lastLogin
                val existingUser = userDoc.toObject(User::class.java)
                    ?: return AuthResult.Error("User profile corrupted")

                val updatedUser = existingUser.copy(
                    lastLogin = System.currentTimeMillis()
                )

                db.collection("users")
                    .document(uid)
                    .update("lastLogin", updatedUser.lastLogin)
                    .await()

                updatedUser
            } else {
                // New user - create profile
                val newUser = User(
                    uid = uid,
                    name = name,
                    motivationSource = motivationSource,
                    createdAt = System.currentTimeMillis(),
                    lastLogin = System.currentTimeMillis()
                )

                db.collection("users")
                    .document(uid)
                    .set(newUser)
                    .await()

                newUser
            }

            AuthResult.Success(userProfile)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Google login failed")
        }
    }

    override fun logout() = auth.signOut()

    override fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    override suspend fun currentUser(): User? {
        val firebaseUser = auth.currentUser ?: return null

        val userDoc = db.collection("users")
            .document(firebaseUser.uid)
            .get()
            .await()

        if (!userDoc.exists()) return null

        val user = userDoc.toObject(User::class.java)
            ?: return null

        return user
    }
}