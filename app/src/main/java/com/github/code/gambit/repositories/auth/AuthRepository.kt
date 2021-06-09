package com.github.code.gambit.repositories.auth

import com.github.code.gambit.data.model.User
import com.github.code.gambit.helper.ServiceResult
import com.github.code.gambit.helper.auth.AuthData

interface AuthRepository {

    suspend fun login(authData: AuthData): ServiceResult<User>
    suspend fun signUp(authData: AuthData): ServiceResult<Unit>
    suspend fun logOut(): ServiceResult<Unit>
    suspend fun signUpConfirmation(authData: AuthData): ServiceResult<User>
    suspend fun resendConfirmationCode(userEmail: String): ServiceResult<Unit>
    suspend fun forgotPassword(userEmail: String): ServiceResult<Unit>
    suspend fun resetForgotPassword(authData: AuthData): ServiceResult<User>
}
