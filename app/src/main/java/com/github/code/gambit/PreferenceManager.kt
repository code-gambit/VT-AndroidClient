package com.github.code.gambit

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferenceManager
@Inject
constructor(@ApplicationContext var context: Context) {
    // todo rename the pref for detecting first launch
    private val launchState = "LAUNCH_PREF"

    private val prefName = "com.github.code.gambit.pref"
    private val pref: SharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
    private val editor get() = pref.edit()

    // return 0 -> launch for first time, 1 -> previously
    fun isFirstLaunch(): Boolean {
        return pref.getBoolean(launchState, true)
    }

    fun updateLaunchState() = editor.putBoolean(launchState, false).apply()
}
