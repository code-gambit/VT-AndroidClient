package com.github.code.gambit.network.api.file

import com.github.code.gambit.data.entity.network.FileNetworkEntity
import com.github.code.gambit.network.api.ApiService

class FileServiceImpl(val apiService: ApiService) : FileService {

    override suspend fun getFiles(userId: String): List<FileNetworkEntity> {
        return apiService.getFiles(userId).body.items
    }

    override suspend fun uploadFile(
        userId: String,
        fileNetworkEntity: FileNetworkEntity
    ): FileNetworkEntity {
        return apiService.uploadFiles(userId, fileNetworkEntity).body
    }

    override suspend fun deleteFile(userId: String, fileId: String): FileNetworkEntity {
        return apiService.deleteFile(userId, fileId).body
    }
}
