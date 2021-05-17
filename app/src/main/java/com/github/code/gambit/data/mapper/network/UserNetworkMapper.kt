package com.github.code.gambit.data.mapper.network

import com.github.code.gambit.data.EntityMapper
import com.github.code.gambit.data.entity.network.UserNetworkEntity
import com.github.code.gambit.data.model.User
import javax.inject.Inject

class UserNetworkMapper
@Inject
constructor() : EntityMapper<UserNetworkEntity, User> {

    override fun mapFromEntity(entity: UserNetworkEntity): User {
        return User(
            id = entity.pk.split("#")[1],
            "",
            "",
            type = entity.type,
            storageUsed = entity.storageUsed
        )
    }

    override fun mapToEntity(domainModel: User): UserNetworkEntity {
        return UserNetworkEntity(
            pk = "USER#${domainModel.id}",
            sk = "METADATA",
            type = domainModel.type,
            storageUsed = domainModel.storageUsed
        )
    }

    override fun mapFromEntityList(entities: List<UserNetworkEntity>): List<User> {
        return entities.map { mapFromEntity(it) }
    }
}
