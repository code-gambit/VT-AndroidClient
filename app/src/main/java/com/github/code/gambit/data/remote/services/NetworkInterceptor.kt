package com.github.code.gambit.data.remote.services

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import com.github.code.gambit.utility.NoInternetException
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class NetworkInterceptor
constructor(@ApplicationContext context: Context) : Interceptor {

    private val applicationContext = context.applicationContext

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!hasActiveInternetConnection()) {
            throw NoInternetException("Not connected to internet")
        }
        return chain.proceed(chain.request())
    }

    @SuppressLint("ServiceCast")
    private fun isConnected(): Boolean {
        val connectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network_info = connectivityManager.activeNetwork
        return network_info != null
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
