package com.github.code.gambit.ui.fragment.profile

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.code.gambit.R
import com.github.code.gambit.data.model.User
import com.github.code.gambit.databinding.FragmentProfileBinding
import com.github.code.gambit.helper.profile.ProfileState
import com.github.code.gambit.ui.activity.main.MainActivity
import com.github.code.gambit.utility.extention.byteToMb
import com.github.code.gambit.utility.extention.hide
import com.github.code.gambit.utility.extention.hideKeyboard
import com.github.code.gambit.utility.extention.setText
import com.github.code.gambit.utility.extention.shortToast
import com.github.code.gambit.utility.extention.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var _binding: FragmentProfileBinding
    private val binding get() = _binding

    private lateinit var user: User

    private val viewModel: ProfileViewModel by viewModels()

    private lateinit var changePasswordListener1: View.OnClickListener
    private lateinit var changePasswordListener2: View.OnClickListener

    private var isFirstEdit = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)

        viewModel.profileState.observe(viewLifecycleOwner) {
            when (it) {
                is ProfileState.Loading -> binding.progressBar.show()
                is ProfileState.LogOutSuccess -> {
                    showSnack("Log out success")
                    findNavController().navigate(R.id.action_profileFragment_to_authFragment)
                }
                is ProfileState.PasswordUpdated -> binding.apply {
                    this.progressBar.hide()
                    showSnack("Password updated")
                    binding.changePasswordButton.isEnabled = true
                }
                is ProfileState.ProfileLoaded -> binding.apply {
                    this.progressBar.hide()
                    setText(it.user)
                }
                ProfileState.ProfileUpdateFailed -> binding.apply {
                    this.progressBar.hide()
                    showSnack("Profile update failed!!")
                }
                is ProfileState.Error -> binding.apply {
                    this.progressBar.hide()
                    showSnack(it.message)
                }
                is ProfileState.UserNameUpdated -> binding.apply {
                    this.progressBar.hide()
                    user.name = it.name
                    this.fullName.editText?.setText(user.name)
                    showSnack("User name updated")
                }
            }
        }
        viewModel.setEvent(ProfileEvent.GetUserInfoEvent)
        setClickListeners()
    }

    private fun setClickListeners() {
        binding.fullName.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!isFirstEdit) {
                    binding.cancelButton.show()
                    binding.doneButton.show()
                    if (s.toString() == user.name) {
                        binding.cancelButton.hide()
                        binding.doneButton.hide()
                    }
                } else {
                    isFirstEdit = false
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.cancelButton.setOnClickListener {
            looseFocus()
            it.hide()
            binding.fullName.setText(user.name)
            if (binding.oldPassword.isVisible) {
                binding.passwordContainer.hide()
                binding.changePasswordButton.setOnClickListener(changePasswordListener1)
                binding.changePasswordButton.isEnabled = true
                binding.changePasswordButton.text = getString(R.string.change_password)
            }
        }
        binding.doneButton.setOnClickListener {
            looseFocus()
            val newName = binding.fullName.editText!!.text.toString().trim()
            if (user.name == newName) {
                shortToast("No change")
            } else {
                viewModel.setEvent(ProfileEvent.UpdateDisplayNameEvent(newName))
            }
        }
        binding.logOutButton.setOnClickListener {
            binding.logOutButton.isEnabled = false
            viewModel.setEvent(ProfileEvent.LogOut)
        }
        changePasswordListener1 = View.OnClickListener {
            binding.passwordContainer.show()
            binding.cancelButton.show()
            binding.changePasswordButton.text = getString(R.string.done)
            binding.changePasswordButton.setOnClickListener(changePasswordListener2)
        }
        changePasswordListener2 = View.OnClickListener {
            val oldPassword = binding.oldPassword.editText!!.text.toString().trim()
            val newPassword = binding.newPassword.editText!!.text.toString().trim()
            if (!isValidatePassword(oldPassword, newPassword)) {
                return@OnClickListener
            }
            binding.passwordContainer.hide()
            binding.changePasswordButton.isEnabled = false
            binding.changePasswordButton.text = getString(R.string.change_password)
            binding.changePasswordButton.setOnClickListener(changePasswordListener1)
            viewModel.setEvent(ProfileEvent.UpdatePasswordEvent(oldPassword, newPassword))
        }
        binding.changePasswordButton.setOnClickListener(changePasswordListener1)
    }

    private fun isValidatePassword(oldPassword: String, newPassword: String): Boolean {
        if (oldPassword == "") {
            binding.oldPassword.error = "Old password cant be empty"
            return false
        } else {
            if (binding.oldPassword.isErrorEnabled) {
                binding.oldPassword.isErrorEnabled = false
            }
        }
        if (newPassword == "") {
            binding.newPassword.error = "New password cant be empty"
            return false
        } else {
            if (binding.newPassword.isErrorEnabled) {
                binding.newPassword.isErrorEnabled = false
            }
        }
        return true
    }

    fun looseFocus() {
        binding.cancelButton.hide()
        binding.doneButton.hide()
        binding.fullName.editText!!.clearFocus()
        hideKeyboard()
    }

    private fun setText(user: User) {
        this.user = user
        binding.userEmail.text = user.email
        binding.fullName.editText?.setText(user.name)
        binding.storage.text = user.storageUsed.byteToMb()
    }

    private fun showSnack(message: String) {
        (requireActivity() as MainActivity).showSnackBar(message)
    }
}
