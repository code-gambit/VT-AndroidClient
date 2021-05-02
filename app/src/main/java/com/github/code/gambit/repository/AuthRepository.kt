package com.github.code.gambit.repository

import android.net.Uri
import com.github.code.gambit.PreferenceManager
import com.github.code.gambit.data.mapper.aws.UserAttributeMapper
import com.github.code.gambit.data.model.User
import com.github.code.gambit.helper.auth.AuthData
import com.github.code.gambit.helper.auth.ServiceResult
import com.github.code.gambit.network.auth.AuthService
import com.github.code.gambit.network.image.ImageService

class AuthRepository

constructor(
    private val authService: AuthService,
    private val imageService: ImageService,
    private val preferenceManager: PreferenceManager,
    private val userAttributeMapper: UserAttributeMapper
) {

    val isUserLoggedIn get() = preferenceManager.isAuthenticated()
    val getToken get() = preferenceManager.getIdToken()
    val revokeAuth = { preferenceManager.revokeAuthentication() }

    suspend fun login(authData: AuthData): ServiceResult<User> {
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
        preferenceManager.setUser(user)

        val idResult = authService.fetchIdToken()
        if (idResult is ServiceResult.Error) {
            return idResult
        }
        (idResult as ServiceResult.Success)
        preferenceManager.updateIdToken(idResult.data)
        preferenceManager.setAuthenticated(true)
        preferenceManager.updateLaunchState()

        return ServiceResult.Success(user)
    }

    private suspend fun uploadProfileImage(imageUri: Uri): ServiceResult<String> {
        return imageService.uploadImage(imageUri)
    }

    suspend fun signUp(authData: AuthData): ServiceResult<Unit> {
        val uploadImageRes = uploadProfileImage(Uri.parse(authData.thumbnail))
        if (uploadImageRes is ServiceResult.Success) {
            authData.thumbnail = uploadImageRes.data
        }
        val signUpRes = authService.signUp(authData)
        if (signUpRes is ServiceResult.Error) {
            return signUpRes
        }
        (signUpRes as ServiceResult.Success)
        return signUpRes
    }

    suspend fun signUpConfirmation(authData: AuthData): ServiceResult<User> {
        val confirmationResult = authService.confirmSignUp(authData)
        if (confirmationResult is ServiceResult.Error) {
            return confirmationResult
        }
        (confirmationResult as ServiceResult.Success)
        return login(authData)
    }
}
