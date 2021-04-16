package com.github.code.gambit.entity.network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FileNetworkEntity(

    @SerializedName("PK")
    var pk: String,

    @SerializedName("SK")
    @Expose
    var sk: String,

    @SerializedName("LS1_SK")
    @Expose
    var ls1_sk: String,

    @SerializedName("size")
    @Expose
    var size: Int,

    @SerializedName("type")
    @Expose
    var type: String

)