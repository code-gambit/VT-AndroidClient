package com.github.code.gambit.utility.sharedpreference

import android.content.Context
import com.github.code.gambit.data.model.User
import dagger.hilt.android.qualifiers.ApplicationContext

class UserManager
constructor(@ApplicationContext context: Context) : PreferenceManager(context) {

    fun putFileLastEvaluatedKey(lastEvaluatedKey: String?) = put(Key.FILE_LEK, lastEvaluatedKey)

    fun getFileLastEvaluatedKey() = get<String>(Key.FILE_LEK)

    fun setAuthenticated(value: Boolean) = put(Key.AUTHSTATE, value)

    private fun setUserEmail(email: String) = put(Key.EMAIL, email)

    fun getUserEmail() = get<String>(Key.EMAIL)

    fun setUserId(id: String) {
        val user = getUser()
        user.id = id
        put(Key.USER, user)
    }

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

    fun isFirstLaunch() = get<Boolean>(Key.LAUNCHSTATE)

    fun updateLaunchState() = put(Key.LAUNCHSTATE, false)

    fun updateIdToken(token: String?) = put(Key.TOKEN, token)

    fun getIdToken() = get<String>(Key.TOKEN)

    fun isAuthenticated() = get<Boolean>(Key.AUTHSTATE)

    fun revokeAuthentication() {
        updateIdToken(null)
        put(Key.AUTHSTATE, false)
    }
}
