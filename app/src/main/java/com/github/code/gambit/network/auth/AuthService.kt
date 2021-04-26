package com.github.code.gambit.network.auth

import com.amplifyframework.auth.AuthUserAttribute
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.github.code.gambit.helper.auth.AuthData
import com.github.code.gambit.helper.auth.AuthResult

interface AuthService {
    suspend fun login(authData: AuthData): AuthResult<Unit>
    suspend fun signUp(authData: AuthData): AuthResult<Unit>
    suspend fun confirmSignUp(authData: AuthData): AuthResult<Unit>
    suspend fun fetchSession(): AuthResult<AWSCognitoAuthSession>
    suspend fun fetchIdToken(): AuthResult<String>
    suspend fun fetchUserAttribute(): AuthResult<List<AuthUserAttribute>>
    suspend fun resentConfirmationCode(email: String): AuthResult<Unit>
}
