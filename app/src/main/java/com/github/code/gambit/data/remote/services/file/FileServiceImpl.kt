package com.github.code.gambit.data.remote.services.file

import com.github.code.gambit.data.entity.network.FileNetworkEntity
import com.github.code.gambit.data.remote.responses.ListResponse
import com.github.code.gambit.data.remote.services.ApiService
import com.github.code.gambit.utility.sharedpreference.LastEvaluatedKeyManager
import com.github.code.gambit.utility.sharedpreference.UserManager

class FileServiceImpl(
    val apiService: ApiService,
    private val userManager: UserManager,
    private val lekManager: LastEvaluatedKeyManager
) : FileService {

    private val userId get() = userManager.getUserId()

    override suspend fun getFiles(): List<FileNetworkEntity> {
        val lek: String = lekManager.getLastEvalKey(LastEvaluatedKeyManager.KeyType.FILE)
        val listResponse: ListResponse<FileNetworkEntity> = apiService.getFiles(userId, lek, null)
        if (listResponse.body.lastEvaluatedKey != null) {
            lekManager.putLastEvalKey(
                listResponse.body.lastEvaluatedKey!!,
                LastEvaluatedKeyManager.KeyType.FILE
            )
        }
        return listResponse.body.items
    }

    override suspend fun searchFile(searchParam: String): List<FileNetworkEntity> {
        val response = apiService.getFiles(userId, null, searchParam)
        return response.body.items
    }

    override suspend fun uploadFile(fileNetworkEntity: FileNetworkEntity): FileNetworkEntity {
        return apiService.uploadFiles(userId, fileNetworkEntity).body
    }

    override suspend fun deleteFile(fileId: String): FileNetworkEntity {
        return apiService.deleteFile(userId, fileId).body
    }
}
