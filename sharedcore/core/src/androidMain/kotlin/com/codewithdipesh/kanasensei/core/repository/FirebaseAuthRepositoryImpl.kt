package com.codewithdipesh.kanasensei.core.repository

import com.codewithdipesh.kanasensei.core.local.dao.ProgressDao
import com.codewithdipesh.kanasensei.core.model.auth.AuthResult
import com.codewithdipesh.kanasensei.core.model.user.User
import com.codewithdipesh.kanasensei.core.util.epochMillisToIso
import com.codewithdipesh.kanasensei.core.util.nowIso
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore,
    private val progressDao : ProgressDao
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
                lastLogin = nowIso()
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
                createdAt = nowIso(),
                lastLogin = nowIso()
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
                    lastLogin = nowIso()
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
                    createdAt = nowIso(),
                    lastLogin = nowIso()
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

        return try {
            // Throws FirebaseAuthInvalidUserException if the account was deleted/disabled.
            firebaseUser.reload().await()

            // Read the profile from the SERVER (not the offline cache) so a stale cache
            // can never produce a false "not found" that drives a destructive decision.
            val userDoc = db.collection("users")
                .document(firebaseUser.uid)
                .get(Source.SERVER)
                .await()

            if (!userDoc.exists()) {
                // Profile doc missing but the auth account is valid (e.g. a failed
                // registration write or a race). Sign out, but DO NOT wipe local
                // progress - it may contain unsynced lessons that are only stored locally.
                auth.signOut()
                return null
            }

            val user = userDoc.toObject(User::class.java)
            if (user == null) {
                auth.signOut()
                return null
            }

            user
        } catch (e: FirebaseAuthInvalidUserException) {
            // Auth itself confirms the account is gone (deleted/disabled). This is the
            // only case where wiping local data is correct.
            auth.signOut()
            progressDao.clearAllUserData(firebaseUser.uid)
            null
        } catch (e: Exception) {
            // Network/other error - don't sign out, return basic user from cached FirebaseAuth
            User(
                uid = firebaseUser.uid,
                name = firebaseUser.displayName ?: "",
                motivationSource = "",
                createdAt = firebaseUser.metadata?.creationTimestamp?.epochMillisToIso() ?: "",
                lastLogin = firebaseUser.metadata?.lastSignInTimestamp?.epochMillisToIso() ?: ""
            )
        }
    }
}