package com.github.code.gambit.data.entity.chache

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fileMetaData")
class FileMetaDataCacheEntity(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "uuid")
    val uuid: String,

    @ColumnInfo(name = "timestamp")
    var timestamp: Long,

    @ColumnInfo(name = "path")
    val path: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "size")
    val size: Int

)
