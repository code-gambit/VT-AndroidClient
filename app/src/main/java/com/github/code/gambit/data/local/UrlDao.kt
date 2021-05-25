package com.github.code.gambit.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.code.gambit.data.entity.chache.UrlCacheEntity

@Dao
interface UrlDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUrl(urlCacheEntity: UrlCacheEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUrls(urlCacheEntities: List<UrlCacheEntity>): List<Long>

    @Query("SELECT * FROM urls WHERE fileId = :fileId ORDER BY timestamp DESC")
    suspend fun getUrls(fileId: String): List<UrlCacheEntity>
}
