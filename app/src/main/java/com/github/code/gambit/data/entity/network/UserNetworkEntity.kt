package com.github.code.gambit.data.entity.network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UserNetworkEntity(

    @SerializedName("PK")
    @Expose
    var pk: String,

    @SerializedName("SK")
    @Expose
    var sk: String,

    @SerializedName("type")
    @Expose
    var type: String,

    @SerializedName("storage_used")
    @Expose
    var storageUsed: Int,

)
