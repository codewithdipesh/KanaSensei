package com.codewithdipesh.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithdipesh.auth.model.AuthUI
import com.codewithdipesh.data.connectivity.ConnectivityObserver
import com.codewithdipesh.data.remote.base.TranslateRepository
import com.codewithdipesh.data.model.auth.AuthResult
import com.codewithdipesh.data.model.user.MotivationSource
import com.codewithdipesh.data.remote.base.FirebaseAuthRepository
import com.codewithdipesh.auth.model.OnboardingUI
import com.codewithdipesh.data.model.user.User
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val translateRepository: TranslateRepository,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    var user : User? = null

    private val _onBoardingState = MutableStateFlow(OnboardingUI())
    val onBoardingState = _onBoardingState.asStateFlow()

    private val _authState = MutableStateFlow(AuthUI())
    val authState = _authState.asStateFlow()

    val errorListener = MutableSharedFlow<String?>()

    val networkStatus = connectivityObserver.observe()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = ConnectivityObserver.Status.Unavailable
        )


    init {
        viewModelScope.launch {
            val result = firebaseAuthRepository.currentUser()
            user = result
        }
    }

    fun setMotivationSource(option : MotivationSource) {
        _onBoardingState.update {
            it.copy(motivationSource = option)
        }
    }
    fun setUserName(name: String) {
        _onBoardingState.update {
            it.copy(name = name)
        }
    }

    private fun isOnline(): Boolean {
        return networkStatus.value == ConnectivityObserver.Status.Available
    }

    fun fetchTranslation(){
       viewModelScope.launch {
           _onBoardingState.update { it.copy(isTranslating = true) }

           if(_onBoardingState.value.name.isEmpty()){
               errorListener.emit("Name is required")
               _onBoardingState.update { it.copy(isTranslating = false) }
               return@launch
           }

           if(!isOnline()){
               errorListener.emit("You're offline")
               _onBoardingState.update { it.copy(isTranslating = false) }
               return@launch
           }

           val translation = translateRepository.translate(_onBoardingState.value.name)
           Log.d("ViewModel", "fetchTranslation: $translation")
           _onBoardingState.update { it.copy(japaneseName = translation) }
           _onBoardingState.update { it.copy(isTranslating = false) }
       }
    }


    fun setEmail(email: String) {
        _authState.update {
            it.copy(email = email)
        }
    }

    fun setPassword(password: String) {
        _authState.update {
            it.copy(password = password)
        }
    }
    fun login(){
        viewModelScope.launch {
            val email = _authState.value.email.trim()
            val password = _authState.value.password

            if(email.isEmpty() || password.isEmpty()){
                errorListener.emit("Field is required")
                return@launch
            }

            if(!isOnline()){
                errorListener.emit("You're offline")
                return@launch
            }

            _authState.update { it.copy(status = AuthResult.Loading)}

            val result = firebaseAuthRepository.login(
                email = email,
                password = password
            )
            _authState.update { it.copy(status = result)}

            if (result is AuthResult.Error) {
                errorListener.emit(result.message)
            }
        }
    }

    fun register(){
        viewModelScope.launch {
            val email = _authState.value.email.trim()
            val password = _authState.value.password

            if(email.isEmpty() || password.isEmpty()){
                errorListener.emit("Field is required")
                return@launch
            }

            if(!isOnline()){
                errorListener.emit("You're offline")
                return@launch
            }

            _authState.update { it.copy(status = AuthResult.Loading)}

            val result = firebaseAuthRepository.register(
                email = email,
                password = password,
                name = _onBoardingState.value.name,
                motivationSource = _onBoardingState.value.motivationSource?.displayName() ?: "Unknown"
            )
            _authState.update { it.copy(status = result)}

            if (result is AuthResult.Error) {
                errorListener.emit(result.message)
            }
        }
    }

    fun googleLogin(idToken: String, name: String, motivationSource: String) {
        viewModelScope.launch {
            if(!isOnline()){
                errorListener.emit("You're offline")
                return@launch
            }

            _authState.update { it.copy(status = AuthResult.Loading)}

            val result = firebaseAuthRepository.googleLogin(idToken, name, motivationSource)

            _authState.update { it.copy(status = result)}

            if (result is AuthResult.Error) {
                errorListener.emit(result.message)
            }
        }
    }


}