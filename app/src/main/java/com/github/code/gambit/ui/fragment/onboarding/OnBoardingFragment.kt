package com.github.code.gambit.ui.fragment.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.code.gambit.R
import com.github.code.gambit.databinding.FragmentOnBoardingBinding
import com.github.code.gambit.ui.fragment.onboarding.infoscreens.FirstOnBoardingFragment
import com.github.code.gambit.ui.fragment.onboarding.infoscreens.SecondOnBoardingFragment
import com.github.code.gambit.ui.fragment.onboarding.infoscreens.ThirdOnBoardingFragment
import com.github.code.gambit.utility.hide
import com.github.code.gambit.utility.show
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OnBoardingFragment : Fragment(R.layout.fragment_on_boarding) {

    private lateinit var _binding: FragmentOnBoardingBinding
    private val binding get() = _binding

    private val currentItem get() = binding.pageContainerView.currentItem

    @Inject
    lateinit var first: FirstOnBoardingFragment

    @Inject
    lateinit var second: SecondOnBoardingFragment

    @Inject
    lateinit var third: ThirdOnBoardingFragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentOnBoardingBinding.bind(view)
        binding.pageContainerView.adapter = OnBoardingFragmentAdapter(
            listOf(first, second, third),
            activity?.supportFragmentManager!!,
            lifecycle
        )

        binding.pageContainerView.isUserInputEnabled = false
        TabLayoutMediator(binding.tabLayout, binding.pageContainerView) { _, _ -> }.attach()

        binding.skipButton.setOnClickListener {
            navigateToAuth()
        }

        binding.backButton.setOnClickListener {
            when (currentItem) {
                1 -> binding.backButton.hide()
            }
            binding.pageContainerView.currentItem -= 1
        }

        binding.floatingActionButton.setOnClickListener {
            when (currentItem) {
                0 -> binding.backButton.show()
                2 -> navigateToAuth()
            }
            binding.pageContainerView.currentItem += 1
        }
    }

    private fun navigateToAuth() = findNavController().navigate(R.id.action_onBoardingFragment_to_authFragment)
}
