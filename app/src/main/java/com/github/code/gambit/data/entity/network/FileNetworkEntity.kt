package com.github.code.gambit.data.entity.network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FileNetworkEntity(

    @SerializedName("PK")
    @Expose
    var pk: String = "",

    @SerializedName("SK")
    @Expose
    var sk: String = "",

    @SerializedName("hash")
    @Expose
    var hash: String,

    @SerializedName("LS1_SK")
    @Expose
    var ls1_sk: String,

    @SerializedName("size")
    @Expose
    var size: Int,

    @SerializedName("f_type")
    @Expose
    var type: String

) {
    companion object {
        fun fromString(str: String): FileNetworkEntity {
            val arr = str.split('\n')
            return FileNetworkEntity(arr[0], arr[1], arr[2], arr[3], arr[4].toInt(), arr[5])
        }
    }

    override fun toString(): String {
        return "$pk\n$sk\n$hash\n$ls1_sk\n$size\n$type"
    }
}
