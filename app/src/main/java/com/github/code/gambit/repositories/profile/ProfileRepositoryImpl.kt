package com.github.code.gambit.repositories.profile

import com.amazonaws.AmazonClientException
import com.amazonaws.services.cognitoidentityprovider.model.InvalidParameterException
import com.github.code.gambit.data.model.User
import com.github.code.gambit.data.remote.NetworkDataSource
import com.github.code.gambit.data.remote.services.auth.AuthService
import com.github.code.gambit.helper.ServiceResult
import com.github.code.gambit.utility.NoInternetException
import com.github.code.gambit.utility.sharedpreference.UserManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception
import javax.inject.Inject

class ProfileRepositoryImpl
@Inject
constructor(
    private val networkDataSource: NetworkDataSource,
    private val authService: AuthService,
    private val userManager: UserManager
) : ProfileRepository {

    override suspend fun getUser(): Flow<ServiceResult<User>> {
        return flow {
            try {
                val user = networkDataSource.getUser()
                emit(ServiceResult.Success(userManager.mergeUser(user)))
            } catch (error: NoInternetException) {
                emit(ServiceResult.Error(error))
                emit(ServiceResult.Success(userManager.getUser()))
            } catch (e: Exception) {
                emit(ServiceResult.Error(Exception("Server error")))
            }
        }
    }

    override suspend fun updateUserName(name: String): ServiceResult<String> {
        try {
            val result = authService.updateUserName(name)
            if (result is ServiceResult.Error) {
                return ServiceResult.Error(result.exception)
            }
            (result as ServiceResult.Success)
            userManager.setUserName(result.data)
            return ServiceResult.Success(userManager.getUserName())
        } catch (e: Exception) {
            return ServiceResult.Error(e)
        }
    }

    override suspend fun updateUserPassword(oldPassword: String, newPassword: String): ServiceResult<Boolean> {
        return try {
            val result = authService.resetPassword(oldPassword, newPassword)
            if (result is ServiceResult.Error) {
                when (result.exception.cause) {
                    is InvalidParameterException -> {
                        ServiceResult.Error(Exception("Invalid old password"))
                    }
                    is AmazonClientException -> {
                        ServiceResult.Error(NoInternetException("Internet!!"))
                    }
                    else -> {
                        ServiceResult.Success(false)
                    }
                }
            } else {
                ServiceResult.Success(true)
            }
        } catch (e: AmazonClientException) {
            ServiceResult.Error(NoInternetException("Internet!!"))
        } catch (e: Exception) {
            ServiceResult.Error(e)
        }
    }

    override suspend fun logOut(): ServiceResult<Unit> {
        return try {
            authService.logOut()
            return ServiceResult.Success(Unit)
        } catch (exception: Exception) {
            ServiceResult.Error(exception)
        }
    }
}
