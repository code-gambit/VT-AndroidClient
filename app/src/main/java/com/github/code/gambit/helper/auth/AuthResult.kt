package com.github.code.gambit.helper.auth

sealed class AuthResult<out R> {
    data class Success<out T>(val data: T) : AuthResult<T>()
    data class Error(val exception: Exception) : AuthResult<Nothing>()
}
