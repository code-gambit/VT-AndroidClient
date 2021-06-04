package com.github.code.gambit.data.remote.services.url

import com.github.code.gambit.data.entity.network.UrlNetworkEntity
import com.github.code.gambit.data.remote.responses.ListResponse
import com.github.code.gambit.data.remote.services.ApiService
import com.github.code.gambit.utility.sharedpreference.LastEvaluatedKeyManager

class UrlServiceImpl(val apiService: ApiService, private val lekManager: LastEvaluatedKeyManager) : UrlService {

    override suspend fun getUrls(fileId: String): List<UrlNetworkEntity> {
        val lek: String = lekManager.getLastEvalKey(LastEvaluatedKeyManager.KeyType.URL)
        val listResponse: ListResponse<UrlNetworkEntity> = if (lek == "") {
            apiService.getUrls(fileId.split("#")[1])
        } else {
            apiService.getUrls(fileId.split("#")[1])
        }
        if (listResponse.body?.lastEvaluatedKey != null) {
            lekManager.putLastEvalKey(
                listResponse.body?.lastEvaluatedKey!!,
                LastEvaluatedKeyManager.KeyType.URL
            )
        }
        return listResponse.body?.items!!
    }

    override suspend fun generateUrl(fileId: String, urlNetworkEntity: UrlNetworkEntity): String {
        val id = fileId.split("#")[1]
        return apiService.generateUrl(id, urlNetworkEntity).body!!
    }

    override suspend fun updateUrl(fileId: String, urlId: String, urlNetworkEntity: UrlNetworkEntity): UrlNetworkEntity {
        return apiService.updateUrl(fileId, urlId, urlNetworkEntity).body!!
    }

    override suspend fun deleteUrl(fileId: String, urlId: String): UrlNetworkEntity {
        return apiService.deleteUrl(fileId, urlId).body!!
    }
}
