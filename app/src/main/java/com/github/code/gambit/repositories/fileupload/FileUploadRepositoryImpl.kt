package com.github.code.gambit.repositories.fileupload

import com.github.code.gambit.data.local.CacheDataSource
import com.github.code.gambit.data.model.FileMetaData
import com.github.code.gambit.helper.ServiceResult
import java.lang.Exception
import java.util.UUID

class FileUploadRepositoryImpl(
    private val cacheDataSource: CacheDataSource
) : FileUploadRepository {

    override suspend fun getFileMetaData(uuid: String): ServiceResult<FileMetaData> {
        return try {
            val res = cacheDataSource.getFileMetaData(uuid)
            ServiceResult.Success(res)
        } catch (err: Exception) {
            ServiceResult.Error(err)
        }
    }

    override suspend fun getFilesMetaData(uuids: List<String>): ServiceResult<List<FileMetaData>> {
        return try {
            val res = mutableListOf<FileMetaData>()
            uuids.forEach {
                val dt = cacheDataSource.getFileMetaData(it)
                res.add(dt)
            }
            ServiceResult.Success(res)
        } catch (err: Exception) {
            ServiceResult.Error(err)
        }
    }

    override suspend fun insertFileUploadInfo(
        fileMetaData: FileMetaData,
        uuid: UUID
    ): ServiceResult<Unit> {
        return try {
            cacheDataSource.insertFileMetaData(fileMetaData, uuid.toString())
            ServiceResult.Success(Unit)
        } catch (err: Exception) {
            ServiceResult.Error(err)
        }
    }

    override suspend fun deleteFileMetaData(uuid: UUID): ServiceResult<Unit> {
        return try {
            cacheDataSource.deleteFileMetaData(uuid.toString())
            ServiceResult.Success(Unit)
        } catch (err: Exception) {
            ServiceResult.Error(err)
        }
    }
}
