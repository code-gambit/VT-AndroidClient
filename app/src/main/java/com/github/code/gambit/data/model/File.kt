package com.github.code.gambit.data.model

data class File(
    var id: String,
    var name: String,
    var hash: String,
    var type: String,
    var timestamp: String,
    var size: String,
    var extension: String
)
