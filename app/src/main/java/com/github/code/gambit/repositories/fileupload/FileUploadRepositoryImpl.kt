package com.github.code.gambit.repositories.fileupload

import com.github.code.gambit.data.local.CacheDataSource
import com.github.code.gambit.data.model.FileMetaData
import com.github.code.gambit.helper.ServiceResult
import timber.log.Timber
import java.lang.Exception

class FileUploadRepositoryImpl(
    private val cacheDataSource: CacheDataSource
) : FileUploadRepository {

    override suspend fun getAllFileMetaData(): ServiceResult<List<FileMetaData>> {
        return try {
            val data = cacheDataSource.getAllFileMetaData()
            ServiceResult.Success(data)
        } catch (err: Exception) {
            ServiceResult.Error(err)
        }
    }

    override suspend fun insertFileMetaData(fileMetaData: FileMetaData): ServiceResult<Unit> {
        return try {
            val res = cacheDataSource.insertFileMetaData(fileMetaData)
            Timber.tag("work").i("inserted $res")
            ServiceResult.Success(Unit)
        } catch (err: Exception) {
            ServiceResult.Error(err)
        }
    }
}
