package com.github.code.gambit.repositories

import com.github.code.gambit.data.local.CacheDataSource
import com.github.code.gambit.data.model.File
import com.github.code.gambit.data.model.Url
import com.github.code.gambit.data.remote.NetworkDataSource
import com.github.code.gambit.helper.ServiceResult
import com.github.code.gambit.utility.NoInternetException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception

class HomeRepositoryImpl
constructor(
    private val cacheDataSource: CacheDataSource,
    private val networkDataSource: NetworkDataSource
) : HomeRepository {

    override suspend fun getFiles(): Flow<ServiceResult<List<File>>> {
        return flow {
            val data: List<File>
            try {
                data = networkDataSource.getFiles()
                cacheDataSource.insertFiles(data)
            } catch (internet: NoInternetException) {
                emit(ServiceResult.Error(internet))
            } catch (exception: Exception) {
                emit(ServiceResult.Error(exception))
            } finally {
                val files = cacheDataSource.getFiles()
                emit(ServiceResult.Success(files))
            }
        }
    }

    override suspend fun generateUrl(file: File): Flow<ServiceResult<Url>> {
        val url = Url("", file.id, file.hash, "", true, 50)
        return flow {
            val data: Url
            try {
                data = networkDataSource.generateUrl(url)
                // cacheDataSource.insertUrls(listOf(data))
                emit(ServiceResult.Success(data))
            } catch (internet: NoInternetException) {
                emit(ServiceResult.Error(internet))
            } catch (exception: Exception) {
                emit(ServiceResult.Error(exception))
            }
        }
    }

    override suspend fun getUrls(fileId: String): Flow<ServiceResult<List<Url>>> {
        return flow {
            val data: List<Url>
            try {
                data = networkDataSource.getUrls(fileId)
                cacheDataSource.insertUrls(data)
            } catch (internet: NoInternetException) {
                emit(ServiceResult.Error(internet))
            } catch (exception: Exception) {
                emit(ServiceResult.Error(exception))
            } finally {
                val urls = cacheDataSource.getUrls(fileId)
                emit(ServiceResult.Success(urls))
            }
        }
    }
}
