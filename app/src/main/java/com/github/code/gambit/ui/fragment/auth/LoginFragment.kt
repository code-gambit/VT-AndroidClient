package com.github.code.gambit.ui.fragment.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.code.gambit.R
import com.github.code.gambit.databinding.FragmentLoginBinding
import com.github.code.gambit.helper.auth.AuthData
import com.github.code.gambit.helper.auth.AuthState
import com.github.code.gambit.ui.fragment.auth.confirmationcomponent.ConfirmationComponent
import com.github.code.gambit.utility.extention.longToast
import com.github.code.gambit.utility.extention.snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var _binding: FragmentLoginBinding
    val binding get() = _binding

    private val viewModel: AuthViewModel by viewModels()

    private lateinit var confirmationComponent: ConfirmationComponent

    private var authData: AuthData? = null

    val username get() = binding.usernameInput.editText?.text.toString().trim()
    val password get() = binding.passwordInput.editText?.text.toString().trim()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)
        binding.linearProgress.hide()
        confirmationComponent = ConfirmationComponent.bind(requireContext())

        binding.forgotPasswordText.setOnClickListener {
            authData = validateFormForForgotPassword()
            authData?.let {
                viewModel.setEvent(AuthEvent.ForgotPassword(it.email))
            }
        }
        confirmationComponent.getOtp().observe(viewLifecycleOwner) {
            it?.let { otp ->
                authData?.let { data ->
                    viewModel.setEvent(AuthEvent.ResetForgotPassword(data.email, data.password, otp))
                }
            }
        }
        confirmationComponent.setResendCallback { email ->
            viewModel.setEvent(AuthEvent.ForgotPassword(email))
        }

        viewModel.authState.observe(viewLifecycleOwner) {
            when (it) {
                AuthState.CodeMissMatch -> confirmationComponent.showError("Invalid code!!")
                AuthState.Confirmation -> confirmationComponent.show(authData?.email ?: "")
                is AuthState.Error -> binding.root.snackbar(it.reason)
                AuthState.Loading -> binding.linearProgress.show()
                is AuthState.Success -> {
                    binding.root.snackbar("Welcome ${it.data.name}")
                    if (this::confirmationComponent.isInitialized && confirmationComponent.isShowing()) {
                        confirmationComponent.exit { findNavController().navigate(R.id.action_authFragment_to_homeFragment) }
                    }
                }
                is AuthState.ResendStatus -> {
                    if (it.success) {
                        longToast("Code send on your email")
                    } else {
                        longToast("Code send failed")
                    }
                }
            }
        }
    }

    private fun validateFormForForgotPassword(): AuthData? {
        var error = false
        if (username == "") {
            binding.usernameInput.error = "Enter User email here"
            error = true
        } else {
            if (binding.usernameInput.isErrorEnabled) {
                binding.usernameInput.isErrorEnabled = false
            }
        }
        if (password == "") {
            binding.passwordInput.error = "Enter new password"
            error = true
        } else {
            if (binding.passwordInput.isErrorEnabled) {
                binding.passwordInput.isErrorEnabled = false
            }
        }
        if (error) {
            return null
        }
        return AuthData("", username, password, null)
    }

    // validates the input fields
    fun validate(): AuthData? {
        var error = false
        if (username == "") {
            binding.usernameInput.error = "Username can't be empty"
            error = true
        } else {
            if (binding.usernameInput.isErrorEnabled) {
                binding.usernameInput.isErrorEnabled = false
            }
        }
        if (password == "") {
            binding.passwordInput.error = "Invalid password"
            error = true
        } else {
            if (binding.passwordInput.isErrorEnabled) {
                binding.passwordInput.isErrorEnabled = false
            }
        }
        if (error) {
            binding.root.snackbar("Validation error!!")
            return null
        }
        return AuthData("", username, password, null)
    }
}
