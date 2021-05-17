package com.github.code.gambit.data.model

import java.lang.IndexOutOfBoundsException

data class User(
    var id: String,
    var name: String,
    var email: String,
    var type: String,
    var storageUsed: Int
) {

    companion object {
        fun fromString(data: String): User {
            val arr = data.split("\n")
            if (arr.size < 5) {
                throw IndexOutOfBoundsException("Invalid data string")
            }
            return User(arr[0], arr[1], arr[2], arr[3], arr[4].toInt())
        }

        fun merge(user1: User, user2: User): User {
            return User("", "", "", "", 0).let {
                for (i in 1..5) {
                    when (i) {
                        1 -> it.id = if (user1.id != "") user1.id else user2.id
                        2 -> it.name = if (user1.name != "") user1.name else user2.name
                        3 -> it.email = if (user1.email != "") user1.email else user2.email
                        4 -> it.type = if (user1.type != "") user1.type else user2.type
                        5 -> it.storageUsed = if (user1.storageUsed != 0) user1.storageUsed else user2.storageUsed
                    }
                }
                it
            }
        }
    }

    override fun toString(): String {
        return "$id\n$name\n$email\n$type\n$storageUsed"
    }
}
