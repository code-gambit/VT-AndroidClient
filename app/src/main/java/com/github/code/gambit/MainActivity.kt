package com.github.code.gambit

import android.os.Bundle
import android.util.Log
import com.amplifyframework.core.Amplify
import dagger.hilt.android.AndroidEntryPoint
import androidx.appcompat.app.AppCompatActivity

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // info: this is a placeholder code for testing will be removed later
        Amplify.Auth.fetchAuthSession(
            { Log.i("AmplifyQuickstart", "Auth session = $it") },
            { Log.i("AmplifyQuickstart", "Failed to fetch auth session"+it.localizedMessage) }
        )

    }
}
