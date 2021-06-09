package com.github.code.gambit.repositories.auth

import com.amazonaws.mobileconnectors.cognitoidentityprovider.util.CognitoJWTParser
import com.github.code.gambit.data.local.CacheDataSource
import com.github.code.gambit.data.mapper.aws.UserAttributeMapper
import com.github.code.gambit.data.model.User
import com.github.code.gambit.data.remote.NetworkDataSource
import com.github.code.gambit.data.remote.services.auth.AuthService
import com.github.code.gambit.helper.ServiceResult
import com.github.code.gambit.helper.auth.AuthData
import com.github.code.gambit.utility.sharedpreference.LastEvaluatedKeyManager
import com.github.code.gambit.utility.sharedpreference.UserManager
import java.lang.Exception

class AuthRepositoryImpl
constructor(
    private val authService: AuthService,
    private val networkDataSource: NetworkDataSource,
    private val cacheDataSource: CacheDataSource,
    private val userManager: UserManager,
    private val lastEvaluatedKeyManager: LastEvaluatedKeyManager,
    private val userAttributeMapper: UserAttributeMapper
) : AuthRepository {

    override suspend fun login(authData: AuthData): ServiceResult<User> {
        val res = authService.login(authData)
        if (res is ServiceResult.Error) {
            return res
        }
        (res as ServiceResult.Success)

        val userFetchResult = authService.fetchUserAttribute()
        if (userFetchResult is ServiceResult.Error) {
            return userFetchResult
        }
        (userFetchResult as ServiceResult.Success)
        val user = userAttributeMapper.mapFromEntity(userFetchResult.data)

        val idResult = authService.fetchIdToken()
        if (idResult is ServiceResult.Error) {
            return idResult
        }
        (idResult as ServiceResult.Success)
        val id = CognitoJWTParser.getClaim(idResult.data, "sub")
        user.id = id
        userManager.setUser(user, idResult.data)

        val user_in_db = networkDataSource.getUser()

        userManager.updateUser(User.merge(user, user_in_db))

        return ServiceResult.Success(user)
    }

    override suspend fun signUp(authData: AuthData): ServiceResult<Unit> {
        val signUpRes = authService.signUp(authData)
        if (signUpRes is ServiceResult.Error) {
            return signUpRes
        }
        (signUpRes as ServiceResult.Success)
        return signUpRes
    }

    override suspend fun logOut(): ServiceResult<Unit> {
        return try {
            authService.logOut()
            userManager.revokeAuthentication()
            lastEvaluatedKeyManager.flush()
            cacheDataSource.deleteFiles()
            return ServiceResult.Success(Unit)
        } catch (exception: Exception) {
            ServiceResult.Error(exception)
        }
    }

    override suspend fun signUpConfirmation(authData: AuthData): ServiceResult<User> {
        val confirmationResult = authService.confirmSignUp(authData)
        if (confirmationResult is ServiceResult.Error) {
            return confirmationResult
        }
        (confirmationResult as ServiceResult.Success)
        return login(authData)
    }

    override suspend fun resendConfirmationCode(userEmail: String): ServiceResult<Unit> {
        return authService.resentConfirmationCode(userEmail)
    }

    override suspend fun forgotPassword(userEmail: String): ServiceResult<Unit> {
        return authService.forgotPassword(userEmail)
    }

    override suspend fun resetForgotPassword(authData: AuthData): ServiceResult<User> {
        return when (val res = authService.changePassword(authData.password, authData.confirmationCode!!)) {
            is ServiceResult.Error -> res
            is ServiceResult.Success -> login(authData)
        }
    }
}
