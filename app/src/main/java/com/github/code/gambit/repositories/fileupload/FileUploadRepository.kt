package com.github.code.gambit.repositories.fileupload

import com.github.code.gambit.data.model.FileMetaData
import com.github.code.gambit.helper.ServiceResult
import java.util.UUID

interface FileUploadRepository {

    suspend fun getFileMetaData(uuid: String): ServiceResult<FileMetaData>

    suspend fun getFilesMetaData(uuids: List<String>): ServiceResult<List<FileMetaData>>

    suspend fun insertFileUploadInfo(fileMetaData: FileMetaData, uuid: UUID): ServiceResult<Unit>

    suspend fun deleteFileMetaData(uuid: UUID): ServiceResult<Unit>
}
