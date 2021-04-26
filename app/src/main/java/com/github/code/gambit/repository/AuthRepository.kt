package com.github.code.gambit.repository

import com.github.code.gambit.PreferenceManager
import com.github.code.gambit.data.mapper.aws.UserAttributeMapper
import com.github.code.gambit.data.model.User
import com.github.code.gambit.helper.auth.AuthData
import com.github.code.gambit.helper.auth.AuthResult
import com.github.code.gambit.network.auth.AuthService

class AuthRepository
constructor(
    private val authService: AuthService,
    private val preferenceManager: PreferenceManager,
    private val userAttributeMapper: UserAttributeMapper
) {

    val isUserLoggedIn get() = preferenceManager.isAuthenticated()
    val getToken get() = preferenceManager.getIdToken()
    val revokeAuth = { preferenceManager.revokeAuthentication() }

    suspend fun login(authData: AuthData): AuthResult<User> {
        val res = authService.login(authData)
        if (res is AuthResult.Error) {
            return res
        }
        (res as AuthResult.Success)

        val userFetchResult = authService.fetchUserAttribute()
        if (userFetchResult is AuthResult.Error) {
            return userFetchResult
        }
        (userFetchResult as AuthResult.Success)
        val user = userAttributeMapper.mapFromEntity(userFetchResult.data)
        preferenceManager.setUser(user)

        val idResult = authService.fetchIdToken()
        if (idResult is AuthResult.Error) {
            return idResult
        }
        (idResult as AuthResult.Success)
        preferenceManager.updateIdToken(idResult.data)
        preferenceManager.setAuthenticated(true)
        preferenceManager.updateLaunchState()

        return AuthResult.Success(user)
    }

    suspend fun signUp(authData: AuthData): AuthResult<Unit> {
        val signUpRes = authService.signUp(authData)
        if (signUpRes is AuthResult.Error) {
            return signUpRes
        }
        (signUpRes as AuthResult.Success)
        return signUpRes
    }

    suspend fun signUpConfirmation(authData: AuthData): AuthResult<User> {
        val confirmationResult = authService.confirmSignUp(authData)
        if (confirmationResult is AuthResult.Error) {
            return confirmationResult
        }
        (confirmationResult as AuthResult.Success)
        return login(authData)
    }
}
