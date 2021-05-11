package com.github.code.gambit.ui.fragment.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amazonaws.services.cognitoidentityprovider.model.CodeMismatchException
import com.github.code.gambit.data.model.User
import com.github.code.gambit.helper.auth.AuthData
import com.github.code.gambit.helper.auth.AuthState
import com.github.code.gambit.helper.auth.ServiceResult
import com.github.code.gambit.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel
@Inject
constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _authState: MutableLiveData<AuthState<User>> = MutableLiveData()

    val authState: LiveData<AuthState<User>>
        get() = _authState

    fun setEvent(event: AuthEvent) {
        viewModelScope.launch {
            when (event) {
                is AuthEvent.LoginEvent -> {
                    logIn(event.authData)
                }
                is AuthEvent.ConfirmationEvent -> {
                    confirmation(event.authData)
                }
                is AuthEvent.SignUpEvent -> {
                    signUp(event.authData)
                }
            }
        }
    }

    private suspend fun logIn(authData: AuthData) {
        _authState.value = AuthState.Loading
        val res = authRepository.login(authData)
        if (res is ServiceResult.Error) {
            _authState.postValue(AuthState.Error(res.exception.message!!))
        } else {
            _authState.postValue(AuthState.Success((res as ServiceResult.Success).data))
        }
    }

    private suspend fun signUp(authData: AuthData) {
        _authState.value = AuthState.Loading
        val res = authRepository.signUp(authData)
        if (res is ServiceResult.Error) {
            _authState.postValue(AuthState.Error(res.exception.message!!))
        } else {
            _authState.postValue(AuthState.Confirmation)
        }
    }

    private suspend fun confirmation(authData: AuthData) {
        _authState.value = AuthState.Loading
        val res = authRepository.signUpConfirmation(authData)
        if (res is ServiceResult.Error) {
            if (res.exception.cause is CodeMismatchException) {
                _authState.postValue(AuthState.CodeMissMatch)
            } else {
                _authState.postValue(AuthState.Error(res.exception.message!!))
            }
        } else {
            _authState.postValue(AuthState.Success((res as ServiceResult.Success).data))
        }
    }
}

sealed class AuthEvent {
    data class LoginEvent(val authData: AuthData) : AuthEvent()
    data class SignUpEvent(val authData: AuthData) : AuthEvent()
    data class ConfirmationEvent(val authData: AuthData) : AuthEvent()
}
