package com.github.code.gambit.repositories.profile

import com.github.code.gambit.data.model.User
import com.github.code.gambit.helper.ServiceResult
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {

    suspend fun getUser(): Flow<ServiceResult<User>>
    suspend fun updateUserName(name: String): ServiceResult<String>
    suspend fun updateUserPassword(oldPassword: String, newPassword: String): ServiceResult<Boolean>
    suspend fun logOut(): ServiceResult<Unit>
}
