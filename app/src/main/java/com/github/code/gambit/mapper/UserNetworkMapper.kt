package com.github.code.gambit.mapper

import com.github.code.gambit.EntityMapper
import com.github.code.gambit.entity.network.UserNetworkEntity
import com.github.code.gambit.model.User
import javax.inject.Inject

class UserNetworkMapper
@Inject
constructor(): EntityMapper<UserNetworkEntity, User>{

    override fun mapFromEntity(entity: UserNetworkEntity): User {
        return User(
            name = entity.name,
            email = entity.pk.split("#")[2],
            type = entity.type,
            thumbnail = entity.thumbnail,
            storageUsed = entity.storageUsed
        )
    }

    override fun mapToEntity(domainModel: User): UserNetworkEntity {
        return UserNetworkEntity(
            pk = "USER#${domainModel.email}",
            sk = "METADATA",
            name = domainModel.name,
            type = domainModel.type,
            storageUsed = domainModel.storageUsed,
            thumbnail = domainModel.thumbnail
        )
    }

    override fun mapFromEntityList(entities: List<UserNetworkEntity>): List<User> {
        return entities.map { mapFromEntity(it) }
    }
}
