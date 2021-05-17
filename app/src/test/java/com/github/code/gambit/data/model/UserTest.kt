package com.github.code.gambit.data.model

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class UserTest {

    private lateinit var user: User
    private lateinit var user1: User
    private lateinit var user2: User

    @Before
    fun setup() {
        user = User("5fed188b-4ea5-4136-afd8-2a19a7dee650", "Test user", "test@example.com", "default", 0)
        user1 = User("", "Test user", "test@example.com", "default", 0)
        user2 = User("5fed188b-4ea5-4136-afd8-2a19a7dee650", "Test user", "", "default", 0)
    }

    @Test
    fun `to string when passed to from string should return valid user`() {
        val str = user.toString()
        val str1 = user1.toString()
        val str2 = user2.toString()
        assertThat(User.fromString(str)).isEqualTo(user)
        assertThat(User.fromString(str1)).isEqualTo(user1)
        assertThat(User.fromString(str2)).isEqualTo(user2)
    }

    @Test
    fun `user1 when merged to user2 should return user`() {
        assertThat(User.merge(user1, user2)).isEqualTo(user)
    }
}
