package com.github.code.gambit.data.local

import com.github.code.gambit.data.entity.chache.FileMetaDataCacheEntity
import com.github.code.gambit.data.mapper.cache.FileCacheMapper
import com.github.code.gambit.data.mapper.cache.FileMetaDataMapper
import com.github.code.gambit.data.mapper.cache.UrlCacheMapper
import com.github.code.gambit.data.model.File
import com.github.code.gambit.data.model.FileMetaData
import com.github.code.gambit.data.model.Url

class CacheDataSourceImpl
constructor(
    private val fileCacheMapper: FileCacheMapper,
    private val urlCacheMapper: UrlCacheMapper,
    private val fileMetaDataMapper: FileMetaDataMapper,
    private val fileDao: FileDao,
    private val urlDao: UrlDao,
    private val fileWorkerDao: FileMetaDataDao
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

    override suspend fun deleteFile(fileId: String): Int {
        return fileDao.deleteFile(fileId)
    }

    override suspend fun deleteFiles(): Int {
        return fileDao.deleteFiles()
    }

    override suspend fun insertUrls(urls: List<Url>): Long {
        val res = urlDao.insertUrls(urlCacheMapper.mapToEntityList(urls))
        return res.size.toLong()
    }

    override suspend fun getUrls(fileId: String): List<Url> {
        val urls = urlDao.getUrls(fileId)
        return urlCacheMapper.mapFromEntityList(urls)
    }

    override suspend fun deleteUrls(): Int {
        return urlDao.deleteUrls()
    }

    override suspend fun insertFileMetaData(fileMetaData: FileMetaData): Long {
        val data: FileMetaDataCacheEntity = fileMetaDataMapper.mapToEntity(fileMetaData)
        return fileWorkerDao.insertFileMetaData(data)
    }

    override suspend fun getAllFileMetaData(): List<FileMetaData> {
        return fileMetaDataMapper.mapFromEntityList(fileWorkerDao.getAllFileMetaData())
    }

    override suspend fun deleteFileMetaData(uuid: String) {
        return fileWorkerDao.deleteFileMetaData(uuid)
    }

    override suspend fun clearAllFileMetaData() {
        return fileWorkerDao.deleteAll()
    }
}
