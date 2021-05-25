package com.github.code.gambit.data.remote.services.url

import com.github.code.gambit.data.entity.network.UrlNetworkEntity

interface UrlService {

    suspend fun getUrls(fileId: String): List<UrlNetworkEntity>
    suspend fun generateUrl(fileId: String, urlNetworkEntity: UrlNetworkEntity): String
    suspend fun updateUrl(fileId: String, urlId: String, urlNetworkEntity: UrlNetworkEntity): UrlNetworkEntity
    suspend fun deleteUrl(fileId: String, urlId: String): UrlNetworkEntity
}
