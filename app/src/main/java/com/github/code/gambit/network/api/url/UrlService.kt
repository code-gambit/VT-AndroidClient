package com.github.code.gambit.network.api.url

import com.github.code.gambit.data.entity.network.UrlNetworkEntity

interface UrlService {

    suspend fun getUrls(fileId: String): List<UrlNetworkEntity>
    suspend fun generateUrl(fileId: String): UrlNetworkEntity
    suspend fun updateUrl(fileId: String, urlId: String, urlNetworkEntity: UrlNetworkEntity): UrlNetworkEntity
    suspend fun deleteUrl(fileId: String, urlId: String): UrlNetworkEntity
}
