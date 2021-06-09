package com.github.code.gambit.helper.auth

sealed class AuthState<out T> {
    data class Success<out T>(val data: T) : AuthState<T>()
    data class Error(val reason: String) : AuthState<Nothing>()
    data class ResendStatus(val success: Boolean) : AuthState<Nothing>()
    object Loading : AuthState<Nothing>()
    object Confirmation : AuthState<Nothing>()
    object CodeMissMatch : AuthState<Nothing>()
}
