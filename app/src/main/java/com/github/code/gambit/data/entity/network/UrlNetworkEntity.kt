package com.github.code.gambit.data.entity.network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UrlNetworkEntity(

    @SerializedName("PK")
    @Expose
    var pk: String = "",

    @SerializedName("SK")
    @Expose
    var sk: String = "",

    @SerializedName("GS1_PK")
    @Expose
    var gs1_pk: String = "",

    @SerializedName("hash")
    @Expose
    var hash: String,

    @SerializedName("visible")
    @Expose
    var visible: String,

    @SerializedName("clicks_left")
    @Expose
    var clicks_left: Int
)
