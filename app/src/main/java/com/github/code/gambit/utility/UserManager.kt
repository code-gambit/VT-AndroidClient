package com.github.code.gambit.utility

import android.content.Context
import com.github.code.gambit.data.model.User
import dagger.hilt.android.qualifiers.ApplicationContext

class UserManager
constructor(@ApplicationContext context: Context) : PreferenceManager(context) {

    fun setAuthenticated(value: Boolean) = put(Key.AUTHSTATE, value)

    private fun setUserEmail(email: String) = put(Key.EMAIL, email)

    fun getUserEmail() = get<String>(Key.EMAIL)

    fun setUserId(id: String) = put(Key.USERID, id)

    fun getUserId() = get<String>(Key.USERID)

    private fun setUserName(name: String) = put(Key.USERNAME, name)

    fun getUserName() = get<String>(Key.USERNAME)

    fun setUser(user: User) {
        setUserEmail(user.email)
        setUserName(user.name)
    }

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
