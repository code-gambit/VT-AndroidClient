package com.github.code.gambit.entity.chache

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "files")
class FileCacheEntity(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    var id: String,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "type")
    var type: String,

    @ColumnInfo(name = "timestamp")
    var timestamp: String,

    @ColumnInfo(name = "size")
    var size: String,

    @ColumnInfo(name = "extension")
    var extension: String
)