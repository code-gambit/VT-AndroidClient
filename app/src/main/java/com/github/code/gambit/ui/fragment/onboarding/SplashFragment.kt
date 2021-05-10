package com.github.code.gambit.ui.fragment.onboarding

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.code.gambit.R
import com.github.code.gambit.databinding.FragmentSplashBinding
import com.github.code.gambit.utility.UserManager
import com.github.code.gambit.utility.anim
import com.github.code.gambit.utility.fullscreen
import com.github.code.gambit.utility.hide
import com.github.code.gambit.utility.show
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.fragment_splash) {

    private lateinit var _binding: FragmentSplashBinding
    private val binding get() = _binding

    private lateinit var textViews: List<View>

    @Inject
    lateinit var userManager: UserManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSplashBinding.bind(view)
        activity?.window?.fullscreen()

        textViews = listOf(binding.welcomeToText, binding.theWorldText, binding.whereText, binding.securityText, binding.isSharingText)

        binding.appIcon.animate()
            .scaleY(1f)
            .scaleX(1f)
            .alpha(1f)
            .setDuration(1000)
            .setInterpolator(DecelerateInterpolator()).start()

        Handler().postDelayed(
            {
                next()
            },
            1500
        )
    }

    private fun next() {
        if (userManager.isFirstLaunch()) {
            showWelcomeUI()
        } else if (!userManager.isAuthenticated()) {
            navigateToAuth()
        } else {
            navigateToHome()
        }
    }

    private fun showWelcomeUI() {
        binding.appIcon.hide()
        binding.multiViewContainer.setBackgroundResource(R.drawable.bg_welcome)
        binding.multiViewContainer.alpha = 0f
        binding.welcomeTextContainer.show()
        startAnimationSequence(textViews)
        startAnimationSequence(listOf(binding.multiViewContainer))
        val ts = 2000L
        Handler().postDelayed(
            {
                binding.nextButton.animate().alpha(1f).setDuration(2000).setInterpolator(AnticipateOvershootInterpolator()).start()
                binding.nextButton.setOnClickListener {
                    navigateToOnBoarding()
                }
            },
            ts
        )
    }

    private fun navigateToOnBoarding() {
        navigate(R.id.action_splashFragment_to_onBoardingFragment)
    }

    private fun navigateToHome() {
        navigate(R.id.action_splashFragment_to_homeFragment)
    }

    private fun navigateToAuth() {
        navigate(R.id.action_splashFragment_to_authFragment)
    }

    private fun navigate(action: Int) {
        findNavController().navigate(action)
    }

    private fun startAnimationSequence(views: List<View>) {
        var i = 0L
        views.forEach {
            it.anim(i)
            i += 100
        }
    }
}
