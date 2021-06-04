package com.github.code.gambit.data.remote.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Response<T>(
    @SerializedName("statusCode")
    @Expose
    var statusCode: Int,

    @SerializedName("error")
    @Expose
    var error: String?,

    @SerializedName("body")
    @Expose
    var body: T?
)
