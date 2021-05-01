package com.github.code.gambit.ui.fragment.auth

import `in`.aabhasjindal.otptextview.OtpTextView
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.code.gambit.PreferenceManager
import com.github.code.gambit.R
import com.github.code.gambit.data.model.User
import com.github.code.gambit.databinding.EmailVerificationLayoutBinding
import com.github.code.gambit.databinding.FragmentAuthBinding
import com.github.code.gambit.helper.auth.AuthData
import com.github.code.gambit.helper.auth.AuthState
import com.github.code.gambit.ui.AuthFragmentAdapter
import com.github.code.gambit.utility.exitFullscreen
import com.github.code.gambit.utility.setStatusColor
import com.github.code.gambit.utility.show
import com.github.code.gambit.utility.snackbar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.hypot

@AndroidEntryPoint
class AuthFragment : Fragment(R.layout.fragment_auth) {

    private lateinit var _binding: FragmentAuthBinding
    private val binding get() = _binding

    private val currentPage get() = binding.fragmentContainer.currentItem

    private val viewModel: AuthViewModel by viewModels()

    private lateinit var authData: AuthData

    private lateinit var mOtpTextView: OtpTextView
    private lateinit var dialogView: View
    private lateinit var dialog: Dialog

    @Inject
    lateinit var preferenceManager: PreferenceManager

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

        binding.buttonSubmit.setOnClickListener {
            val fg = (binding.fragmentContainer.adapter as AuthFragmentAdapter).getFragment(
                currentPage
            )
            if (currentPage == 0) {
                val data = (fg as SignUpFragment).validate()
                if (data != null) {
                    signUp(data)
                }
                Log.i("test", "onViewCreated: $data")
            } else {
                val data = (fg as LoginFragment).validate()
                logIn(data)
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
                        showConfirmationDialog()
                    }
                    is AuthState.Success<User> -> {
                        binding.progressBar.hide()
                        binding.root.snackbar("Welcome ${it.data.name}")
                        if (this::dialog.isInitialized && dialog.isShowing) {
                            revealShow(false, exit = true) { navigateToHome() }
                        } else {
                            navigateToHome()
                        }
                    }
                    is AuthState.Error -> {
                        binding.progressBar.hide()
                        if (this::dialog.isInitialized && dialog.isShowing) {
                            revealShow(false, exit = true)
                        }
                        binding.root.snackbar(it.reason)
                    }
                    AuthState.CodeMissMatch -> {
                        mOtpTextView.showError()
                    }
                }
            }
        )
    }

    private fun showConfirmationDialog() {
        dialog = Dialog(requireContext(), R.style.Theme_VTransfer)
        EmailVerificationLayoutBinding.inflate(layoutInflater)
        dialog.setContentView(R.layout.email_verification_layout)
        dialog.window?.setStatusColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.secondary
            )
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        val otpTextView: OtpTextView = dialog.findViewById(R.id.otp_view)
        val progressBar: View = dialog.findViewById(R.id.progress_bar)
        mOtpTextView = otpTextView
        val validate: TextView = dialog.findViewById(R.id.validate)
        dialogView = dialog.findViewById(R.id.root)

        validate.setOnClickListener {
            val otp = otpTextView.otp
            when {
                otp == null -> {
                    dialogView.snackbar("Code can't be empty")
                }
                otp.length != 6 -> {
                    dialogView.snackbar("Invalid code length")
                }
                else -> {
                    progressBar.show()
                    validate.isClickable = false
                    confirmSignUp(otp)
                }
            }
        }

        dialog.setOnShowListener { revealShow(true) }

        dialog.setOnKeyListener(
            DialogInterface.OnKeyListener { _, i, _ ->
                if (i == KeyEvent.KEYCODE_BACK) {
                    revealShow(false)
                    return@OnKeyListener true
                }
                false
            }
        )

        dialog.show()
    }

    private fun revealShow(b: Boolean, exit: Boolean = false, exitFunction: () -> Unit = {}) {
        val view = dialogView.findViewById<View>(R.id.root)
        val w = view.width
        val h = view.height
        val endRadius = hypot(w.toDouble(), h.toDouble()).toInt()
        val cx = (view.width / 2)
        val cy = 0
        if (b) {
            val revealAnimator =
                ViewAnimationUtils.createCircularReveal(view, cx, cy, 0f, endRadius.toFloat())
            view.visibility = View.VISIBLE
            revealAnimator.duration = 700
            revealAnimator.start()
        } else {
            val anim =
                ViewAnimationUtils.createCircularReveal(view, cx, cy, endRadius.toFloat(), 0f)
            anim.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    dialog.dismiss()
                    view.visibility = View.INVISIBLE
                    if (exit) {
                        exitFunction()
                    }
                }
            })
            anim.duration = 700
            anim.start()
        }
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
}
