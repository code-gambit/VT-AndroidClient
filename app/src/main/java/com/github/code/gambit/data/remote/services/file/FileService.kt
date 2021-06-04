package com.github.code.gambit.data.remote.services.file

import com.github.code.gambit.data.entity.network.FileNetworkEntity

interface FileService {

    suspend fun getFiles(): List<FileNetworkEntity>
    suspend fun searchFile(searchParam: String): List<FileNetworkEntity>
    suspend fun filterFiles(start: String, end: String): List<FileNetworkEntity>
    suspend fun uploadFile(fileNetworkEntity: FileNetworkEntity): FileNetworkEntity
    suspend fun deleteFile(fileId: String): Boolean
}
