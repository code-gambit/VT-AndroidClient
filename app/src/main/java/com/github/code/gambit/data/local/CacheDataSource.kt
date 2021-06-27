package com.github.code.gambit.data.local

import com.github.code.gambit.data.model.File
import com.github.code.gambit.data.model.FileMetaData
import com.github.code.gambit.data.model.Url

interface CacheDataSource {

    suspend fun insertFiles(files: List<File>): Long
    suspend fun getFiles(): List<File>
    suspend fun deleteFile(fileId: String): Int
    suspend fun deleteFiles(): Int
    suspend fun insertUrls(urls: List<Url>): Long
    suspend fun getUrls(fileId: String): List<Url>
    suspend fun deleteUrls(): Int
    suspend fun insertFileMetaData(fileMetaData: FileMetaData): Long
    suspend fun getAllFileMetaData(): List<FileMetaData>
    suspend fun deleteFileMetaData(uuid: String)
    suspend fun clearAllFileMetaData()
}
