package com.github.code.gambit.data.remote.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ListResponse<T>(
    @SerializedName("statusCode")
    @Expose
    var statusCode: Int,

    @SerializedName("error")
    @Expose
    var error: String?,

    @SerializedName("body")
    @Expose
    var body: BodyTemplate<T>?
)

class BodyTemplate<T>(

    @SerializedName("items")
    @Expose
    var items: List<T>,

    @SerializedName("LastEvaluatedKey")
    @Expose
    var lastEvaluatedKey: String?
)
