package com.github.code.gambit

import java.util.*

fun String.toBase64(): String {
    val byteArray = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        Base64.getEncoder().encode(this.toByteArray())
    } else {
        android.util.Base64.encode(this.toByteArray(), android.util.Base64.DEFAULT)
    }
    return String(byteArray)
}

fun String.fromBase64(): String {
    val byteArray = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        Base64.getDecoder().decode(this)
    } else {
        android.util.Base64.decode(this, android.util.Base64.DEFAULT)
    }
    return String(byteArray)
}