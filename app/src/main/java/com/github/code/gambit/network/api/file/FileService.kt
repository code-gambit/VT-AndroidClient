package com.github.code.gambit.network.api.file

import com.github.code.gambit.data.entity.network.FileNetworkEntity
import javax.inject.Named

interface FileService {

    suspend fun getFiles(@Named("UID") userId: String): List<FileNetworkEntity>
    suspend fun uploadFile(@Named("UID") userId: String, fileNetworkEntity: FileNetworkEntity): FileNetworkEntity
    suspend fun deleteFile(userId: String, fileId: String): FileNetworkEntity
}
