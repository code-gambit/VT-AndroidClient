package com.github.code.gambit.model

data class User(
    var name: String,
    var email: String,
    var type: String,
    var thumbnail: String,
    var storageUsed: Int
)