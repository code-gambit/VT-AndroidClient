package com.github.code.gambit.data.mapper.cache

import com.github.code.gambit.data.EntityMapper
import com.github.code.gambit.data.entity.chache.FileMetaDataCacheEntity
import com.github.code.gambit.data.model.FileMetaData
import javax.inject.Inject

class FileMetaDataMapper
@Inject
constructor() : EntityMapper<FileMetaDataCacheEntity, FileMetaData> {

    override fun mapFromEntity(entity: FileMetaDataCacheEntity): FileMetaData {
        return FileMetaData(entity.path, entity.name, entity.size, entity.uuid)
    }

    override fun mapToEntity(domainModel: FileMetaData): FileMetaDataCacheEntity {
        val uuid = domainModel.uuid?.let { domainModel.uuid } ?: ""
        return FileMetaDataCacheEntity(
            uuid,
            0L,
            domainModel.path,
            domainModel.name,
            domainModel.size
        )
    }

    override fun mapFromEntityList(entities: List<FileMetaDataCacheEntity>): List<FileMetaData> {
        return entities.map { mapFromEntity(it) }
    }

    fun mapToEntityList(entities: List<FileMetaData>): List<FileMetaDataCacheEntity> {
        return entities.map { mapToEntity(it) }
    }
}
