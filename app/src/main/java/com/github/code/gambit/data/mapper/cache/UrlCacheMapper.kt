package com.github.code.gambit.data.mapper.cache

import com.github.code.gambit.EntityMapper
import com.github.code.gambit.data.entity.chache.UrlCacheEntity
import com.github.code.gambit.data.model.Url
import javax.inject.Inject

class UrlCacheMapper
@Inject
constructor() : EntityMapper<UrlCacheEntity, Url> {

    override fun mapFromEntity(entity: UrlCacheEntity): Url {
        return Url(
            id = entity.id,
            file = entity.file,
            timestamp = entity.timestamp,
            visible = entity.visible
        )
    }

    override fun mapToEntity(domainModel: Url): UrlCacheEntity {
        return UrlCacheEntity(
            id = domainModel.id,
            file = domainModel.file,
            timestamp = domainModel.timestamp,
            visible = domainModel.visible
        )
    }

    override fun mapFromEntityList(entities: List<UrlCacheEntity>): List<Url> {
        return entities.map { mapFromEntity(it) }
    }
}
