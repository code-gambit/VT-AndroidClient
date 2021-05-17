package com.github.code.gambit.repositories

import android.net.Uri
import com.amazonaws.mobileconnectors.cognitoidentityprovider.util.CognitoJWTParser
import com.github.code.gambit.data.mapper.aws.UserAttributeMapper
import com.github.code.gambit.data.model.User
import com.github.code.gambit.data.remote.NetworkDataSource
import com.github.code.gambit.data.remote.services.auth.AuthService
import com.github.code.gambit.data.remote.services.image.ImageService
import com.github.code.gambit.helper.ServiceResult
import com.github.code.gambit.helper.auth.AuthData
import com.github.code.gambit.utility.sharedpreference.UserManager

class AuthRepository

constructor(
    private val authService: AuthService,
    private val imageService: ImageService,
    private val networkDataSource: NetworkDataSource,
    private val userManager: UserManager,
    private val userAttributeMapper: UserAttributeMapper
) {

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

        val update = userManager.getUser()

        return ServiceResult.Success(user)
    }

    @Deprecated(level = DeprecationLevel.WARNING, message = "This service is no longer supported")
    private suspend fun uploadProfileImage(imageUri: Uri): ServiceResult<String> {
        return imageService.uploadImage(imageUri)
    }

    suspend fun signUp(authData: AuthData): ServiceResult<Unit> {
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
