package com.github.code.gambit.data.remote

import com.github.code.gambit.data.mapper.network.FileNetworkMapper
import com.github.code.gambit.data.mapper.network.UrlNetworkMapper
import com.github.code.gambit.data.mapper.network.UserNetworkMapper
import com.github.code.gambit.data.model.File
import com.github.code.gambit.data.model.Url
import com.github.code.gambit.data.model.User
import com.github.code.gambit.data.remote.services.file.FileService
import com.github.code.gambit.data.remote.services.url.UrlService
import com.github.code.gambit.data.remote.services.user.UserService

class NetworkDataSourceImpl(
    val fileService: FileService,
    val urlService: UrlService,
    val userService: UserService,
    private val fileNetworkMapper: FileNetworkMapper,
    private val urlNetworkMapper: UrlNetworkMapper,
    private val userNetworkMapper: UserNetworkMapper
) : NetworkDataSource {

    override suspend fun getFiles(): List<File> {
        return fileNetworkMapper.mapFromEntityList(fileService.getFiles())
    }

    override suspend fun filterFiles(start: String, end: String): List<File> {
        return fileNetworkMapper.mapFromEntityList(fileService.filterFiles(start, end))
    }

    override suspend fun searchFiles(searchParam: String): List<File> {
        return fileNetworkMapper.mapFromEntityList(fileService.searchFile(searchParam))
    }

    override suspend fun uploadFile(file: File): File {
        return fileNetworkMapper.mapFromEntity(fileService.uploadFile(fileNetworkMapper.mapToEntity(file)))
    }

    override suspend fun deleteFile(fileId: String): Boolean {
        return fileService.deleteFile(fileId)
    }

    override suspend fun getUrls(fileId: String): List<Url> {
        val res = urlService.getUrls(fileId)
        return urlNetworkMapper.mapFromEntityList(res)
    }

    override suspend fun generateUrl(url: Url): Url {
        val id = urlService.generateUrl(url.fileId, urlNetworkMapper.mapToEntity(url))
        url.id = id
        return url
    }

    override suspend fun updateUrl(fileId: String, urlId: String, url: Url): Url {
        return urlNetworkMapper.mapFromEntity(urlService.updateUrl(fileId, urlId, urlNetworkMapper.mapToEntity(url)))
    }

    override suspend fun deleteUrl(fileId: String, urlId: String): Url {
        return urlNetworkMapper.mapFromEntity(urlService.deleteUrl(fileId, urlId))
    }

    override suspend fun getUser(): User {
        return userNetworkMapper.mapFromEntity(userService.getUser())
    }

    override suspend fun updateUser(user: User): User {
        return userNetworkMapper.mapFromEntity(userService.updateUser(userNetworkMapper.mapToEntity(user)))
    }

    override suspend fun deleteUser(): User {
        return userNetworkMapper.mapFromEntity(userService.deleteUser())
    }
}
