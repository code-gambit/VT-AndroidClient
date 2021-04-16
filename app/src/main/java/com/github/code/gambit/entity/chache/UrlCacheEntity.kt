package com.github.code.gambit.entity.chache

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "urls")
class UrlCacheEntity(

    @ColumnInfo(name = "id")
    var id: String,

    @ColumnInfo(name = "file")
    var file: String,

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "timestamp")
    var timestamp: String,

    @ColumnInfo(name = "visible")
    var visible: Boolean

)