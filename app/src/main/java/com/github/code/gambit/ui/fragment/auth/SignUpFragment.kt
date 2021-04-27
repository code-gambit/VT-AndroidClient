package com.github.code.gambit.ui.fragment.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.github.code.gambit.R
import com.github.code.gambit.databinding.FragmentSignUpBinding
import com.github.code.gambit.helper.auth.AuthData
import com.github.code.gambit.utility.snackbar

class SignUpFragment : Fragment(R.layout.fragment_sign_up) {

    private lateinit var _binding: FragmentSignUpBinding
    val binding get() = _binding

    private val fullName get() = binding.fullNameInput.editText?.text.toString().trim()
    private val userEmail get() = binding.userEmailInput.editText?.text.toString().trim()
    private val password get() = binding.passwordInput.editText?.text.toString().trim()
    private val confirmPassword get() = binding.confirmPasswordInput.editText?.text.toString().trim()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSignUpBinding.bind(view)
    }

    // validates the input fields
    fun validate(): AuthData? {
        var error = false
        if (fullName == "") {
            binding.fullNameInput.error = "Username can't be empty"
            error = true
        } else {
            if (binding.fullNameInput.isErrorEnabled) {
                binding.fullNameInput.isErrorEnabled = false
            }
        }
        if (userEmail == "") {
            binding.userEmailInput.error = "Email can't be empty"
            error = true
        } else {
            if (binding.userEmailInput.isErrorEnabled) {
                binding.userEmailInput.isErrorEnabled = false
            }
        }
        if (password == "") {
            binding.passwordInput.error = "Password can't be empty"
            error = true
        } else {
            if (binding.passwordInput.isErrorEnabled) {
                binding.passwordInput.isErrorEnabled = false
            }
        }
        if (confirmPassword == "") {
            binding.confirmPasswordInput.error = "Confirm password can't be empty"
            error = true
        } else {
            if (binding.confirmPasswordInput.isErrorEnabled) {
                binding.confirmPasswordInput.isErrorEnabled = false
            }
        }
        if (password != "" && confirmPassword != "") {
            if (password != confirmPassword) {
                binding.passwordInput.error = "Password didn't match"
                binding.confirmPasswordInput.error = "Password didn't match"
                error = true
            } else {
                binding.passwordInput.isErrorEnabled = false
                binding.confirmPasswordInput.isErrorEnabled = false
            }
        }
        if (error) {
            binding.root.snackbar("Validation error!!")
            return null
        }
        return AuthData(fullName, userEmail, password, null)
    }
}
