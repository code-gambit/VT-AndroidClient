package com.github.code.gambit.mapper

import com.github.code.gambit.EntityMapper
import com.github.code.gambit.entity.chache.FileCacheEntity
import com.github.code.gambit.model.File

class FileCacheMapper
constructor() : EntityMapper<FileCacheEntity, File> {

    override fun mapFromEntity(entity: FileCacheEntity): File {
        return File(
            id = entity.id,
            name = entity.name,
            type = entity.type,
            timestamp = entity.timestamp,
            size = entity.size,
            extension = entity.extension
        )
    }

    override fun mapToEntity(domainModel: File): FileCacheEntity {
        return FileCacheEntity(
            id = domainModel.id,
            name = domainModel.name,
            type = domainModel.type,
            timestamp = domainModel.timestamp,
            size = domainModel.size,
            extension = domainModel.extension
        )
    }

    override fun mapFromEntityList(entities: List<FileCacheEntity>): List<File> {
        return entities.map { mapFromEntity(it) }
    }
}
