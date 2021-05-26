package com.github.code.gambit.data.mapper.cache

import com.github.code.gambit.data.EntityMapper
import com.github.code.gambit.data.entity.chache.UrlCacheEntity
import com.github.code.gambit.data.model.Url
import javax.inject.Inject

class UrlCacheMapper
@Inject
constructor() : EntityMapper<UrlCacheEntity, Url> {

    override fun mapFromEntity(entity: UrlCacheEntity): Url {
        return Url(
            id = entity.id,
            fileId = entity.fileId,
            hash = entity.hash,
            timestamp = entity.timestamp,
            visible = entity.visible,
            clicksLeft = entity.clicksLeft
        )
    }

    override fun mapToEntity(domainModel: Url): UrlCacheEntity {
        return UrlCacheEntity(
            id = domainModel.id,
            fileId = domainModel.fileId,
            hash = domainModel.hash,
            timestamp = domainModel.timestamp,
            visible = domainModel.visible,
            clicksLeft = domainModel.clicksLeft
        )
    }

    override fun mapFromEntityList(entities: List<UrlCacheEntity>): List<Url> {
        return entities.map { mapFromEntity(it) }
    }

    fun mapToEntityList(domainModels: List<Url>): List<UrlCacheEntity> {
        return domainModels.map { mapToEntity(it) }
    }
}
