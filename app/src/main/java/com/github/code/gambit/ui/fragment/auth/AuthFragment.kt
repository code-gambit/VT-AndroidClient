package com.github.code.gambit.ui.fragment.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.github.code.gambit.R
import com.github.code.gambit.databinding.FragmentAuthBinding
import com.github.code.gambit.ui.AuthFragmentAdapter
import com.google.android.material.tabs.TabLayoutMediator

class AuthFragment : Fragment(R.layout.fragment_auth) {

    private lateinit var _binding: FragmentAuthBinding
    private val binding get() = _binding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAuthBinding.bind(view)

        binding.fragmentContainer.adapter = AuthFragmentAdapter.getInstance(activity?.supportFragmentManager!!, lifecycle)

        TabLayoutMediator(binding.tabLayout, binding.fragmentContainer) { tab, pos ->
            (
                when (pos) {
                    1 -> tab.text = "Login"
                    else -> tab.text = "SignUp"
                }
                )
        }.attach()
    }
}
