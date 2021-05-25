package com.github.code.gambit.data.local

import com.github.code.gambit.data.mapper.cache.FileCacheMapper
import com.github.code.gambit.data.mapper.cache.UrlCacheMapper
import com.github.code.gambit.data.model.File
import com.github.code.gambit.data.model.Url

class CacheDataSourceImpl
constructor(
    val fileCacheMapper: FileCacheMapper,
    val urlCacheMapper: UrlCacheMapper,
    val fileDao: FileDao,
    val urlDao: UrlDao
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

    override suspend fun insertUrls(urls: List<Url>): Long {
        val res = urlDao.insertUrls(urlCacheMapper.mapToEntityList(urls))
        return res.size.toLong()
    }

    override suspend fun getUrls(fileId: String): List<Url> {
        val urls = urlDao.getUrls(fileId)
        return urlCacheMapper.mapFromEntityList(urls)
    }
}
