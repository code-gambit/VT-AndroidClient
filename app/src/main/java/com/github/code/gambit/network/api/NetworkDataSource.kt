package com.github.code.gambit.network.api

import com.github.code.gambit.data.model.File
import com.github.code.gambit.data.model.Url
import com.github.code.gambit.data.model.User
import javax.inject.Named

interface NetworkDataSource {
    suspend fun getFiles(@Named("UID") userId: String): List<File>
    suspend fun uploadFile(@Named("UID") userId: String, fileNetworkEntity: File): File
    suspend fun deleteFile(userId: String, fileId: String): File

    suspend fun getUrls(fileId: String): List<Url>
    suspend fun generateUrl(fileId: String): Url
    suspend fun updateUrl(fileId: String, urlId: String, urlNetworkEntity: Url): Url
    suspend fun deleteUrl(fileId: String, urlId: String): Url

    suspend fun getUser(): User
    suspend fun updateUser(userNetworkEntity: User): User
    suspend fun deleteUser(userId: String): User
}
