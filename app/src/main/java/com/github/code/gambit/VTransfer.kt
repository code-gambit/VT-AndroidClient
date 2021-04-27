package com.github.code.gambit

import android.app.Application
import android.util.Log
import com.amplifyframework.AmplifyException
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VTransfer : Application() {

    override fun onCreate() {
        super.onCreate()

        try {
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.configure(this)
            Log.i("MyAmplifyApp", "Initialized Amplify")
            // val user = com.amplifyframework.kotlin.core.Amplify.Auth.getCurrentUser()
            // Log.i("MyAmplifyApp", "${user}")
        } catch (error: AmplifyException) {
            Log.i("MyAmplifyApp", "Could not initialize Amplify", error)
        }
    }
}
