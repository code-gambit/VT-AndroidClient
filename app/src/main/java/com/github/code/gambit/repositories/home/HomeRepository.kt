package com.github.code.gambit.repositories.home

import com.github.code.gambit.data.model.File
import com.github.code.gambit.data.model.Url
import com.github.code.gambit.helper.ServiceResult
import com.github.code.gambit.ui.fragment.home.filtercomponent.Filter
import kotlinx.coroutines.flow.Flow

interface HomeRepository {

    suspend fun getFiles(clearCache: Boolean = false): Flow<ServiceResult<List<File>>>
    suspend fun searchFile(searchString: String): Flow<ServiceResult<List<File>>>
    suspend fun searchFileByFilter(filter: Filter): Flow<ServiceResult<List<File>>>
    suspend fun deleteFile(file: File): Flow<ServiceResult<Boolean>>
    suspend fun generateUrl(file: File): Flow<ServiceResult<Url>>
    suspend fun getUrls(fileId: String): Flow<ServiceResult<List<Url>>>
    suspend fun updateUrl(url: Url): Flow<ServiceResult<Url>>
    suspend fun deleteUrl(url: Url): Flow<ServiceResult<Url>>
}
