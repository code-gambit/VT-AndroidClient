package com.github.code.gambit.ui.fragment.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.github.code.gambit.R
import com.github.code.gambit.databinding.FragmentLoginBinding
import com.github.code.gambit.helper.auth.AuthData
import com.github.code.gambit.utility.snackbar

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var _binding: FragmentLoginBinding
    val binding get() = _binding

    val username get() = binding.usernameInput.editText?.text.toString().trim()
    val password get() = binding.passwordInput.editText?.text.toString().trim()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)
    }

    // validates the input fields
    fun validate(): AuthData {
        val error = false
        if (username == "") {
            binding.usernameInput.error = "Username can't be empty"
        } else {
            if (binding.usernameInput.isErrorEnabled) {
                binding.usernameInput.isErrorEnabled = false
            }
        }
        if (password == "") {
            binding.passwordInput.error = "Invalid password"
        } else {
            if (binding.passwordInput.isErrorEnabled) {
                binding.passwordInput.isErrorEnabled = false
            }
        }
        if (error) {
            binding.root.snackbar("Validation error!!")
        }
        return AuthData("", username, password, null, null)
    }
}
