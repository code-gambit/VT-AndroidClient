package com.github.code.gambit.network.api.url

import com.github.code.gambit.data.entity.network.UrlNetworkEntity
import com.github.code.gambit.network.api.ApiService

class UrlServiceImpl(val apiService: ApiService) : UrlService {

    override suspend fun getUrls(fileId: String): List<UrlNetworkEntity> {
        return apiService.getUrls(fileId)
    }

    override suspend fun generateUrl(fileId: String): UrlNetworkEntity {
        return apiService.generateUrl(fileId)
    }

    override suspend fun updateUrl(fileId: String, urlId: String, urlNetworkEntity: UrlNetworkEntity): UrlNetworkEntity {
        return apiService.updateUrl(fileId, urlId, urlNetworkEntity)
    }

    override suspend fun deleteUrl(fileId: String, urlId: String): UrlNetworkEntity {
        return apiService.deleteUrl(fileId, urlId)
    }
}
