package com.github.code.gambit.data.remote.interceptor

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

open class InternetCheck(@ApplicationContext val applicationContext: Context) {
    @SuppressLint("ServiceCast")
    private fun isConnected(): Boolean {
        val connectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetwork
        return networkInfo != null
    }

    fun hasActiveInternetConnection(): Boolean {
        if (isConnected()) {
            try {
                val urlc: HttpURLConnection =
                    URL("https://clients3.google.com/generate_204").openConnection() as HttpURLConnection
                urlc.setRequestProperty("User-Agent", "Android")
                urlc.setRequestProperty("Connection", "close")
                urlc.connectTimeout = 1500
                urlc.connect()
                return (urlc.responseCode == 204 && urlc.contentLength == 0)
            } catch (e: IOException) {
                Timber.e("Error checking internet connection")
            }
        } else {
            Timber.e("No network available!")
        }
        return false
    }
}
