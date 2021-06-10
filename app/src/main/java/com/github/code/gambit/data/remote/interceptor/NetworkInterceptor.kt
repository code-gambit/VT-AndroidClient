package com.github.code.gambit.data.remote.interceptor

import android.content.Context
import com.github.code.gambit.R
import com.github.code.gambit.utility.NoInternetException
import okhttp3.Interceptor
import okhttp3.Response

open class NetworkInterceptor
constructor(val context: Context) : Interceptor, InternetCheck(context) {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!hasActiveInternetConnection()) {
            throw NoInternetException(context.getString(R.string.no_internet_message))
        }
        return chain.proceed(chain.request())
    }
}
