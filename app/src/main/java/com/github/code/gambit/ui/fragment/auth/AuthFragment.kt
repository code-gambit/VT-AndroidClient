package com.github.code.gambit.ui.fragment.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.code.gambit.R
import com.github.code.gambit.data.model.User
import com.github.code.gambit.databinding.FragmentAuthBinding
import com.github.code.gambit.helper.auth.AuthData
import com.github.code.gambit.helper.auth.AuthState
import com.github.code.gambit.ui.fragment.auth.confirmationcomponent.ConfirmationComponent
import com.github.code.gambit.utility.extention.exitFullscreen
import com.github.code.gambit.utility.extention.longToast
import com.github.code.gambit.utility.extention.snackbar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthFragment : Fragment(R.layout.fragment_auth) {

    private lateinit var _binding: FragmentAuthBinding
    private val binding get() = _binding

    val currentPage get() = binding.fragmentContainer.currentItem

    private val viewModel: AuthViewModel by viewModels()

    private lateinit var authData: AuthData

    private lateinit var confirmationComponent: ConfirmationComponent

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAuthBinding.bind(view)
        activity?.window?.exitFullscreen()

        binding.fragmentContainer.adapter = AuthFragmentAdapter.getInstance(
            activity?.supportFragmentManager!!,
            lifecycle
        )

        TabLayoutMediator(binding.tabLayout, binding.fragmentContainer) { tab, pos ->
            (
                when (pos) {
                    1 -> tab.text = "Login"
                    else -> tab.text = "SignUp"
                }
                )
        }.attach()

        confirmationComponent = ConfirmationComponent.bind(requireContext())

        confirmationComponent.getOtp().observe(viewLifecycleOwner) {
            it?.let { confirmSignUp(it) }
        }

        confirmationComponent.setResendCallback { email ->
            viewModel.setEvent(AuthEvent.ResendCode(email))
        }

        confirmationComponent.setOnCancelCallback { enableInteraction() }

        binding.buttonSubmit.setOnClickListener {
            disableInteraction()
            val fg = (binding.fragmentContainer.adapter as AuthFragmentAdapter).getFragment(
                currentPage
            )
            if (currentPage == 0) {
                val data = (fg as SignUpFragment).validate()
                if (data != null) {
                    signUp(data)
                } else {
                    enableInteraction()
                }
            } else {
                val data = (fg as LoginFragment).validate()
                if (data != null) {
                    logIn(data)
                } else {
                    enableInteraction()
                }
            }
        }

        viewModel.authState.observe(
            viewLifecycleOwner,
            {
                when (it) {
                    is AuthState.Loading -> {
                        binding.progressBar.show()
                    }
                    is AuthState.Confirmation -> {
                        binding.progressBar.hide()
                        confirmationComponent.show(authData.email)
                    }
                    is AuthState.Success<User> -> {
                        binding.progressBar.hide()
                        binding.root.snackbar("Welcome ${it.data.name}")
                        if (this::confirmationComponent.isInitialized && confirmationComponent.isShowing()) {
                            confirmationComponent.exit { navigateToHome() }
                        } else {
                            navigateToHome()
                        }
                    }
                    is AuthState.Error -> {
                        enableInteraction()
                        binding.progressBar.hide()
                        if (this::confirmationComponent.isInitialized && confirmationComponent.isShowing()) {
                            confirmationComponent.exit()
                        }
                        binding.root.snackbar(it.reason)
                    }
                    is AuthState.ResendStatus -> {
                        confirmationComponent.onResendResult(it.success)
                        if (it.success) {
                            longToast("Code send on your email")
                        }
                    }
                    AuthState.CodeMissMatch -> {
                        confirmationComponent.showError("Invalid code")
                    }
                }
            }
        )
    }

    private fun signUp(authData: AuthData) {
        this.authData = authData
        viewModel.setEvent(AuthEvent.SignUpEvent(authData))
    }

    private fun logIn(authData: AuthData) {
        this.authData = authData
        viewModel.setEvent(AuthEvent.LoginEvent(authData))
    }

    private fun confirmSignUp(code: String) {
        authData.confirmationCode = code
        viewModel.setEvent(AuthEvent.ConfirmationEvent(authData))
    }

    private fun navigateToHome() =
        findNavController().navigate(R.id.action_authFragment_to_homeFragment)

    private fun disableInteraction() {
        binding.buttonSubmit.isEnabled = false
    }

    private fun enableInteraction() {
        binding.buttonSubmit.isEnabled = true
    }

    fun setPage(page: Int) {
        if (page == 0 || page == 1) {
            binding.tabLayout.getTabAt(page)?.select()
        }
    }
}
