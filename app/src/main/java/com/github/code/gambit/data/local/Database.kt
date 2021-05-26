package com.github.code.gambit.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.code.gambit.data.entity.chache.FileCacheEntity
import com.github.code.gambit.data.entity.chache.UrlCacheEntity

@Database(entities = [FileCacheEntity::class, UrlCacheEntity::class], version = 1)
abstract class Database : RoomDatabase() {

    abstract fun fileDao(): FileDao
    abstract fun urlDao(): UrlDao
}
