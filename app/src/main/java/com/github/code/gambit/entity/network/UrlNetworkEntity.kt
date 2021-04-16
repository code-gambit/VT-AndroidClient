package com.github.code.gambit.entity.network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UrlNetworkEntity(

    @SerializedName("PK")
    var pk: String,

    @SerializedName("SK")
    @Expose
    var sk: String,

    @SerializedName("GS1_PK")
    @Expose
    var gs1_pk: String,

    @SerializedName("visible")
    @Expose
    var visible: Boolean
)