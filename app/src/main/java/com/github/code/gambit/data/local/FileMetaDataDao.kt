package com.github.code.gambit.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.code.gambit.data.entity.chache.FileMetaDataCacheEntity

@Dao
interface FileMetaDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFileMetaData(fileMetaDataCacheEntity: FileMetaDataCacheEntity): Long

    @Query("SELECT * FROM fileMetaData ORDER BY timestamp DESC")
    suspend fun getAllFileMetaData(): List<FileMetaDataCacheEntity>

    @Query("DELETE FROM fileMetaData WHERE uuid = :uuid")
    suspend fun deleteFileMetaData(uuid: String)

    @Query("DELETE FROM fileMetaData")
    suspend fun deleteAll()
}
