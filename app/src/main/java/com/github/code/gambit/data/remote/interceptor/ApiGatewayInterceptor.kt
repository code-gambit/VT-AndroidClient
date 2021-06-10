package com.github.code.gambit.data.remote.interceptor

import com.github.code.gambit.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class ApiGatewayInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("x-api-key", BuildConfig.API_KEY)
            .build()
        return chain.proceed(request)
    }
}
