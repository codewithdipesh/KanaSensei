package com.codewithdipesh.auth.onboarding

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithdipesh.auth.model.AuthUI
import com.codewithdipesh.data.connection.translation.TranslateRepository
import com.codewithdipesh.data.model.auth.AuthResult
import com.codewithdipesh.data.model.user.MotivationSource
import com.codewithdipesh.data.remote.base.FirebaseAuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val translateRepository: TranslateRepository
) : ViewModel() {

    private val _Uistate = MutableStateFlow(AuthUI())
    val state = _Uistate.asStateFlow()

    private val _authState = MutableStateFlow<AuthResult>(AuthResult.NotStarted)
    val authState = _authState.asStateFlow()

    val errorListener = MutableSharedFlow<String?>()

    private val _googleSignInLauncher = MutableStateFlow<ActivityResultLauncher<Intent>?>(null)

    fun setMotivationSource(option : MotivationSource) {
        _Uistate.update {
            it.copy(motivationSource = option)
        }
    }

    fun setUserName(name: String) {
        _Uistate.update {
            it.copy(name = name)
        }
    }

    fun nextPage() {
        _Uistate.update {
            it.copy(selectedPage = it.selectedPage + 1)
        }
    }

    fun prevPage() {
        _Uistate.update {
            it.copy(selectedPage = it.selectedPage - 1)
        }
    }

    suspend fun fetchTranslation(){
       viewModelScope.launch {
           val translation = translateRepository.translate(_Uistate.value.name)
           if(translation != null ){
               _Uistate.update { it.copy(japaneseName = translation) }
           }else{
               errorListener.emit("Translation failed")
           }
       }
    }

    suspend fun login(){
        viewModelScope.launch {
            _authState.update { AuthResult.Loading }

            val result = firebaseAuthRepository.login(
                email = _Uistate.value.email,
                password = _Uistate.value.password
            )
            _authState.update { result }

            if (result is AuthResult.Error) {
                errorListener.emit(result.message)
            }
        }
    }

    suspend fun register(){
        viewModelScope.launch {
            _authState.update { AuthResult.Loading }

            val result = firebaseAuthRepository.register(
                email = _Uistate.value.email,
                password = _Uistate.value.password,
                name = _Uistate.value.name,
                motivationSource = _Uistate.value.motivationSource?.displayName() ?: "Unknown"
            )
            _authState.update { result }

            if (result is AuthResult.Error) {
                errorListener.emit(result.message)
            }
        }
    }

    suspend fun googleLogin(idToken: String, name: String, motivationSource: String) {
        viewModelScope.launch {
            _authState.update { AuthResult.Loading }

            val result = firebaseAuthRepository.googleLogin(idToken, name, motivationSource)

            _authState.update { result }

            if (result is AuthResult.Error) {
                errorListener.emit(result.message)
            }
        }
    }


}