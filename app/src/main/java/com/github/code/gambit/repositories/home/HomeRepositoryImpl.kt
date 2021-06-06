package com.github.code.gambit.repositories.home

import com.github.code.gambit.data.local.CacheDataSource
import com.github.code.gambit.data.model.File
import com.github.code.gambit.data.model.Url
import com.github.code.gambit.data.remote.NetworkDataSource
import com.github.code.gambit.helper.ServiceResult
import com.github.code.gambit.ui.fragment.home.filtercomponent.Filter
import com.github.code.gambit.utility.AppConstant
import com.github.code.gambit.utility.NoInternetException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Locale

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
                val files = cacheDataSource.getFiles()
                emit(ServiceResult.Success(files))
            } catch (internet: NoInternetException) {
                emit(ServiceResult.Error(internet))
                val files = cacheDataSource.getFiles()
                emit(ServiceResult.Success(files))
            } catch (exception: Exception) {
                emit(ServiceResult.Error(exception))
            }
        }
    }

    override suspend fun searchFileByFilter(filter: Filter): Flow<ServiceResult<List<File>>> {
        val sdf = SimpleDateFormat(AppConstant.FILTER_DATE_TEMPLATE, Locale.getDefault())
        val start = sdf.format(filter.start)
        val end = sdf.format(filter.end)
        return flow {
            try {
                val data = networkDataSource.filterFiles(start, end)
                emit(ServiceResult.Success(data))
            } catch (internet: NoInternetException) {
                emit(ServiceResult.Error(internet))
            } catch (exception: Exception) {
                emit(ServiceResult.Error(exception))
            }
        }
    }

    override suspend fun searchFile(searchString: String): Flow<ServiceResult<List<File>>> {
        return flow {
            try {
                val data = networkDataSource.searchFiles(searchString)
                emit(ServiceResult.Success(data))
            } catch (internet: NoInternetException) {
                emit(ServiceResult.Error(internet))
            } catch (exception: Exception) {
                emit(ServiceResult.Error(exception))
            }
        }
    }

    override suspend fun deleteFile(file: File): Flow<ServiceResult<Boolean>> {
        return flow {
            try {
                val success = networkDataSource.deleteFile(file.id)
                if (success) {
                    val dels = cacheDataSource.deleteFile(file.id)
                    if (dels == 1) {
                        emit(ServiceResult.Success(true))
                    } else {
                        emit(ServiceResult.Success(false))
                    }
                } else {
                    emit(ServiceResult.Success(false))
                }
            } catch (internet: NoInternetException) {
                emit(ServiceResult.Error(internet))
            } catch (exception: Exception) {
                emit(ServiceResult.Error(exception))
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
