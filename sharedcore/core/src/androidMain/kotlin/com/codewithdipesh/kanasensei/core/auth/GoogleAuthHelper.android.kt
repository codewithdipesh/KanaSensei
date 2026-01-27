package com.codewithdipesh.kanasensei.core.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

actual class GoogleAuthHelper(
    private val webClientId: String
) {
    actual suspend fun signIn(activityContext: Any): GoogleAuthResult {
        val context = activityContext as Context
        val credentialManager = CredentialManager.create(context)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(webClientId)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return try {
            val result = credentialManager.getCredential(
                request = request,
                context = context
            )

            val credential = result.credential

            when {
                credential is GoogleIdTokenCredential -> {
                    GoogleAuthResult.Success(
                        idToken = credential.idToken,
                        displayName = credential.displayName
                    )
                }
                credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL -> {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    GoogleAuthResult.Success(
                        idToken = googleIdTokenCredential.idToken,
                        displayName = googleIdTokenCredential.displayName
                    )
                }
                else -> {
                    GoogleAuthResult.Error("Unsupported credential type: ${credential.type}")
                }
            }
        } catch (e: GetCredentialCancellationException) {
            GoogleAuthResult.Cancelled
        } catch (e: NoCredentialException) {
            GoogleAuthResult.Error("No Google accounts found. Please add a Google account to your device.")
        } catch (e: GetCredentialException) {
            GoogleAuthResult.Error(e.message ?: "Google Sign-In failed")
        } catch (e: Exception) {
            GoogleAuthResult.Error(e.message ?: "An unexpected error occurred")
        }
    }
}
