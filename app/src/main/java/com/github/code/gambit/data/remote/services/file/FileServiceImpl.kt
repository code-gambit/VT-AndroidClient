package com.github.code.gambit.data.remote.services.file

import com.github.code.gambit.data.entity.network.FileNetworkEntity
import com.github.code.gambit.data.remote.services.ApiService
import com.github.code.gambit.utility.sharedpreference.UserManager

class FileServiceImpl(
    val apiService: ApiService,
    private val userManager: UserManager
) : FileService {

    private val userId get() = userManager.getUserId()

    override suspend fun getFiles(): List<FileNetworkEntity> {
        var lek: String? = userManager.getFileLastEvaluatedKey()
        if (lek == "") {
            lek = null
        }
        val listResponse = apiService.getFiles(userId, lek)
        userManager.putFileLastEvaluatedKey(listResponse.body.lastEvaluatedKey)
        return listResponse.body.items
    }

    override suspend fun uploadFile(fileNetworkEntity: FileNetworkEntity): FileNetworkEntity {
        return apiService.uploadFiles(userId, fileNetworkEntity).body
    }

    override suspend fun deleteFile(fileId: String): FileNetworkEntity {
        return apiService.deleteFile(userId, fileId).body
    }
}
