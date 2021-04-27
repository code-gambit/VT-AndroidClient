package com.github.code.gambit

import android.content.Context
import android.content.SharedPreferences
import com.github.code.gambit.data.model.User
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PreferenceManager
@Inject
constructor(@ApplicationContext var context: Context) {
    // todo rename the pref for detecting first launch
    private val launchState = "LAUNCH_PREF"
    private val authState = "IS-AUTHENTICATED"
    private val emailKey = "EMAIL"
    private val userIdKey = "USER-ID"
    private val userNameKey = "USER-NAME"
    private val tokenKey = "ID-TOKEN"

    private val prefName = "com.github.code.gambit.pref"
    private val pref: SharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
    private val editor get() = pref.edit()

    // return 0 -> launch for first time, 1 -> previously
    fun isFirstLaunch(): Boolean {
        return pref.getBoolean(launchState, true)
    }

    fun updateLaunchState() = editor.putBoolean(launchState, false).apply()

    fun updateIdToken(token: String?) = editor.putString(tokenKey, token).apply()

    fun getIdToken() = pref.getString(tokenKey, null)

    fun isAuthenticated() = pref.getBoolean(authState, false)

    fun setAuthenticated(value: Boolean) = editor.putBoolean(authState, value).apply()

    fun setUserEmail(email: String) = editor.putString(emailKey, email).apply()

    fun getUserEmail() = pref.getString(emailKey, null)

    fun setUserId(id: String) = editor.putString(userIdKey, id).apply()

    fun getUserId() = pref.getString(userIdKey, null)

    fun setUserName(name: String) = editor.putString(userNameKey, name).apply()

    fun getUserName() = pref.getString(userNameKey, null)

    fun setUser(user: User) {
        setUserEmail(user.email)
        setUserName(user.name)
    }

    fun revokeAuthentication() {
        updateIdToken(null)
        editor.putBoolean(authState, false)
    }
}
