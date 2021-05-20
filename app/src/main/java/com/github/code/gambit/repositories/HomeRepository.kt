package com.github.code.gambit.repositories

import com.github.code.gambit.data.model.File
import com.github.code.gambit.helper.ServiceResult
import kotlinx.coroutines.flow.Flow

interface HomeRepository {

    suspend fun getFiles(): Flow<ServiceResult<List<File>>>
}
