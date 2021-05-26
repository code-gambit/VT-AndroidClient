package com.github.code.gambit.data.entity.chache

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "urls")
class UrlCacheEntity(

    @ColumnInfo(name = "id")
    var id: String,

    @ColumnInfo(name = "fileId")
    var fileId: String,

    @ColumnInfo(name = "hash")
    var hash: String,

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "timestamp")
    var timestamp: String,

    @ColumnInfo(name = "visible")
    var visible: Boolean,

    @ColumnInfo(name = "clicks_left")
    val clicksLeft: Int

)
