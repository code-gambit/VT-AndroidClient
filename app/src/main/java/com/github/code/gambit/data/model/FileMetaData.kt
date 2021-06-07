package com.github.code.gambit.data.model

import java.lang.IndexOutOfBoundsException

class FileMetaData(val path: String, val name: String, val size: Int, val timestamp: Long, var uuid: String) {

    companion object {
        fun fromString(string: String): FileMetaData {
            val arr = string.split("\n")
            if (arr.size < 5) {
                throw throw IndexOutOfBoundsException("Invalid string")
            }
            return FileMetaData(arr[0], arr[1], arr[2].toInt(), arr[3].toLong(), arr[4])
        }
    }

    override fun toString(): String {
        return "$path\n$name\n$size\n$timestamp\n$uuid"
    }
}
