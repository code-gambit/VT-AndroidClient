package com.github.code.gambit.network.api

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Response<T>(
    @SerializedName("statusCode")
    @Expose
    var statusCode: Int,

    @SerializedName("body")
    @Expose
    var body: T
)
