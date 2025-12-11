package com.codewithdipesh.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithdipesh.auth.model.AuthUI
import com.codewithdipesh.data.connection.translation.TranslateRepository
import com.codewithdipesh.data.model.auth.AuthResult
import com.codewithdipesh.data.model.user.MotivationSource
import com.codewithdipesh.data.remote.base.FirebaseAuthRepository
import com.codewithdipesh.auth.model.OnboardingUI
import com.codewithdipesh.data.model.user.User
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val firebaseAuthRepository: FirebaseAuthRepository,
    private val translateRepository: TranslateRepository
) : ViewModel() {

    var user : User? = null

    private val _onBoardingState = MutableStateFlow(OnboardingUI())
    val onBoardingState = _onBoardingState.asStateFlow()

    private val _authState = MutableStateFlow(AuthUI())
    val authState = _authState.asStateFlow()

    val errorListener = MutableSharedFlow<String?>()


    fun checkAuth(){
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
    fun nextPage() {
        _onBoardingState.update {
            it.copy(selectedPage = it.selectedPage + 1)
        }
    }
    fun prevPage() {
        _onBoardingState.update {
            it.copy(selectedPage = it.selectedPage - 1)
        }
    }
    suspend fun fetchTranslation(){
       viewModelScope.launch {
           val translation = translateRepository.translate(_onBoardingState.value.name)
           if(translation != null ){
               _onBoardingState.update { it.copy(japaneseName = translation) }
           }else{
               errorListener.emit("Translation failed")
           }
       }
    }



    suspend fun login(){
        viewModelScope.launch {
            _authState.update { it.copy(status = AuthResult.Loading)}

            val result = firebaseAuthRepository.login(
                email = _authState.value.email,
                password = _authState.value.password
            )
            _authState.update { it.copy(status = result)}

            if (result is AuthResult.Error) {
                errorListener.emit(result.message)
            }
        }
    }
    suspend fun register(){
        viewModelScope.launch {
            _authState.update { it.copy(status = AuthResult.Loading)}

            val result = firebaseAuthRepository.register(
                email = _authState.value.email,
                password = _authState.value.password,
                name = _onBoardingState.value.name,
                motivationSource = _onBoardingState.value.motivationSource?.displayName() ?: "Unknown"
            )
            _authState.update { it.copy(status = result)}

            if (result is AuthResult.Error) {
                errorListener.emit(result.message)
            }
        }
    }
    suspend fun googleLogin(idToken: String, name: String, motivationSource: String) {
        viewModelScope.launch {
            _authState.update { it.copy(status = AuthResult.Loading)}

            val result = firebaseAuthRepository.googleLogin(idToken, name, motivationSource)

            _authState.update { it.copy(status = result)}

            if (result is AuthResult.Error) {
                errorListener.emit(result.message)
            }
        }
    }


}