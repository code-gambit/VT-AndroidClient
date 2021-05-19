package com.github.code.gambit.data.local

import com.github.code.gambit.data.mapper.cache.FileCacheMapper
import com.github.code.gambit.data.model.File

class CacheDataSourceImpl
constructor(
    val fileCacheMapper: FileCacheMapper,
    val fileDao: FileDao
) : CacheDataSource {

    override suspend fun getFiles(): List<File> {
        val fileCacheEntities = fileDao.getFiles()
        return fileCacheMapper.mapFromEntityList(fileCacheEntities)
    }

    override suspend fun insertFiles(files: List<File>): Long {
        var c = 0L
        for (file in files) {
            c += fileDao.insertFiles(fileCacheMapper.mapToEntity(file))
        }
        return c
    }
}
