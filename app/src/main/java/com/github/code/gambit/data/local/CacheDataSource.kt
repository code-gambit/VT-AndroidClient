package com.github.code.gambit.data.local

import com.github.code.gambit.data.model.File
import com.github.code.gambit.data.model.Url

interface CacheDataSource {

    suspend fun insertFiles(files: List<File>): Long
    suspend fun getFiles(): List<File>
    suspend fun insertUrls(urls: List<Url>): Long
    suspend fun getUrls(fileId: String): List<Url>
}
