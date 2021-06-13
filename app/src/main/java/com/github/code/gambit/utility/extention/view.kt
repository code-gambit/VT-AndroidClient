package com.github.code.gambit.utility.extention

import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Handler
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.github.code.gambit.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout

fun Window.setStatusColor(color: Int) {
    this.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    this.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    this.statusBarColor = color
}

fun Window.fullscreen() {
    this.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
}

fun Window.exitFullscreen() {
    this.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
}

fun View.anim(delay: Long) {
    this.animate().translationY(0f)
        .alpha(1f)
        .setInterpolator(DecelerateInterpolator())
        .setStartDelay(delay).setDuration(700).start()
}

fun View?.hide() {
    this?.apply {
        if (visibility != View.GONE) {
            this.visibility = View.GONE
        }
    }
}

fun View?.show() {
    this?.apply {
        if (visibility != View.VISIBLE) {
            this.visibility = View.VISIBLE
        }
    }
}

fun View?.enableAfter(timeInSeconds: Int, counterView: TextView) {
    if (this == null) {
        return
    }
    var count = timeInSeconds
    this.isEnabled = false
    counterView.visibility = View.VISIBLE
    Handler().post(object : Runnable {
        override fun run() {
            counterView.text = count.toString()
            if (count <= 0) {
                this@enableAfter.isEnabled = true
                counterView.visibility = View.INVISIBLE
                return
            }
            count--
            handler.postDelayed(this, 1000)
        }
    })
}

fun View.toggleVisibility() {
    if (isVisible) {
        visibility = View.GONE
        return
    }
    visibility = View.VISIBLE
}

fun View.snackbar(message: String, view: View) {
    Snackbar.make(this, message, Snackbar.LENGTH_INDEFINITE)
        .setAnchorView(view)
        .also { snackbar ->
            snackbar.setAction("OK") {
                snackbar.dismiss()
            }
                .show()
        }
}

fun View.snackbar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_INDEFINITE)
        .also { snackbar ->
            snackbar.setAction("OK") {
                snackbar.dismiss()
            }
                .show()
        }
}

fun RelativeLayout.bottomNavHide() {
    if (isVisible) {
        visibility = View.GONE
        animation = AnimationUtils.loadAnimation(context, R.anim.bottom_nav_hide)
    }
}

fun RelativeLayout.bottomNavShow() {
    if (!isVisible) {
        visibility = View.VISIBLE
        animation = AnimationUtils.loadAnimation(context, R.anim.bottom_nav_show)
    }
}

fun TextInputLayout.setText(text: String) {
    if (this.editText != null) {
        this.editText!!.setText(text)
    }
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Fragment.shortToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

fun Fragment.longToast(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
}

fun Fragment.copyToClipboard(text: String) {
    val clipboard: ClipboardManager? =
        requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
    val url = text.toSharableUrl()
    val clip = ClipData.newPlainText("COPIED", url)
    clipboard?.setPrimaryClip(clip)
    shortToast("Url copied")
}

fun Context.showDefaultAlert(title: String, message: String, positiveButtonPress: () -> Unit) {
    AlertDialog.Builder(this)
        .setTitle(title).setMessage(message)
        .setPositiveButton("Yes") { _, _ -> positiveButtonPress() }
        .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }.create().show()
}

fun Context.showDefaultMaterialAlert(
    title: String,
    message: String,
    positiveButtonPress: () -> Unit
) {
    MaterialAlertDialogBuilder(this)
        .setTitle(title).setMessage(message)
        .setPositiveButton("Yes") { _, _ -> positiveButtonPress() }
        .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }.create().show()
}
