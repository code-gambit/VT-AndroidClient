package com.github.code.gambit.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.code.gambit.data.entity.chache.FileCacheEntity

@Dao
interface FileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFiles(fileCacheEntity: FileCacheEntity): Long

    @Query("SELECT * FROM files ORDER BY timestamp DESC")
    suspend fun getFiles(): List<FileCacheEntity>

    @Query("SELECT * FROM files WHERE id = :id")
    suspend fun getFile(id: String): List<FileCacheEntity>

    @Query("DELETE FROM files WHERE id = :id")
    suspend fun deleteFile(id: String): Int
}
