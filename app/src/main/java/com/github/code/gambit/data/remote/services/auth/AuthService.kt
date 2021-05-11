package com.github.code.gambit.data.remote.services.auth

import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.github.code.gambit.helper.auth.AuthData
import com.github.code.gambit.helper.auth.ServiceResult

interface AuthService {
    suspend fun login(authData: AuthData): ServiceResult<Unit>
    suspend fun signUp(authData: AuthData): ServiceResult<Unit>
    suspend fun confirmSignUp(authData: AuthData): ServiceResult<Unit>
    suspend fun fetchSession(): ServiceResult<AWSCognitoAuthSession>
    suspend fun fetchIdToken(): ServiceResult<String>
    suspend fun fetchUserAttribute(): ServiceResult<List<AuthUserAttribute>>
    suspend fun resentConfirmationCode(email: String): ServiceResult<Unit>
}
