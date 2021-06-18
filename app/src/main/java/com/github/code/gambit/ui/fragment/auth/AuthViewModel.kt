package com.github.code.gambit.ui.fragment.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amazonaws.services.cognitoidentityprovider.model.CodeMismatchException
import com.github.code.gambit.data.model.User
import com.github.code.gambit.helper.ServiceResult
import com.github.code.gambit.helper.auth.AuthData
import com.github.code.gambit.helper.auth.AuthState
import com.github.code.gambit.repositories.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.lang.Exception
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
                is AuthEvent.ForgotPassword -> {
                    forgotPassword(event.userEmail)
                }
                is AuthEvent.ResetForgotPassword -> {
                    resetForgotPassword(event.userEmail, event.newPassword, event.confirmationCode)
                }
                is AuthEvent.ResendCode -> {
                    when (val it = authRepository.resendConfirmationCode(event.userEmail)) {
                        is ServiceResult.Error -> {
                            postError(it.exception)
                            postValue(AuthState.ResendStatus(false))
                        }
                        is ServiceResult.Success -> postValue(AuthState.ResendStatus(true))
                    }
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
                postValue(AuthState.CodeMissMatch)
            } else {
                postError(res.exception)
            }
        } else {
            postValue(AuthState.Success((res as ServiceResult.Success).data))
        }
    }

    private suspend fun forgotPassword(userEmail: String) {
        _authState.value = AuthState.Loading
        when (val res = authRepository.forgotPassword(userEmail)) {
            is ServiceResult.Error -> postError(res.exception)
            is ServiceResult.Success -> postValue(AuthState.Confirmation)
        }
    }

    private suspend fun resetForgotPassword(userEmail: String, newPassword: String, confirmationCode: String) {
        when (val res = authRepository.resetForgotPassword(AuthData("", userEmail, newPassword, confirmationCode))) {
            is ServiceResult.Error -> {
                if (res.exception.cause is CodeMismatchException) {
                    postValue(AuthState.CodeMissMatch)
                } else {
                    postError(res.exception)
                }
            }
            is ServiceResult.Success -> postValue(AuthState.Success(res.data))
        }
    }

    private fun postError(exception: Exception) {
        _authState.postValue(AuthState.Error(exception.localizedMessage!!))
    }

    private fun postValue(state: AuthState<User>) {
        _authState.postValue(state)
    }
}

sealed class AuthEvent {
    data class LoginEvent(val authData: AuthData) : AuthEvent()
    data class SignUpEvent(val authData: AuthData) : AuthEvent()
    data class ConfirmationEvent(val authData: AuthData) : AuthEvent()
    data class ResendCode(val userEmail: String) : AuthEvent()
    data class ForgotPassword(val userEmail: String) : AuthEvent()
    data class ResetForgotPassword(val userEmail: String, val newPassword: String, val confirmationCode: String) : AuthEvent()
}
