package com.github.code.gambit.data.local

import com.github.code.gambit.data.model.File

interface CacheDataSource {

    suspend fun insertFiles(files: List<File>): Long
    suspend fun getFiles(): List<File>
}
