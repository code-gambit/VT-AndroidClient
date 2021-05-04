package com.github.code.gambit.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.amplifyframework.auth.AuthChannelEventName
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.InitializationStatus
import com.amplifyframework.hub.HubChannel
import com.github.code.gambit.PreferenceManager
import com.github.code.gambit.R
import com.github.code.gambit.databinding.ActivityMainBinding
import com.github.code.gambit.ui.fragment.HomeFragment
import com.github.code.gambit.utility.bottomNavHide
import com.github.code.gambit.utility.bottomNavShow
import com.github.code.gambit.utility.toggleVisibility
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityMainBinding
    private val binding get() = _binding

    @Inject
    lateinit var preferenceManager: PreferenceManager

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registerAmplifyCallback()
        val hostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container)
        if (hostFragment is NavHostFragment)
            navController = hostFragment.navController

        binding.addFileButton.setOnClickListener { binding.fileUploadContainer.toggleVisibility() }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.splashFragment -> {
                    binding.bottomNavContainer.bottomNavHide()
                }
                R.id.onBoardingFragment -> {
                    binding.bottomNavContainer.bottomNavHide()
                }
                R.id.homeFragment -> {
                    // info bottomNav is hiddent until data is loading in homeFragment
                    // binding.bottomNavContainer.bottomNavShow()
                }
                R.id.authFragment -> {
                    binding.bottomNavContainer.bottomNavHide()
                }
            }
        }

        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)
    }

    private fun registerAmplifyCallback() {
        Amplify.Hub.subscribe(HubChannel.AUTH) { event ->
            when (event.name) {
                InitializationStatus.SUCCEEDED.toString() ->
                    Log.i("AuthQuickstart", "Auth successfully initialized")
                InitializationStatus.FAILED.toString() ->
                    Log.i("AuthQuickstart", "Auth failed to succeed")
                else -> when (AuthChannelEventName.valueOf(event.name)) {
                    AuthChannelEventName.SIGNED_IN ->
                        Log.i("AuthQuickstart", "Auth just became signed in")
                    AuthChannelEventName.SIGNED_OUT -> {
                        preferenceManager.revokeAuthentication()
                        Log.i("AuthQuickstart", "Auth just became signed out")
                    }
                    AuthChannelEventName.SESSION_EXPIRED -> {
                        preferenceManager.revokeAuthentication()
                        Log.i("AuthQuickstart", "Auth session just expired")
                    }
                    else ->
                        Log.w("AuthQuickstart", "Unhandled Auth Event: ${event.name}")
                }
            }
        }
    }

    override fun onBackPressed() {
        val hostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container)
        if (hostFragment is NavHostFragment) {
            when (val fragment = hostFragment.childFragmentManager.fragments.first()) {
                is HomeFragment -> {
                    when {
                        fragment.isFilterEnable() -> {
                            fragment.closeFilter()
                        }
                        fragment.isSearchEnable() -> {
                            fragment.closeSearch()
                        }
                        else -> {
                            finish()
                        }
                    }
                }
                else -> super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }
    }

    fun animateBottomNav(offset: Float) {
        if (this::_binding.isInitialized && offset.isFinite()) {
            binding.bottomLayout.animate().alpha(offset).scaleX(offset).scaleY(offset)
                .setDuration(0).start()
        }
    }

    fun showBottomNav() = binding.bottomNavContainer.bottomNavShow()

    fun hideBottomNav() = binding.bottomNavContainer.bottomNavHide()
}
