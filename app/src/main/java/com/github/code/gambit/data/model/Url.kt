package com.github.code.gambit.data.model

data class Url(
    var id: String,
    var fileId: String,
    var hash: String,
    var timestamp: String,
    var visible: Boolean = true,
    var clicksLeft: Int = 50
)
