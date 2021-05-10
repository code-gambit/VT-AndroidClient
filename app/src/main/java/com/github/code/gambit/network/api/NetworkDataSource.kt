package com.github.code.gambit.network.api

import com.github.code.gambit.data.model.File
import com.github.code.gambit.data.model.Url
import com.github.code.gambit.data.model.User

interface NetworkDataSource {
    suspend fun getFiles(): List<File>
    suspend fun uploadFile(file: File): File
    suspend fun deleteFile(fileId: String): File

    suspend fun getUrls(fileId: String): List<Url>
    suspend fun generateUrl(fileId: String): Url
    suspend fun updateUrl(fileId: String, urlId: String, url: Url): Url
    suspend fun deleteUrl(fileId: String, urlId: String): Url

    suspend fun getUser(): User
    suspend fun updateUser(user: User): User
    suspend fun deleteUser(): User
}
