package com.github.code.gambit.data.remote.services.url

import com.github.code.gambit.data.entity.network.UrlNetworkEntity
import com.github.code.gambit.data.remote.apiRequest
import com.github.code.gambit.data.remote.services.ApiService
import com.github.code.gambit.utility.extention.getAbsoluteId
import com.github.code.gambit.utility.sharedpreference.LastEvaluatedKeyManager

class UrlServiceImpl(val apiService: ApiService, private val lekManager: LastEvaluatedKeyManager) : UrlService {

    override suspend fun getUrls(fileId: String): List<UrlNetworkEntity> {
        return apiRequest(lekManager, LastEvaluatedKeyManager.KeyType.URL) {
            apiService.getUrls(fileId.split("#")[1])
        }
    }

    override suspend fun generateUrl(fileId: String, urlNetworkEntity: UrlNetworkEntity): UrlNetworkEntity {
        val id = fileId.split("#")[1]
        return apiRequest { apiService.generateUrl(id, urlNetworkEntity) }
    }

    override suspend fun updateUrl(fileId: String, urlId: String, urlNetworkEntity: UrlNetworkEntity): UrlNetworkEntity {
        return apiRequest { apiService.updateUrl(fileId.getAbsoluteId(), urlId.getAbsoluteId(), urlNetworkEntity) }
    }

    override suspend fun deleteUrl(fileId: String, urlId: String): UrlNetworkEntity {
        return apiRequest { apiService.deleteUrl(fileId.getAbsoluteId(), urlId.getAbsoluteId()) }
    }
}
