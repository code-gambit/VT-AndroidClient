package com.github.code.gambit.data.remote.services.file

import com.github.code.gambit.data.entity.network.FileNetworkEntity
import com.github.code.gambit.data.remote.apiRequest
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
        return apiRequest(lekManager, LastEvaluatedKeyManager.KeyType.FILE) { lek ->
            apiService.getFiles(userId, lek, null)
        }
    }

    override suspend fun filterFiles(start: String, end: String): List<FileNetworkEntity> {
        return apiRequest(lekManager, LastEvaluatedKeyManager.KeyType.FILTER_FILE) {
            apiService.filterFiles(userId, null, start, end)
        }
    }

    override suspend fun searchFile(searchParam: String): List<FileNetworkEntity> {
        return apiRequest(lekManager, LastEvaluatedKeyManager.KeyType.SEARCH_FILE) {
            apiService.getFiles(userId, null, searchParam)
        }
    }

    override suspend fun uploadFile(fileNetworkEntity: FileNetworkEntity): FileNetworkEntity {
        return apiRequest { apiService.uploadFiles(userId, fileNetworkEntity) }
    }

    override suspend fun deleteFile(fileId: String): Boolean {
        var id = fileId
        if (fileId.contains("FILE#")) {
            id = fileId.split("#")[1]
        }
        val response = apiRequest { apiService.deleteFile(userId, id) }
        return true
    }
}
