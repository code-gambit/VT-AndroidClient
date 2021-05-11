package com.github.code.gambit.data.remote.responses

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ListResponse<T>(
    @SerializedName("statusCode")
    @Expose
    var statusCode: Int,

    @SerializedName("body")
    @Expose
    var body: BodyTemplate<T>
)

class BodyTemplate<T>(

    @SerializedName("items")
    @Expose
    var items: List<T>,

    @SerializedName("lastEvaluatedKey")
    @Expose
    var lastEvaluatedKey: String
)
