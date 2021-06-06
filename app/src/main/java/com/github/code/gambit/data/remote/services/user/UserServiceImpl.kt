package com.github.code.gambit.data.remote.services.user

import com.github.code.gambit.data.entity.network.UserNetworkEntity
import com.github.code.gambit.data.remote.apiRequest
import com.github.code.gambit.data.remote.services.ApiService
import com.github.code.gambit.utility.sharedpreference.UserManager

class UserServiceImpl(val apiService: ApiService, private val userManager: UserManager) : UserService {

    private val userId get() = userManager.getUserId()

    override suspend fun getUser(): UserNetworkEntity {
        return apiRequest { apiService.getUser(userId) }
    }

    override suspend fun updateUser(userNetworkEntity: UserNetworkEntity): UserNetworkEntity {
        return apiRequest { apiService.updateUser(userId, userNetworkEntity) }
    }

    override suspend fun deleteUser(): UserNetworkEntity {
        return apiRequest { apiService.deleteUser(userId) }
    }
}
