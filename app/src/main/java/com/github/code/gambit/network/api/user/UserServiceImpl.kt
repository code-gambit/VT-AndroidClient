package com.github.code.gambit.network.api.user

import com.github.code.gambit.data.entity.network.UserNetworkEntity
import com.github.code.gambit.network.api.ApiService
import com.github.code.gambit.utility.UserManager

class UserServiceImpl(val apiService: ApiService, private val userManager: UserManager) : UserService {

    private val userId get() = userManager.getUserId()

    override suspend fun getUser(): UserNetworkEntity {
        return apiService.getUser(userId).body
    }

    override suspend fun updateUser(userNetworkEntity: UserNetworkEntity): UserNetworkEntity {
        return apiService.updateUser(userId, userNetworkEntity).body
    }

    override suspend fun deleteUser(): UserNetworkEntity {
        return apiService.deleteUser(userId).body
    }
}
