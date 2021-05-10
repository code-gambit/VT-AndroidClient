package com.github.code.gambit.network.api.user

import com.github.code.gambit.data.entity.network.UserNetworkEntity

interface UserService {
    suspend fun getUser(): UserNetworkEntity
    suspend fun updateUser(userNetworkEntity: UserNetworkEntity): UserNetworkEntity
    suspend fun deleteUser(): UserNetworkEntity
}
