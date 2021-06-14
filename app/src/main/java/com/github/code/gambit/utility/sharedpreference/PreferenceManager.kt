package com.github.code.gambit.utility.sharedpreference

import android.content.Context
import android.content.SharedPreferences
import com.github.code.gambit.data.model.User
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
            else -> editor.putString(key.value, value.toString()).apply()
        }
    }

    inline fun <reified T> get(key: Key, default: T? = null): T {
        return when (T::class) {
            String::class -> {
                val a = pref.getString(key.value, default?.let { it as String } ?: "") as T
                a
            }
            Boolean::class -> {
                pref.getBoolean(key.value, default?.let { it as Boolean } ?: false) as T
            }
            Int::class -> {
                pref.getInt(key.value, default?.let { it as Int } ?: -1) as T
            }
            User::class -> {
                val st = pref.getString(key.value, default?.let { it as String } ?: "")!!
                User.fromString(st) as T
            }
            else -> throw IllegalStateException("Only supports String, Boolean and Int")
        }
    }
}

enum class Key(val value: String) {
    LAUNCHSTATE("IS-FIRST-LAUNCH"),
    AUTHSTATE("IS-AUTHENTICATED"),
    HOMEONBOARDINGSTATE("IS-HOME-ONBOARDED"),
    USER("USER"),
    TOKEN("ID-TOKEN"),
    FILE_LEK("FILE-LAST-EVALUATED-KEY"),
    FILTER_FILE_LEK("FILE-FILTER-LAST-EVALUATED-KEY"),
    SEARCH_FILE_LEK("FILE-SEARCH-LAST-EVALUATED-KEY"),
    URL_LEK("URL-LAST-EVALUATED-KEY")
}
