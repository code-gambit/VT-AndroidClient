package com.github.code.gambit.network.api.file

import com.github.code.gambit.data.entity.network.FileNetworkEntity
import com.github.code.gambit.network.api.ApiService
import com.github.code.gambit.utility.UserManager

class FileServiceImpl(
    val apiService: ApiService,
    private val userManager: UserManager
) : FileService {

    private val userId get() = userManager.getUserId()

    override suspend fun getFiles(): List<FileNetworkEntity> {
        return apiService.getFiles(userId).body.items
    }

    override suspend fun uploadFile(fileNetworkEntity: FileNetworkEntity): FileNetworkEntity {
        return apiService.uploadFiles(userId, fileNetworkEntity).body
    }

    override suspend fun deleteFile(fileId: String): FileNetworkEntity {
        return apiService.deleteFile(userId, fileId).body
    }
}
