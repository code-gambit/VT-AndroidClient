package com.github.code.gambit.ui.fragment.auth.confirmationcomponent

import android.content.Context
import androidx.lifecycle.LiveData

interface ConfirmationComponent {
    companion object {
        fun bind(context: Context): ConfirmationDialog {
            return ConfirmationDialog(context)
        }
    }
    fun isShowing(): Boolean
    fun show(userEmail: String)
    fun exit(exitFunction: () -> Unit = {})
    fun getOtp(): LiveData<String>
    fun setResendCallback(callback: (email: String) -> Unit)
    fun showError(message: String = "")
}
