package com.github.code.gambit.network.api.user

import com.github.code.gambit.data.entity.network.UserNetworkEntity
import com.github.code.gambit.network.api.ApiService
import javax.inject.Named

class UserServiceImpl(val apiService: ApiService, @Named("UID") val userId: String) : UserService {

    override suspend fun getUser(): UserNetworkEntity {
        return apiService.getUser(userId)
    }

    override suspend fun updateUser(userNetworkEntity: UserNetworkEntity): UserNetworkEntity {
        return apiService.updateUser(userId, userNetworkEntity)
    }

    override suspend fun deleteUser(userId: String): UserNetworkEntity {
        return apiService.deleteUser(userId)
    }
}
