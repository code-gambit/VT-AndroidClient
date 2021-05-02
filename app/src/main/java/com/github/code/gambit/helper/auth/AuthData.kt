package com.github.code.gambit.helper.auth

data class AuthData(
    var fullname: String,
    var email: String,
    var password: String,
    var thumbnail: String?,
    var confirmationCode: String?
)
