package com.github.code.gambit.data.model

import java.lang.IndexOutOfBoundsException

class FileMetaData(val path: String, val name: String, val size: Int, var uuid: String? = null) {

    companion object {
        fun fromString(string: String): FileMetaData {
            val arr = string.split("\n")
            if (arr.size < 3) {
                throw throw IndexOutOfBoundsException("Invalid string")
            }
            if (arr.size == 4) {
                return FileMetaData(arr[0], arr[1], arr[2].toInt(), arr[3])
            }
            return FileMetaData(arr[0], arr[1], arr[2].toInt())
        }
    }

    override fun toString(): String {
        if (uuid != null) {
            return "$path\n$name\n$size\n$uuid"
        }
        return "$path\n$name\n$size"
    }
}
