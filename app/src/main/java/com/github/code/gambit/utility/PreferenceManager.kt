package com.github.code.gambit.utility

import android.content.Context
import android.content.SharedPreferences
import java.lang.IllegalStateException

abstract class PreferenceManager(var context: Context) {

    private val prefName = "com.github.code.gambit.pref"
    val pref: SharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor get() = pref.edit()

    fun <T> put(key: Key, value: T) {
        when (value) {
            is String -> {
                editor.putString(key.value, value).apply()
            }
            is Boolean -> {
                editor.putBoolean(key.value, value).apply()
            }
        }
    }

    inline fun <reified T> get(key: Key): T {
        return when (T::class) {
            String::class -> {
                pref.getString(key.value, "") as T
            }
            Boolean::class -> {
                pref.getBoolean(key.value, false) as T
            }
            Int::class -> {
                pref.getInt(key.value, -1) as T
            }
            else -> throw IllegalStateException("Only supports String, Boolean and Int")
        }
    }
}

enum class Key(val value: String) {
    LAUNCHSTATE("IS-FIRST-LAUNCH"),
    AUTHSTATE("IS-AUTHENTICATED"),
    EMAIL("EMAIL"),
    USERID("USER-ID"),
    USERNAME("USER-NAME"),
    TOKEN("ID-TOKEN")
}
