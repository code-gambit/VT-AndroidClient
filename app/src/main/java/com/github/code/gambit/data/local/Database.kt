package com.github.code.gambit.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.code.gambit.data.entity.chache.FileCacheEntity

@Database(entities = [FileCacheEntity::class], version = 1)
abstract class Database : RoomDatabase() {

    abstract fun fileDao(): FileDao
}
