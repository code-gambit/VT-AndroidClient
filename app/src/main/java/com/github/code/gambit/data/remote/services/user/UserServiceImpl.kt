package com.github.code.gambit.data.remote.services.user

import com.github.code.gambit.data.entity.network.UserNetworkEntity
import com.github.code.gambit.data.remote.services.ApiService
import com.github.code.gambit.utility.sharedpreference.UserManager

class UserServiceImpl(val apiService: ApiService, private val userManager: UserManager) : UserService {

    private val userId get() = userManager.getUserId()

    override suspend fun getUser(): UserNetworkEntity {
        val id = userId
        val user = apiService.getUser(id)
        return user.body
    }

    override suspend fun updateUser(userNetworkEntity: UserNetworkEntity): UserNetworkEntity {
        return apiService.updateUser(userId, userNetworkEntity).body
    }

    override suspend fun deleteUser(): UserNetworkEntity {
        return apiService.deleteUser(userId).body
    }
}
