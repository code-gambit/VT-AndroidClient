package com.github.code.gambit.data.remote

import com.github.code.gambit.data.model.File
import com.github.code.gambit.data.model.Url
import com.github.code.gambit.data.model.User

interface NetworkDataSource {
    suspend fun getFiles(force: Boolean = false): List<File>
    suspend fun searchFiles(searchParam: String): List<File>
    suspend fun filterFiles(start: String, end: String): List<File>
    suspend fun uploadFile(file: File): File
    suspend fun deleteFile(fileId: String): Boolean

    suspend fun getUrls(fileId: String): List<Url>
    suspend fun generateUrl(url: Url): Url
    suspend fun updateUrl(url: Url): Url
    suspend fun deleteUrl(url: Url): Url

    suspend fun getUser(): User
    suspend fun updateUser(user: User): User
    suspend fun deleteUser(): User
}
