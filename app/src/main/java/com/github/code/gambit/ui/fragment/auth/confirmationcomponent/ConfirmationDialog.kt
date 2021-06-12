package com.github.code.gambit.ui.fragment.auth.confirmationcomponent

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.code.gambit.R
import com.github.code.gambit.databinding.EmailVerificationLayoutBinding
import com.github.code.gambit.utility.extention.enableAfter
import com.github.code.gambit.utility.extention.setStatusColor
import com.github.code.gambit.utility.extention.snackbar
import kotlin.math.hypot

class ConfirmationDialog(val context: Context) : ConfirmationComponent {

    private var dialog: Dialog = Dialog(context, R.style.Theme_VTransfer)

    private var _binding: EmailVerificationLayoutBinding = EmailVerificationLayoutBinding.bind(
        LayoutInflater.from(context).inflate(R.layout.email_verification_layout, null)
    )
    private val binding get() = _binding

    private val defaultResendCounter = 50
    private val _otp = MutableLiveData<String>()
    private var resendCallBack: ((email: String) -> Unit)? = null
    private var cancelCallBack: (() -> Unit)? = null

    private var userEmail: String = ""

    init {
        dialog.setContentView(binding.root)

        dialog.window?.setStatusColor(
            ContextCompat.getColor(
                context,
                R.color.secondary
            )
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.validate.setOnClickListener {
            val otp = binding.otpView.otp
            otp?.let {
                if (otp.length != 6) {
                    snackbar("Invalid code length")
                    showError()
                } else {
                    binding.progressBar.show()
                    binding.validate.isClickable = false
                    _otp.value = it
                }
            } ?: snackbar("Code can't be empty")
        }

        binding.cancel.setOnClickListener {
            revealShow(b = false, exit = true, exitFunction = cancelCallBack ?: {})
        }

        binding.resend.setOnClickListener { v ->
            resendCallBack?.let {
                v.isEnabled = false
                it(userEmail)
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
    }

    private fun revealShow(b: Boolean, exit: Boolean = false, exitFunction: () -> Unit = {}) {
        val view = binding.root
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
                    if (exit) {
                        dialog.dismiss()
                        view.visibility = View.INVISIBLE
                        exitFunction()
                    }
                }
            })
            anim.duration = 700
            anim.start()
        }
    }

    private fun snackbar(message: String) {
        binding.root.snackbar(message)
    }

    override fun isShowing() = dialog.isShowing

    @SuppressLint("SetTextI18n")
    override fun show(userEmail: String) {
        binding.resend.enableAfter(defaultResendCounter, binding.resendTimer)
        this.userEmail = userEmail
        binding.infoText.text = "${binding.infoText.text}\n$userEmail"
        dialog.show()
    }

    override fun exit(exitFunction: () -> Unit) {
        revealShow(b = false, exit = true, exitFunction = exitFunction)
    }

    override fun getOtp(): LiveData<String> {
        return _otp
    }

    override fun setResendCallback(callback: (email: String) -> Unit) {
        resendCallBack = callback
    }

    override fun showError(message: String) {
        binding.validate.isClickable = true
        binding.progressBar.hide()
        binding.otpView.showError()
        if (message.isNotEmpty()) {
            snackbar(message)
        }
    }

    override fun setOnCancelCallback(cancelFunction: () -> Unit) {
        cancelCallBack = cancelFunction
    }

    override fun onResendResult(success: Boolean) {
        if (success) {
            binding.resend.enableAfter(defaultResendCounter, binding.resendTimer)
        } else {
            binding.resend.enableAfter(0, binding.resendTimer)
        }
    }
}
