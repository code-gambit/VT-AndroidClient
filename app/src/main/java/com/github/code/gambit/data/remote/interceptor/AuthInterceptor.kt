package com.github.code.gambit.data.remote.interceptor

import android.content.Context
import com.amazonaws.services.cognitoidentityprovider.model.NotAuthorizedException
import com.github.code.gambit.R
import com.github.code.gambit.helper.ServiceResult
import com.github.code.gambit.utility.NoInternetException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

class AuthInterceptor
constructor(val context: Context) : InternetCheck(context) {

    private suspend fun <T> authRequest(call: suspend () -> T): T {
        val a = withContext(Dispatchers.IO) { hasActiveInternetConnection() }
        if (!a) {
            throw NoInternetException("Not connected to internet")
        }
        return call.invoke()
    }

    suspend fun <T, B> authRequest(call: suspend () -> T, result: (res: T) -> ServiceResult<B>): ServiceResult<B> {
        return try {
            val response = authRequest { call.invoke() }
            result(response)
        } catch (exception: Exception) {
            if (exception.cause is NotAuthorizedException) {
                ServiceResult.Error(Exception(context.getString(R.string.incorrect_username_or_password)))
            } else {
                ServiceResult.Error(exception)
            }
        }
    }
}
