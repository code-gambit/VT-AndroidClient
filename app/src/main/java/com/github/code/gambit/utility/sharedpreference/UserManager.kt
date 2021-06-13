package com.github.code.gambit.utility.sharedpreference

import android.content.Context
import com.github.code.gambit.data.model.User
import dagger.hilt.android.qualifiers.ApplicationContext

class UserManager
constructor(@ApplicationContext context: Context) : PreferenceManager(context) {

    fun getUserId() = get<User>(Key.USER).id

    fun setUserName(name: String) {
        val user = getUser()
        user.name = name
        put(Key.USER, user)
    }

    fun getUserName() = getUser().name

    fun mergeUser(user: User): User {
        val localUser = getUser()
        put(Key.USER, User.merge(localUser, user))
        return getUser()
    }

    fun updateUser(user: User) {
        put(Key.USER, user)
    }

    fun setUser(user: User, token: String) {
        put(Key.USER, user)
        updateIdToken(token)
        setAuthenticated(true)
        updateLaunchState()
    }

    fun getUser() = get<User>(Key.USER)

    fun isFirstLaunch() = get(Key.LAUNCHSTATE, true)

    private fun updateLaunchState() = put(Key.LAUNCHSTATE, false)

    private fun updateIdToken(token: String?) = put(Key.TOKEN, token)

    fun getIdToken() = get<String>(Key.TOKEN)

    private fun setAuthenticated(value: Boolean) = put(Key.AUTHSTATE, value)

    fun isAuthenticated() = get<Boolean>(Key.AUTHSTATE)

    fun revokeAuthentication() {
        updateIdToken(null)
        setAuthenticated(false)
    }
}
