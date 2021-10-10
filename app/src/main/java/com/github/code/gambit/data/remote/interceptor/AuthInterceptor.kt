package com.github.code.gambit.data.remote.interceptor

import android.content.Context
import com.amazonaws.services.cognitoidentityprovider.model.NotAuthorizedException
import com.github.code.gambit.R
import com.github.code.gambit.helper.ServiceResult
import com.github.code.gambit.utility.NoInternetException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

/**
 * Use as auth interceptor for validating auth request and returning response
 * @author Danish Jamal [https://github.com/danishjamal104]
 * @param [context] The context in which interceptor is to be used
 */
class AuthInterceptor
constructor(val context: Context) : InternetCheck(context) {

    /**
     * Checks the internet connectivity and calls the requested function
     * @param [T] generic type to be returned by function
     * @param [call] lambda function called when intern is available
     */
    private suspend fun <T> authRequest(call: suspend () -> T): T {
        val a = withContext(Dispatchers.IO) { hasActiveInternetConnection() }
        if (!a) {
            throw NoInternetException("Not connected to internet")
        }
        return call.invoke()
    }

    /**
     * Checks for the credential validation and internet availability using [authRequest]
     *
     * @param [T] generic type returned by auth request
     * @param [B] generic type to be returned by [ServiceResult]
     */
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
