package com.github.code.gambit.data.mapper.network

import com.github.code.gambit.data.EntityMapper
import com.github.code.gambit.data.entity.network.UrlNetworkEntity
import com.github.code.gambit.data.model.Url
import javax.inject.Inject

class UrlNetworkMapper
@Inject
constructor() : EntityMapper<UrlNetworkEntity, Url> {

    override fun mapFromEntity(entity: UrlNetworkEntity): Url {
        return Url(
            id = entity.gs1_pk,
            fileId = entity.pk,
            hash = entity.hash,
            timestamp = entity.sk.split("#")[1],
            visible = entity.visible,
            clicksLeft = entity.clicks_left
        )
    }

    override fun mapToEntity(domainModel: Url): UrlNetworkEntity {
        return UrlNetworkEntity(
            hash = domainModel.hash,
            gs1_pk = domainModel.id,
            visible = domainModel.visible,
            clicks_left = domainModel.clicksLeft
        )
    }

    override fun mapFromEntityList(entities: List<UrlNetworkEntity>): List<Url> {
        return entities.map { mapFromEntity(it) }
    }
}
