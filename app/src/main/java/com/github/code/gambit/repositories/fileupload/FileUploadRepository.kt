package com.github.code.gambit.repositories.fileupload

import com.github.code.gambit.data.model.FileMetaData
import com.github.code.gambit.helper.ServiceResult

interface FileUploadRepository {

    suspend fun getAllFileMetaData(): ServiceResult<List<FileMetaData>>

    suspend fun insertFileMetaData(fileMetaData: FileMetaData): ServiceResult<Unit>
}
